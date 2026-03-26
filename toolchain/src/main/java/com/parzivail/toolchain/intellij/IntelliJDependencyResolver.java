package com.parzivail.toolchain.intellij;

import com.parzivail.toolchain.fabric.FabricRuntimeResolver;
import com.parzivail.toolchain.fabric.MavenArtifactResolver;
import com.parzivail.toolchain.fabric.MavenCoordinate;
import com.parzivail.toolchain.model.BuildGraph;
import com.parzivail.toolchain.model.MavenDependencySpec;
import com.parzivail.toolchain.model.ModuleSpec;
import com.parzivail.toolchain.model.SourceSetNames;
import com.parzivail.toolchain.mojang.MojangMetadataClient;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.runtime.LaunchEnvironment;
import com.parzivail.toolchain.source.MinecraftSourcesGenerator;
import com.parzivail.toolchain.util.ToolchainLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
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
	 * @param projectName the IntelliJ project name
	 * @param graph       the authoritative build graph
	 * @param module      the target module
	 * @param refresh     whether to refresh external artifact resolution
	 *
	 * @return the ordered processor path entries
	 *
	 * @throws IOException if external artifacts cannot be resolved
	 */
	public List<Path> resolveAnnotationProcessorModulePath(
			String projectName,
			BuildGraph graph,
			ModuleSpec module,
			boolean refresh
	) throws IOException
	{
		Set<Path> entries = new LinkedHashSet<>();

		for (var processorId : module.annotationProcessors())
		{
			var processorModule = requireModule(graph, processorId);
			entries.add(ToolchainPaths.INTELLIJ_OUTPUT_DIRECTORY.resolve(IntelliJModuleNames.sourceSetModuleName(projectName, processorId, SourceSetNames.MAIN)));

			for (var dependencyId : processorModule.dependencies())
			{
				entries.add(ToolchainPaths.INTELLIJ_OUTPUT_DIRECTORY.resolve(IntelliJModuleNames.sourceSetModuleName(projectName, dependencyId, SourceSetNames.MAIN)));
			}

			entries.addAll(resolveExternalDependencies(processorModule.compileDependencies(), refresh));
			entries.addAll(resolveExternalDependencies(processorModule.annotationProcessorDependencies(), refresh));
		}

		return List.copyOf(entries);
	}

	/**
	 * Resolves external Maven dependencies into cached artifact paths.
	 *
	 * @param dependencies the declared dependencies
	 * @param refresh      whether to refresh external artifact resolution
	 *
	 * @return the resolved artifact paths
	 *
	 * @throws IOException if an artifact cannot be resolved
	 */
	public List<Path> resolveExternalDependencies(
			List<MavenDependencySpec> dependencies,
			boolean refresh
	) throws IOException
	{
		List<Path> paths = new ArrayList<>();

		for (var dependency : dependencies)
		{
			var artifact = _artifactResolver.resolve(
					MavenCoordinate.parse(dependency.notation()),
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
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 * @param module        the module specification
	 * @param includeClient whether client-only declared dependencies should be included
	 *
	 * @return the ordered IntelliJ-visible library artifacts
	 *
	 * @throws IOException if dependency resolution fails
	 */
	public Set<Path> resolveModuleLibraries(
			BuildGraph graph,
			String loaderVersion,
			boolean refresh,
			ModuleSpec module,
			boolean includeClient
	) throws IOException
	{
		Set<Path> dependencies = new LinkedHashSet<>();
		Set<Path> declaredCompileDependencies = new LinkedHashSet<>(expandIntelliJLibraryArtifacts(
				resolveExternalDependencies(module.compileDependencies(), refresh)
		));

		dependencies.addAll(expandIntelliJLibraryArtifacts(
				resolveImplicitCompileDependencies(
						graph,
						loaderVersion,
						refresh,
						module,
						declaredCompileDependencies,
						includeClient
				)
		));
		dependencies.addAll(declaredCompileDependencies);

		if (includeClient)
		{
			dependencies.addAll(expandIntelliJLibraryArtifacts(
					resolveExternalDependencies(module.clientDependencies(), refresh)
			));
		}

		return dependencies;
	}

	/**
	 * Resolves all project library artifacts implied by the current graph.
	 *
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh external artifact resolution
	 *
	 * @return the unique IntelliJ-visible library artifacts
	 *
	 * @throws IOException if dependency resolution fails
	 */
	public Set<Path> resolveProjectLibraries(
			BuildGraph graph,
			String loaderVersion,
			boolean refresh
	) throws IOException
	{
		Set<Path> resolvedArtifacts = new LinkedHashSet<>();

		for (var module : graph.modules())
		{
			ToolchainLog.info("idea", "Resolving libraries for module " + module.id());
			resolvedArtifacts.addAll(resolveModuleLibraries(graph, loaderVersion, refresh, module, false));
			resolvedArtifacts.addAll(resolveModuleLibraries(graph, loaderVersion, refresh, module, true));
		}

		return resolvedArtifacts;
	}

	/**
	 * Resolves the implicit platform compile dependencies for a module.
	 *
	 * @param graph         the authoritative build graph
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to refresh downloaded artifacts
	 * @param module        the module specification
	 *
	 * @return the implicit compile artifacts
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private Set<Path> resolveImplicitCompileDependencies(
			BuildGraph graph,
			String loaderVersion,
			boolean refresh,
			ModuleSpec module,
			Collection<Path> declaredCompileDependencies,
			boolean includeClient
	) throws IOException
	{
		Set<Path> dependencies = new LinkedHashSet<>();

		if (module.fabricModJson() == null)
		{
			return dependencies;
		}

		var fabricDependencies = resolveFabricCompileDependencies(loaderVersion, refresh);
		Set<Path> modArtifacts = new LinkedHashSet<>(declaredCompileDependencies);
		modArtifacts.addAll(fabricDependencies);
		var localFabricModJsons = collectLocalFabricModJsons(graph, module);
		var minecraftDependencies = resolveMinecraftCompileDependencies(
				graph.minecraftVersion(),
				refresh,
				modArtifacts,
				localFabricModJsons,
				includeClient
		);

		dependencies.addAll(minecraftDependencies);
		dependencies.addAll(fabricDependencies);
		return dependencies;
	}

	/**
	 * Resolves the compile-time Minecraft jars needed for official-namespace modules.
	 *
	 * @param minecraftVersion the tracked Minecraft version
	 * @param refresh          whether to refresh downloaded artifacts
	 *
	 * @return the compile-time Minecraft jars
	 *
	 * @throws IOException if resolution fails
	 */
	private Set<Path> resolveMinecraftCompileDependencies(
			String minecraftVersion,
			boolean refresh,
			Collection<Path> modArtifacts,
			Collection<Path> localFabricModJsons,
			boolean includeClient
	) throws IOException
	{
		var cacheKey = minecraftVersion + "|" + includeClient + "|" + refresh + "|" + modArtifacts.hashCode() + "|" + localFabricModJsons.hashCode();
		var cached = _minecraftCompileDependenciesCache.get(cacheKey);

		if (cached != null)
		{
			return cached;
		}

		Set<Path> dependencies = new LinkedHashSet<>();
		var metadata = _mojangClient.getVersionMetadata(minecraftVersion, refresh);
		var minecraftCompileJar = includeClient
		                          ? _mojangClient.downloadClientJar(minecraftVersion, refresh)
		                          : _mojangClient.downloadServerJar(minecraftVersion, refresh);
		ToolchainLog.info(
				"transform",
				"Preparing transformed " + (includeClient ? "client" : "common/server") + " Minecraft compile jar for " + minecraftVersion
		);
		dependencies.add(
				_minecraftJarTransformer.transformMinecraftJar(
						minecraftVersion,
						minecraftCompileJar,
						modArtifacts,
						localFabricModJsons
				)
		);

		var mcDeps = MinecraftSourcesGenerator.resolveLibraries(_mojangClient, metadata, refresh);
		dependencies.addAll(mcDeps);

		var resolved = Set.copyOf(dependencies);
		_minecraftCompileDependenciesCache.put(cacheKey, resolved);
		return resolved;
	}

	/**
	 * Collects local Fabric mod metadata files whose interface injections should be reflected in the
	 * module-specific Minecraft compile jar.
	 *
	 * @param graph  the authoritative build graph
	 * @param module the module currently being compiled
	 *
	 * @return the local Fabric mod metadata files
	 */
	private Set<Path> collectLocalFabricModJsons(
			BuildGraph graph,
			ModuleSpec module
	)
	{
		Set<Path> paths = new LinkedHashSet<>();
		collectLocalFabricModJsons(graph, module.id(), paths, new LinkedHashSet<>());
		return paths;
	}

	/**
	 * Recursively collects local Fabric mod metadata files from a module dependency chain.
	 *
	 * @param graph    the authoritative build graph
	 * @param moduleId the module identifier to inspect
	 * @param paths    the accumulated metadata paths
	 * @param visited  the visited module identifiers
	 */
	private void collectLocalFabricModJsons(
			BuildGraph graph,
			String moduleId,
			Set<Path> paths,
			Set<String> visited
	)
	{
		if (!visited.add(moduleId))
		{
			return;
		}

		var candidate = requireModule(graph, moduleId);

		if (candidate.fabricModJson() != null)
		{
			paths.add(ToolchainPaths.PROJECT_ROOT.resolve(candidate.fabricModJson()));
		}

		for (var dependencyId : candidate.dependencies())
		{
			collectLocalFabricModJsons(graph, dependencyId, paths, visited);
		}
	}

	/**
	 * Resolves the compile-time Fabric jars needed for Fabric-backed modules.
	 *
	 * @param loaderVersion the tracked Fabric Loader version
	 * @param refresh       whether to refresh downloaded artifacts
	 *
	 * @return the compile-time Fabric jars
	 *
	 * @throws IOException if resolution fails
	 */
	private Set<Path> resolveFabricCompileDependencies(String loaderVersion, boolean refresh) throws IOException
	{
		var cacheKey = loaderVersion + "|" + refresh;
		var cached = _fabricCompileDependenciesCache.get(cacheKey);

		if (cached != null)
		{
			return cached;
		}

		Set<Path> dependencies = new LinkedHashSet<>();

		if (loaderVersion == null || loaderVersion.isBlank())
		{
			return dependencies;
		}

		var runtimeArtifacts = _fabricRuntimeResolver.resolveRuntime(
				loaderVersion,
				refresh,
				LaunchEnvironment.CLIENT
		);
		dependencies.addAll(runtimeArtifacts.classpath());
		var resolved = Set.copyOf(dependencies);
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
	 *
	 * @return the IntelliJ-visible classpath artifacts
	 *
	 * @throws IOException if nested jars cannot be extracted
	 */
	private Set<Path> expandIntelliJLibraryArtifacts(Collection<Path> artifacts) throws IOException
	{
		Set<Path> expandedArtifacts = new LinkedHashSet<>();

		for (var artifact : artifacts)
		{
			// Keep the container jar itself on IntelliJ's classpath. Some Fabric jars expose API classes
			// directly from the outer archive while also nesting implementation shards under META-INF/jars.
			var nestedArtifacts = _expandedLibraryArtifactsCache.get(artifact);

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
	 *
	 * @return the extracted nested jars, or an empty list if the artifact is a normal jar
	 *
	 * @throws IOException if extraction fails
	 */
	private List<Path> extractNestedClasspathJars(Path artifact) throws IOException
	{
		if (!artifact.getFileName().toString().endsWith(".jar"))
		{
			return List.of();
		}

		if (isMinecraftCompileJar(artifact))
		{
			return List.of();
		}

		List<Path> nestedArtifacts = new ArrayList<>();
		var extractionRoot = artifact.getParent().resolve(".intellij-exploded").resolve(projectLibraryName(artifact));

		try (var inputStream = Files.newInputStream(artifact);
		     var zipInputStream = new ZipInputStream(inputStream))
		{
			ZipEntry entry;

			while ((entry = zipInputStream.getNextEntry()) != null)
			{
				if (entry.isDirectory() || !entry.getName().startsWith("META-INF/jars/") || !entry.getName().endsWith(".jar"))
				{
					continue;
				}

				var target = extractionRoot.resolve(Path.of(entry.getName()).getFileName().toString());
				Files.createDirectories(target.getParent());

				try (var outputStream = Files.newOutputStream(
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
	 * Checks whether an artifact is one of the IntelliJ-facing Minecraft compile jars.
	 *
	 * <p>These jars are already fully materialized by the toolchain and do not need Fabric-style
	 * nested-jar expansion.
	 *
	 * @param artifact the candidate artifact
	 *
	 * @return whether the artifact is a Minecraft compile jar
	 */
	private boolean isMinecraftCompileJar(Path artifact)
	{
		var fileName = artifact.getFileName().toString();
		return fileName.startsWith("minecraft-client-") || fileName.startsWith("minecraft-server-") || fileName.startsWith("server-extracted-");
	}

	/**
	 * Resolves a module from the authoritative graph.
	 *
	 * @param graph    the authoritative build graph
	 * @param moduleId the module identifier
	 *
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
	 *
	 * @return the library name
	 */
	private String projectLibraryName(Path artifact)
	{
		var fileName = artifact.getFileName().toString();
		return fileName.endsWith(".jar") ? fileName.substring(0, fileName.length() - 4) : fileName;
	}
}
