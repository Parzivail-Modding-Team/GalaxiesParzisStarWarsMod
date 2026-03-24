package dev.pswg.toolchain.build;

import dev.pswg.toolchain.intellij.IntelliJDependencyResolver;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.model.SourceSetDependencyResolver;
import dev.pswg.toolchain.model.SourceSetLayout;
import dev.pswg.toolchain.model.SourceSetNames;
import dev.pswg.toolchain.pswg.PswgRepositoryContext;
import dev.pswg.toolchain.util.ToolchainLog;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compiles PSWG modules into a toolchain-owned output tree without relying on IntelliJ.
 */
public final class CiCompilationService
{
	/**
	 * The toolchain-owned output root used for CI-safe compilation.
	 */
	private static final Path OUTPUT_ROOT = Path.of("work", "ci-build", "out", "production");

	/**
	 * Resolves external and implicit compile dependencies.
	 */
	private final IntelliJDependencyResolver _dependencyResolver;

	/**
	 * Creates the CI compilation service.
	 */
	public CiCompilationService()
	{
		_dependencyResolver = new IntelliJDependencyResolver();
	}

	/**
	 * Compiles the requested artifact closure into the toolchain-owned output tree.
	 *
	 * @param requestedModuleId the requested root module, or {@code null} for the graph default
	 * @param refresh whether to refresh external dependency resolution
	 * @return the output root that now contains compiled module outputs
	 * @throws IOException if compilation fails
	 */
	public Path compileArtifactInputs(String requestedModuleId, boolean refresh) throws IOException
	{
		PswgRepositoryContext repository = PswgRepositoryContext.discoverFromToolchainWorkingDirectory();
		String rootModuleId = requestedModuleId == null || requestedModuleId.isBlank()
			? repository.buildGraph().developmentModuleId()
			: requestedModuleId;
		Path outputRoot = repository.toolchainRoot().resolve(OUTPUT_ROOT);

		deleteDirectory(outputRoot);
		Files.createDirectories(outputRoot);

		for (ModuleSpec module : compilationClosure(repository.buildGraph(), rootModuleId))
		{
			compileSourceSet(repository, refresh, outputRoot, module, SourceSetNames.MAIN);

			if (SourceSetLayout.hasClientSourceSet(module))
			{
				compileSourceSet(repository, refresh, outputRoot, module, SourceSetNames.CLIENT);
			}
		}

		return outputRoot;
	}

	/**
	 * Resolves the topologically ordered compilation closure for one root module.
	 *
	 * @param graph the authoritative build graph
	 * @param rootModuleId the requested root module
	 * @return the ordered compilation closure
	 */
	private List<ModuleSpec> compilationClosure(BuildGraph graph, String rootModuleId)
	{
		List<ModuleSpec> ordered = new ArrayList<>();
		collectCompilationClosure(graph, rootModuleId, ordered, new LinkedHashSet<>());
		return List.copyOf(ordered);
	}

	/**
	 * Recursively collects modules needed to compile one root module and its aggregate members.
	 *
	 * @param graph the authoritative build graph
	 * @param moduleId the current module identifier
	 * @param ordered the ordered module list
	 * @param visited the visited module identifiers
	 */
	private void collectCompilationClosure(
		BuildGraph graph,
		String moduleId,
		List<ModuleSpec> ordered,
		Set<String> visited
	)
	{
		if (!visited.add(moduleId))
		{
			return;
		}

		ModuleSpec module = requireModule(graph, moduleId);

		for (String dependencyId : module.dependencies())
		{
			collectCompilationClosure(graph, dependencyId, ordered, visited);
		}

		for (String processorId : module.annotationProcessors())
		{
			collectCompilationClosure(graph, processorId, ordered, visited);
		}

		for (String aggregateMemberId : module.aggregateMembers())
		{
			collectCompilationClosure(graph, aggregateMemberId, ordered, visited);
		}

		ordered.add(module);
	}

