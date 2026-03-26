package com.parzivail.toolchain.fabric;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parzivail.toolchain.maven.ToolchainMavenRepositories;
import com.parzivail.toolchain.model.MavenDependencySpec;
import com.parzivail.toolchain.runtime.LaunchEnvironment;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Resolves the Fabric-side runtime artifacts needed for a development launch.
 */
public final class FabricRuntimeResolver
{
	/**
	 * Loom's current development launch injector version.
	 *
	 * <p>This mirrors the vendored Loom runtime catalog.
	 */
	public static final String DEV_LAUNCH_INJECTOR_VERSION = "0.2.1+build.8";

	/**
	 * Loom's current Fabric log4j utility version.
	 *
	 * <p>This mirrors the vendored Loom runtime catalog.
	 */
	public static final String FABRIC_LOG4J_UTIL_VERSION = "1.0.2";

	/**
	 * The shared JSON serializer.
	 */
	private final ObjectMapper _mapper;

	/**
	 * The Maven artifact resolver.
	 */
	private final MavenArtifactResolver _artifactResolver;

	/**
	 * Creates a new Fabric runtime resolver.
	 */
	public FabricRuntimeResolver()
	{
		_mapper = new ObjectMapper();
		_artifactResolver = new MavenArtifactResolver();
	}

	/**
	 * Resolves the Fabric development runtime artifacts for one environment and loader version.
	 *
	 * @param loaderVersion the Fabric Loader version
	 * @param refresh       whether to force a fresh download
	 * @param environment   the target launch environment
	 *
	 * @return the resolved runtime artifact bundle
	 *
	 * @throws IOException if runtime metadata or jars cannot be resolved
	 */
	public FabricRuntimeArtifacts resolveRuntime(
			String loaderVersion,
			boolean refresh,
			LaunchEnvironment environment
	) throws IOException
	{
		var loaderJar = _artifactResolver.resolve(
				MavenCoordinate.parse("net.fabricmc:fabric-loader:" + loaderVersion),
				ToolchainMavenRepositories.FABRIC,
				refresh
		);
		var installerMetadata = readInstallerMetadata(loaderJar);
		Set<Path> classpath = new LinkedHashSet<>();
		classpath.add(_artifactResolver.resolve(
				MavenCoordinate.parse("net.fabricmc:dev-launch-injector:" + DEV_LAUNCH_INJECTOR_VERSION),
				ToolchainMavenRepositories.FABRIC,
				refresh
		));
		classpath.add(_artifactResolver.resolve(
				MavenCoordinate.parse("net.fabricmc:fabric-log4j-util:" + FABRIC_LOG4J_UTIL_VERSION),
				ToolchainMavenRepositories.FABRIC,
				refresh
		));
		classpath.add(loaderJar);
		classpath.addAll(resolveLibraries(installerMetadata.path("libraries").path("common"), refresh));
		classpath.addAll(resolveLibraries(installerMetadata.path("libraries").path(environment.id()), refresh));
		classpath.addAll(resolveLibraries(installerMetadata.path("libraries").path("development"), refresh));

		var mixinJavaAgentJar = resolveMixinJavaAgent(installerMetadata, refresh);
		var runtimeMainClass = installerMetadata.path("mainClass").path(environment.id()).asText(
				defaultRuntimeMainClass(environment)
		);

		return new FabricRuntimeArtifacts(
				runtimeMainClass,
				List.copyOf(classpath),
				mixinJavaAgentJar
		);
	}

	/**
	 * Gets the fallback Fabric runtime main class for one environment.
	 *
	 * @param environment the target environment
	 *
	 * @return the fallback runtime main class
	 */
	private String defaultRuntimeMainClass(LaunchEnvironment environment)
	{
		return environment.isClient()
		       ? FabricDevLaunchInspector.DEFAULT_CLIENT_MAIN_CLASS
		       : FabricDevLaunchInspector.DEFAULT_SERVER_MAIN_CLASS;
	}

	/**
	 * Resolves a declared module runtime dependency against the provided properties.
	 *
	 * @param dependency the declared runtime dependency
	 * @param refresh    whether to force a fresh download
	 *
	 * @return the resolved dependency artifact
	 *
	 * @throws IOException if the artifact cannot be downloaded
	 */
	public Path resolveRuntimeDependency(
			MavenDependencySpec dependency,
			boolean refresh
	) throws IOException
	{
		return _artifactResolver.resolve(
				MavenCoordinate.parse(dependency.notation()),
				dependency.repository(),
				refresh
		);
	}

	/**
	 * Resolves the declared installer libraries in order.
	 *
	 * @param librariesNode the installer library array
	 * @param refresh       whether to force a fresh download
	 *
	 * @return the resolved library jars
	 *
	 * @throws IOException if a library cannot be downloaded
	 */
	private List<Path> resolveLibraries(JsonNode librariesNode, boolean refresh) throws IOException
	{
		List<Path> paths = new ArrayList<>();

		if (!librariesNode.isArray())
		{
			return paths;
		}

		for (var libraryNode : librariesNode)
		{
			var notation = libraryNode.path("name").asText(null);

			if (notation == null || notation.isBlank())
			{
				continue;
			}

			var repositoryUrl = libraryNode.path("url").asText(ToolchainMavenRepositories.FABRIC.toString());
			paths.add(_artifactResolver.resolve(
					MavenCoordinate.parse(notation),
					URI.create(repositoryUrl),
					refresh
			));
		}

		return paths;
	}

	/**
	 * Resolves the Mixin javaagent jar from installer metadata.
	 *
	 * @param installerMetadata the parsed Fabric installer metadata
	 * @param refresh           whether to force a fresh download
	 *
	 * @return the resolved Mixin javaagent jar, or {@code null}
	 *
	 * @throws IOException if the jar cannot be downloaded
	 */
	private Path resolveMixinJavaAgent(JsonNode installerMetadata, boolean refresh) throws IOException
	{
		var commonLibraries = installerMetadata.path("libraries").path("common");

		if (!commonLibraries.isArray())
		{
			return null;
		}

		for (var libraryNode : commonLibraries)
		{
			var notation = libraryNode.path("name").asText("");

			if (!notation.startsWith("net.fabricmc:sponge-mixin:"))
			{
				continue;
			}

			var repositoryUrl = libraryNode.path("url").asText(ToolchainMavenRepositories.FABRIC.toString());
			return _artifactResolver.resolve(
					MavenCoordinate.parse(notation),
					URI.create(repositoryUrl),
					refresh
			);
		}

		return null;
	}

	/**
	 * Reads the embedded Fabric installer metadata from the Fabric Loader jar.
	 *
	 * @param loaderJar the resolved loader jar
	 *
	 * @return the parsed installer metadata JSON
	 *
	 * @throws IOException if the metadata cannot be read
	 */
	private JsonNode readInstallerMetadata(Path loaderJar) throws IOException
	{
		try (var inputStream = java.nio.file.Files.newInputStream(loaderJar);
		     var zipInputStream = new ZipInputStream(inputStream))
		{
			ZipEntry entry;

			while ((entry = zipInputStream.getNextEntry()) != null)
			{
				if (!"fabric-installer.json".equals(entry.getName()))
				{
					continue;
				}

				return _mapper.readTree(zipInputStream);
			}
		}

		throw new IOException("Fabric installer metadata is missing from " + loaderJar);
	}
}
