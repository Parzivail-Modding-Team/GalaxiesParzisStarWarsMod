package dev.pswg.toolchain.config;

import dev.pswg.toolchain.maven.ToolchainMavenCoordinates;
import dev.pswg.toolchain.maven.ToolchainMavenRepositories;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.path.ModulePaths;

import io.github.wasabithumb.jtoml.JToml;
import io.github.wasabithumb.jtoml.document.TomlDocument;
import io.github.wasabithumb.jtoml.key.TomlKey;
import io.github.wasabithumb.jtoml.value.TomlValue;
import io.github.wasabithumb.jtoml.value.array.TomlArray;
import io.github.wasabithumb.jtoml.value.primitive.TomlPrimitive;
import io.github.wasabithumb.jtoml.value.table.TomlTable;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the host project's text-backed toolchain configuration.
 */
public final class ToolchainProjectConfigLoader
{
	/**
	 * The tracked configuration file name.
	 */
	public static final String CONFIG_FILE_NAME = "toolchain.toml";

	/**
	 * The shared TOML reader.
	 */
	private final JToml _toml;

	/**
	 * Creates the project configuration loader.
	 */
	public ToolchainProjectConfigLoader()
	{
		_toml = JToml.jToml();
	}

	/**
	 * Loads the tracked toolchain configuration from one project root.
	 *
	 * @param projectRoot the tracked host project root
	 * @return the loaded project configuration
	 * @throws IOException if the configuration cannot be read
	 */
	public ToolchainProjectConfig load(Path projectRoot) throws IOException
	{
		Path configPath = projectRoot.resolve(CONFIG_FILE_NAME);

		if (!Files.isRegularFile(configPath))
		{
			throw new IOException("Missing toolchain config: " + configPath);
		}

		try
		{
			TomlDocument document = _toml.read(configPath);
			TomlTable project = requireTable(document, "project", "root");
			TomlTable modulesTable = requireTable(document, "modules", "root");

			return new ToolchainProjectConfig(
				requireString(project, "id", "project"),
				requireString(project, "name", "project"),
				requireString(project, "minecraft_version", "project"),
				requireString(project, "default_development_module", "project"),
				loadModules(modulesTable)
			);
		}
		catch (RuntimeException exception)
		{
			throw new IOException("Failed to load toolchain config from " + configPath, exception);
		}
	}

	/**
	 * Loads all configured modules from the `modules` table.
	 *
	 * @param modulesTable the configured module table
	 * @return the loaded module specs
	 */
	private List<ModuleSpec> loadModules(TomlTable modulesTable)
	{
		List<ModuleSpec> modules = new ArrayList<>();

		for (TomlKey key : modulesTable.keys(false))
		{
			String moduleId = key.toString();
			TomlTable moduleTable = requireTable(modulesTable, moduleId, "modules");
			modules.add(loadModule(moduleId, moduleTable));
		}

		return List.copyOf(modules);
	}

