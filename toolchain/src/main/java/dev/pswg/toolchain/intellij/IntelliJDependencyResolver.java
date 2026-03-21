package dev.pswg.toolchain.intellij;

import dev.pswg.toolchain.fabric.FabricRuntimeArtifacts;
import dev.pswg.toolchain.fabric.FabricRuntimeResolver;
import dev.pswg.toolchain.fabric.MavenArtifactResolver;
import dev.pswg.toolchain.fabric.MavenCoordinate;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.MavenDependencySpec;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.model.SourceSetNames;
import dev.pswg.toolchain.mojang.MojangMetadataClient;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadata;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataLibrary;
import dev.pswg.toolchain.util.ToolchainLog;

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
 * Resolves the IntelliJ-facing compile and processor classpaths implied by the toolchain graph.
 *
 * <p>This class intentionally centralizes the places where the toolchain still has to emulate
 * Gradle/Loom-era behavior, such as assembling implicit Minecraft/Fabric compile baselines and
 * exploding container jars like Fabric API for IntelliJ consumption.
 */
public final class IntelliJDependencyResolver
{
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
	 * Applies compile-time class tweaker transformations to Minecraft jars before IntelliJ consumes them.
	 */
	private final IntelliJMinecraftJarTransformer _minecraftJarTransformer;

	/**
	 * Per-run cache of Mojang compile dependencies keyed by Minecraft version and refresh mode.
	 */
	private final Map<String, Set<Path>> _minecraftCompileDependenciesCache;

	/**
	 * Per-run cache of Fabric compile dependencies keyed by loader version and refresh mode.
	 */
	private final Map<String, Set<Path>> _fabricCompileDependenciesCache;

	/**
	 * Creates a new IntelliJ dependency resolver.
	 */
	public IntelliJDependencyResolver()
	{
		_artifactResolver = new MavenArtifactResolver();
		_mojangClient = new MojangMetadataClient();
		_fabricRuntimeResolver = new FabricRuntimeResolver();
		_expandedLibraryArtifactsCache = new LinkedHashMap<>();
		_minecraftCompileDependenciesCache = new LinkedHashMap<>();
		_fabricCompileDependenciesCache = new LinkedHashMap<>();
		_minecraftJarTransformer = new IntelliJMinecraftJarTransformer();
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
	public List<Path> resolveAnnotationProcessorModulePath(
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
			                      .resolve(IntelliJModuleNames.sourceSetModuleName(projectName, processorId, SourceSetNames.MAIN)));

			for (String dependencyId : processorModule.dependencies())
			{
				entries.add(projectRoot.resolve("out")
				                      .resolve("production")
				                      .resolve(IntelliJModuleNames.sourceSetModuleName(projectName, dependencyId, SourceSetNames.MAIN)));
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
	public List<Path> resolveExternalDependencies(
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
	 * Resolves the full IntelliJ-visible library set for a module source set.
	 *
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @param module the module specification
	 * @param includeClient whether client-only declared dependencies should be included
	 * @return the ordered IntelliJ-visible library artifacts
	 * @throws IOException if dependency resolution fails
	 */
	public Set<Path> resolveModuleLibraries(
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh,
		ModuleSpec module,
		boolean includeClient
	) throws IOException
	{
		Set<Path> dependencies = new LinkedHashSet<>();
		Set<Path> declaredCompileDependencies = new LinkedHashSet<>(expandIntelliJLibraryArtifacts(
			resolveExternalDependencies(module.compileDependencies(), gradleProperties, refresh)
		));

		dependencies.addAll(expandIntelliJLibraryArtifacts(
			resolveImplicitCompileDependencies(graph, gradleProperties, refresh, module, declaredCompileDependencies)
		));
		dependencies.addAll(declaredCompileDependencies);

		if (includeClient)
		{
			dependencies.addAll(expandIntelliJLibraryArtifacts(
				resolveExternalDependencies(module.clientDependencies(), gradleProperties, refresh)
			));
		}

		return dependencies;
	}

	/**
	 * Resolves all project library artifacts implied by the current graph.
	 *
	 * @param graph the authoritative build graph
	 * @param gradleProperties the tracked Gradle properties
	 * @param refresh whether to refresh external artifact resolution
	 * @return the unique IntelliJ-visible library artifacts
	 * @throws IOException if dependency resolution fails
	 */
	public Set<Path> resolveProjectLibraries(
		BuildGraph graph,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		Set<Path> resolvedArtifacts = new LinkedHashSet<>();

		for (ModuleSpec module : graph.modules())
		{
			ToolchainLog.info("idea", "Resolving libraries for module " + module.id());
			resolvedArtifacts.addAll(resolveModuleLibraries(graph, gradleProperties, refresh, module, true));
		}

		return resolvedArtifacts;
	}

	/**
	 * Applies simple Gradle-style property substitution to a notation string.
	 *
	 * @param value the raw notation value
	 * @param properties the available properties
	 * @return the substituted notation value
	 */
	public String substituteProperties(String value, Properties properties)
	{
		String substituted = value;

		for (String propertyName : properties.stringPropertyNames())
		{
			substituted = substituted.replace("${" + propertyName + "}", properties.getProperty(propertyName));
		}

		return substituted;
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
		ModuleSpec module,
		Collection<Path> declaredCompileDependencies
	) throws IOException
	{
		Set<Path> dependencies = new LinkedHashSet<>();

		if (module.fabricModJson() == null)
		{
			return dependencies;
		}

		Set<Path> fabricDependencies = resolveFabricCompileDependencies(gradleProperties.getProperty("loader_version"), refresh);
		Set<Path> modArtifacts = new LinkedHashSet<>(declaredCompileDependencies);
		modArtifacts.addAll(fabricDependencies);
		Set<Path> minecraftDependencies = resolveMinecraftCompileDependencies(
			graph.minecraftVersion(),
			refresh,
			modArtifacts
		);

		dependencies.addAll(minecraftDependencies);
		dependencies.addAll(fabricDependencies);
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
	private Set<Path> resolveMinecraftCompileDependencies(
		String minecraftVersion,
		boolean refresh,
		Collection<Path> modArtifacts
	) throws IOException
	{
		String cacheKey = minecraftVersion + "|" + refresh + "|" + modArtifacts.hashCode();
		Set<Path> cached = _minecraftCompileDependenciesCache.get(cacheKey);

		if (cached != null)
		{
			return cached;
		}

		Set<Path> dependencies = new LinkedHashSet<>();
		MojangVersionMetadata metadata = _mojangClient.getVersionMetadata(minecraftVersion, refresh);
		Path clientJar = _mojangClient.downloadClientJar(minecraftVersion, refresh);
		ToolchainLog.info("transform", "Preparing transformed Minecraft compile jar for " + minecraftVersion);
		dependencies.add(_minecraftJarTransformer.transformMinecraftJar(minecraftVersion, clientJar, modArtifacts));

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
	 * <p>The outer artifact is still retained on the classpath because jars like Fabric Loader contain
	 * their own public API classes in addition to nested helper jars.
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
			// Keep the container jar itself on IntelliJ's classpath. Some Fabric jars expose API classes
			// directly from the outer archive while also nesting implementation shards under META-INF/jars.
			Set<Path> nestedArtifacts = _expandedLibraryArtifactsCache.get(artifact);

			if (nestedArtifacts == null)
			{
				nestedArtifacts = Set.copyOf(extractNestedClasspathJars(artifact));
				_expandedLibraryArtifactsCache.put(artifact, nestedArtifacts);
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
}