	/**
	 * Compiles one source set into the toolchain-owned output tree.
	 *
	 * @param repository the discovered repository context
	 * @param refresh whether to refresh external dependency resolution
	 * @param outputRoot the toolchain-owned output root
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @throws IOException if compilation fails
	 */
	private void compileSourceSet(
		PswgRepositoryContext repository,
		boolean refresh,
		Path outputRoot,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		Path classOutput = CompilationOutputLayout.sourceSetOutputRoot(
			outputRoot,
			repository.projectName(),
			module.id(),
			sourceSetName
		);
		List<Path> generatedRoots = SourceSetLayout.generatedRoots(module, sourceSetName)
		                                           .stream()
		                                           .map(repository.projectRoot()::resolve)
		                                           .toList();
		List<Path> sourceFiles = collectSourceFiles(repository, module, sourceSetName);

		deleteDirectory(classOutput);
		Files.createDirectories(classOutput);

		for (Path generatedRoot : generatedRoots)
		{
			deleteDirectory(generatedRoot);
			Files.createDirectories(generatedRoot);
		}

		copyResources(repository, module, sourceSetName, classOutput);

		if (sourceFiles.isEmpty())
		{
			ToolchainLog.info("ci-build", "Skipped javac for " + module.id() + ":" + sourceSetName + " (no Java sources)");
			return;
		}

		invokeCompiler(
			repository,
			refresh,
			outputRoot,
			module,
			sourceSetName,
			classOutput,
			generatedRoots,
			sourceFiles
		);
	}

	/**
	 * Collects all Java source files that belong to one source set.
	 *
	 * @param repository the discovered repository context
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @return the ordered source files
	 * @throws IOException if source discovery fails
	 */
	private List<Path> collectSourceFiles(
		PswgRepositoryContext repository,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		List<Path> sourceFiles = new ArrayList<>();

		for (Path root : SourceSetLayout.sourceRoots(module, sourceSetName))
		{
			addJavaFiles(sourceFiles, repository.projectRoot().resolve(root));
		}

		return List.copyOf(sourceFiles);
	}

	/**
	 * Adds Java source files from one root to the accumulated source list.
	 *
	 * @param output the accumulated source files
	 * @param root the root to scan
	 * @throws IOException if walking the directory fails
	 */
	private void addJavaFiles(List<Path> output, Path root) throws IOException
	{
		if (!Files.isDirectory(root))
		{
			return;
		}

		try (Stream<Path> paths = Files.walk(root))
		{
			for (Path path : paths.filter(Files::isRegularFile)
			                     .filter(path -> path.getFileName().toString().endsWith(".java"))
			                     .sorted()
			                     .toList())
			{
				if (!output.contains(path))
				{
					output.add(path);
				}
			}
		}
	}

	/**
	 * Copies one source set's resource roots into its class output directory.
	 *
	 * @param repository the discovered repository context
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @param classOutput the class output directory
	 * @throws IOException if resource copying fails
	 */
	private void copyResources(
		PswgRepositoryContext repository,
		ModuleSpec module,
		String sourceSetName,
		Path classOutput
	) throws IOException
	{
		for (Path root : SourceSetLayout.resourceRoots(module, sourceSetName))
		{
			Path resolvedRoot = repository.projectRoot().resolve(root);

			if (!Files.isDirectory(resolvedRoot))
			{
				continue;
			}

			try (Stream<Path> paths = Files.walk(resolvedRoot))
			{
				for (Path path : paths.filter(Files::isRegularFile).toList())
				{
					Path relative = resolvedRoot.relativize(path);
					Path target = classOutput.resolve(relative);
					Files.createDirectories(target.getParent());
					Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	/**
	 * Invokes the JDK compiler for one module source set.
	 *
	 * @param repository the discovered repository context
	 * @param refresh whether to refresh external dependency resolution
	 * @param outputRoot the toolchain-owned output root
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @param classOutput the class output directory
	 * @param generatedRoots the generated source roots
	 * @param sourceFiles the source files to compile
	 * @throws IOException if compilation fails
	 */
	private void invokeCompiler(
		PswgRepositoryContext repository,
		boolean refresh,
		Path outputRoot,
		ModuleSpec module,
		String sourceSetName,
		Path classOutput,
		List<Path> generatedRoots,
		List<Path> sourceFiles
	) throws IOException
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		if (compiler == null)
		{
			throw new IOException("No system Java compiler is available. Run the toolchain with a JDK, not a JRE.");
		}

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		Properties gradleProperties = repository.gradleProperties();
		List<Path> classpath = compileClasspath(repository, refresh, outputRoot, module, sourceSetName);
		List<Path> processorPath = processorPath(repository, refresh, outputRoot, module);
		List<String> processorClassNames = annotationProcessorClassNames(repository.buildGraph(), module);
		List<String> options = new ArrayList<>();

		options.add("--release");
		options.add(Integer.toString(module.javaVersion()));
		options.add("-encoding");
		options.add(StandardCharsets.UTF_8.name());
		options.add("-d");
		options.add(classOutput.toString());

		if (!generatedRoots.isEmpty())
		{
			options.add("-s");
			options.add(generatedRoots.getFirst().toString());
		}

		if (!classpath.isEmpty())
		{
			options.add("-classpath");
			options.add(joinPaths(classpath));
		}

		if (!processorPath.isEmpty())
		{
			options.add("-processorpath");
			options.add(joinPaths(processorPath));
		}

		if (!processorClassNames.isEmpty())
		{
			options.add("-processor");
			options.add(String.join(",", processorClassNames));
		}
		else if (processorPath.isEmpty())
		{
			options.add("-proc:none");
		}

		ToolchainLog.info(
			"ci-build",
			"Compiling " + module.id() + ":" + sourceSetName + " with " + sourceFiles.size() + " source files"
		);

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8))
		{
			fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(classOutput));

			if (!generatedRoots.isEmpty())
			{
				fileManager.setLocationFromPaths(StandardLocation.SOURCE_OUTPUT, List.of(generatedRoots.getFirst()));
			}

			if (!classpath.isEmpty())
			{
				fileManager.setLocationFromPaths(StandardLocation.CLASS_PATH, classpath);
			}

			if (!processorPath.isEmpty())
			{
				fileManager.setLocationFromPaths(StandardLocation.ANNOTATION_PROCESSOR_PATH, processorPath);
			}

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromPaths(sourceFiles);
			boolean success = Boolean.TRUE.equals(compiler.getTask(
				null,
				fileManager,
				diagnostics,
				options,
				null,
				compilationUnits
			).call());

			if (!success)
			{
				throw new IOException(formatDiagnostics(module, sourceSetName, diagnostics.getDiagnostics()));
			}
		}
	}