	/**
	 * Loads one configured module into a mutable module specification.
	 *
	 * @param moduleId the configured module identifier
	 * @param table the configured module table
	 * @return the loaded module specification
	 */
	private ModuleSpec loadModule(String moduleId, TomlTable table)
	{
		String root = optionalString(table, "root");

		if (root == null || root.isBlank())
		{
			root = "projects/" + moduleId;
		}

		ModuleSpec spec = new ModuleSpec(moduleId, new ModulePaths(root));
		ConfiguredModuleKind kind = ConfiguredModuleKind.parse(requireString(table, "kind", "modules." + moduleId));
		applyDefaults(spec, kind);

		Integer javaVersion = optionalInteger(table, "java_version");

		if (javaVersion != null)
		{
			spec.javaVersion(javaVersion);
		}

		applyOptionalPathList(table, "main_sources", spec::mainSources);
		applyOptionalPathList(table, "client_sources", spec::clientSources);
		applyOptionalPathList(table, "main_resources", spec::mainResources);
		applyOptionalPathList(table, "client_resources", spec::clientResources);
		applyOptionalPathList(table, "generated_sources", spec::generatedSources);
		applyOptionalPathList(table, "generated_client_sources", spec::generatedClientSources);
		applyOptionalStringList(table, "dependencies", spec::dependency);
		applyOptionalStringList(table, "aggregate_members", spec::aggregateMember);
		applyOptionalStringList(table, "annotation_processors", spec::annotationProcessor);
		applyOptionalStringList(table, "provided_annotation_processor_classes", spec::providedAnnotationProcessorClass);
		applyOptionalPathList(table, "mixins", spec::mixin);
		applyOptionalDependencyList(table, "compile_dependencies", spec::compileDependency);
		applyOptionalDependencyList(table, "client_dependencies", spec::clientDependency);
		applyOptionalDependencyList(table, "annotation_processor_dependencies", spec::annotationProcessorDependency);
		applyOptionalDependencyList(table, "runtime_dependencies", spec::runtimeDependency);

		String artifactId = optionalString(table, "artifact_id");

		if (artifactId != null && !artifactId.isBlank())
		{
			spec.artifactId(artifactId);
		}

		String fabricModId = optionalString(table, "fabric_mod_id");

		if (fabricModId != null && !fabricModId.isBlank())
		{
			spec.fabricModId(fabricModId);
		}

		String fabricModJson = optionalString(table, "fabric_mod_json");

		if (fabricModJson != null && !fabricModJson.isBlank())
		{
			spec.fabricModJson(spec.paths().resolve(fabricModJson));
		}

		String datagenOutput = optionalString(table, "datagen_output");

		if (datagenOutput != null && !datagenOutput.isBlank())
		{
			spec.datagenOutput(spec.paths().resolve(datagenOutput));
		}

		return spec;
	}

	/**
	 * Applies built-in defaults for one configured module kind.
	 *
	 * @param spec the mutable module specification
	 * @param kind the configured module kind
	 */
	private void applyDefaults(ModuleSpec spec, ConfiguredModuleKind kind)
	{
		spec.javaVersion(25);

		switch (kind)
		{
			case JAVA ->
			{
				spec.mainSources(spec.paths().mainJava());
				spec.mainResources(spec.paths().mainResources());
			}

			case FABRIC_COMMON_CLIENT ->
			{
				spec.mainSources(spec.paths().mainJava());
				spec.mainResources(spec.paths().mainResources());
				spec.clientSources(spec.paths().clientJava());
				spec.clientResources(spec.paths().clientResources());
				spec.fabricModJson(spec.paths().mainResource("fabric.mod.json"));
				spec.fabricModId(spec.id());
				spec.compileDependency(ToolchainMavenCoordinates.FABRIC_API, ToolchainMavenRepositories.FABRIC);
				spec.runtimeDependency(ToolchainMavenCoordinates.FABRIC_API, ToolchainMavenRepositories.FABRIC);
				spec.compileDependency(ToolchainMavenCoordinates.JETBRAINS_ANNOTATIONS, ToolchainMavenRepositories.MAVEN_CENTRAL);
			}

			case FABRIC_RESOURCE_ONLY ->
			{
				spec.mainResources(spec.paths().mainResources());
				spec.fabricModJson(spec.paths().mainResource("fabric.mod.json"));
				spec.fabricModId(spec.id());
				spec.compileDependency(ToolchainMavenCoordinates.FABRIC_API, ToolchainMavenRepositories.FABRIC);
				spec.runtimeDependency(ToolchainMavenCoordinates.FABRIC_API, ToolchainMavenRepositories.FABRIC);
			}
		}
	}

	/**
	 * Applies an optional string list to one module mutator.
	 *
	 * @param table the owning table
	 * @param key the list key
	 * @param consumer the target mutator
	 */
	private void applyOptionalStringList(TomlTable table, String key, java.util.function.Consumer<String> consumer)
	{
		for (String value : optionalStringList(table, key))
		{
			consumer.accept(value);
		}
	}

