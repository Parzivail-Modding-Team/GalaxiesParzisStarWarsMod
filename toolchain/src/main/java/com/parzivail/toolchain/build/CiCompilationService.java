package com.parzivail.toolchain.build;

import com.parzivail.toolchain.intellij.IntelliJDependencyResolver;
import com.parzivail.toolchain.model.*;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.project.RepositoryContext;
import com.parzivail.toolchain.util.ToolchainLog;

import javax.tools.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Compiles modules into a toolchain-owned output tree using javac.
 */
public final class CiCompilationService
{
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
	 * @param refresh           whether to refresh external dependency resolution
	 *
	 * @return the output root that now contains compiled module outputs
	 *
	 * @throws IOException if compilation fails
	 */
	public Path compileArtifactInputs(String requestedModuleId, boolean refresh) throws IOException
	{
		var repository = RepositoryContext.load();
		var rootModuleId = requestedModuleId == null || requestedModuleId.isBlank()
		                   ? repository.buildGraph().developmentModuleId()
		                   : requestedModuleId;
		var outputRoot = ToolchainPaths.CI_OUTPUT_ROOT;

		deleteDirectory(outputRoot);
		Files.createDirectories(outputRoot);

		for (var module : compilationClosure(repository.buildGraph(), rootModuleId))
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
	 * @param graph        the authoritative build graph
	 * @param rootModuleId the requested root module
	 *
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
	 * @param graph    the authoritative build graph
	 * @param moduleId the current module identifier
	 * @param ordered  the ordered module list
	 * @param visited  the visited module identifiers
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

		var module = requireModule(graph, moduleId);

		for (var dependencyId : module.dependencies())
		{
			collectCompilationClosure(graph, dependencyId, ordered, visited);
		}

		for (var processorId : module.annotationProcessors())
		{
			collectCompilationClosure(graph, processorId, ordered, visited);
		}

		for (var aggregateMemberId : module.aggregateMembers())
		{
			collectCompilationClosure(graph, aggregateMemberId, ordered, visited);
		}

		ordered.add(module);
	}

