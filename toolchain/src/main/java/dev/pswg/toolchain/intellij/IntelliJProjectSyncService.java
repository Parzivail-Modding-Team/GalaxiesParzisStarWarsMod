package dev.pswg.toolchain.intellij;

import dev.pswg.toolchain.fabric.FabricRuntimeArtifacts;
import dev.pswg.toolchain.fabric.FabricRuntimeResolver;
import dev.pswg.toolchain.fabric.MavenArtifactResolver;
import dev.pswg.toolchain.fabric.MavenCoordinate;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.MavenDependencySpec;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.mojang.MojangMetadataClient;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadata;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataLibrary;
import dev.pswg.toolchain.pswg.definition.PswgBuildDefinition;
import dev.pswg.toolchain.template.FileTemplateRenderer;
import dev.pswg.toolchain.template.XmlEscaper;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Generates IntelliJ compiler and generated-source metadata from the authoritative PSWG graph.
 */
public final class IntelliJProjectSyncService
{
	/**
	 * The source-set name used for main compilation.
	 */
	public static final String MAIN_SOURCE_SET = "main";

	/**
	 * The source-set name used for client compilation.
	 */
	public static final String CLIENT_SOURCE_SET = "client";

	/**
	 * Shared Gradle properties file name.
	 */
	private static final String GRADLE_PROPERTIES_FILE = "gradle.properties";

	/**
	 * The shared Maven artifact resolver.
	 */
	private final MavenArtifactResolver _artifactResolver;

	/**
	 * The Mojang metadata resolver.
	 */
	private final MojangMetadataClient _mojangClient;

	/**
	 * The Fabric runtime resolver.
	 */
	private final FabricRuntimeResolver _fabricRuntimeResolver;

	/**
	 * Per-run cache of expanded IntelliJ library artifacts keyed by the original artifact path.
	 */
	private final Map<Path, Set<Path>> _expandedLibraryArtifactsCache;

	/**
	 * Per-run cache of Mojang compile dependencies keyed by Minecraft version and refresh mode.
	 */
	private final Map<String, Set<Path>> _minecraftCompileDependenciesCache;

	/**
	 * Per-run cache of Fabric compile dependencies keyed by loader version and refresh mode.
	 */
	private final Map<String, Set<Path>> _fabricCompileDependenciesCache;

	/**
	 * Creates a new IntelliJ metadata sync service.
	 */
	public IntelliJProjectSyncService()
	{
		_artifactResolver = new MavenArtifactResolver();
		_mojangClient = new MojangMetadataClient();
		_fabricRuntimeResolver = new FabricRuntimeResolver();
		_expandedLibraryArtifactsCache = new LinkedHashMap<>();
		_minecraftCompileDependenciesCache = new LinkedHashMap<>();
		_fabricCompileDependenciesCache = new LinkedHashMap<>();
	}