	/**
	 * Applies an optional path list to one module mutator.
	 *
	 * @param table the owning table
	 * @param key the list key
	 * @param consumer the target mutator
	 */
	private void applyOptionalPathList(TomlTable table, String key, java.util.function.Consumer<Path> consumer)
	{
		for (String value : optionalStringList(table, key))
		{
			consumer.accept(Path.of(value.replace('\\', '/')));
		}
	}

	/**
	 * Applies an optional dependency list to one module mutator.
	 *
	 * @param table the owning table
	 * @param key the dependency-list key
	 * @param consumer the target mutator
	 */
	private void applyOptionalDependencyList(
		TomlTable table,
		String key,
		java.util.function.BiConsumer<String, URI> consumer
	)
	{
		TomlArray array = optionalArray(table, key);

		if (array == null)
		{
			return;
		}

		for (TomlValue value : array)
		{
			TomlTable dependency = value.asTable();
			consumer.accept(
				requireString(dependency, "notation", key),
				parseRepository(requireString(dependency, "repository", key))
			);
		}
	}

	/**
	 * Gets an optional string list from one table.
	 *
	 * @param table the owning table
	 * @param key the list key
	 * @return the parsed string list
	 */
	private List<String> optionalStringList(TomlTable table, String key)
	{
		TomlArray array = optionalArray(table, key);

		if (array == null)
		{
			return List.of();
		}

		List<String> values = new ArrayList<>();

		for (TomlValue value : array)
		{
			values.add(value.asPrimitive().asString());
		}

		return List.copyOf(values);
	}

	/**
	 * Gets an optional array from one table.
	 *
	 * @param table the owning table
	 * @param key the array key
	 * @return the array, or {@code null}
	 */
	private TomlArray optionalArray(TomlTable table, String key)
	{
		TomlValue value = table.get(key);
		return value == null ? null : value.asArray();
	}

	/**
	 * Gets one required child table.
	 *
	 * @param table the owning table
	 * @param key the table key
	 * @param context the human-readable context
	 * @return the child table
	 */
	private TomlTable requireTable(TomlTable table, String key, String context)
	{
		TomlValue value = table.get(key);

		if (value == null)
		{
			throw new IllegalArgumentException("Missing table '" + key + "' in " + context);
		}

		return value.asTable();
	}

	/**
	 * Gets one required string.
	 *
	 * @param table the owning table
	 * @param key the string key
	 * @param context the human-readable context
	 * @return the parsed string
	 */
	private String requireString(TomlTable table, String key, String context)
	{
		String value = optionalString(table, key);

		if (value == null || value.isBlank())
		{
			throw new IllegalArgumentException("Missing string '" + key + "' in " + context);
		}

		return value;
	}

	/**
	 * Gets one optional string.
	 *
	 * @param table the owning table
	 * @param key the string key
	 * @return the parsed string, or {@code null}
	 */
	private String optionalString(TomlTable table, String key)
	{
		TomlValue value = table.get(key);

		if (value == null)
		{
			return null;
		}

		return value.asPrimitive().asString();
	}

	/**
	 * Gets one optional integer.
	 *
	 * @param table the owning table
	 * @param key the integer key
	 * @return the parsed integer, or {@code null}
	 */
	private Integer optionalInteger(TomlTable table, String key)
	{
		TomlValue value = table.get(key);

		if (value == null)
		{
			return null;
		}

		TomlPrimitive primitive = value.asPrimitive();
		return Math.toIntExact(primitive.asLong());
	}

	/**
	 * Parses one configured repository token.
	 *
	 * @param value the configured repository token
	 * @return the resolved repository URI
	 */
	private URI parseRepository(String value)
	{
		return switch (value)
		{
			case "maven_central" -> ToolchainMavenRepositories.MAVEN_CENTRAL;
			case "fabric" -> ToolchainMavenRepositories.FABRIC;
			default -> URI.create(value);
		};
	}
}
