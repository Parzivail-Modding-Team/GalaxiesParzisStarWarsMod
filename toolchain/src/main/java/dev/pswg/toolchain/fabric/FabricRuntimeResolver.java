package dev.pswg.toolchain.fabric;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
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
	 * The Fabric Maven repository.
	 */
	public static final URI FABRIC_MAVEN = URI.create("https://maven.fabricmc.net/");

	/**
	 * Loom's current development launch injector version.
	 *
	 * <p>This currently mirrors the vendored Loom runtime catalog.
	 */
	public static final String DEV_LAUNCH_INJECTOR_VERSION = "0.2.1+build.8";

	/**
	 * Loom's current Fabric log4j utility version.
	 *
	 * <p>This currently mirrors the vendored Loom runtime catalog.
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
	 * Resolves the client-side Fabric development runtime artifacts for a loader version.
	 *
	 * @param loaderVersion the Fabric Loader version
	 * @param refresh whether to force a fresh download
	 * @return the resolved runtime artifact bundle
	 * @throws IOException if runtime metadata or jars cannot be resolved
	 */
	public FabricRuntimeArtifacts resolveClientRuntime(String loaderVersion, boolean refresh) throws IOException
	{
		Path loaderJar = _artifactResolver.resolve(
			MavenCoordinate.parse("net.fabricmc:fabric-loader:" + loaderVersion),
			FABRIC_MAVEN,
			refresh
		);
		JsonNode installerMetadata = readInstallerMetadata(loaderJar);
		Set<Path> classpath = new LinkedHashSet<>();
		classpath.add(_artifactResolver.resolve(
			MavenCoordinate.parse("net.fabricmc:dev-launch-injector:" + DEV_LAUNCH_INJECTOR_VERSION),
			FABRIC_MAVEN,
			refresh
		));
		classpath.add(_artifactResolver.resolve(
			MavenCoordinate.parse("net.fabricmc:fabric-log4j-util:" + FABRIC_LOG4J_UTIL_VERSION),
			FABRIC_MAVEN,
			refresh
		));
		classpath.add(loaderJar);
		classpath.addAll(resolveLibraries(installerMetadata.path("libraries").path("common"), refresh));
		classpath.addAll(resolveLibraries(installerMetadata.path("libraries").path("client"), refresh));
		classpath.addAll(resolveLibraries(installerMetadata.path("libraries").path("development"), refresh));

		Path mixinJavaAgentJar = resolveMixinJavaAgent(installerMetadata, refresh);
		String runtimeMainClass = installerMetadata.path("mainClass").path("client").asText(
			FabricDevLaunchInspector.DEFAULT_CLIENT_MAIN_CLASS
		);

		return new FabricRuntimeArtifacts(
			runtimeMainClass,
			List.copyOf(classpath),
			mixinJavaAgentJar
		);
	}

	/**
	 * Resolves the declared installer libraries in order.
	 *
	 * @param librariesNode the installer library array
	 * @param refresh whether to force a fresh download
	 * @return the resolved library jars
	 * @throws IOException if a library cannot be downloaded
	 */
	private List<Path> resolveLibraries(JsonNode librariesNode, boolean refresh) throws IOException
	{
		List<Path> paths = new ArrayList<>();

		if (!librariesNode.isArray())
		{
			return paths;
		}

		for (JsonNode libraryNode : librariesNode)
		{
			String notation = libraryNode.path("name").asText(null);

			if (notation == null || notation.isBlank())
			{
				continue;
			}

			String repositoryUrl = libraryNode.path("url").asText(FABRIC_MAVEN.toString());
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
	 * @param refresh whether to force a fresh download
	 * @return the resolved Mixin javaagent jar, or {@code null}
	 * @throws IOException if the jar cannot be downloaded
	 */
	private Path resolveMixinJavaAgent(JsonNode installerMetadata, boolean refresh) throws IOException
	{
		JsonNode commonLibraries = installerMetadata.path("libraries").path("common");

		if (!commonLibraries.isArray())
		{
			return null;
		}

		for (JsonNode libraryNode : commonLibraries)
		{
			String notation = libraryNode.path("name").asText("");

			if (!notation.startsWith("net.fabricmc:sponge-mixin:"))
			{
				continue;
			}

			String repositoryUrl = libraryNode.path("url").asText(FABRIC_MAVEN.toString());
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
	 * @return the parsed installer metadata JSON
	 * @throws IOException if the metadata cannot be read
	 */
	private JsonNode readInstallerMetadata(Path loaderJar) throws IOException
	{
		try (InputStream inputStream = java.nio.file.Files.newInputStream(loaderJar);
		     ZipInputStream zipInputStream = new ZipInputStream(inputStream))
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
