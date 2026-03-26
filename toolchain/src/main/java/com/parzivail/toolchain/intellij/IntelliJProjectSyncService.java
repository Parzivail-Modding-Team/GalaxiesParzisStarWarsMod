package com.parzivail.toolchain.intellij;

import com.parzivail.toolchain.maven.ToolchainMavenRepositories;
import com.parzivail.toolchain.model.*;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.project.RepositoryContext;
import com.parzivail.toolchain.source.SourceAttachmentResolver;
import com.parzivail.toolchain.template.FileTemplateRenderer;
import com.parzivail.toolchain.template.TemplateXmlWriter;
import com.parzivail.toolchain.template.XmlEscaper;
import com.parzivail.toolchain.util.ToolchainLog;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Generates the IntelliJ project metadata that lets the host project compile from the authoritative toolchain
 * graph instead of from IDE state imported out of Gradle.
 *
 * <p>This service deliberately owns the "shape" of the IntelliJ project: module registration,
 * compiler configuration, generated-source markers, and project-library wiring. The lower-level
 * details of what jars belong on those classpaths live in {@link IntelliJDependencyResolver}.
 */
public final class IntelliJProjectSyncService
{
	/**
	 * The external libraries needed to compile and run the standalone toolchain module from the
	 * root IntelliJ project.
	 */
	static final List<MavenDependencySpec> TOOLCHAIN_DEPENDENCIES = List.of(
			new MavenDependencySpec("com.fasterxml.jackson.core:jackson-databind:2.21.1", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("com.fasterxml.jackson.core:jackson-annotations:2.21", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("com.fasterxml.jackson.core:jackson-core:2.21.1", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("io.github.wasabithumb:jtoml:1.5.0", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("io.github.wasabithumb:jtoml-api:1.5.0", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("io.github.wasabithumb:jtoml-internals:1.5.0", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("org.dom4j:dom4j:2.2.0", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("net.fabricmc:class-tweaker:0.1.1", ToolchainMavenRepositories.FABRIC),
			new MavenDependencySpec("net.fabricmc:tiny-remapper:0.11.2", ToolchainMavenRepositories.FABRIC),
			new MavenDependencySpec("org.vineflower:vineflower:1.11.2", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("org.ow2.asm:asm:9.9", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("org.ow2.asm:asm-commons:9.8", ToolchainMavenRepositories.MAVEN_CENTRAL),
			new MavenDependencySpec("org.ow2.asm:asm-tree:9.8", ToolchainMavenRepositories.MAVEN_CENTRAL)
	);

	/**
	 * Resolves IntelliJ-facing classpath and processor-path artifacts from the authoritative graph.
	 */
	private final IntelliJDependencyResolver _dependencyResolver;

	/**
	 * Resolves optional source archives for IntelliJ project libraries.
	 */
	private final SourceAttachmentResolver _sourceAttachmentResolver;

	/**
	 * Creates a new IntelliJ metadata sync service.
	 */
	public IntelliJProjectSyncService()
	{
		_dependencyResolver = new IntelliJDependencyResolver();
		_sourceAttachmentResolver = new SourceAttachmentResolver();
	}

	/**
	 * Synchronizes compiler and generated-source metadata into the IntelliJ project.
	 *
	 * @param refresh whether to refresh externally resolved Maven artifacts
	 *
	 * @throws IOException if metadata generation fails
	 */
	public void syncProject(boolean refresh) throws IOException
	{
		ToolchainLog.info("idea", "Discovering repository context");
		var repository = RepositoryContext.load();
		var graph = repository.buildGraph();
		String loaderVersion = repository.loaderVersion();
		var projectName = repository.projectName();

		// Keep the sync phases explicit so future maintainers can line them up with IntelliJ files on disk.
		ToolchainLog.info("idea", "Writing project registration");
		writeProjectRegistration(projectName, graph);
		ToolchainLog.info("idea", "Writing project settings");
		writeProjectSettings();
		ToolchainLog.info("idea", "Writing compiler configuration");
		writeCompilerConfiguration(projectName, graph, refresh);
		ToolchainLog.info("idea", "Writing project libraries");
		writeProjectLibraries(graph, loaderVersion, refresh);
		ToolchainLog.info("idea", "Writing module metadata");
		writeModuleMetadata(projectName, graph, loaderVersion, refresh);
		ToolchainLog.info("idea", "IntelliJ sync complete");
	}

	/**
	 * Writes the active IntelliJ project registration files so the generated modules are actually loaded.
	 *
	 * @param projectName the IntelliJ project name
	 * @param graph       the authoritative build graph
	 *
	 * @throws IOException if the registration files cannot be written
	 */
	private void writeProjectRegistration(
			String projectName,
			BuildGraph graph
	) throws IOException
	{
		TemplateXmlWriter.write(ToolchainPaths.INTELLIJ_META_DIRECTORY.resolve(projectName + ".iml"), createRootModuleDocument());
		TemplateXmlWriter.write(ToolchainPaths.INTELLIJ_META_MODULES_FILE, createModulesDocument(projectName, graph));
	}

	/**
	 * Writes the root IntelliJ compiler configuration file.
	 *
	 * @param projectName the IntelliJ project name
	 * @param graph       the authoritative build graph
	 * @param refresh     whether to refresh external artifact resolution
	 *
	 * @throws IOException if the file cannot be written
	 */
	private void writeCompilerConfiguration(
			String projectName,
			BuildGraph graph,
			boolean refresh
	) throws IOException
	{
		TemplateXmlWriter.write(
				ToolchainPaths.INTELLIJ_META_COMPILER_FILE,
				createCompilerConfigurationDocument(projectName, graph, refresh)
		);
	}

	/**
	 * Writes project-level IntelliJ settings that affect how JPS materializes compiler outputs.
	 *
	 * <p>Without an explicit project output root in `misc.xml`, IntelliJ may fall back to its
	 * compile-server cache even when module `.iml` files declare per-module output paths. The root
	 * output entry keeps root builds and launches anchored in the tracked repo.
	 *
	 * @throws IOException if the project settings cannot be written
	 */
	private void writeProjectSettings() throws IOException
	{
		var document = readExistingProjectDocument();
		var project = document.getRootElement();
		var projectRootManager = findOrCreateComponent(project, "ProjectRootManager");
		var output = projectRootManager.element("output");

		if (output == null)
		{
			output = projectRootManager.addElement("output");
		}

		output.addAttribute("url", "file://$PROJECT_DIR$/out");
		TemplateXmlWriter.write(ToolchainPaths.INTELLIJ_META_MISC_FILE, document);
	}

	/**
	 * Writes project library metadata for external compile and client dependencies.
	 *
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 *
	 * @throws IOException if metadata generation fails
	 */
	private void writeProjectLibraries(
			BuildGraph graph,
			String loaderVersion,
			boolean refresh
	) throws IOException
	{
		Set<Path> resolvedArtifacts = new LinkedHashSet<>(
				_dependencyResolver.resolveProjectLibraries(graph, loaderVersion, refresh)
		);
		resolvedArtifacts.addAll(_dependencyResolver.resolveExternalDependencies(TOOLCHAIN_DEPENDENCIES, refresh));
		ToolchainLog.info("idea", "Resolved " + resolvedArtifacts.size() + " project libraries");

		var librariesDirectory = ToolchainPaths.INTELLIJ_META_LIBRARIES_DIRECTORY;
		Files.createDirectories(librariesDirectory);
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (var artifact : resolvedArtifacts)
		{
			var fileName = sanitizeLibraryFileName(projectLibraryName(artifact)) + ".xml";
			expectedFileNames.add(fileName);
			var sourceArchive = _sourceAttachmentResolver.resolveSourceArchive(artifact, refresh);
			TemplateXmlWriter.write(
					librariesDirectory.resolve(fileName),
					createProjectLibraryDocument(artifact, sourceArchive)
			);
		}

		deleteObsoleteGeneratedFiles(librariesDirectory, expectedFileNames);
		ToolchainLog.info("idea", "Wrote " + resolvedArtifacts.size() + " project library definitions");
	}

	/**
	 * Writes IntelliJ module metadata for modeled source sets.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 *
	 * @throws IOException if metadata generation fails
	 */
	private void writeModuleMetadata(
			String projectName,
			BuildGraph graph,
			String loaderVersion,
			boolean refresh
	) throws IOException
	{
		ToolchainLog.info("idea", "Generating metadata for " + graph.modules().size() + " modeled modules");
		Set<String> expectedModuleFiles = new LinkedHashSet<>();
		ToolchainLog.info("idea", "Writing module metadata for toolchain");
		expectedModuleFiles.add("toolchain/" + IntelliJModuleNames.toolchainModuleFileName(projectName));
		writeToolchainModuleMetadata(projectName, refresh);

		for (var module : graph.modules())
		{
			ToolchainLog.info("idea", "Writing module metadata for " + module.id());
			expectedModuleFiles.add(module.id() + "/" + IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), SourceSetNames.MAIN));
			writeSourceSetModuleMetadata(projectName, graph, loaderVersion, refresh, module, SourceSetNames.MAIN);

			if (hasClientSourceSet(module))
			{
				ToolchainLog.info("idea", "Writing client source set metadata for " + module.id());
				expectedModuleFiles.add(module.id() + "/" + IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), SourceSetNames.CLIENT));
				writeSourceSetModuleMetadata(projectName, graph, loaderVersion, refresh, module, SourceSetNames.CLIENT);
			}
		}

		deleteObsoleteGeneratedProjectModuleFiles(expectedModuleFiles);
		ToolchainLog.info("idea", "Finished module metadata generation");
	}

	/**
	 * Writes a single source-set module `.iml`.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @throws IOException if metadata generation fails
	 */
	private void writeSourceSetModuleMetadata(
			String projectName,
			BuildGraph graph,
			String loaderVersion,
			boolean refresh,
			ModuleSpec module,
			String sourceSetName
	) throws IOException
	{
		var outputPath = ToolchainPaths.INTELLIJ_META_PROJECTS_MODULE_DIRECTORY
				.resolve(module.id())
				.resolve(IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), sourceSetName));
		TemplateXmlWriter.write(
				outputPath,
				createModuleDocument(projectName, graph, loaderVersion, refresh, module, sourceSetName)
		);
	}

	/**
	 * Writes the project-root IntelliJ module metadata for the standalone toolchain sources.
	 *
	 * @param projectName the IntelliJ project name
	 *
	 * @throws IOException if the metadata cannot be written
	 */
	private void writeToolchainModuleMetadata(
			String projectName,
			boolean refresh
	) throws IOException
	{
		var outputPath = ToolchainPaths.INTELLIJ_META_TOOLCHAIN_PROJECT_MODULE_DIRECTORY.resolve(IntelliJModuleNames.toolchainModuleFileName(projectName));
		TemplateXmlWriter.write(outputPath, createToolchainModuleDocument(projectName, refresh));
	}

	/**
	 * Creates the IntelliJ compiler configuration XML document.
	 *
	 * @param projectName the IntelliJ project name
	 * @param graph       the authoritative build graph
	 * @param refresh     whether to refresh external artifact resolution
	 *
	 * @return the compiler configuration document
	 *
	 * @throws IOException if external artifacts cannot be resolved
	 */
	private Document createCompilerConfigurationDocument(
			String projectName,
			BuildGraph graph,
			boolean refresh
	) throws IOException
	{
		var document = DocumentHelper.createDocument();
		var project = document.addElement("project");
		project.addAttribute("version", "4");
		var compilerProjectExtension = project.addElement("component");
		compilerProjectExtension.addAttribute("name", "CompilerProjectExtension");
		compilerProjectExtension.addElement("output")
		                        .addAttribute("url", "file://$PROJECT_DIR$/out");
		var compilerConfiguration = project.addElement("component");
		compilerConfiguration.addAttribute("name", "CompilerConfiguration");
		var annotationProcessing = compilerConfiguration.addElement("annotationProcessing");
		var defaultProfile = annotationProcessing.addElement("profile");
		defaultProfile.addAttribute("default", "true");
		defaultProfile.addAttribute("name", "Default");
		defaultProfile.addAttribute("enabled", "true");
		addDisabledAnnotationProfile(
				annotationProcessing,
				IntelliJModuleNames.toolchainModuleName(projectName)
		);

		for (var module : graph.modules())
		{
			if (!module.annotationProcessorDependencies().isEmpty())
			{
				addAnnotationProfile(
						annotationProcessing,
						projectName,
						graph,
						module,
						SourceSetNames.MAIN,
						generatedRoots(module, SourceSetNames.MAIN),
						_dependencyResolver.resolveExternalDependencies(module.annotationProcessorDependencies(), refresh)
				);
			}

			if (!module.annotationProcessors().isEmpty())
			{
				addAnnotationProfile(
						annotationProcessing,
						projectName,
						graph,
						module,
						SourceSetNames.MAIN,
						generatedRoots(module, SourceSetNames.MAIN),
						_dependencyResolver.resolveAnnotationProcessorModulePath(projectName, graph, module, refresh)
				);

				if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
				{
					addAnnotationProfile(
							annotationProcessing,
							projectName,
							graph,
							module,
							SourceSetNames.CLIENT,
							generatedRoots(module, SourceSetNames.CLIENT),
							_dependencyResolver.resolveAnnotationProcessorModulePath(projectName, graph, module, refresh)
					);
				}
			}
		}

		compilerConfiguration.addElement("bytecodeTargetLevel")
		                     .addAttribute("target", Integer.toString(graph.modules().stream().mapToInt(ModuleSpec::javaVersion).max().orElse(25)));
		var javacSettings = project.addElement("component");
		javacSettings.addAttribute("name", "JavacSettings");
		var additionalOptions = javacSettings.addElement("option");
		additionalOptions.addAttribute("name", "ADDITIONAL_OPTIONS_OVERRIDE");
		addJavacOptions(additionalOptions, projectName, graph);
		return document;
	}

	/**
	 * Adds a single IntelliJ annotation processing profile.
	 *
	 * @param annotationProcessing the annotation processing element
	 * @param projectName          the IntelliJ project name
	 * @param graph                the authoritative build graph
	 * @param module               the module specification
	 * @param sourceSetName        the source-set name
	 * @param generatedRoots       the generated roots for this source set
	 * @param processorPathEntries the resolved processor path entries
	 */
	private void addAnnotationProfile(
			Element annotationProcessing,
			String projectName,
			BuildGraph graph,
			ModuleSpec module,
			String sourceSetName,
			List<Path> generatedRoots,
			List<Path> processorPathEntries
	)
	{
		var profile = annotationProcessing.addElement("profile");
		profile.addAttribute("name", "Toolchain: " + IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName));
		profile.addAttribute("enabled", "true");
		profile.addElement("outputRelativeToContentRoot").addAttribute("value", "true");

		if (!generatedRoots.isEmpty())
		{
			var moduleRoot = ToolchainPaths.PROJECT_ROOT.resolve(module.paths().root()).toAbsolutePath().normalize();
			var generatedRoot = ToolchainPaths.PROJECT_ROOT.resolve(generatedRoots.getFirst()).toAbsolutePath().normalize();
			profile.addElement("sourceOutputDir")
			       .addAttribute("name", moduleRoot.relativize(generatedRoot).toString().replace('\\', '/'));
		}

		var processorPath = profile.addElement("processorPath");
		var useClasspath = !module.annotationProcessors().isEmpty();
		processorPath.addAttribute("useClasspath", Boolean.toString(useClasspath));

		for (var entry : processorPathEntries)
		{
			if (useClasspath)
			{
				break;
			}

			processorPath.addElement("entry")
			             .addAttribute("name", IntelliJPathMacros.projectRelativeMacro(entry));
		}

		for (var processorClassName : annotationProcessorClassNames(graph, module))
		{
			profile.addElement("processor").addAttribute("name", processorClassName);
		}

		profile.addElement("module").addAttribute("name", IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName));
	}

	/**
	 * Collects the explicit annotation processor classes contributed by module-backed processor
	 * dependencies.
	 *
	 * @param graph  the authoritative build graph
	 * @param module the consumer module specification
	 *
	 * @return the ordered processor class names
	 */
	private List<String> annotationProcessorClassNames(BuildGraph graph, ModuleSpec module)
	{
		List<String> processorClassNames = new ArrayList<>();

		for (var processorId : module.annotationProcessors())
		{
			var processorModule = graph.modules()
			                           .stream()
			                           .filter(candidate -> processorId.equals(candidate.id()))
			                           .findFirst()
			                           .orElseThrow(() -> new IllegalArgumentException("Unknown module id: " + processorId));

			for (var className : processorModule.providedAnnotationProcessorClasses())
			{
				if (!processorClassNames.contains(className))
				{
					processorClassNames.add(className);
				}
			}
		}

		return processorClassNames;
	}

	/**
	 * Adds a disabled annotation processing profile for a module that must never discover processors
	 * from its classpath.
	 *
	 * @param annotationProcessing the annotation processing component
	 * @param moduleName           the IntelliJ module name
	 */
	private void addDisabledAnnotationProfile(Element annotationProcessing, String moduleName)
	{
		var profile = annotationProcessing.addElement("profile");
		profile.addAttribute("name", "Toolchain: Disabled AP for " + moduleName);
		profile.addAttribute("enabled", "false");
		profile.addElement("module").addAttribute("name", moduleName);
	}

	/**
	 * Adds IntelliJ Javac option override entries for modeled modules and source sets.
	 *
	 * @param additionalOptions the `ADDITIONAL_OPTIONS_OVERRIDE` option element
	 * @param projectName       the IntelliJ project name
	 * @param graph             the authoritative build graph
	 */
	private void addJavacOptions(Element additionalOptions, String projectName, BuildGraph graph)
	{
		additionalOptions.addElement("module")
		                 .addAttribute("name", projectName)
		                 .addAttribute("options", "-Xmaxerrs 1000 -Xdiags:verbose");
		additionalOptions.addElement("module")
		                 .addAttribute("name", projectName + ".main")
		                 .addAttribute("options", "-Xmaxerrs 1000 -Xdiags:verbose");
		additionalOptions.addElement("module")
		                 .addAttribute("name", IntelliJModuleNames.toolchainModuleName(projectName))
		                 .addAttribute("options", "-Xmaxerrs 1000 -Xdiags:verbose");

		for (var module : graph.modules())
		{
			additionalOptions.addElement("module")
			                 .addAttribute("name", IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), SourceSetNames.MAIN))
			                 .addAttribute("options", "-Xmaxerrs 1000 -Xdiags:verbose");

			if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
			{
				additionalOptions.addElement("module")
				                 .addAttribute("name", IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), SourceSetNames.CLIENT))
				                 .addAttribute("options", "-Xmaxerrs 1000 -Xdiags:verbose");
			}
		}
	}

	/**
	 * Renders the optional Fabric facet block for a modeled module.
	 *
	 * @param module           the module specification
	 * @param minecraftVersion the tracked Minecraft version
	 *
	 * @return the rendered facet block, or an empty string
	 */
	private String renderOptionalFabricFacet(ModuleSpec module, String minecraftVersion)
			throws IOException
	{
		if (module.fabricModJson() == null)
		{
			return "";
		}

		return FileTemplateRenderer.render(
				"com/parzivail/toolchain/templates/intellij-fabric-facet.xml",
				Map.of("MINECRAFT_VERSION", XmlEscaper.escapeAttribute(minecraftVersion))
		);
	}

	/**
	 * Adds Fabric facet components to a module document when the module is Fabric-backed.
	 *
	 * @param moduleElement    the module element
	 * @param module           the module specification
	 * @param minecraftVersion the tracked Minecraft version
	 *
	 * @throws IOException if facet rendering fails
	 */
	private void addFabricFacetComponents(
			Element moduleElement,
			ModuleSpec module,
			String minecraftVersion
	) throws IOException
	{
		var rendered = renderOptionalFabricFacet(module, minecraftVersion);

		if (rendered.isBlank())
		{
			return;
		}

		try
		{
			var fragment = DocumentHelper.parseText("<module>" + rendered + "</module>");
			List<Element> copiedChildren = new ArrayList<>();

			for (Object child : fragment.getRootElement().elements())
			{
				copiedChildren.add(((Element)child).createCopy());
			}

			for (var copiedChild : copiedChildren)
			{
				moduleElement.add(copiedChild);
			}
		}
		catch (DocumentException exception)
		{
			throw new IOException("Failed to render IntelliJ Fabric facet metadata for " + module.id(), exception);
		}
	}

	/**
	 * Gets the generated roots for a selected source set.
	 *
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the generated roots for that source set
	 */
	private List<Path> generatedRoots(ModuleSpec module, String sourceSetName)
	{
		return SourceSetLayout.generatedRoots(module, sourceSetName);
	}

	/**
	 * Creates a project library XML document for a resolved external artifact.
	 *
	 * @param artifact the resolved artifact path
	 *
	 * @return the library document
	 */
	private Document createProjectLibraryDocument(Path artifact, Path sourceArchive)
	{
		var document = DocumentHelper.createDocument();
		var component = document.addElement("component");
		component.addAttribute("name", "libraryTable");
		var library = component.addElement("library");
		library.addAttribute("name", projectLibraryName(artifact));
		var classes = library.addElement("CLASSES");
		classes.addElement("root").addAttribute("url", IntelliJPathMacros.jarUrl(artifact));
		library.addElement("JAVADOC");
		var sources = library.addElement("SOURCES");

		if (sourceArchive != null)
		{
			sources.addElement("root").addAttribute("url", IntelliJPathMacros.jarUrl(sourceArchive));
		}

		return document;
	}

	/**
	 * Creates the root IntelliJ module placeholder document.
	 *
	 * @return the root module document
	 */
	private Document createRootModuleDocument()
	{
		var document = DocumentHelper.createDocument();
		var module = document.addElement("module");
		module.addAttribute("type", "JAVA_MODULE");
		module.addAttribute("version", "4");
		var rootManager = module.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "true");
		rootManager.addElement("exclude-output");
		rootManager.addElement("content").addAttribute("url", "file://$PROJECT_DIR$");
		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");
		return document;
	}

	/**
	 * Creates the IntelliJ `modules.xml` project registration document.
	 *
	 * @param projectName the IntelliJ project name
	 * @param graph       the authoritative build graph
	 *
	 * @return the modules registration document
	 */
	private Document createModulesDocument(String projectName, BuildGraph graph) throws IOException
	{
		var document = DocumentHelper.createDocument();
		var project = document.addElement("project");
		project.addAttribute("version", "4");
		var component = project.addElement("component");
		component.addAttribute("name", "ProjectModuleManager");
		var modules = component.addElement("modules");

		addRegisteredModule(modules, "$PROJECT_DIR$/.idea/" + projectName + ".iml");
		addRegisteredModule(modules, "$PROJECT_DIR$/.idea/modules/projects/toolchain/" + IntelliJModuleNames.toolchainModuleFileName(projectName));

		for (var module : graph.modules())
		{
			addRegisteredModule(
					modules,
					"$PROJECT_DIR$/.idea/modules/projects/" + module.id() + "/" + IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), SourceSetNames.MAIN)
			);

			if (hasClientSourceSet(module))
			{
				addRegisteredModule(
						modules,
						"$PROJECT_DIR$/.idea/modules/projects/" + module.id() + "/" + IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), SourceSetNames.CLIENT)
				);
			}
		}

		for (var preservedModulePath : preservedGeneratedLaunchModules())
		{
			addRegisteredModule(modules, preservedModulePath);
		}

		return document;
	}

	/**
	 * Creates the IntelliJ module document for the standalone toolchain sources inside the root
	 * project.
	 *
	 * @param projectName the IntelliJ project name
	 *
	 * @return the toolchain module document
	 *
	 * @throws IOException if external toolchain dependencies cannot be resolved
	 */
	private Document createToolchainModuleDocument(
			String projectName,
			boolean refresh
	) throws IOException
	{
		var document = DocumentHelper.createDocument();
		var moduleElement = document.addElement("module");
		moduleElement.addAttribute("version", "4");
		addToolchainRootManager(moduleElement, projectName, refresh);
		return document;
	}

	/**
	 * Creates a fully modeled IntelliJ module document for a selected source set.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the module document
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private Document createModuleDocument(
			String projectName,
			BuildGraph graph,
			String loaderVersion,
			boolean refresh,
			ModuleSpec module,
			String sourceSetName
	) throws IOException
	{
		var document = DocumentHelper.createDocument();
		var moduleElement = document.addElement("module");
		moduleElement.addAttribute("version", "4");
		addRootManager(moduleElement, projectName, graph, loaderVersion, refresh, module, sourceSetName);
		addGeneratedSourcesComponent(moduleElement, module, generatedRoots(module, sourceSetName));

		addFabricFacetComponents(moduleElement, module, graph.minecraftVersion());

		return document;
	}

	/**
	 * Adds the IntelliJ root manager and classpath model to a module document.
	 *
	 * @param moduleElement the module element
	 * @param projectName   the IntelliJ project name
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private void addRootManager(
			Element moduleElement,
			String projectName,
			BuildGraph graph,
			String loaderVersion,
			boolean refresh,
			ModuleSpec module,
			String sourceSetName
	) throws IOException
	{
		var moduleRoot = ToolchainPaths.PROJECT_ROOT.resolve(module.paths().root());
		var rootManager = moduleElement.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "false");
		rootManager.addElement("output")
		           .addAttribute("url", IntelliJPathMacros.generatedModuleOutputUrl(IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName)));
		rootManager.addElement("exclude-output");

		var content = rootManager.addElement("content");
		content.addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, moduleRoot));
		addSourceFolders(content, module, sourceSetName);
		addExcludedFolder(content, moduleRoot.resolve("build"), moduleRoot);

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");
		addModuleDependencyEntries(rootManager, projectName, graph, module, sourceSetName);
		addLibraryDependencyEntries(rootManager, graph, loaderVersion, refresh, module, sourceSetName);
	}

	/**
	 * Adds the root manager and classpath model for the toolchain module.
	 *
	 * @param moduleElement the module element
	 * @param projectName   the IntelliJ project name
	 *
	 * @throws IOException if external toolchain dependencies cannot be resolved
	 */
	private void addToolchainRootManager(
			Element moduleElement,
			String projectName,
			boolean refresh
	) throws IOException
	{
		var toolchainRoot = ToolchainPaths.TOOLCHAIN_ROOT;
		var rootManager = moduleElement.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "false");
		rootManager.addElement("output")
		           .addAttribute("url", IntelliJPathMacros.generatedModuleOutputUrl(IntelliJModuleNames.toolchainModuleName(projectName)));
		rootManager.addElement("exclude-output");

		var content = rootManager.addElement("content");
		content.addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, toolchainRoot));
		content.addElement("sourceFolder")
		       .addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, ToolchainPaths.TOOLCHAIN_MAIN_SOURCES))
		       .addAttribute("isTestSource", "false");
		content.addElement("sourceFolder")
		       .addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, ToolchainPaths.TOOLCHAIN_MAIN_RESOURCES))
		       .addAttribute("type", "java-resource")
		       .addAttribute("isTestSource", "false");
		content.addElement("excludeFolder")
		       .addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, toolchainRoot.resolve("build")));

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");

		for (var dependency : _dependencyResolver.resolveExternalDependencies(TOOLCHAIN_DEPENDENCIES, refresh))
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", projectLibraryName(dependency))
			           .addAttribute("level", "project");
		}
	}

	/**
	 * Preserves previously generated launch modules that live outside the static source-set graph.
	 *
	 * <p>`idea sync-project` owns the bulk of `modules.xml`, but version/platform-specific launch modules
	 * are generated later by Fabric launch preparation. Preserving those entries keeps IntelliJ from
	 * dropping the launch module registration every time the project metadata is resynced.
	 *
	 * @return the generated launch module file paths already present on disk
	 *
	 * @throws IOException if the launch module directory cannot be scanned
	 */
	private List<String> preservedGeneratedLaunchModules() throws IOException
	{
		if (!Files.isDirectory(ToolchainPaths.INTELLIJ_LAUNCH_MODULE_DIRECTORY))
		{
			return List.of();
		}

		List<String> modulePaths = new ArrayList<>();

		try (var paths = Files.walk(ToolchainPaths.INTELLIJ_LAUNCH_MODULE_DIRECTORY))
		{
			paths.filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".iml"))
			     .sorted()
			     .forEach(path -> modulePaths.add(
					     "$PROJECT_DIR$/.idea/modules/launch/" + ToolchainPaths.INTELLIJ_LAUNCH_MODULE_DIRECTORY.relativize(path).toString().replace('\\', '/')
			     ));
		}

		return modulePaths;
	}

	/**
	 * Adds IntelliJ source and resource folder declarations for a source set.
	 *
	 * @param content       the module content element
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 */
	private void addSourceFolders(Element content, ModuleSpec module, String sourceSetName)
	{
		var moduleRoot = ToolchainPaths.PROJECT_ROOT.resolve(module.paths().root());

		for (var sourceRoot : sourceRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, ToolchainPaths.PROJECT_ROOT.resolve(sourceRoot)))
			       .addAttribute("isTestSource", "false");
		}

		for (var resourceRoot : resourceRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, ToolchainPaths.PROJECT_ROOT.resolve(resourceRoot)))
			       .addAttribute("type", "java-resource")
			       .addAttribute("isTestSource", "false");
		}

		for (var generatedRoot : generatedRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, ToolchainPaths.PROJECT_ROOT.resolve(generatedRoot)))
			       .addAttribute("isTestSource", "false")
			       .addAttribute("generated", "true");
		}
	}

	/**
	 * Adds an excluded folder entry to the module content root.
	 *
	 * @param content    the module content element
	 * @param path       the excluded path
	 * @param moduleRoot the module root used to derive a stable module-relative URL
	 */
	private void addExcludedFolder(Element content, Path path, Path moduleRoot)
	{
		content.addElement("excludeFolder").addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, path));
	}

	/**
	 * Adds module dependency order entries for a modeled source set.
	 *
	 * @param rootManager   the root manager element
	 * @param projectName   the IntelliJ project name
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 */
	private void addModuleDependencyEntries(
			Element rootManager,
			String projectName,
			BuildGraph graph,
			ModuleSpec module,
			String sourceSetName
	)
	{
		Set<String> dependencyModuleNames = new LinkedHashSet<>();

		for (var dependencyId : module.dependencies())
		{
			dependencyModuleNames.add(
					IntelliJModuleNames.sourceSetModuleName(
							projectName,
							dependencyId,
							SourceSetDependencyResolver.dependencySourceSetName(graph, dependencyId, sourceSetName)
					)
			);
		}

		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			dependencyModuleNames.add(IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), SourceSetNames.MAIN));
		}

		for (var dependencyModuleName : dependencyModuleNames)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "module")
			           .addAttribute("module-name", dependencyModuleName)
			           .addAttribute("exported", "");
		}

		for (var processorId : module.annotationProcessors())
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "module")
			           .addAttribute("module-name", IntelliJModuleNames.sourceSetModuleName(projectName, processorId, SourceSetNames.MAIN))
			           .addAttribute("scope", "PROVIDED");
		}
	}

	/**
	 * Adds project library dependency entries for a modeled source set.
	 *
	 * @param rootManager   the root manager element
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private void addLibraryDependencyEntries(
			Element rootManager,
			BuildGraph graph,
			String loaderVersion,
			boolean refresh,
			ModuleSpec module,
			String sourceSetName
	) throws IOException
	{
		var dependencies = _dependencyResolver.resolveModuleLibraries(
				graph,
				loaderVersion,
				refresh,
				module,
				SourceSetNames.CLIENT.equals(sourceSetName)
		);

		for (var dependency : dependencies)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", projectLibraryName(dependency))
			           .addAttribute("level", "project");
		}

		for (var dependency : resolveAnnotationProcessorSupportLibraries(graph, refresh, module))
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", projectLibraryName(dependency))
			           .addAttribute("level", "project")
			           .addAttribute("scope", "PROVIDED");
		}
	}

	/**
	 * Resolves the external support libraries needed by module-backed annotation processors.
	 *
	 * @param graph   the authoritative build graph
	 * @param refresh whether to refresh external artifact resolution
	 * @param module  the consumer module
	 *
	 * @return the ordered external support libraries
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private List<Path> resolveAnnotationProcessorSupportLibraries(
			BuildGraph graph,
			boolean refresh,
			ModuleSpec module
	) throws IOException
	{
		if (module.annotationProcessors().isEmpty())
		{
			return List.of();
		}

		List<Path> libraries = new ArrayList<>();

		for (var processorId : module.annotationProcessors())
		{
			var processorModule = graph.modules()
			                           .stream()
			                           .filter(candidate -> processorId.equals(candidate.id()))
			                           .findFirst()
			                           .orElseThrow(() -> new IllegalArgumentException("Unknown module id: " + processorId));

			addDistinctPaths(libraries, _dependencyResolver.resolveExternalDependencies(processorModule.compileDependencies(), refresh));
			addDistinctPaths(libraries, _dependencyResolver.resolveExternalDependencies(processorModule.annotationProcessorDependencies(), refresh));
		}

		return libraries;
	}

	/**
	 * Adds the generated-sources component used by IntelliJ to mark AP outputs.
	 *
	 * @param moduleElement  the module element
	 * @param module         the module specification
	 * @param generatedRoots the generated roots for the source set
	 */
	private void addGeneratedSourcesComponent(Element moduleElement, ModuleSpec module, List<Path> generatedRoots)
	{
		if (generatedRoots.isEmpty())
		{
			return;
		}

		var moduleRoot = ToolchainPaths.PROJECT_ROOT.resolve(module.paths().root());
		var additional = moduleElement.addElement("component");
		additional.addAttribute("name", "AdditionalModuleElements");

		for (var generatedRoot : generatedRoots)
		{
			// IntelliJ does not reliably preserve generated-root markers when they only exist under the
			// main content root, so we mirror them into AdditionalModuleElements the same way Gradle/JPS does.
			var content = additional.addElement("content");
			content.addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, ToolchainPaths.PROJECT_ROOT.resolve(generatedRoot)));
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, ToolchainPaths.PROJECT_ROOT.resolve(generatedRoot)))
			       .addAttribute("isTestSource", "false")
			       .addAttribute("generated", "true");
		}
	}

	/**
	 * Adds paths to an output list while preserving order and avoiding duplicates.
	 *
	 * @param output the accumulated output list
	 * @param values the candidate paths
	 */
	private void addDistinctPaths(List<Path> output, List<Path> values)
	{
		for (var value : values)
		{
			if (!output.contains(value))
			{
				output.add(value);
			}
		}
	}

	/**
	 * Gets the source roots for a selected source set.
	 *
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the source roots
	 */
	private List<Path> sourceRoots(ModuleSpec module, String sourceSetName)
	{
		return SourceSetLayout.sourceRoots(module, sourceSetName);
	}

	/**
	 * Gets the resource roots for a selected source set.
	 *
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the resource roots
	 */
	private List<Path> resourceRoots(ModuleSpec module, String sourceSetName)
	{
		return SourceSetLayout.resourceRoots(module, sourceSetName);
	}

	/**
	 * Checks whether a module has a client source set worth modeling.
	 *
	 * @param module the module specification
	 *
	 * @return {@code true} if the client source set exists
	 */
	private boolean hasClientSourceSet(ModuleSpec module)
	{
		return SourceSetLayout.hasClientSourceSet(module);
	}

	/**
	 * Builds a stable project-library name for a resolved artifact.
	 *
	 * @param artifact the resolved artifact path
	 *
	 * @return the library name
	 */
	private String projectLibraryName(Path artifact)
	{
		var fileName = artifact.getFileName().toString();
		return fileName.endsWith(".jar") ? fileName.substring(0, fileName.length() - 4) : fileName;
	}

	/**
	 * Sanitizes a project-library name for use as a metadata file name.
	 *
	 * @param name the raw library name
	 *
	 * @return the sanitized file name
	 */
	private String sanitizeLibraryFileName(String name)
	{
		return name.replace(':', '_').replace('/', '_').replace('\\', '_').replace(' ', '_');
	}

	/**
	 * Adds a registered module entry to the IntelliJ `modules.xml` document.
	 *
	 * @param modules  the `modules` element
	 * @param filePath the IntelliJ macro file path
	 */
	private void addRegisteredModule(Element modules, String filePath)
	{
		modules.addElement("module")
		       .addAttribute("fileurl", "file://" + filePath)
		       .addAttribute("filepath", filePath);
	}

	/**
	 * Reads an existing IntelliJ project document when present, or creates a new empty project
	 * document otherwise.
	 *
	 * @return the parsed or synthesized project document
	 *
	 * @throws IOException if the existing document cannot be parsed
	 */
	private Document readExistingProjectDocument() throws IOException
	{
		if (!Files.exists(ToolchainPaths.INTELLIJ_META_MISC_FILE))
		{
			var document = DocumentHelper.createDocument();
			document.addElement("project").addAttribute("version", "4");
			return document;
		}

		try
		{
			return DocumentHelper.parseText(Files.readString(ToolchainPaths.INTELLIJ_META_MISC_FILE));
		}
		catch (DocumentException exception)
		{
			throw new IOException("Failed to parse IntelliJ project document: " + ToolchainPaths.INTELLIJ_META_MISC_FILE, exception);
		}
	}

	/**
	 * Finds an IntelliJ `<component>` by name, creating it when missing.
	 *
	 * @param project the root `project` element
	 * @param name    the component name
	 *
	 * @return the existing or newly created component
	 */
	private Element findOrCreateComponent(Element project, String name)
	{
		for (Object child : project.elements("component"))
		{
			var component = (Element)child;

			if (name.equals(component.attributeValue("name")))
			{
				return component;
			}
		}

		var component = project.addElement("component");
		component.addAttribute("name", name);
		return component;
	}

	/**
	 * Deletes obsolete generated IntelliJ project-library metadata files.
	 *
	 * <p>The toolchain owns the contents of `.idea/libraries`, so stale files from replaced transformed
	 * jars or dependency graph changes should be removed as part of each sync.
	 *
	 * @param directory         the IntelliJ libraries directory
	 * @param expectedFileNames the generated library metadata files expected after this sync
	 *
	 * @throws IOException if stale files cannot be removed
	 */
	private void deleteObsoleteGeneratedFiles(
			Path directory,
			Set<String> expectedFileNames
	) throws IOException
	{
		if (!Files.isDirectory(directory))
		{
			return;
		}

		try (var entries = Files.list(directory))
		{
			for (var entry : entries.toList())
			{
				if (!Files.isRegularFile(entry))
				{
					continue;
				}

				var fileName = entry.getFileName().toString();

				if (!fileName.endsWith(".xml")
				    || expectedFileNames.contains(fileName)
				    || fileName.startsWith("fabric-launch-"))
				{
					continue;
				}

				Files.deleteIfExists(entry);
			}
		}
	}

	/**
	 * Deletes obsolete generated source-set module metadata from `.idea/modules/projects`.
	 *
	 * @param expectedModuleFiles the module-relative `.iml` files expected after this sync
	 *
	 * @throws IOException if stale files cannot be removed
	 */
	private void deleteObsoleteGeneratedProjectModuleFiles(Set<String> expectedModuleFiles) throws IOException
	{
		if (!Files.isDirectory(ToolchainPaths.INTELLIJ_META_PROJECTS_MODULE_DIRECTORY))
		{
			return;
		}

		try (var entries = Files.walk(ToolchainPaths.INTELLIJ_META_PROJECTS_MODULE_DIRECTORY))
		{
			for (var entry : entries.filter(Files::isRegularFile).toList())
			{
				var relativePath = ToolchainPaths.INTELLIJ_META_PROJECTS_MODULE_DIRECTORY.relativize(entry);
				var normalizedPath = relativePath.toString().replace('\\', '/');

				if (!normalizedPath.endsWith(".iml") || expectedModuleFiles.contains(normalizedPath))
				{
					continue;
				}

				Files.deleteIfExists(entry);
			}
		}
	}
}
