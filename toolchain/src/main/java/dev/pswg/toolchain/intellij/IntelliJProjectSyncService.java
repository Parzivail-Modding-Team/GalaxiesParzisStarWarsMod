package dev.pswg.toolchain.intellij;

import dev.pswg.toolchain.fabric.MavenArtifactResolver;
import dev.pswg.toolchain.fabric.MavenCoordinate;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.MavenDependencySpec;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.pswg.definition.PswgBuildDefinition;
import dev.pswg.toolchain.template.FileTemplateRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
	 * Creates a new IntelliJ metadata sync service.
	 */
	public IntelliJProjectSyncService()
	{
		_artifactResolver = new MavenArtifactResolver();
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

		writeCompilerConfiguration(projectRoot, projectName, graph, gradleProperties, refresh);
		writeGeneratedSourceMetadata(projectRoot, graph);
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
		Map<String, String> templateValues = new LinkedHashMap<>();
		templateValues.put("ANNOTATION_PROCESSING_PROFILES", renderAnnotationProcessingProfiles(
			projectRoot,
			projectName,
			graph,
			gradleProperties,
			refresh
		));
		templateValues.put("BYTECODE_TARGET_LEVEL", renderBytecodeTargetLevel(graph));
		templateValues.put("JAVAC_OPTIONS", renderJavacOptions(projectName, graph));

		String rendered = FileTemplateRenderer.render(
			"dev/pswg/toolchain/templates/intellij-compiler.xml",
			templateValues
		);

		Path outputPath = projectRoot.resolve(".idea").resolve("compiler.xml");
		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Writes generated-source `.iml` metadata for modeled PSWG modules.
	 *
	 * @param projectRoot the PSWG project root
	 * @param graph the authoritative build graph
	 * @throws IOException if a metadata file cannot be written
	 */
	private void writeGeneratedSourceMetadata(Path projectRoot, BuildGraph graph) throws IOException
	{
		String projectName = readProjectName(projectRoot);

		for (ModuleSpec module : graph.modules())
		{
			writeSourceSetModuleMetadata(projectRoot, projectName, graph.minecraftVersion(), module, MAIN_SOURCE_SET);
			if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty() || !module.generatedClientSources().isEmpty())
			{
				writeSourceSetModuleMetadata(projectRoot, projectName, graph.minecraftVersion(), module, CLIENT_SOURCE_SET);
			}
		}
	}

	/**
	 * Writes generated-source `.iml` metadata for a single source set module.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param minecraftVersion the tracked Minecraft version
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @throws IOException if the metadata file cannot be written
	 */
	private void writeSourceSetModuleMetadata(
		Path projectRoot,
		String projectName,
		String minecraftVersion,
		ModuleSpec module,
		String sourceSetName
	) throws IOException
	{
		List<Path> generatedRoots = generatedRoots(module, sourceSetName);
		Map<String, String> templateValues = new LinkedHashMap<>();
		templateValues.put("GENERATED_ROOTS", renderGeneratedRoots(projectRoot, generatedRoots));
		templateValues.put("OPTIONAL_FABRIC_FACET", renderOptionalFabricFacet(module, minecraftVersion));
		String rendered = FileTemplateRenderer.render(
			"dev/pswg/toolchain/templates/intellij-generated-sources.iml",
			templateValues
		);
		Path outputPath = projectRoot.resolve(".idea")
		                             .resolve("modules")
		                             .resolve("projects")
		                             .resolve(module.id())
		                             .resolve(projectName + ".projects." + module.id() + "." + sourceSetName + ".iml");

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Renders annotation processing profiles for modeled modules.
	 *
	 * @param projectRoot the PSWG project root
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @return the rendered profile block
	 * @throws IOException if external artifacts cannot be resolved
	 */
	private String renderAnnotationProcessingProfiles(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		StringBuilder builder = new StringBuilder();

		for (ModuleSpec module : graph.modules())
		{
			if (!module.annotationProcessorDependencies().isEmpty())
			{
				appendProfile(
					builder,
					projectName,
					module,
					MAIN_SOURCE_SET,
					resolveExternalDependencies(module.annotationProcessorDependencies(), gradleProperties, refresh)
				);
			}

			if (!module.annotationProcessors().isEmpty())
			{
				appendProfile(
					builder,
					projectName,
					module,
					MAIN_SOURCE_SET,
					resolveAnnotationProcessorModulePath(projectRoot, projectName, graph, module, gradleProperties, refresh)
				);

				if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
				{
					appendProfile(
						builder,
						projectName,
						module,
						CLIENT_SOURCE_SET,
						resolveAnnotationProcessorModulePath(projectRoot, projectName, graph, module, gradleProperties, refresh)
					);
				}
			}
		}

		return builder.toString();
	}

	/**
	 * Appends a single IntelliJ annotation processing profile.
	 *
	 * @param builder the output builder
	 * @param projectName the IntelliJ project name
	 * @param module the module specification
	 * @param sourceSetName the source-set name
	 * @param processorPathEntries the resolved processor path entries
	 */
	private void appendProfile(
		StringBuilder builder,
		String projectName,
		ModuleSpec module,
		String sourceSetName,
		List<Path> processorPathEntries
	)
	{
		builder.append("      <profile name=\"PSWG Toolchain: ")
		       .append(xml(projectName + ".projects." + module.id() + "." + sourceSetName))
		       .append("\" enabled=\"true\">\n")
		       .append("        <outputRelativeToContentRoot value=\"true\" />\n")
		       .append("        <processorPath useClasspath=\"false\">\n");

		for (Path entry : processorPathEntries)
		{
			builder.append("          <entry name=\"")
			       .append(xml(projectRelativeMacro(entry)))
			       .append("\" />\n");
		}

		builder.append("        </processorPath>\n")
		       .append("        <module name=\"")
		       .append(xml(projectName + ".projects." + module.id() + "." + sourceSetName))
		       .append("\" />\n")
		       .append("      </profile>\n");
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
	 * Renders the bytecode target level block.
	 *
	 * @param graph the authoritative build graph
	 * @return the rendered target level
	 */
	private String renderBytecodeTargetLevel(BuildGraph graph)
	{
		int languageLevel = graph.modules().stream().mapToInt(ModuleSpec::javaVersion).max().orElse(25);
		return Integer.toString(languageLevel);
	}

	/**
	 * Renders IntelliJ Javac option overrides for modeled modules and source sets.
	 *
	 * @param projectName the IntelliJ project name
	 * @param graph the authoritative build graph
	 * @return the rendered options block
	 */
	private String renderJavacOptions(String projectName, BuildGraph graph)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("      <module name=\"").append(xml(projectName)).append("\" options=\"-Xmaxerrs 1000 -Xdiags:verbose\" />\n")
		       .append("      <module name=\"").append(xml(projectName + ".main")).append("\" options=\"-Xmaxerrs 1000 -Xdiags:verbose\" />\n");

		for (ModuleSpec module : graph.modules())
		{
			builder.append("      <module name=\"")
			       .append(xml(projectName + ".projects." + module.id() + "." + MAIN_SOURCE_SET))
			       .append("\" options=\"-Xmaxerrs 1000 -Xdiags:verbose\" />\n");

			if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
			{
				builder.append("      <module name=\"")
				       .append(xml(projectName + ".projects." + module.id() + "." + CLIENT_SOURCE_SET))
				       .append("\" options=\"-Xmaxerrs 1000 -Xdiags:verbose\" />\n");
			}
		}

		return builder.toString();
	}

	/**
	 * Renders generated root entries for a source-set `.iml`.
	 *
	 * @param projectRoot the PSWG project root
	 * @param generatedRoots the generated roots
	 * @return the rendered XML block
	 */
	private String renderGeneratedRoots(Path projectRoot, List<Path> generatedRoots)
	{
		StringBuilder builder = new StringBuilder();

		for (Path generatedRoot : generatedRoots)
		{
			String url = "file://$PROJECT_DIR$/" + projectRoot.relativize(projectRoot.resolve(generatedRoot)).toString().replace('\\', '/');
			builder.append("    <content url=\"").append(xml(url)).append("\">\n")
			       .append("      <sourceFolder url=\"").append(xml(url)).append("\" isTestSource=\"false\" generated=\"true\" />\n")
			       .append("    </content>\n");
		}

		return builder.toString();
	}

	/**
	 * Renders the optional Fabric facet block for a modeled module.
	 *
	 * @param module the module specification
	 * @param minecraftVersion the tracked Minecraft version
	 * @return the rendered facet block, or an empty string
	 */
	private String renderOptionalFabricFacet(ModuleSpec module, String minecraftVersion)
	{
		if (module.fabricModJson() == null)
		{
			return "";
		}

		return "  <component name=\"FacetManager\">\n"
			+ "    <facet type=\"minecraft\" name=\"Minecraft\">\n"
			+ "      <configuration>\n"
			+ "        <autoDetectTypes>\n"
			+ "          <platformType>FABRIC</platformType>\n"
			+ "          <platformType>MIXIN</platformType>\n"
			+ "          <platformType>MCP</platformType>\n"
			+ "        </autoDetectTypes>\n"
			+ "        <projectReimportVersion>1</projectReimportVersion>\n"
			+ "      </configuration>\n"
			+ "    </facet>\n"
			+ "  </component>\n"
			+ "  <component name=\"McpModuleSettings\">\n"
			+ "    <option name=\"minecraftVersion\" value=\"" + xml(minecraftVersion) + "\" />\n"
			+ "  </component>\n";
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
	 * Escapes XML attribute values.
	 *
	 * @param value the raw value
	 * @return the escaped value
	 */
	private String xml(String value)
	{
		return value.replace("&", "&amp;")
		            .replace("\"", "&quot;")
		            .replace("<", "&lt;")
		            .replace(">", "&gt;");
	}
}