	/**
	 * Synchronizes compiler and generated-source metadata into the PSWG IntelliJ project.
	 *
	 * @param refresh whether to refresh externally resolved Maven artifacts
	 * @throws IOException if metadata generation fails
	 */
	public void syncPswgProject(boolean refresh) throws IOException
	{
		Path toolchainRoot = Path.of("").toAbsolutePath().normalize();
		Path projectRoot = toolchainRoot.getParent();
		BuildGraph graph = new PswgBuildDefinition().define();
		Properties gradleProperties = loadGradleProperties(projectRoot);
		String projectName = readProjectName(projectRoot);

		writeProjectRegistration(projectRoot, projectName, graph);
		writeCompilerConfiguration(projectRoot, projectName, graph, gradleProperties, refresh);
		writeProjectLibraries(projectRoot, graph, gradleProperties, refresh);
		writeModuleMetadata(projectRoot, projectName, graph, gradleProperties, refresh);
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
		Set<Path> resolvedArtifacts = new LinkedHashSet<>();

		for (ModuleSpec module : graph.modules())
		{
			resolvedArtifacts.addAll(expandIntelliJLibraryArtifacts(resolveImplicitCompileDependencies(graph, gradleProperties, refresh, module)));
			resolvedArtifacts.addAll(expandIntelliJLibraryArtifacts(resolveExternalDependencies(module.compileDependencies(), gradleProperties, refresh)));
			resolvedArtifacts.addAll(expandIntelliJLibraryArtifacts(resolveExternalDependencies(module.clientDependencies(), gradleProperties, refresh)));
		}

		Path librariesDirectory = projectRoot.resolve(".idea").resolve("libraries");
		Files.createDirectories(librariesDirectory);

		for (Path artifact : resolvedArtifacts)
		{
			IntelliJXmlWriter.write(
				librariesDirectory.resolve(sanitizeLibraryFileName(projectLibraryName(artifact)) + ".xml"),
				createProjectLibraryDocument(projectRoot, artifact)
			);
		}
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
		for (ModuleSpec module : graph.modules())
		{
			writeSourceSetModuleMetadata(projectRoot, projectName, graph, gradleProperties, refresh, module, MAIN_SOURCE_SET);

			if (hasClientSourceSet(module))
			{
				writeSourceSetModuleMetadata(projectRoot, projectName, graph, gradleProperties, refresh, module, CLIENT_SOURCE_SET);
			}
		}
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
		                             .resolve(projectName + ".projects." + module.id() + "." + sourceSetName + ".iml");
		IntelliJXmlWriter.write(
			outputPath,
			createModuleDocument(projectRoot, projectName, graph, gradleProperties, refresh, module, sourceSetName)
		);
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
					projectName,
					module,
					MAIN_SOURCE_SET,
					resolveExternalDependencies(module.annotationProcessorDependencies(), gradleProperties, refresh)
				);
			}

			if (!module.annotationProcessors().isEmpty())
			{
				addAnnotationProfile(
					annotationProcessing,
					projectName,
					module,
					MAIN_SOURCE_SET,
					resolveAnnotationProcessorModulePath(projectRoot, projectName, graph, module, gradleProperties, refresh)
				);

				if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
				{
					addAnnotationProfile(
						annotationProcessing,
						projectName,
						module,
						CLIENT_SOURCE_SET,
						resolveAnnotationProcessorModulePath(projectRoot, projectName, graph, module, gradleProperties, refresh)
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
	 * @param projectName the IntelliJ project name
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @param processorPathEntries the resolved processor path entries
	 */
	private void addAnnotationProfile(
		Element annotationProcessing,
		String projectName,
		ModuleSpec module,
		String sourceSetName,
		List<Path> processorPathEntries
	)
	{
		Element profile = annotationProcessing.addElement("profile");
		profile.addAttribute("name", "PSWG Toolchain: " + projectName + ".projects." + module.id() + "." + sourceSetName);
		profile.addAttribute("enabled", "true");
		profile.addElement("outputRelativeToContentRoot").addAttribute("value", "true");
		Element processorPath = profile.addElement("processorPath");
		processorPath.addAttribute("useClasspath", "false");

		for (Path entry : processorPathEntries)
		{
			processorPath.addElement("entry").addAttribute("name", projectRelativeMacro(entry));
		}

		profile.addElement("module").addAttribute("name", projectName + ".projects." + module.id() + "." + sourceSetName);
	}

	/**
	 * Resolves the processor path for module-backed annotation processors.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param module the target module
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @return the ordered processor path entries
	 * @throws IOException if external artifacts cannot be resolved
	 */
	private List<Path> resolveAnnotationProcessorModulePath(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		ModuleSpec module,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		Set<Path> entries = new LinkedHashSet<>();

		for (String processorId : module.annotationProcessors())
		{
			ModuleSpec processorModule = requireModule(graph, processorId);
			entries.add(projectRoot.resolve("out")
			                      .resolve("production")
			                      .resolve(projectName + ".projects." + processorId + "." + MAIN_SOURCE_SET));

			for (String dependencyId : processorModule.dependencies())
			{
				entries.add(projectRoot.resolve("out")
				                      .resolve("production")
				                      .resolve(projectName + ".projects." + dependencyId + "." + MAIN_SOURCE_SET));
			}

			entries.addAll(resolveExternalDependencies(processorModule.compileDependencies(), gradleProperties, refresh));
			entries.addAll(resolveExternalDependencies(processorModule.annotationProcessorDependencies(), gradleProperties, refresh));
		}

		return List.copyOf(entries);
	}

	/**
	 * Resolves external Maven dependencies into cached artifact paths.
	 *
	 * @param dependencies the declared dependencies
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @return the resolved artifact paths
	 * @throws IOException if an artifact cannot be resolved
	 */
	private List<Path> resolveExternalDependencies(
		List<MavenDependencySpec> dependencies,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		List<Path> paths = new ArrayList<>();

		for (MavenDependencySpec dependency : dependencies)
		{
			Path artifact = _artifactResolver.resolve(
				MavenCoordinate.parse(substituteProperties(dependency.notation(), gradleProperties)),
				dependency.repository(),
				refresh
			);

			if (!paths.contains(artifact))
			{
				paths.add(artifact);
			}
		}

		return paths;
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

		for (ModuleSpec module : graph.modules())
		{
			additionalOptions.addElement("module")
			                 .addAttribute("name", projectName + ".projects." + module.id() + "." + MAIN_SOURCE_SET)
			                 .addAttribute("options", "-Xmaxerrs 1000 -Xdiags:verbose");

			if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
			{
				additionalOptions.addElement("module")
				                 .addAttribute("name", projectName + ".projects." + module.id() + "." + CLIENT_SOURCE_SET)
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
		if (CLIENT_SOURCE_SET.equals(sourceSetName))
		{
			return module.generatedClientSources();
		}

		return module.generatedSources();
	}

	/**
	 * Resolves a module from the authoritative graph.
	 *
	 * @param graph the authoritative build graph
	 * @param moduleId the module identifier
	 * @return the resolved module
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
	 * Loads the tracked Gradle properties from the PSWG project root.
	 *
	 * @param projectRoot the PSWG project root
	 * @return the loaded properties
	 * @throws IOException if the file cannot be read
	 */
	private Properties loadGradleProperties(Path projectRoot) throws IOException
	{
		Properties properties = new Properties();
		Path path = projectRoot.resolve(GRADLE_PROPERTIES_FILE);

		try (InputStream inputStream = Files.newInputStream(path))
		{
			properties.load(inputStream);
		}

		return properties;
	}

	/**
	 * Reads the IntelliJ project name.
	 *
	 * @param projectRoot the PSWG project root
	 * @return the project name
	 */
	private String readProjectName(Path projectRoot)
	{
		Path projectNameFile = projectRoot.resolve(".idea").resolve(".name");

		try
		{
			if (Files.exists(projectNameFile))
			{
				String value = Files.readString(projectNameFile).trim();

				if (!value.isBlank())
				{
					return value;
				}
			}
		}
		catch (IOException ignored)
		{
		}

		return projectRoot.getFileName().toString();
	}

	/**
	 * Converts a resolved path into a `$PROJECT_DIR$` macro path when possible.
	 *
	 * @param path the resolved path
	 * @return the macro path
	 */
	private String projectRelativeMacro(Path path)
	{
		Path projectRoot = Path.of("").toAbsolutePath().normalize().getParent();
		Path normalized = path.toAbsolutePath().normalize();

		if (normalized.startsWith(projectRoot))
		{
			return "$PROJECT_DIR$/" + projectRoot.relativize(normalized).toString().replace('\\', '/');
		}

		return normalized.toString().replace('\\', '/');
	}

	/**
	 * Applies simple Gradle-style property substitution to a dependency notation.
	 *
	 * @param value the raw notation value
	 * @param properties the available properties
	 * @return the substituted value
	 */
	private String substituteProperties(String value, Properties properties)
	{
		String substituted = value;

		for (String propertyName : properties.stringPropertyNames())
		{
			substituted = substituted.replace("${" + propertyName + "}", properties.getProperty(propertyName));
		}

		return substituted;
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
		classes.addElement("root").addAttribute("url", jarUrl(projectRoot, artifact));
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

		for (ModuleSpec module : graph.modules())
		{
			addRegisteredModule(
				modules,
				"$PROJECT_DIR$/.idea/modules/projects/" + module.id() + "/" + projectName + ".projects." + module.id() + "." + MAIN_SOURCE_SET + ".iml"
			);

			if (hasClientSourceSet(module))
			{
				addRegisteredModule(
					modules,
					"$PROJECT_DIR$/.idea/modules/projects/" + module.id() + "/" + projectName + ".projects." + module.id() + "." + CLIENT_SOURCE_SET + ".iml"
				);
			}
		}

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
		rootManager.addElement("output").addAttribute("url", fileUrl(projectRoot, compileOutputDirectory(projectRoot, projectName, module, sourceSetName)));
		rootManager.addElement("exclude-output");

		Element content = rootManager.addElement("content");
		content.addAttribute("url", moduleFileUrl(moduleRoot, moduleRoot));
		addSourceFolders(projectRoot, content, module, sourceSetName);
		addExcludedFolder(content, moduleRoot.resolve("build"), moduleRoot);

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");
		addModuleDependencyEntries(rootManager, projectName, graph, module, sourceSetName);
		addLibraryDependencyEntries(rootManager, graph, projectRoot, gradleProperties, refresh, module, sourceSetName);
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
			       .addAttribute("url", moduleFileUrl(moduleRoot, projectRoot.resolve(sourceRoot)))
			       .addAttribute("isTestSource", "false");
		}

		for (Path resourceRoot : resourceRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", moduleFileUrl(moduleRoot, projectRoot.resolve(resourceRoot)))
			       .addAttribute("type", "java-resource")
			       .addAttribute("isTestSource", "false");
		}

		for (Path generatedRoot : generatedRoots(module, sourceSetName))
		{
			content.addElement("sourceFolder")
			       .addAttribute("url", moduleFileUrl(moduleRoot, projectRoot.resolve(generatedRoot)))
			       .addAttribute("isTestSource", "false")
			       .addAttribute("generated", "true");
		}
	}

	/**
	 * Adds an excluded folder entry to the module content root.
	 *
	 * @param projectRoot the PSWG project root
	 * @param content the module content element
	 * @param path the excluded path
	 */
	private void addExcludedFolder(Element content, Path path, Path moduleRoot)
	{
		content.addElement("excludeFolder").addAttribute("url", moduleFileUrl(moduleRoot, path));
	}

	/**
	 * Adds module dependency order entries for a modeled source set.
	 *
	 * @param rootManager the root manager element
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param module the module specification
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

		for (String dependencyId : module.dependencies())
		{
			dependencyModuleNames.add(projectName + ".projects." + dependencyId + "." + MAIN_SOURCE_SET);
		}

		if (CLIENT_SOURCE_SET.equals(sourceSetName))
		{
			dependencyModuleNames.add(projectName + ".projects." + module.id() + "." + MAIN_SOURCE_SET);
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
		Set<Path> dependencies = new LinkedHashSet<>(expandIntelliJLibraryArtifacts(
			resolveImplicitCompileDependencies(graph, gradleProperties, refresh, module)
		));
		dependencies.addAll(expandIntelliJLibraryArtifacts(
			resolveExternalDependencies(module.compileDependencies(), gradleProperties, refresh)
		));

		if (CLIENT_SOURCE_SET.equals(sourceSetName))
		{
			dependencies.addAll(expandIntelliJLibraryArtifacts(
				resolveExternalDependencies(module.clientDependencies(), gradleProperties, refresh)
			));
		}

		for (Path dependency : dependencies)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", projectLibraryName(dependency))
			           .addAttribute("level", "project");
		}
	}

	/**
	 * Resolves the implicit platform compile dependencies for a module.
	 *
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh downloaded artifacts
	 * @param module the module specification
	 * @return the implicit compile artifacts
	 * @throws IOException if dependency resolution fails
	 */
	private Set<Path> resolveImplicitCompileDependencies(
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh,
		ModuleSpec module
	) throws IOException
	{
		Set<Path> dependencies = new LinkedHashSet<>();

		if (module.fabricModJson() == null)
		{
			return dependencies;
		}

		dependencies.addAll(resolveMinecraftCompileDependencies(graph.minecraftVersion(), refresh));
		dependencies.addAll(resolveFabricCompileDependencies(gradleProperties.getProperty("loader_version"), refresh));
		return dependencies;
	}

	/**
	 * Resolves the compile-time Minecraft jars needed for official-namespace PSWG modules.
	 *
	 * @param minecraftVersion the tracked Minecraft version
	 * @param refresh whether to refresh downloaded artifacts
	 * @return the compile-time Minecraft jars
	 * @throws IOException if resolution fails
	 */
	private Set<Path> resolveMinecraftCompileDependencies(String minecraftVersion, boolean refresh) throws IOException
	{
		String cacheKey = minecraftVersion + "|" + refresh;
		Set<Path> cached = _minecraftCompileDependenciesCache.get(cacheKey);

		if (cached != null)
		{
			return cached;
		}

		Set<Path> dependencies = new LinkedHashSet<>();
		MojangVersionMetadata metadata = _mojangClient.getVersionMetadata(minecraftVersion, refresh);
		dependencies.add(_mojangClient.downloadClientJar(minecraftVersion, refresh));

		for (MojangVersionMetadataLibrary library : metadata.libraries())
		{
			if (!_mojangClient.isLibraryAllowed(library))
			{
				continue;
			}

			if (library.name() != null && library.name().contains(":natives-"))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null || library.downloads().artifact().path() == null)
			{
				continue;
			}

			Path target = _mojangClient.paths().libraryFile(library.downloads().artifact().path());
			_mojangClient.download(
				java.net.URI.create(library.downloads().artifact().url()),
				target,
				refresh
			);
			dependencies.add(target);
		}

		Set<Path> resolved = Set.copyOf(dependencies);
		_minecraftCompileDependenciesCache.put(cacheKey, resolved);
		return resolved;
	}

	/**
	 * Resolves the compile-time Fabric jars needed for Fabric-backed PSWG modules.
	 *
	 * @param loaderVersion the tracked Fabric Loader version
	 * @param refresh whether to refresh downloaded artifacts
	 * @return the compile-time Fabric jars
	 * @throws IOException if resolution fails
	 */
	private Set<Path> resolveFabricCompileDependencies(String loaderVersion, boolean refresh) throws IOException
	{
		String cacheKey = String.valueOf(loaderVersion) + "|" + refresh;
		Set<Path> cached = _fabricCompileDependenciesCache.get(cacheKey);

		if (cached != null)
		{
			return cached;
		}

		Set<Path> dependencies = new LinkedHashSet<>();

		if (loaderVersion == null || loaderVersion.isBlank())
		{
			return dependencies;
		}

		FabricRuntimeArtifacts runtimeArtifacts = _fabricRuntimeResolver.resolveClientRuntime(loaderVersion, refresh);
		dependencies.addAll(runtimeArtifacts.classpath());
		Set<Path> resolved = Set.copyOf(dependencies);
		_fabricCompileDependenciesCache.put(cacheKey, resolved);
		return resolved;
	}

	/**
	 * Expands artifacts for IntelliJ when a dependency jar is only a container for nested jars.
	 *
	 * @param artifacts the resolved artifacts
	 * @return the IntelliJ-visible classpath artifacts
	 * @throws IOException if nested jars cannot be extracted
	 */
	private Set<Path> expandIntelliJLibraryArtifacts(Collection<Path> artifacts) throws IOException
	{
		Set<Path> expandedArtifacts = new LinkedHashSet<>();

		for (Path artifact : artifacts)
		{
			Set<Path> nestedArtifacts = _expandedLibraryArtifactsCache.get(artifact);

			if (nestedArtifacts == null)
			{
				nestedArtifacts = Set.copyOf(extractNestedClasspathJars(artifact));
				_expandedLibraryArtifactsCache.put(artifact, nestedArtifacts);
			}

			if (nestedArtifacts.isEmpty())
			{
				expandedArtifacts.add(artifact);
				continue;
			}

			expandedArtifacts.add(artifact);
			expandedArtifacts.addAll(nestedArtifacts);
		}

		return expandedArtifacts;
	}

	/**
	 * Extracts nested `META-INF/jars/*.jar` classpath entries from a container jar when present.
	 *
	 * @param artifact the candidate artifact
	 * @return the extracted nested jars, or an empty list if the artifact is a normal jar
	 * @throws IOException if extraction fails
	 */
	private List<Path> extractNestedClasspathJars(Path artifact) throws IOException
	{
		if (!artifact.getFileName().toString().endsWith(".jar"))
		{
			return List.of();
		}

		List<Path> nestedArtifacts = new ArrayList<>();
		Path extractionRoot = artifact.getParent().resolve(".intellij-exploded").resolve(projectLibraryName(artifact));

		try (InputStream inputStream = Files.newInputStream(artifact);
		     ZipInputStream zipInputStream = new ZipInputStream(inputStream))
		{
			ZipEntry entry;

			while ((entry = zipInputStream.getNextEntry()) != null)
			{
				if (entry.isDirectory() || !entry.getName().startsWith("META-INF/jars/") || !entry.getName().endsWith(".jar"))
				{
					continue;
				}

				Path target = extractionRoot.resolve(Path.of(entry.getName()).getFileName().toString());
				Files.createDirectories(target.getParent());

				try (OutputStream outputStream = Files.newOutputStream(
					target,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE
				))
				{
					zipInputStream.transferTo(outputStream);
				}

				nestedArtifacts.add(target);
			}
		}

		return nestedArtifacts;
	}

	/**
	 * Adds the generated-sources component used by IntelliJ to mark AP outputs.
	 *
	 * @param moduleElement the module element
	 * @param projectRoot the PSWG project root
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
			Element content = additional.addElement("content");
			content.addAttribute("url", moduleFileUrl(moduleRoot, projectRoot.resolve(generatedRoot)));
			content.addElement("sourceFolder")
			       .addAttribute("url", moduleFileUrl(moduleRoot, projectRoot.resolve(generatedRoot)))
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
		                  .resolve(projectName + ".projects." + module.id() + "." + sourceSetName);
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
		if (CLIENT_SOURCE_SET.equals(sourceSetName))
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
		if (CLIENT_SOURCE_SET.equals(sourceSetName))
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
	 * Builds a `jar://...!/` URL for a resolved artifact.
	 *
	 * @param projectRoot the PSWG project root
	 * @param artifact the resolved artifact path
	 * @return the jar URL
	 */
	private String jarUrl(Path projectRoot, Path artifact)
	{
		return "jar://" + projectRelativeMacro(artifact) + "!/";
	}

	/**
	 * Builds a `file://...` URL for a project-relative path.
	 *
	 * @param projectRoot the PSWG project root
	 * @param path the target path
	 * @return the file URL
	 */
	private String fileUrl(Path projectRoot, Path path)
	{
		return "file://" + projectRelativeMacro(path);
	}

	/**
	 * Builds a module-local `file://...` URL for a path rooted under the module directory.
	 *
	 * @param moduleRoot the module root path
	 * @param path the target path
	 * @return the file URL
	 */
	private String moduleFileUrl(Path moduleRoot, Path path)
	{
		Path normalizedModuleRoot = moduleRoot.toAbsolutePath().normalize();
		Path normalizedPath = path.toAbsolutePath().normalize();

		if (normalizedPath.startsWith(normalizedModuleRoot))
		{
			Path relativePath = normalizedModuleRoot.relativize(normalizedPath);

			if (relativePath.toString().isEmpty())
			{
				return "file://$MODULE_DIR$/../../../../projects/" + normalizedModuleRoot.getFileName();
			}

			return "file://$MODULE_DIR$/../../../../projects/" + normalizedModuleRoot.getFileName() + "/" + relativePath.toString().replace('\\', '/');
		}

		return "file://" + normalizedPath.toString().replace('\\', '/');
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

}