	/**
	 * Resolves the compile classpath for one module source set.
	 *
	 * @param repository the discovered repository context
	 * @param refresh whether to refresh external dependency resolution
	 * @param outputRoot the toolchain-owned output root
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @return the ordered classpath
	 * @throws IOException if dependency resolution fails
	 */
	private List<Path> compileClasspath(
		PswgRepositoryContext repository,
		boolean refresh,
		Path outputRoot,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		Set<Path> classpath = new LinkedHashSet<>();
		boolean includeClient = SourceSetNames.CLIENT.equals(sourceSetName);
		BuildGraph graph = repository.buildGraph();

		if (includeClient)
		{
			classpath.add(CompilationOutputLayout.sourceSetOutputRoot(
				outputRoot,
				repository.projectName(),
				module.id(),
				SourceSetNames.MAIN
			));
		}

		for (String dependencyId : module.dependencies())
		{
			collectDependencyOutputs(
				graph,
				outputRoot,
				repository.projectName(),
				dependencyId,
				sourceSetName,
				classpath,
				new LinkedHashSet<>()
			);
		}

		classpath.addAll(
			_dependencyResolver.resolveModuleLibraries(
				graph,
				repository.projectRoot(),
				repository.gradleProperties(),
				refresh,
				module,
				includeClient
			)
		);

		return List.copyOf(classpath);
	}

	/**
	 * Recursively collects compiled module outputs needed on the classpath for one dependency chain.
	 *
	 * @param graph the authoritative build graph
	 * @param outputRoot the toolchain-owned output root
	 * @param projectName the IntelliJ project name
	 * @param moduleId the current dependency module identifier
	 * @param consumerSourceSetName the consuming source-set name
	 * @param output the accumulated classpath entries
	 * @param visited the visited module identifiers
	 */
	private void collectDependencyOutputs(
		BuildGraph graph,
		Path outputRoot,
		String projectName,
		String moduleId,
		String consumerSourceSetName,
		Set<Path> output,
		Set<String> visited
	)
	{
		if (!visited.add(moduleId))
		{
			return;
		}

		ModuleSpec module = requireModule(graph, moduleId);
		output.add(CompilationOutputLayout.sourceSetOutputRoot(
			outputRoot,
			projectName,
			moduleId,
			SourceSetNames.MAIN
		));

		if (SourceSetNames.CLIENT.equals(consumerSourceSetName)
			&& SourceSetNames.CLIENT.equals(SourceSetDependencyResolver.dependencySourceSetName(graph, moduleId, consumerSourceSetName)))
		{
			output.add(CompilationOutputLayout.sourceSetOutputRoot(
				outputRoot,
				projectName,
				moduleId,
				SourceSetNames.CLIENT
			));
		}

		for (String dependencyId : module.dependencies())
		{
			collectDependencyOutputs(
				graph,
				outputRoot,
				projectName,
				dependencyId,
				consumerSourceSetName,
				output,
				visited
			);
		}
	}

