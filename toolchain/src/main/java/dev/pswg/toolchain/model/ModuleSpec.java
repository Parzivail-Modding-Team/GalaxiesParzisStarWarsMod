package dev.pswg.toolchain.model;

import dev.pswg.toolchain.path.ModulePaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Mutable module declaration that accumulates passive graph facts.
 */
public final class ModuleSpec
{
	/**
	 * The logical module identifier.
	 */
	private final String _id;

	/**
	 * The path helper rooted at this module.
	 */
	private final ModulePaths _paths;

	/**
	 * The declared Java language version.
	 */
	private int _javaVersion;

	/**
	 * The main Java source roots.
	 */
	private final List<Path> _mainSources;

	/**
	 * The client Java source roots.
	 */
	private final List<Path> _clientSources;

	/**
	 * The main resource roots.
	 */
	private final List<Path> _mainResources;

	/**
	 * The client resource roots.
	 */
	private final List<Path> _clientResources;

	/**
	 * The declared logical module dependencies.
	 */
	private final List<String> _dependencies;

	/**
	 * The declared annotation processor module dependencies.
	 */
	private final List<String> _annotationProcessors;

	/**
	 * The generated source roots.
	 */
	private final List<Path> _generatedSources;

	/**
	 * The declared mixin configuration files.
	 */
	private final List<Path> _mixins;

	/**
	 * The optional Fabric mod descriptor path.
	 */
	private Path _fabricModJson;

	/**
	 * The optional checked-in datagen output path.
	 */
	private Path _datagenOutput;

	/**
	 * Creates a new mutable module declaration.
	 *
	 * @param id the logical module identifier
	 * @param paths the rooted module path helper
	 */
	public ModuleSpec(String id, ModulePaths paths)
	{
		_id = id;
		_paths = paths;
		_mainSources = new ArrayList<>();
		_clientSources = new ArrayList<>();
		_mainResources = new ArrayList<>();
		_clientResources = new ArrayList<>();
		_dependencies = new ArrayList<>();
		_annotationProcessors = new ArrayList<>();
		_generatedSources = new ArrayList<>();
		_mixins = new ArrayList<>();
	}

	/**
	 * Gets the logical module identifier.
	 *
	 * @return the module identifier
	 */
	public String id()
	{
		return _id;
	}

	/**
	 * Gets the rooted path helper for this module.
	 *
	 * @return the path helper
	 */
	public ModulePaths paths()
	{
		return _paths;
	}

	/**
	 * Gets the declared Java language version.
	 *
	 * @return the Java language version
	 */
	public int javaVersion()
	{
		return _javaVersion;
	}

	/**
	 * Gets the main Java source roots.
	 *
	 * @return the immutable main source roots
	 */
	public List<Path> mainSources()
	{
		return List.copyOf(_mainSources);
	}

	/**
	 * Gets the client Java source roots.
	 *
	 * @return the immutable client source roots
	 */
	public List<Path> clientSources()
	{
		return List.copyOf(_clientSources);
	}

	/**
	 * Gets the main resource roots.
	 *
	 * @return the immutable main resource roots
	 */
	public List<Path> mainResources()
	{
		return List.copyOf(_mainResources);
	}

	/**
	 * Gets the client resource roots.
	 *
	 * @return the immutable client resource roots
	 */
	public List<Path> clientResources()
	{
		return List.copyOf(_clientResources);
	}

	/**
	 * Gets the declared logical dependencies.
	 *
	 * @return the immutable dependency identifiers
	 */
	public List<String> dependencies()
	{
		return List.copyOf(_dependencies);
	}

	/**
	 * Gets the declared annotation processors.
	 *
	 * @return the immutable processor identifiers
	 */
	public List<String> annotationProcessors()
	{
		return List.copyOf(_annotationProcessors);
	}

	/**
	 * Gets the generated source roots.
	 *
	 * @return the immutable generated source roots
	 */
	public List<Path> generatedSources()
	{
		return List.copyOf(_generatedSources);
	}

	/**
	 * Gets the declared mixin configuration files.
	 *
	 * @return the immutable mixin configuration paths
	 */
	public List<Path> mixins()
	{
		return List.copyOf(_mixins);
	}

	/**
	 * Gets the optional Fabric mod descriptor path.
	 *
	 * @return the descriptor path, or {@code null}
	 */
	public Path fabricModJson()
	{
		return _fabricModJson;
	}

	/**
	 * Gets the optional checked-in datagen output path.
	 *
	 * @return the datagen output path, or {@code null}
	 */
	public Path datagenOutput()
	{
		return _datagenOutput;
	}

	/**
	 * Declares the Java language version.
	 *
	 * @param javaVersion the Java language version
	 * @return this module specification
	 */
	public ModuleSpec javaVersion(int javaVersion)
	{
		_javaVersion = javaVersion;
		return this;
	}

	/**
	 * Adds a main Java source root.
	 *
	 * @param path the source root
	 * @return this module specification
	 */
	public ModuleSpec mainSources(Path path)
	{
		_mainSources.add(path);
		return this;
	}

	/**
	 * Adds a client Java source root.
	 *
	 * @param path the source root
	 * @return this module specification
	 */
	public ModuleSpec clientSources(Path path)
	{
		_clientSources.add(path);
		return this;
	}

	/**
	 * Adds a main resource root.
	 *
	 * @param path the resource root
	 * @return this module specification
	 */
	public ModuleSpec mainResources(Path path)
	{
		_mainResources.add(path);
		return this;
	}

	/**
	 * Adds a client resource root.
	 *
	 * @param path the resource root
	 * @return this module specification
	 */
	public ModuleSpec clientResources(Path path)
	{
		_clientResources.add(path);
		return this;
	}

	/**
	 * Adds a logical dependency.
	 *
	 * @param dependencyId the dependency module identifier
	 * @return this module specification
	 */
	public ModuleSpec dependency(String dependencyId)
	{
		_dependencies.add(dependencyId);
		return this;
	}

	/**
	 * Adds an annotation processor dependency.
	 *
	 * @param processorId the processor module identifier
	 * @return this module specification
	 */
	public ModuleSpec annotationProcessor(String processorId)
	{
		_annotationProcessors.add(processorId);
		return this;
	}

	/**
	 * Adds a generated source root.
	 *
	 * @param path the generated source root
	 * @return this module specification
	 */
	public ModuleSpec generatedSources(Path path)
	{
		_generatedSources.add(path);
		return this;
	}

	/**
	 * Declares a mixin configuration file.
	 *
	 * @param path the mixin configuration file
	 * @return this module specification
	 */
	public ModuleSpec mixin(Path path)
	{
		_mixins.add(path);
		return this;
	}

	/**
	 * Declares the Fabric mod descriptor path.
	 *
	 * @param path the descriptor path
	 * @return this module specification
	 */
	public ModuleSpec fabricModJson(Path path)
	{
		_fabricModJson = path;
		return this;
	}

	/**
	 * Declares the checked-in datagen output path.
	 *
	 * @param path the datagen output path
	 * @return this module specification
	 */
	public ModuleSpec datagenOutput(Path path)
	{
		_datagenOutput = path;
		return this;
	}
}
