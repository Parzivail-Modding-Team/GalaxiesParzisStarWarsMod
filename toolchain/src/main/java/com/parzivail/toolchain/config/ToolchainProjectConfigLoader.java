package com.parzivail.toolchain.config;

import com.parzivail.toolchain.maven.ToolchainMavenCoordinates;
import com.parzivail.toolchain.maven.ToolchainMavenRepositories;
import com.parzivail.toolchain.model.ModuleSpec;
import com.parzivail.toolchain.path.ModulePaths;
import com.parzivail.toolchain.path.ToolchainPaths;
import io.github.wasabithumb.jtoml.JToml;
import io.github.wasabithumb.jtoml.value.TomlValue;
import io.github.wasabithumb.jtoml.value.array.TomlArray;
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
	 * @return the loaded project configuration
	 *
	 * @throws IOException if the configuration cannot be read
	 */
	public ToolchainProjectConfig load() throws IOException
	{
		var configPath = ToolchainPaths.CONFIG_FILE;

		if (!Files.isRegularFile(configPath))
		{
			throw new IOException("Missing toolchain config: " + configPath);
		}

		try
		{
			var document = _toml.read(configPath);
			var project = requireTable(document, "project", "root");
			var modulesTable = requireTable(document, "modules", "root");
			var modules = loadModules(modulesTable);
			var defaultDevelopmentModule = requireString(project, "default_development_module", "project");

			validateProjectConfiguration(defaultDevelopmentModule, modules);

			return new ToolchainProjectConfig(
					requireString(project, "id", "project"),
					requireString(project, "name", "project"),
					requireString(project, "minecraft_version", "project"),
					requireString(project, "loader_version", "project"),
					defaultDevelopmentModule,
					modules
			);
		}
		catch (RuntimeException exception)
		{
			throw new IOException("Failed to load toolchain config from " + configPath, exception);
		}
	}

	/**
	 * Validates the project-level configuration against the loaded module graph.
	 *
	 * @param defaultDevelopmentModule the configured default development module id
	 * @param modules                  the loaded modules
	 */
	private void validateProjectConfiguration(String defaultDevelopmentModule, List<ModuleSpec> modules)
	{
		if (modules.isEmpty())
		{
			throw new IllegalArgumentException("toolchain.toml must define at least one module");
		}

		var foundDefault = modules.stream()
		                          .map(ModuleSpec::id)
		                          .anyMatch(defaultDevelopmentModule::equals);

		if (!foundDefault)
		{
			throw new IllegalArgumentException(
					"Default development module '" + defaultDevelopmentModule + "' is not declared under [modules]"
			);
		}
	}

	/**
	 * Loads all configured modules from the `modules` table.
	 *
	 * @param modulesTable the configured module table
	 *
	 * @return the loaded module specs
	 */
	private List<ModuleSpec> loadModules(TomlTable modulesTable)
	{
		List<ModuleSpec> modules = new ArrayList<>();

		for (var key : modulesTable.keys(false))
		{
			var moduleId = key.toString();
			var moduleTable = requireTable(modulesTable, moduleId, "modules");
			modules.add(loadModule(moduleId, moduleTable));
		}

		validateModules(modules);
		return List.copyOf(modules);
	}

	/**
	 * Loads one configured module into a mutable module specification.
	 *
	 * @param moduleId the configured module identifier
	 * @param table    the configured module table
	 *
	 * @return the loaded module specification
	 */
	private ModuleSpec loadModule(String moduleId, TomlTable table)
	{
		var root = optionalString(table, "root");

		if (root == null || root.isBlank())
		{
			root = "projects/" + moduleId;
		}

		var spec = new ModuleSpec(moduleId, new ModulePaths(root));
		var kind = ConfiguredModuleKind.parse(requireString(table, "kind", "modules." + moduleId));
		applyDefaults(spec, kind);

		var javaVersion = optionalInteger(table, "java_version");

		if (javaVersion != null)
		{
			spec.javaVersion(javaVersion);
		}

		applyOptionalPathList(table, "main_sources", spec::mainSources);
		applyOptionalPathList(table, "client_sources", spec::clientSources);
		applyOptionalPathList(table, "main_resources", spec::mainResources);
		applyOptionalPathList(table, "client_resources", spec::clientResources);
		applyOptionalDefaultablePathList(table, "generated_sources", spec.paths().generatedAnnotationProcessorMain(), spec::generatedSources);
		applyOptionalDefaultablePathList(table, "generated_client_sources", spec.paths().generatedAnnotationProcessorClient(), spec::generatedClientSources);
		applyOptionalStringList(table, "dependencies", spec::dependency);
		applyOptionalString(table, "dependency", spec::dependency);
		applyOptionalStringList(table, "aggregate_members", spec::aggregateMember);
		applyOptionalString(table, "aggregate_member", spec::aggregateMember);
		applyOptionalStringList(table, "annotation_processors", spec::annotationProcessor);
		applyOptionalString(table, "annotation_processor", spec::annotationProcessor);
		applyOptionalStringList(table, "provided_annotation_processor_classes", spec::providedAnnotationProcessorClass);
		applyOptionalString(table, "provided_annotation_processor_class", spec::providedAnnotationProcessorClass);
		applyOptionalPathList(table, "mixins", spec::mixin);
		applyOptionalRelativeResourceList(table, "main_mixins", spec.paths()::mainResource, spec::mixin);
		applyOptionalRelativeResourceList(table, "client_mixins", spec.paths()::clientResource, spec::mixin);
		applyOptionalDependencyList(table, "compile_dependencies", spec::compileDependency);
		applyOptionalDependencyList(table, "client_dependencies", spec::clientDependency);
		applyOptionalDependencyList(table, "annotation_processor_dependencies", spec::annotationProcessorDependency);
		applyOptionalDependencyList(table, "runtime_dependencies", spec::runtimeDependency);

		var artifactId = optionalString(table, "artifact_id");

		if (artifactId != null && !artifactId.isBlank())
		{
			spec.artifactId(artifactId);
		}

		var fabricModId = optionalString(table, "fabric_mod_id");

		if (fabricModId != null && !fabricModId.isBlank())
		{
			spec.fabricModId(fabricModId);
		}

		var fabricModJson = optionalString(table, "fabric_mod_json");

		if (fabricModJson != null && !fabricModJson.isBlank())
		{
			spec.fabricModJson(spec.paths().resolve(fabricModJson));
		}

		applyOptionalDatagenOutput(table, spec);

		if (!spec.annotationProcessors().isEmpty() && spec.generatedSources().isEmpty())
		{
			spec.generatedSources(spec.paths().generatedAnnotationProcessorMain());
		}

		if (kind == ConfiguredModuleKind.FABRIC_SPLIT_SOURCES
		    && !spec.annotationProcessors().isEmpty()
		    && spec.generatedClientSources().isEmpty())
		{
			spec.generatedClientSources(spec.paths().generatedAnnotationProcessorClient());
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

			case FABRIC_SPLIT_SOURCES ->
			{
				spec.mainSources(spec.paths().mainJava());
				spec.mainResources(spec.paths().mainResources());
				spec.clientSources(spec.paths().clientJava());
				spec.clientResources(spec.paths().clientResources());
				spec.fabricModJson(spec.paths().mainResource("fabric.mod.json"));
				spec.fabricModId(spec.id());
				spec.compileDependency(ToolchainMavenCoordinates.JETBRAINS_ANNOTATIONS, ToolchainMavenRepositories.MAVEN_CENTRAL);
			}

			case FABRIC_RESOURCE_ONLY ->
			{
				spec.mainResources(spec.paths().mainResources());
				spec.fabricModJson(spec.paths().mainResource("fabric.mod.json"));
				spec.fabricModId(spec.id());
			}
		}
	}

	/**
	 * Validates the loaded module graph shape before it reaches the wider toolchain.
	 *
	 * @param modules the loaded modules
	 */
	private void validateModules(List<ModuleSpec> modules)
	{
		var moduleIds = modules.stream().map(ModuleSpec::id).toList();

		for (var module : modules)
		{
			validateModuleReferences(module, "dependency", module.dependencies(), moduleIds);
			validateModuleReferences(module, "aggregate member", module.aggregateMembers(), moduleIds);
			validateModuleReferences(module, "annotation processor", module.annotationProcessors(), moduleIds);

			if (module.aggregateMembers().contains(module.id()))
			{
				throw new IllegalArgumentException("Module '" + module.id() + "' cannot aggregate itself");
			}
		}
	}

	/**
	 * Validates one logical module-reference list.
	 *
	 * @param module           the owning module
	 * @param relationshipName the human-readable relationship name
	 * @param referencedIds    the referenced identifiers
	 * @param knownModuleIds   the loaded module identifiers
	 */
	private void validateModuleReferences(
			ModuleSpec module,
			String relationshipName,
			List<String> referencedIds,
			List<String> knownModuleIds
	)
	{
		for (var referencedId : referencedIds)
		{
			if (!knownModuleIds.contains(referencedId))
			{
				throw new IllegalArgumentException(
						"Module '" + module.id() + "' references unknown " + relationshipName + " module '" + referencedId + "'"
				);
			}
		}
	}

	/**
	 * Applies an optional string list to one module mutator.
	 *
	 * @param table    the owning table
	 * @param key      the list key
	 * @param consumer the target mutator
	 */
	private void applyOptionalStringList(TomlTable table, String key, java.util.function.Consumer<String> consumer)
	{
		for (var value : optionalStringList(table, key))
		{
			consumer.accept(value);
		}
	}

	/**
	 * Applies one optional singular string to one module mutator.
	 *
	 * @param table    the owning table
	 * @param key      the singular key
	 * @param consumer the target mutator
	 */
	private void applyOptionalString(TomlTable table, String key, java.util.function.Consumer<String> consumer)
	{
		var value = optionalString(table, key);

		if (value != null && !value.isBlank())
		{
			consumer.accept(value);
		}
	}

	/**
	 * Applies an optional path list to one module mutator.
	 *
	 * @param table    the owning table
	 * @param key      the list key
	 * @param consumer the target mutator
	 */
	private void applyOptionalPathList(TomlTable table, String key, java.util.function.Consumer<Path> consumer)
	{
		for (var value : optionalStringList(table, key))
		{
			consumer.accept(Path.of(value.replace('\\', '/')));
		}
	}

	/**
	 * Applies one optional path list where `true` means "use the default generated root".
	 *
	 * @param table       the owning table
	 * @param key         the list key
	 * @param defaultPath the default generated path
	 * @param consumer    the target mutator
	 */
	private void applyOptionalDefaultablePathList(
			TomlTable table,
			String key,
			Path defaultPath,
			java.util.function.Consumer<Path> consumer
	)
	{
		var value = table.get(key);

		if (value == null)
		{
			return;
		}

		if (isBooleanTrue(value))
		{
			consumer.accept(defaultPath);
			return;
		}

		for (var path : stringList(value))
		{
			consumer.accept(Path.of(path.replace('\\', '/')));
		}
	}

	/**
	 * Applies one optional resource-relative list using one base resolver.
	 *
	 * @param table    the owning table
	 * @param key      the list key
	 * @param resolver the relative-path resolver
	 * @param consumer the target mutator
	 */
	private void applyOptionalRelativeResourceList(
			TomlTable table,
			String key,
			java.util.function.Function<String, Path> resolver,
			java.util.function.Consumer<Path> consumer
	)
	{
		for (var value : optionalStringList(table, key))
		{
			consumer.accept(resolver.apply(value));
		}
	}

	/**
	 * Applies the optional datagen output shorthand.
	 *
	 * @param table the owning module table
	 * @param spec  the mutable module specification
	 */
	private void applyOptionalDatagenOutput(TomlTable table, ModuleSpec spec)
	{
		var datagen = table.get("datagen");

		if (datagen != null)
		{
			if (isBooleanTrue(datagen))
			{
				spec.datagenOutput(spec.paths().generatedDatagen());
				return;
			}

			spec.datagenOutput(spec.paths().resolve(datagen.asPrimitive().asString()));
			return;
		}

		var datagenOutput = optionalString(table, "datagen_output");

		if (datagenOutput != null && !datagenOutput.isBlank())
		{
			spec.datagenOutput(spec.paths().resolve(datagenOutput));
		}
	}

	/**
	 * Applies an optional dependency list to one module mutator.
	 *
	 * @param table    the owning table
	 * @param key      the dependency-list key
	 * @param consumer the target mutator
	 */
	private void applyOptionalDependencyList(
			TomlTable table,
			String key,
			java.util.function.BiConsumer<String, URI> consumer
	)
	{
		var array = optionalArray(table, key);

		if (array == null)
		{
			return;
		}

		for (var value : array)
		{
			var dependency = parseDependency(value, key);
			consumer.accept(dependency.notation(), dependency.repository());
		}
	}

	/**
	 * Parses one dependency entry from either table or string shorthand form.
	 *
	 * @param value   the raw TOML value
	 * @param context the human-readable config key
	 *
	 * @return the parsed dependency entry
	 */
	private ParsedDependency parseDependency(TomlValue value, String context)
	{
		try
		{
			var dependency = value.asTable();
			return new ParsedDependency(
					requireString(dependency, "notation", context),
					parseRepository(requireString(dependency, "repository", context))
			);
		}
		catch (RuntimeException ignored)
		{
		}

		var shorthand = value.asPrimitive().asString();
		var separator = shorthand.lastIndexOf('@');

		if (separator < 0)
		{
			throw new IllegalArgumentException(
					"Dependency '" + shorthand + "' in " + context + " must use '<notation> @ <repository>' string shorthand"
			);
		}

		var notation = shorthand.substring(0, separator).trim();
		var repository = shorthand.substring(separator + 1).trim();

		if (notation.isBlank() || repository.isBlank())
		{
			throw new IllegalArgumentException("Dependency shorthand in " + context + " must include notation and repository");
		}

		return new ParsedDependency(notation, parseRepository(repository));
	}

	/**
	 * Gets an optional string list from one table.
	 *
	 * @param table the owning table
	 * @param key   the list key
	 *
	 * @return the parsed string list
	 */
	private List<String> optionalStringList(TomlTable table, String key)
	{
		var value = table.get(key);

		if (value == null)
		{
			return List.of();
		}

		return stringList(value);
	}

	/**
	 * Converts one TOML value into a string list, accepting either one string or an array.
	 *
	 * @param value the raw TOML value
	 *
	 * @return the parsed string list
	 */
	private List<String> stringList(TomlValue value)
	{
		try
		{
			return List.of(value.asPrimitive().asString());
		}
		catch (RuntimeException ignored)
		{
		}

		var array = value.asArray();
		List<String> values = new ArrayList<>();

		for (var entry : array)
		{
			values.add(entry.asPrimitive().asString());
		}

		return List.copyOf(values);
	}

	/**
	 * Gets an optional array from one table.
	 *
	 * @param table the owning table
	 * @param key   the array key
	 *
	 * @return the array, or {@code null}
	 */
	private TomlArray optionalArray(TomlTable table, String key)
	{
		var value = table.get(key);
		return value == null ? null : value.asArray();
	}

	/**
	 * Gets one required child table.
	 *
	 * @param table   the owning table
	 * @param key     the table key
	 * @param context the human-readable context
	 *
	 * @return the child table
	 */
	private TomlTable requireTable(TomlTable table, String key, String context)
	{
		var value = table.get(key);

		if (value == null)
		{
			throw new IllegalArgumentException("Missing table '" + key + "' in " + context);
		}

		return value.asTable();
	}

	/**
	 * Gets one required string.
	 *
	 * @param table   the owning table
	 * @param key     the string key
	 * @param context the human-readable context
	 *
	 * @return the parsed string
	 */
	private String requireString(TomlTable table, String key, String context)
	{
		var value = optionalString(table, key);

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
	 * @param key   the string key
	 *
	 * @return the parsed string, or {@code null}
	 */
	private String optionalString(TomlTable table, String key)
	{
		var value = table.get(key);

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
	 * @param key   the integer key
	 *
	 * @return the parsed integer, or {@code null}
	 */
	private Integer optionalInteger(TomlTable table, String key)
	{
		var value = table.get(key);

		if (value == null)
		{
			return null;
		}

		var primitive = value.asPrimitive();
		return Math.toIntExact(primitive.asLong());
	}

	/**
	 * Checks whether one TOML value is the boolean literal `true`.
	 *
	 * @param value the raw TOML value
	 *
	 * @return {@code true} when the value is boolean true
	 */
	private boolean isBooleanTrue(TomlValue value)
	{
		try
		{
			return value.asPrimitive().asBoolean();
		}
		catch (RuntimeException ignored)
		{
			return false;
		}
	}

	/**
	 * Parses one configured repository token.
	 *
	 * @param value the configured repository token
	 *
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

	/**
	 * Parsed external dependency entry.
	 *
	 * @param notation   the Maven coordinate notation
	 * @param repository the owning repository URI
	 */
	private record ParsedDependency(
			String notation,
			URI repository
	)
	{
	}
}
