package dev.pswg.toolchain.intellij;

import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.MavenDependencySpec;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.model.SourceSetNames;
import dev.pswg.toolchain.pswg.PswgRepositoryContext;
import dev.pswg.toolchain.pswg.definition.PswgBuildDefinition;
import dev.pswg.toolchain.template.FileTemplateRenderer;
import dev.pswg.toolchain.template.XmlEscaper;
import dev.pswg.toolchain.util.ToolchainLog;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Generates the IntelliJ project metadata that lets PSWG compile from the authoritative toolchain
 * graph instead of from IDE state imported out of Gradle.
 *
 * <p>This service deliberately owns the "shape" of the PSWG IntelliJ project: module registration,
 * compiler configuration, generated-source markers, and project-library wiring. The lower-level
 * details of what jars belong on those classpaths live in {@link IntelliJDependencyResolver}.
 */
public final class IntelliJProjectSyncService
{
	/**
	 * The root-relative toolchain source directory.
	 */
	private static final Path TOOLCHAIN_MAIN_SOURCES = Path.of("toolchain", "src", "main", "java");

	/**
	 * The root-relative toolchain resource directory.
	 */
	private static final Path TOOLCHAIN_MAIN_RESOURCES = Path.of("toolchain", "src", "main", "resources");

	/**
	 * The external libraries needed to compile and run the standalone toolchain module from the PSWG
	 * root IntelliJ project.
	 */
	private static final List<MavenDependencySpec> TOOLCHAIN_DEPENDENCIES = List.of(
		new MavenDependencySpec("com.fasterxml.jackson.core:jackson-databind:2.21.1", URI.create("https://repo1.maven.org/maven2")),
		new MavenDependencySpec("com.fasterxml.jackson.core:jackson-annotations:2.21", URI.create("https://repo1.maven.org/maven2")),
		new MavenDependencySpec("com.fasterxml.jackson.core:jackson-core:2.21.1", URI.create("https://repo1.maven.org/maven2")),
		new MavenDependencySpec("org.dom4j:dom4j:2.2.0", URI.create("https://repo1.maven.org/maven2")),
		new MavenDependencySpec("net.fabricmc:class-tweaker:0.1.1", URI.create("https://maven.fabricmc.net/")),
		new MavenDependencySpec("net.fabricmc:tiny-remapper:0.11.2", URI.create("https://maven.fabricmc.net/")),
		new MavenDependencySpec("org.ow2.asm:asm:9.9", URI.create("https://repo1.maven.org/maven2")),
		new MavenDependencySpec("org.ow2.asm:asm-commons:9.8", URI.create("https://repo1.maven.org/maven2")),
		new MavenDependencySpec("org.ow2.asm:asm-tree:9.8", URI.create("https://repo1.maven.org/maven2"))
	);

	/**
	 * Resolves IntelliJ-facing classpath and processor-path artifacts from the authoritative graph.
	 */
	private final IntelliJDependencyResolver _dependencyResolver;

	/**
	 * Creates a new IntelliJ metadata sync service.
	 */
	public IntelliJProjectSyncService()
	{
		_dependencyResolver = new IntelliJDependencyResolver();
	}

	/**
	 * Synchronizes compiler and generated-source metadata into the PSWG IntelliJ project.
	 *
	 * @param refresh whether to refresh externally resolved Maven artifacts
	 * @throws IOException if metadata generation fails
	 */
	public void syncPswgProject(boolean refresh) throws IOException
	{
		ToolchainLog.info("idea", "Discovering PSWG repository context");
		PswgRepositoryContext repository = PswgRepositoryContext.discoverFromToolchainWorkingDirectory();
		Path projectRoot = repository.projectRoot();
		BuildGraph graph = new PswgBuildDefinition().define();
		Properties gradleProperties = repository.gradleProperties();
		String projectName = repository.projectName();

		ToolchainLog.info("idea", "Writing project registration");
		writeProjectRegistration(projectRoot, projectName, graph);
		ToolchainLog.info("idea", "Writing project settings");
		writeProjectSettings(projectRoot);
		ToolchainLog.info("idea", "Writing compiler configuration");
		writeCompilerConfiguration(projectRoot, projectName, graph, gradleProperties, refresh);
		ToolchainLog.info("idea", "Writing project libraries");
		writeProjectLibraries(projectRoot, graph, gradleProperties, refresh);
		ToolchainLog.info("idea", "Writing module metadata");
		writeModuleMetadata(projectRoot, projectName, graph, gradleProperties, refresh);
		ToolchainLog.info("idea", "IntelliJ sync complete");
	}