	/**
	 * Compiles one source set into the toolchain-owned output tree.
	 *
	 * @param repository    the discovered repository context
	 * @param refresh       whether to refresh external dependency resolution
	 * @param outputRoot    the toolchain-owned output root
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @throws IOException if compilation fails
	 */
	private void compileSourceSet(
			RepositoryContext repository,
			boolean refresh,
			Path outputRoot,
			ModuleSpec module,
			String sourceSetName
	) throws IOException
	{
		var classOutput = CompilationOutputLayout.sourceSetOutputRoot(
				outputRoot,
				repository.projectName(),
				module.id(),
				sourceSetName
		);
		var generatedRoots = SourceSetLayout.generatedRoots(module, sourceSetName)
		                                    .stream()
		                                    .map(ToolchainPaths.PROJECT_ROOT::resolve)
		                                    .toList();
		var sourceFiles = collectSourceFiles(module, sourceSetName);

		deleteDirectory(classOutput);
		Files.createDirectories(classOutput);

		for (var generatedRoot : generatedRoots)
		{
			deleteDirectory(generatedRoot);
			Files.createDirectories(generatedRoot);
		}

		copyResources(module, sourceSetName, classOutput);

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
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the ordered source files
	 *
	 * @throws IOException if source discovery fails
	 */
	private List<Path> collectSourceFiles(
			ModuleSpec module,
			String sourceSetName
	) throws IOException
	{
		List<Path> sourceFiles = new ArrayList<>();

		for (var root : SourceSetLayout.sourceRoots(module, sourceSetName))
		{
			addJavaFiles(sourceFiles, ToolchainPaths.PROJECT_ROOT.resolve(root));
		}

		return List.copyOf(sourceFiles);
	}

	/**
	 * Adds Java source files from one root to the accumulated source list.
	 *
	 * @param output the accumulated source files
	 * @param root   the root to scan
	 *
	 * @throws IOException if walking the directory fails
	 */
	private void addJavaFiles(List<Path> output, Path root) throws IOException
	{
		if (!Files.isDirectory(root))
		{
			return;
		}

		try (var paths = Files.walk(root))
		{
			for (var path : paths.filter(Files::isRegularFile)
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
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 * @param classOutput   the class output directory
	 *
	 * @throws IOException if resource copying fails
	 */
	private void copyResources(
			ModuleSpec module,
			String sourceSetName,
			Path classOutput
	) throws IOException
	{
		for (var root : SourceSetLayout.resourceRoots(module, sourceSetName))
		{
			var resolvedRoot = ToolchainPaths.PROJECT_ROOT.resolve(root);

			if (!Files.isDirectory(resolvedRoot))
			{
				continue;
			}

			try (var paths = Files.walk(resolvedRoot))
			{
				for (var path : paths.filter(Files::isRegularFile).toList())
				{
					var relative = resolvedRoot.relativize(path);
					var target = classOutput.resolve(relative);
					Files.createDirectories(target.getParent());
					Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	/**
	 * Invokes the JDK compiler for one module source set.
	 *
	 * @param repository     the discovered repository context
	 * @param refresh        whether to refresh external dependency resolution
	 * @param outputRoot     the toolchain-owned output root
	 * @param module         the module specification
	 * @param sourceSetName  the source-set name
	 * @param classOutput    the class output directory
	 * @param generatedRoots the generated source roots
	 * @param sourceFiles    the source files to compile
	 *
	 * @throws IOException if compilation fails
	 */
	private void invokeCompiler(
			RepositoryContext repository,
			boolean refresh,
			Path outputRoot,
			ModuleSpec module,
			String sourceSetName,
			Path classOutput,
			List<Path> generatedRoots,
			List<Path> sourceFiles
	) throws IOException
	{
		var compiler = ToolProvider.getSystemJavaCompiler();

		if (compiler == null)
		{
			throw new IOException("No system Java compiler is available. Run the toolchain with a JDK, not a JRE.");
		}

		var diagnostics = new DiagnosticCollector<JavaFileObject>();
		var classpath = compileClasspath(repository, refresh, outputRoot, module, sourceSetName);
		var processorPath = processorPath(repository, refresh, outputRoot, module);
		var processorClassNames = annotationProcessorClassNames(repository.buildGraph(), module);
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

		try (var fileManager = compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8))
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

			var compilationUnits = fileManager.getJavaFileObjectsFromPaths(sourceFiles);
			var success = Boolean.TRUE.equals(compiler.getTask(
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
	 * @param repository    the discovered repository context
	 * @param refresh       whether to refresh external dependency resolution
	 * @param outputRoot    the toolchain-owned output root
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the ordered classpath
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private List<Path> compileClasspath(
			RepositoryContext repository,
			boolean refresh,
			Path outputRoot,
			ModuleSpec module,
			String sourceSetName
	) throws IOException
	{
		Set<Path> classpath = new LinkedHashSet<>();
		var includeClient = SourceSetNames.CLIENT.equals(sourceSetName);
		var graph = repository.buildGraph();

		if (includeClient)
		{
			classpath.add(CompilationOutputLayout.sourceSetOutputRoot(
					outputRoot,
					repository.projectName(),
					module.id(),
					SourceSetNames.MAIN
			));
		}

		for (var dependencyId : module.dependencies())
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
						repository.loaderVersion(),
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
	 * @param graph                 the authoritative build graph
	 * @param outputRoot            the toolchain-owned output root
	 * @param projectName           the IntelliJ project name
	 * @param moduleId              the current dependency module identifier
	 * @param consumerSourceSetName the consuming source-set name
	 * @param output                the accumulated classpath entries
	 * @param visited               the visited module identifiers
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

		var module = requireModule(graph, moduleId);
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

		for (var dependencyId : module.dependencies())
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
	 * @param refresh    whether to refresh external dependency resolution
	 * @param outputRoot the toolchain-owned output root
	 * @param module     the module specification
	 *
	 * @return the ordered processor path
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private List<Path> processorPath(
			RepositoryContext repository,
			boolean refresh,
			Path outputRoot,
			ModuleSpec module
	) throws IOException
	{
		var graph = repository.buildGraph();

		Set<Path> path = new LinkedHashSet<>(_dependencyResolver.resolveExternalDependencies(module.annotationProcessorDependencies(), refresh));

		for (var processorId : module.annotationProcessors())
		{
			var processorModule = requireModule(graph, processorId);
			path.add(CompilationOutputLayout.sourceSetOutputRoot(
					outputRoot,
					repository.projectName(),
					processorId,
					SourceSetNames.MAIN
			));

			for (var dependencyId : processorModule.dependencies())
			{
				path.add(CompilationOutputLayout.sourceSetOutputRoot(
						outputRoot,
						repository.projectName(),
						dependencyId,
						SourceSetNames.MAIN
				));
			}

			path.addAll(_dependencyResolver.resolveExternalDependencies(processorModule.compileDependencies(), refresh));
			path.addAll(_dependencyResolver.resolveExternalDependencies(processorModule.annotationProcessorDependencies(), refresh));
		}

		return List.copyOf(path);
	}

	/**
	 * Collects the explicit annotation processor class names for one module.
	 *
	 * @param graph  the authoritative build graph
	 * @param module the module specification
	 *
	 * @return the ordered processor class names
	 */
	private List<String> annotationProcessorClassNames(BuildGraph graph, ModuleSpec module)
	{
		List<String> classNames = new ArrayList<>();

		for (var processorId : module.annotationProcessors())
		{
			var processorModule = requireModule(graph, processorId);

			for (var className : processorModule.providedAnnotationProcessorClasses())
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
	 * @param module        the module that failed to compile
	 * @param sourceSetName the failing source set
	 * @param diagnostics   the compiler diagnostics
	 *
	 * @return the formatted diagnostic message
	 */
	private String formatDiagnostics(
			ModuleSpec module,
			String sourceSetName,
			Collection<Diagnostic<? extends JavaFileObject>> diagnostics
	)
	{
		var builder = new StringBuilder();
		builder.append("Compilation failed for ")
		       .append(module.id())
		       .append(':')
		       .append(sourceSetName);

		for (var diagnostic : diagnostics)
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
	 *
	 * @return the classpath string
	 */
	private String joinPaths(Collection<Path> paths)
	{
		return paths.stream().map(Path::toString).collect(Collectors.joining(java.io.File.pathSeparator));
	}

	/**
	 * Resolves one module specification by id.
	 *
	 * @param graph    the authoritative build graph
	 * @param moduleId the module id
	 *
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
	 *
	 * @throws IOException if deletion fails
	 */
	private void deleteDirectory(Path root) throws IOException
	{
		if (!Files.exists(root))
		{
			return;
		}

		try (var paths = Files.walk(root))
		{
			for (var path : paths.sorted(Comparator.reverseOrder()).toList())
			{
				Files.deleteIfExists(path);
			}
		}
	}
}
