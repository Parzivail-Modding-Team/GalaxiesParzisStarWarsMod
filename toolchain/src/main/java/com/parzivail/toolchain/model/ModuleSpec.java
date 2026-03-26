package com.parzivail.toolchain.model;

import com.parzivail.toolchain.path.ModulePaths;

import java.net.URI;
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
	 * The declared aggregate module members.
	 */
	private final List<String> _aggregateMembers;

	/**
	 * The declared annotation processor module dependencies.
	 */
	private final List<String> _annotationProcessors;

	/**
	 * The annotation processor classes contributed by this module when other modules place it on
	 * their processor path.
	 */
	private final List<String> _providedAnnotationProcessorClasses;

	/**
	 * The generated source roots.
	 */
	private final List<Path> _generatedSources;

	/**
	 * The generated client source roots.
	 */
	private final List<Path> _generatedClientSources;

	/**
	 * The declared mixin configuration files.
	 */
	private final List<Path> _mixins;

	/**
	 * The declared external compile Maven dependencies.
	 */
	private final List<MavenDependencySpec> _compileDependencies;

	/**
	 * The declared external client-only compile Maven dependencies.
	 */
	private final List<MavenDependencySpec> _clientDependencies;

	/**
	 * The declared external annotation processor Maven dependencies.
	 */
	private final List<MavenDependencySpec> _annotationProcessorDependencies;

	/**
	 * The declared external runtime Maven dependencies.
	 */
	private final List<MavenDependencySpec> _runtimeDependencies;

	/**
	 * The optional Fabric mod descriptor path.
	 */
	private Path _fabricModJson;

	/**
	 * The optional Fabric mod identifier exposed by this module.
	 */
	private String _fabricModId;

	/**
	 * The optional packaged artifact identifier override.
	 */
	private String _artifactId;

	/**
	 * The optional checked-in datagen output path.
	 */
	private Path _datagenOutput;

	/**
	 * Creates a new mutable module declaration.
	 *
	 * @param id    the logical module identifier
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
		_aggregateMembers = new ArrayList<>();
		_annotationProcessors = new ArrayList<>();
		_providedAnnotationProcessorClasses = new ArrayList<>();
		_generatedSources = new ArrayList<>();
		_generatedClientSources = new ArrayList<>();
		_mixins = new ArrayList<>();
		_compileDependencies = new ArrayList<>();
		_clientDependencies = new ArrayList<>();
		_annotationProcessorDependencies = new ArrayList<>();
		_runtimeDependencies = new ArrayList<>();
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
	 * Gets the declared aggregate module members.
	 *
	 * @return the immutable aggregate member identifiers
	 */
	public List<String> aggregateMembers()
	{
		return List.copyOf(_aggregateMembers);
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
	 * Gets the annotation processor classes provided by this module.
	 *
	 * @return the immutable processor class names
	 */
	public List<String> providedAnnotationProcessorClasses()
	{
		return List.copyOf(_providedAnnotationProcessorClasses);
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
	 * Gets the generated client source roots.
	 *
	 * @return the immutable generated client source roots
	 */
	public List<Path> generatedClientSources()
	{
		return List.copyOf(_generatedClientSources);
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
	 * Gets the declared external runtime Maven dependencies.
	 *
	 * @return the immutable runtime Maven dependencies
	 */
	public List<MavenDependencySpec> runtimeDependencies()
	{
		return List.copyOf(_runtimeDependencies);
	}

	/**
	 * Gets the declared external compile Maven dependencies.
	 *
	 * @return the immutable compile Maven dependencies
	 */
	public List<MavenDependencySpec> compileDependencies()
	{
		return List.copyOf(_compileDependencies);
	}

	/**
	 * Gets the declared external client-only compile Maven dependencies.
	 *
	 * @return the immutable client-only compile Maven dependencies
	 */
	public List<MavenDependencySpec> clientDependencies()
	{
		return List.copyOf(_clientDependencies);
	}

	/**
	 * Gets the declared external annotation processor Maven dependencies.
	 *
	 * @return the immutable annotation processor Maven dependencies
	 */
	public List<MavenDependencySpec> annotationProcessorDependencies()
	{
		return List.copyOf(_annotationProcessorDependencies);
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
	 * Gets the optional Fabric mod identifier.
	 *
	 * @return the Fabric mod identifier, or {@code null}
	 */
	public String fabricModId()
	{
		return _fabricModId;
	}

	/**
	 * Gets the packaged artifact identifier.
	 *
	 * @return the packaged artifact identifier
	 */
	public String artifactId()
	{
		if (_artifactId == null || _artifactId.isBlank())
		{
			return _id;
		}

		return _artifactId;
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
	 *
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
	 *
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
	 *
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
	 *
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
	 *
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
	 *
	 * @return this module specification
	 */
	public ModuleSpec dependency(String dependencyId)
	{
		_dependencies.add(dependencyId);
		return this;
	}

	/**
	 * Adds an aggregate module member.
	 *
	 * <p>Aggregate members are packaged and launched alongside this module by
	 * the bespoke toolchain, but they are not ordinary compile-time dependency
	 * edges in the module graph.
	 *
	 * @param memberId the aggregate member identifier
	 *
	 * @return this module specification
	 */
	public ModuleSpec aggregateMember(String memberId)
	{
		_aggregateMembers.add(memberId);
		return this;
	}

	/**
	 * Adds an annotation processor dependency.
	 *
	 * @param processorId the processor module identifier
	 *
	 * @return this module specification
	 */
	public ModuleSpec annotationProcessor(String processorId)
	{
		_annotationProcessors.add(processorId);
		return this;
	}

	/**
	 * Declares an annotation processor class provided by this module.
	 *
	 * @param className the processor class name
	 *
	 * @return this module specification
	 */
	public ModuleSpec providedAnnotationProcessorClass(String className)
	{
		_providedAnnotationProcessorClasses.add(className);
		return this;
	}

	/**
	 * Adds a generated source root.
	 *
	 * @param path the generated source root
	 *
	 * @return this module specification
	 */
	public ModuleSpec generatedSources(Path path)
	{
		_generatedSources.add(path);
		return this;
	}

	/**
	 * Adds a generated client source root.
	 *
	 * @param path the generated client source root
	 *
	 * @return this module specification
	 */
	public ModuleSpec generatedClientSources(Path path)
	{
		_generatedClientSources.add(path);
		return this;
	}

	/**
	 * Declares a mixin configuration file.
	 *
	 * @param path the mixin configuration file
	 *
	 * @return this module specification
	 */
	public ModuleSpec mixin(Path path)
	{
		_mixins.add(path);
		return this;
	}

	/**
	 * Declares an external compile Maven dependency.
	 *
	 * @param notation   the dependency coordinate notation
	 * @param repository the repository that serves the dependency
	 *
	 * @return this module specification
	 */
	public ModuleSpec compileDependency(String notation, URI repository)
	{
		_compileDependencies.add(new MavenDependencySpec(notation, repository));
		return this;
	}

	/**
	 * Declares an external client-only compile Maven dependency.
	 *
	 * @param notation   the dependency coordinate notation
	 * @param repository the repository that serves the dependency
	 *
	 * @return this module specification
	 */
	public ModuleSpec clientDependency(String notation, URI repository)
	{
		_clientDependencies.add(new MavenDependencySpec(notation, repository));
		return this;
	}

	/**
	 * Declares an external annotation processor Maven dependency.
	 *
	 * @param notation   the dependency coordinate notation
	 * @param repository the repository that serves the dependency
	 *
	 * @return this module specification
	 */
	public ModuleSpec annotationProcessorDependency(String notation, URI repository)
	{
		_annotationProcessorDependencies.add(new MavenDependencySpec(notation, repository));
		return this;
	}

	/**
	 * Declares an external runtime Maven dependency.
	 *
	 * @param notation   the dependency coordinate notation
	 * @param repository the repository that serves the dependency
	 *
	 * @return this module specification
	 */
	public ModuleSpec runtimeDependency(String notation, URI repository)
	{
		_runtimeDependencies.add(new MavenDependencySpec(notation, repository));
		return this;
	}

	/**
	 * Declares the Fabric mod descriptor path.
	 *
	 * @param path the descriptor path
	 *
	 * @return this module specification
	 */
	public ModuleSpec fabricModJson(Path path)
	{
		_fabricModJson = path;
		return this;
	}

	/**
	 * Declares the Fabric mod identifier.
	 *
	 * @param id the Fabric mod identifier
	 *
	 * @return this module specification
	 */
	public ModuleSpec fabricModId(String id)
	{
		_fabricModId = id;
		return this;
	}

	/**
	 * Declares the packaged artifact identifier.
	 *
	 * @param id the packaged artifact identifier
	 *
	 * @return this module specification
	 */
	public ModuleSpec artifactId(String id)
	{
		_artifactId = id;
		return this;
	}

	/**
	 * Declares the checked-in datagen output path.
	 *
	 * @param path the datagen output path
	 *
	 * @return this module specification
	 */
	public ModuleSpec datagenOutput(Path path)
	{
		_datagenOutput = path;
		return this;
	}
}