	/**
	 * Writes the active IntelliJ project registration files so the generated modules are actually loaded.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @throws IOException if the registration files cannot be written
	 */
	private void writeProjectRegistration(
		Path projectRoot,
		String projectName,
		BuildGraph graph
	) throws IOException
	{
		Path projectMetadataRoot = projectRoot.resolve(".idea");
		IntelliJXmlWriter.write(projectMetadataRoot.resolve(projectName + ".iml"), createRootModuleDocument());
		IntelliJXmlWriter.write(projectMetadataRoot.resolve("modules.xml"), createModulesDocument(projectName, graph));
	}

	/**
	 * Writes the root IntelliJ compiler configuration file.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @throws IOException if the file cannot be written
	 */
	private void writeCompilerConfiguration(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		Path outputPath = projectRoot.resolve(".idea").resolve("compiler.xml");
		IntelliJXmlWriter.write(
			outputPath,
			createCompilerConfigurationDocument(projectRoot, projectName, graph, gradleProperties, refresh)
		);
	}

	/**
	 * Writes project-level IntelliJ settings that affect how JPS materializes compiler outputs.
	 *
	 * <p>Without an explicit project output root in `misc.xml`, IntelliJ may fall back to its
	 * compile-server cache even when module `.iml` files declare per-module output paths. The root
	 * output entry keeps PSWG-root builds and launches anchored in the tracked repo.
	 *
	 * @param projectRoot the PSWG project root
	 * @throws IOException if the project settings cannot be written
	 */
	private void writeProjectSettings(Path projectRoot) throws IOException
	{
		Path miscPath = projectRoot.resolve(".idea").resolve("misc.xml");
		Document document = readExistingProjectDocument(miscPath);
		Element project = document.getRootElement();
		Element projectRootManager = findOrCreateComponent(project, "ProjectRootManager");
		Element output = projectRootManager.element("output");

		if (output == null)
		{
			output = projectRootManager.addElement("output");
		}

		output.addAttribute("url", "file://$PROJECT_DIR$/out");
		IntelliJXmlWriter.write(miscPath, document);
	}

	/**
	 * Writes project library metadata for external compile and client dependencies.
	 *
	 * @param projectRoot the PSWG project root
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @throws IOException if metadata generation fails
	 */
	private void writeProjectLibraries(
		Path projectRoot,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		Set<Path> resolvedArtifacts = new LinkedHashSet<>(
			_dependencyResolver.resolveProjectLibraries(graph, projectRoot, gradleProperties, refresh)
		);
		resolvedArtifacts.addAll(_dependencyResolver.resolveExternalDependencies(TOOLCHAIN_DEPENDENCIES, gradleProperties, refresh));
		ToolchainLog.info("idea", "Resolved " + resolvedArtifacts.size() + " project libraries");

		Path librariesDirectory = projectRoot.resolve(".idea").resolve("libraries");
		Files.createDirectories(librariesDirectory);
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (Path artifact : resolvedArtifacts)
		{
			String fileName = sanitizeLibraryFileName(projectLibraryName(artifact)) + ".xml";
			expectedFileNames.add(fileName);
			IntelliJXmlWriter.write(
				librariesDirectory.resolve(fileName),
				createProjectLibraryDocument(projectRoot, artifact)
			);
		}

		deleteObsoleteGeneratedFiles(librariesDirectory, expectedFileNames);
		ToolchainLog.info("idea", "Wrote " + resolvedArtifacts.size() + " project library definitions");
	}

	/**
	 * Writes IntelliJ module metadata for modeled PSWG source sets.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @throws IOException if metadata generation fails
	 */
	private void writeModuleMetadata(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		ToolchainLog.info("idea", "Generating metadata for " + graph.modules().size() + " modeled modules");
		Path modulesDirectory = projectRoot.resolve(".idea").resolve("modules").resolve("projects");
		Set<String> expectedModuleFiles = new LinkedHashSet<>();
		ToolchainLog.info("idea", "Writing module metadata for toolchain");
		expectedModuleFiles.add("toolchain/" + IntelliJModuleNames.toolchainModuleFileName(projectName));
		writeToolchainModuleMetadata(projectRoot, projectName);

		for (ModuleSpec module : graph.modules())
		{
			ToolchainLog.info("idea", "Writing module metadata for " + module.id());
			expectedModuleFiles.add(module.id() + "/" + IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), SourceSetNames.MAIN));
			writeSourceSetModuleMetadata(projectRoot, projectName, graph, gradleProperties, refresh, module, SourceSetNames.MAIN);