	/**
	 * Resolves the annotation processor path for one module.
	 *
	 * @param repository the discovered repository context
	 * @param refresh whether to refresh external dependency resolution
	 * @param outputRoot the toolchain-owned output root
	 * @param module the module specification
	 * @return the ordered processor path
	 * @throws IOException if dependency resolution fails
	 */
	private List<Path> processorPath(
		PswgRepositoryContext repository,
		boolean refresh,
		Path outputRoot,
		ModuleSpec module
	) throws IOException
	{
		Set<Path> path = new LinkedHashSet<>();
		BuildGraph graph = repository.buildGraph();
		Properties gradleProperties = repository.gradleProperties();

		path.addAll(_dependencyResolver.resolveExternalDependencies(module.annotationProcessorDependencies(), gradleProperties, refresh));

		for (String processorId : module.annotationProcessors())
		{
			ModuleSpec processorModule = requireModule(graph, processorId);
			path.add(CompilationOutputLayout.sourceSetOutputRoot(
				outputRoot,
				repository.projectName(),
				processorId,
				SourceSetNames.MAIN
			));

			for (String dependencyId : processorModule.dependencies())
			{
				path.add(CompilationOutputLayout.sourceSetOutputRoot(
					outputRoot,
					repository.projectName(),
					dependencyId,
					SourceSetNames.MAIN
				));
			}

			path.addAll(_dependencyResolver.resolveExternalDependencies(processorModule.compileDependencies(), gradleProperties, refresh));
			path.addAll(_dependencyResolver.resolveExternalDependencies(processorModule.annotationProcessorDependencies(), gradleProperties, refresh));
		}

		return List.copyOf(path);
	}

	/**
	 * Collects the explicit annotation processor class names for one module.
	 *
	 * @param graph the authoritative build graph
	 * @param module the module specification
	 * @return the ordered processor class names
	 */
	private List<String> annotationProcessorClassNames(BuildGraph graph, ModuleSpec module)
	{
		List<String> classNames = new ArrayList<>();

		for (String processorId : module.annotationProcessors())
		{
			ModuleSpec processorModule = requireModule(graph, processorId);

			for (String className : processorModule.providedAnnotationProcessorClasses())
			{
				if (!classNames.contains(className))
				{
					classNames.add(className);
				}
			}
		}

		return List.copyOf(classNames);
	}

	/**
	 * Formats compiler diagnostics into one readable failure message.
	 *
	 * @param module the module that failed to compile
	 * @param sourceSetName the failing source set
	 * @param diagnostics the compiler diagnostics
	 * @return the formatted diagnostic message
	 */
	private String formatDiagnostics(
		ModuleSpec module,
		String sourceSetName,
		Collection<Diagnostic<? extends JavaFileObject>> diagnostics
	)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Compilation failed for ")
		       .append(module.id())
		       .append(':')
		       .append(sourceSetName);

		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics)
		{
			builder.append(System.lineSeparator())
			       .append(diagnostic.getKind())
			       .append(": ");

			if (diagnostic.getSource() != null)
			{
				builder.append(diagnostic.getSource().toUri());

				if (diagnostic.getLineNumber() > 0)
				{
					builder.append(':').append(diagnostic.getLineNumber());
				}

				builder.append(": ");
			}

			builder.append(diagnostic.getMessage(Locale.ROOT));
		}

		return builder.toString();
	}

	/**
	 * Joins filesystem paths into a platform classpath string.
	 *
	 * @param paths the paths to join
	 * @return the classpath string
	 */
	private String joinPaths(Collection<Path> paths)
	{
		return paths.stream().map(Path::toString).collect(Collectors.joining(java.io.File.pathSeparator));
	}

	/**
	 * Resolves one module specification by id.
	 *
	 * @param graph the authoritative build graph
	 * @param moduleId the module id
	 * @return the resolved module specification
	 */
	private ModuleSpec requireModule(BuildGraph graph, String moduleId)
	{
		return graph.modules()
		            .stream()
		            .filter(candidate -> moduleId.equals(candidate.id()))
		            .findFirst()
		            .orElseThrow(() -> new IllegalArgumentException("Unknown module id: " + moduleId));
	}

	/**
	 * Deletes one directory tree if it exists.
	 *
	 * @param root the root to delete
	 * @throws IOException if deletion fails
	 */
	private void deleteDirectory(Path root) throws IOException
	{
		if (!Files.exists(root))
		{
			return;
		}

		try (Stream<Path> paths = Files.walk(root))
		{
			for (Path path : paths.sorted(Comparator.reverseOrder()).toList())
			{
				Files.deleteIfExists(path);
			}
		}
	}
}