			if (hasClientSourceSet(module))
			{
				ToolchainLog.info("idea", "Writing client source set metadata for " + module.id());
				expectedModuleFiles.add(module.id() + "/" + IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), SourceSetNames.CLIENT));
				writeSourceSetModuleMetadata(projectRoot, projectName, graph, gradleProperties, refresh, module, SourceSetNames.CLIENT);
			}
		}

		deleteObsoleteGeneratedProjectModuleFiles(modulesDirectory, expectedModuleFiles);
		ToolchainLog.info("idea", "Finished module metadata generation");
	}

	/**
	 * Writes a single source-set module `.iml`.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @throws IOException if metadata generation fails
	 */
	private void writeSourceSetModuleMetadata(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		Path outputPath = projectRoot.resolve(".idea")
		                             .resolve("modules")
		                             .resolve("projects")
		                             .resolve(module.id())
		                             .resolve(IntelliJModuleNames.sourceSetModuleFileName(projectName, module.id(), sourceSetName));
		IntelliJXmlWriter.write(
			outputPath,
			createModuleDocument(projectRoot, projectName, graph, gradleProperties, refresh, module, sourceSetName)
		);
	}

	/**
	 * Writes the PSWG-root IntelliJ module metadata for the standalone toolchain sources.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @throws IOException if the metadata cannot be written
	 */
	private void writeToolchainModuleMetadata(
		Path projectRoot,
		String projectName
	) throws IOException
	{
		Path outputPath = projectRoot.resolve(".idea")
		                             .resolve("modules")
		                             .resolve("projects")
		                             .resolve("toolchain")
		                             .resolve(IntelliJModuleNames.toolchainModuleFileName(projectName));
		IntelliJXmlWriter.write(outputPath, createToolchainModuleDocument(projectRoot, projectName));
	}

	/**
	 * Creates the IntelliJ compiler configuration XML document.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @return the compiler configuration document
	 * @throws IOException if external artifacts cannot be resolved
	 */
	private Document createCompilerConfigurationDocument(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		Document document = DocumentHelper.createDocument();
		Element project = document.addElement("project");
		project.addAttribute("version", "4");
		Element compilerConfiguration = project.addElement("component");
		compilerConfiguration.addAttribute("name", "CompilerConfiguration");
		Element annotationProcessing = compilerConfiguration.addElement("annotationProcessing");
		Element defaultProfile = annotationProcessing.addElement("profile");
		defaultProfile.addAttribute("default", "true");
		defaultProfile.addAttribute("name", "Default");
		defaultProfile.addAttribute("enabled", "true");

		for (ModuleSpec module : graph.modules())
		{
			if (!module.annotationProcessorDependencies().isEmpty())
			{
				addAnnotationProfile(
					annotationProcessing,
					projectRoot,
					projectName,
					module,
					SourceSetNames.MAIN,
					_dependencyResolver.resolveExternalDependencies(module.annotationProcessorDependencies(), gradleProperties, refresh)
				);
			}

			if (!module.annotationProcessors().isEmpty())
			{
				addAnnotationProfile(
					annotationProcessing,
					projectRoot,
					projectName,
					module,
					SourceSetNames.MAIN,
					_dependencyResolver.resolveAnnotationProcessorModulePath(projectRoot, projectName, graph, module, gradleProperties, refresh)
				);

				if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
				{
					addAnnotationProfile(
						annotationProcessing,
						projectRoot,
						projectName,
						module,
						SourceSetNames.CLIENT,
						_dependencyResolver.resolveAnnotationProcessorModulePath(projectRoot, projectName, graph, module, gradleProperties, refresh)
					);
				}
			}
		}

		compilerConfiguration.addElement("bytecodeTargetLevel")
		                     .addAttribute("target", Integer.toString(graph.modules().stream().mapToInt(ModuleSpec::javaVersion).max().orElse(25)));
		Element javacSettings = project.addElement("component");
		javacSettings.addAttribute("name", "JavacSettings");
		Element additionalOptions = javacSettings.addElement("option");
		additionalOptions.addAttribute("name", "ADDITIONAL_OPTIONS_OVERRIDE");
		addJavacOptions(additionalOptions, projectName, graph);
		return document;
	}

	/**
	 * Adds a single IntelliJ annotation processing profile.
	 *
	 * @param annotationProcessing the annotation processing element
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @param processorPathEntries the resolved processor path entries
	 */
	private void addAnnotationProfile(
		Element annotationProcessing,
		Path projectRoot,
		String projectName,
		ModuleSpec module,
		String sourceSetName,
		List<Path> processorPathEntries
	)
	{
		Element profile = annotationProcessing.addElement("profile");
		profile.addAttribute("name", "PSWG Toolchain: " + IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName));
		profile.addAttribute("enabled", "true");
		profile.addElement("outputRelativeToContentRoot").addAttribute("value", "true");
		Element processorPath = profile.addElement("processorPath");
		processorPath.addAttribute("useClasspath", "false");

		for (Path entry : processorPathEntries)
		{
			processorPath.addElement("entry")
			             .addAttribute("name", IntelliJPathMacros.projectRelativeMacro(projectRoot, entry));
		}

		profile.addElement("module").addAttribute("name", IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName));
	}

	/**
	 * Adds IntelliJ Javac option override entries for modeled modules and source sets.
	 *
	 * @param additionalOptions the `ADDITIONAL_OPTIONS_OVERRIDE` option element
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
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

		for (ModuleSpec module : graph.modules())
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
	 * @param module the module specification
	 * @param minecraftVersion the tracked Minecraft version
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
			"dev/pswg/toolchain/templates/intellij-fabric-facet.xml",
			Map.of("MINECRAFT_VERSION", XmlEscaper.escapeAttribute(minecraftVersion))
		);
	}

	/**
	 * Adds Fabric facet components to a module document when the module is Fabric-backed.
	 *
	 * @param moduleElement the module element
	 * @param module the module specification
	 * @param minecraftVersion the tracked Minecraft version
	 * @throws IOException if facet rendering fails
	 */
	private void addFabricFacetComponents(
		Element moduleElement,
		ModuleSpec module,
		String minecraftVersion
	) throws IOException
	{
		String rendered = renderOptionalFabricFacet(module, minecraftVersion);

		if (rendered.isBlank())
		{
			return;
		}

		try
		{
			Document fragment = DocumentHelper.parseText("<module>" + rendered + "</module>");
			List<Element> copiedChildren = new ArrayList<>();

			for (Object child : fragment.getRootElement().elements())
			{
				copiedChildren.add(((Element) child).createCopy());
			}

			for (Element copiedChild : copiedChildren)
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
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @return the generated roots for that source set
	 */
	private List<Path> generatedRoots(ModuleSpec module, String sourceSetName)
	{
		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			return module.generatedClientSources();
		}

		return module.generatedSources();
	}

	/**
	 * Creates a project library XML document for a resolved external artifact.
	 *
	 * @param projectRoot the PSWG project root
	 * @param artifact the resolved artifact path
	 * @return the library document
	 */
	private Document createProjectLibraryDocument(Path projectRoot, Path artifact)
	{
		Document document = DocumentHelper.createDocument();
		Element component = document.addElement("component");
		component.addAttribute("name", "libraryTable");
		Element library = component.addElement("library");
		library.addAttribute("name", projectLibraryName(artifact));
		Element classes = library.addElement("CLASSES");
		classes.addElement("root").addAttribute("url", IntelliJPathMacros.jarUrl(projectRoot, artifact));
		library.addElement("JAVADOC");
		library.addElement("SOURCES");
		return document;
	}

	/**
	 * Creates the root IntelliJ module placeholder document.
	 *
	 * @return the root module document
	 */
	private Document createRootModuleDocument()
	{
		Document document = DocumentHelper.createDocument();
		Element module = document.addElement("module");
		module.addAttribute("type", "JAVA_MODULE");
		module.addAttribute("version", "4");
		Element rootManager = module.addElement("component");
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
	 * @param graph the authoritative build graph
	 * @return the modules registration document
	 */
	private Document createModulesDocument(String projectName, BuildGraph graph)
	{
		Document document = DocumentHelper.createDocument();
		Element project = document.addElement("project");
		project.addAttribute("version", "4");
		Element component = project.addElement("component");
		component.addAttribute("name", "ProjectModuleManager");
		Element modules = component.addElement("modules");

		addRegisteredModule(modules, "$PROJECT_DIR$/.idea/" + projectName + ".iml");
		addRegisteredModule(modules, "$PROJECT_DIR$/.idea/modules/projects/toolchain/" + IntelliJModuleNames.toolchainModuleFileName(projectName));

		for (ModuleSpec module : graph.modules())
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

		return document;
	}

	/**
	 * Creates the IntelliJ module document for the standalone toolchain sources inside the PSWG root
	 * project.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @return the toolchain module document
	 * @throws IOException if external toolchain dependencies cannot be resolved
	 */
	private Document createToolchainModuleDocument(
		Path projectRoot,
		String projectName
	) throws IOException
	{
		Document document = DocumentHelper.createDocument();
		Element moduleElement = document.addElement("module");
		moduleElement.addAttribute("version", "4");
		addToolchainRootManager(moduleElement, projectRoot, projectName);
		return document;
	}

	/**
	 * Creates a fully modeled IntelliJ module document for a selected source set.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @return the module document
	 * @throws IOException if dependency resolution fails
	 */
	private Document createModuleDocument(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		Document document = DocumentHelper.createDocument();
		Element moduleElement = document.addElement("module");
		moduleElement.addAttribute("version", "4");
		addRootManager(moduleElement, projectRoot, projectName, graph, gradleProperties, refresh, module, sourceSetName);
		addGeneratedSourcesComponent(moduleElement, projectRoot, module, generatedRoots(module, sourceSetName));

		addFabricFacetComponents(moduleElement, module, graph.minecraftVersion());

		return document;
	}

	/**
	 * Adds the IntelliJ root manager and classpath model to a module document.
	 *
	 * @param moduleElement the module element
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @throws IOException if dependency resolution fails
	 */
	private void addRootManager(
		Element moduleElement,
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		Path moduleRoot = projectRoot.resolve(module.paths().root());
		Element rootManager = moduleElement.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "false");
		rootManager.addElement("output").addAttribute("url", IntelliJPathMacros.fileUrl(projectRoot, compileOutputDirectory(projectRoot, projectName, module, sourceSetName)));
		rootManager.addElement("exclude-output");

		Element content = rootManager.addElement("content");
		content.addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, moduleRoot));
		addSourceFolders(projectRoot, content, module, sourceSetName);
		addExcludedFolder(content, moduleRoot.resolve("build"), moduleRoot);

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");
		addModuleDependencyEntries(rootManager, projectName, module, sourceSetName);
		addLibraryDependencyEntries(rootManager, graph, projectRoot, gradleProperties, refresh, module, sourceSetName);
	}

	/**
	 * Adds the root manager and classpath model for the toolchain module.
	 *
	 * @param moduleElement the module element
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @throws IOException if external toolchain dependencies cannot be resolved
	 */
	private void addToolchainRootManager(
		Element moduleElement,
		Path projectRoot,
		String projectName
	) throws IOException
	{
		Path toolchainRoot = projectRoot.resolve("toolchain");
		Element rootManager = moduleElement.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "false");
		rootManager.addElement("output")
		           .addAttribute(
			           "url",
			           IntelliJPathMacros.fileUrl(
				           projectRoot,
				           projectRoot.resolve("out").resolve("production").resolve(IntelliJModuleNames.toolchainModuleName(projectName))
			           )
		           );
		rootManager.addElement("exclude-output");

		Element content = rootManager.addElement("content");
		content.addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, toolchainRoot));
		content.addElement("sourceFolder")
		       .addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, projectRoot.resolve(TOOLCHAIN_MAIN_SOURCES)))
		       .addAttribute("isTestSource", "false");
		content.addElement("sourceFolder")
		       .addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, projectRoot.resolve(TOOLCHAIN_MAIN_RESOURCES)))
		       .addAttribute("type", "java-resource")
		       .addAttribute("isTestSource", "false");
		content.addElement("excludeFolder")
		       .addAttribute("url", IntelliJPathMacros.toolchainModuleFileUrl(toolchainRoot, toolchainRoot.resolve("build")));

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");

		for (Path dependency : _dependencyResolver.resolveExternalDependencies(TOOLCHAIN_DEPENDENCIES, new Properties(), false))
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", projectLibraryName(dependency))
			           .addAttribute("level", "project");
		}
	}

	/**
	 * Adds IntelliJ source and resource folder declarations for a source set.
	 *
	 * @param projectRoot the PSWG project root
	 * @param content the module content element
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 */
	private void addSourceFolders(Path projectRoot, Element content, ModuleSpec module, String sourceSetName)
	{
		Path moduleRoot = projectRoot.resolve(module.paths().root());

		for (Path sourceRoot : sourceRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, projectRoot.resolve(sourceRoot)))
			       .addAttribute("isTestSource", "false");
		}

		for (Path resourceRoot : resourceRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, projectRoot.resolve(resourceRoot)))
			       .addAttribute("type", "java-resource")
			       .addAttribute("isTestSource", "false");
		}

		for (Path generatedRoot : generatedRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, projectRoot.resolve(generatedRoot)))
			       .addAttribute("isTestSource", "false")
			       .addAttribute("generated", "true");
		}
	}

	/**
	 * Adds an excluded folder entry to the module content root.
	 *
	 * @param content the module content element
	 * @param path the excluded path
	 * @param moduleRoot the module root used to derive a stable module-relative URL
	 */
	private void addExcludedFolder(Element content, Path path, Path moduleRoot)
	{
		content.addElement("excludeFolder").addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, path));
	}

	/**
	 * Adds module dependency order entries for a modeled source set.
	 *
	 * @param rootManager the root manager element
	 * @param projectName the IntelliJ project name
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 */
	private void addModuleDependencyEntries(
		Element rootManager,
		String projectName,
		ModuleSpec module,
		String sourceSetName
	)
	{
		Set<String> dependencyModuleNames = new LinkedHashSet<>();

		for (String dependencyId : module.dependencies())
		{
			dependencyModuleNames.add(IntelliJModuleNames.sourceSetModuleName(projectName, dependencyId, SourceSetNames.MAIN));
		}

		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			dependencyModuleNames.add(IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), SourceSetNames.MAIN));
		}

		for (String dependencyModuleName : dependencyModuleNames)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "module")
			           .addAttribute("module-name", dependencyModuleName)
			           .addAttribute("exported", "");
		}
	}

	/**
	 * Adds project library dependency entries for a modeled source set.
	 *
	 * @param rootManager the root manager element
	 * @param graph the authoritative build graph
	 * @param projectRoot the PSWG project root
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @throws IOException if dependency resolution fails
	 */
	private void addLibraryDependencyEntries(
		Element rootManager,
		BuildGraph graph,
		Path projectRoot,
		Properties gradleProperties,
		boolean refresh,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		Set<Path> dependencies = _dependencyResolver.resolveModuleLibraries(
			graph,
			projectRoot,
			gradleProperties,
			refresh,
			module,
			SourceSetNames.CLIENT.equals(sourceSetName)
		);

		for (Path dependency : dependencies)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", projectLibraryName(dependency))
			           .addAttribute("level", "project");
		}
	}

	/**
	 * Adds the generated-sources component used by IntelliJ to mark AP outputs.
	 *
	 * @param moduleElement the module element
	 * @param projectRoot the PSWG project root
	 * @param module the module specification
	 * @param generatedRoots the generated roots for the source set
	 */
	private void addGeneratedSourcesComponent(Element moduleElement, Path projectRoot, ModuleSpec module, List<Path> generatedRoots)
	{
		if (generatedRoots.isEmpty())
		{
			return;
		}

		Path moduleRoot = projectRoot.resolve(module.paths().root());
		Element additional = moduleElement.addElement("component");
		additional.addAttribute("name", "AdditionalModuleElements");

		for (Path generatedRoot : generatedRoots)
		{
			// IntelliJ does not reliably preserve generated-root markers when they only exist under the
			// main content root, so we mirror them into AdditionalModuleElements the same way Gradle/JPS does.
			Element content = additional.addElement("content");
			content.addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, projectRoot.resolve(generatedRoot)));
			content.addElement("sourceFolder")
			       .addAttribute("url", IntelliJPathMacros.moduleFileUrl(moduleRoot, projectRoot.resolve(generatedRoot)))
			       .addAttribute("isTestSource", "false")
			       .addAttribute("generated", "true");
		}
	}

	/**
	 * Gets the compile output directory for a modeled source set.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @return the compile output directory
	 */
	private Path compileOutputDirectory(Path projectRoot, String projectName, ModuleSpec module, String sourceSetName)
	{
		return projectRoot.resolve("out")
		                  .resolve("production")
		                  .resolve(IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName));
	}

	/**
	 * Gets the source roots for a selected source set.
	 *
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @return the source roots
	 */
	private List<Path> sourceRoots(ModuleSpec module, String sourceSetName)
	{
		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			return module.clientSources();
		}

		return module.mainSources();
	}

	/**
	 * Gets the resource roots for a selected source set.
	 *
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @return the resource roots
	 */
	private List<Path> resourceRoots(ModuleSpec module, String sourceSetName)
	{
		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			return module.clientResources();
		}

		return module.mainResources();
	}

	/**
	 * Checks whether a module has a client source set worth modeling.
	 *
	 * @param module the module specification
	 * @return {@code true} if the client source set exists
	 */
	private boolean hasClientSourceSet(ModuleSpec module)
	{
		return !module.clientSources().isEmpty() || !module.clientResources().isEmpty() || !module.generatedClientSources().isEmpty();
	}

	/**
	 * Builds a stable project-library name for a resolved artifact.
	 *
	 * @param artifact the resolved artifact path
	 * @return the library name
	 */
	private String projectLibraryName(Path artifact)
	{
		String fileName = artifact.getFileName().toString();
		return fileName.endsWith(".jar") ? fileName.substring(0, fileName.length() - 4) : fileName;
	}

	/**
	 * Sanitizes a project-library name for use as a metadata file name.
	 *
	 * @param name the raw library name
	 * @return the sanitized file name
	 */
	private String sanitizeLibraryFileName(String name)
	{
		return name.replace(':', '_').replace('/', '_').replace('\\', '_').replace(' ', '_');
	}

	/**
	 * Adds a registered module entry to the IntelliJ `modules.xml` document.
	 *
	 * @param modules the `modules` element
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
	 * @param path the target IntelliJ project XML path
	 * @return the parsed or synthesized project document
	 * @throws IOException if the existing document cannot be parsed
	 */
	private Document readExistingProjectDocument(Path path) throws IOException
	{
		if (!Files.exists(path))
		{
			Document document = DocumentHelper.createDocument();
			document.addElement("project").addAttribute("version", "4");
			return document;
		}

		try
		{
			return DocumentHelper.parseText(Files.readString(path));
		}
		catch (DocumentException exception)
		{
			throw new IOException("Failed to parse IntelliJ project document: " + path, exception);
		}
	}

	/**
	 * Finds an IntelliJ `<component>` by name, creating it when missing.
	 *
	 * @param project the root `project` element
	 * @param name the component name
	 * @return the existing or newly created component
	 */
	private Element findOrCreateComponent(Element project, String name)
	{
		for (Object child : project.elements("component"))
		{
			Element component = (Element) child;

			if (name.equals(component.attributeValue("name")))
			{
				return component;
			}
		}

		Element component = project.addElement("component");
		component.addAttribute("name", name);
		return component;
	}

	/**
	 * Deletes obsolete generated IntelliJ project-library metadata files.
	 *
	 * <p>The toolchain owns the contents of `.idea/libraries`, so stale files from old transformed
	 * jars or dependency graph changes should be removed as part of each sync.
	 *
	 * @param directory the IntelliJ libraries directory
	 * @param expectedFileNames the generated library metadata files expected after this sync
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
			for (Path entry : entries.toList())
			{
				if (!Files.isRegularFile(entry))
				{
					continue;
				}

				String fileName = entry.getFileName().toString();

				if (!fileName.endsWith(".xml") || expectedFileNames.contains(fileName))
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
	 * @param modulesDirectory the generated project-modules root
	 * @param expectedModuleFiles the module-relative `.iml` files expected after this sync
	 * @throws IOException if stale files cannot be removed
	 */
	private void deleteObsoleteGeneratedProjectModuleFiles(
		Path modulesDirectory,
		Set<String> expectedModuleFiles
	) throws IOException
	{
		if (!Files.isDirectory(modulesDirectory))
		{
			return;
		}

		try (var entries = Files.walk(modulesDirectory))
		{
			for (Path entry : entries.filter(Files::isRegularFile).toList())
			{
				Path relativePath = modulesDirectory.relativize(entry);
				String normalizedPath = relativePath.toString().replace('\\', '/');

				if (!normalizedPath.endsWith(".iml") || expectedModuleFiles.contains(normalizedPath))
				{
					continue;
				}

				Files.deleteIfExists(entry);
			}
		}
	}

}
