package dev.pswg.toolchain.mojang;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.pswg.toolchain.mojang.model.MojangVersionManifest;
import dev.pswg.toolchain.mojang.model.MojangVersionManifestEntry;
import dev.pswg.toolchain.mojang.model.MojangAssetIndex;
import dev.pswg.toolchain.mojang.model.MojangAssetObject;
import dev.pswg.toolchain.mojang.model.MojangRule;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadata;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataLibrary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

/**
 * Resolves and caches Mojang launcher metadata used by the standalone toolchain.
 */
public final class MojangMetadataClient
{
	/**
	 * The official Mojang version manifest endpoint.
	 */
	public static final URI VERSION_MANIFEST_URI = URI.create("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");

	/**
	 * The shared JSON object mapper.
	 */
	private final ObjectMapper _mapper;

	/**
	 * The shared HTTP client.
	 */
	private final HttpClient _httpClient;

	/**
	 * The local toolchain cache paths.
	 */
	private final MojangPaths _paths;

	/**
	 * Creates a metadata client with default runtime services.
	 */
	public MojangMetadataClient()
	{
		_mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		_httpClient = HttpClient.newHttpClient();
		_paths = new MojangPaths();
	}

	/**
	 * Gets the cache path helper used by the metadata client.
	 *
	 * @return the cache path helper
	 */
	public MojangPaths paths()
	{
		return _paths;
	}

	/**
	 * Resolves the version manifest from cache or Mojang.
	 *
	 * @param refresh whether to force a fresh download
	 * @return the resolved version manifest
	 * @throws IOException if resolution fails
	 */
	public MojangVersionManifest getVersionManifest(boolean refresh) throws IOException
	{
		return readCachedJson(
			VERSION_MANIFEST_URI,
			_paths.versionManifestFile(),
			MojangVersionManifest.class,
			refresh
		);
	}

	/**
	 * Resolves a specific version entry from the version manifest.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force a fresh manifest download
	 * @return the resolved version entry
	 * @throws IOException if the version is missing or the manifest fails to resolve
	 */
	public MojangVersionManifestEntry getVersion(String versionId, boolean refresh) throws IOException
	{
		MojangVersionManifest manifest = getVersionManifest(refresh);

		return manifest.versions()
		               .stream()
		               .filter(version -> version.id().equals(versionId))
		               .findFirst()
		               .orElseThrow(() -> new IOException("Unknown Minecraft version: " + versionId));
	}

	/**
	 * Resolves a specific version metadata document from cache or Mojang.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force a fresh download
	 * @return the resolved version metadata
	 * @throws IOException if resolution fails
	 */
	public MojangVersionMetadata getVersionMetadata(String versionId, boolean refresh) throws IOException
	{
		MojangVersionManifestEntry version = getVersion(versionId, refresh);

		return readCachedJson(
			URI.create(version.url()),
			_paths.versionMetadataFile(versionId),
			MojangVersionMetadata.class,
			refresh
		);
	}

	/**
	 * Downloads the vanilla client jar for a resolved Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force a fresh download
	 * @return the cached client jar path
	 * @throws IOException if the jar cannot be downloaded
	 */
	public Path downloadClientJar(String versionId, boolean refresh) throws IOException
	{
		MojangVersionMetadata metadata = getVersionMetadata(versionId, refresh);
		Path target = _paths.clientJarFile(versionId);
		ensureCached(URI.create(metadata.downloads().client().url()), target, refresh);
		return target;
	}

	/**
	 * Downloads the asset index JSON for a resolved Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force a fresh download
	 * @return the cached asset index path
	 * @throws IOException if the asset index cannot be downloaded
	 */
	public Path downloadAssetIndex(String versionId, boolean refresh) throws IOException
	{
		MojangVersionMetadata metadata = getVersionMetadata(versionId, refresh);
		Path target = _paths.assetIndexFile(metadata.assetIndex().id());
		ensureCached(URI.create(metadata.assetIndex().url()), target, refresh);
		return target;
	}

	/**
	 * Downloads the runtime libraries and asset objects required by a selected version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force fresh downloads
	 * @return the runtime download summary
	 * @throws IOException if runtime files cannot be downloaded
	 */
	public RuntimeDownloadResult downloadRuntime(String versionId, boolean refresh) throws IOException
	{
		MojangVersionMetadata metadata = getVersionMetadata(versionId, refresh);
		Path assetIndexPath = downloadAssetIndex(versionId, refresh);
		MojangAssetIndex assetIndex = readCachedJson(
			URI.create(metadata.assetIndex().url()),
			assetIndexPath,
			MojangAssetIndex.class,
			false
		);

		int libraryCount = 0;

		for (MojangVersionMetadataLibrary library : metadata.libraries())
		{
			if (!isAllowed(library.rules()))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null)
			{
				continue;
			}

			Path target = _paths.libraryFile(library.downloads().artifact().path());
			ensureCached(URI.create(library.downloads().artifact().url()), target, refresh);
			libraryCount++;
		}

		int assetObjectCount = 0;

		for (Map.Entry<String, MojangAssetObject> entry : assetIndex.objects().entrySet())
		{
			MojangAssetObject object = entry.getValue();
			Path target = _paths.assetObjectFile(object.hash());
			String prefix = object.hash().substring(0, 2);
			URI source = URI.create("https://resources.download.minecraft.net/" + prefix + "/" + object.hash());
			ensureCached(source, target, refresh);
			assetObjectCount++;
		}

		return new RuntimeDownloadResult(
			libraryCount,
			assetObjectCount,
			_paths.librariesRoot(),
			_paths.assetObjectsRoot()
		);
	}

	/**
	 * Reads a JSON object from cache or downloads it into the cache first.
	 *
	 * @param sourceUri the source URI to fetch
	 * @param cacheFile the local cache file
	 * @param refresh whether to force a fresh download
	 * @return the parsed JSON object
	 * @throws IOException if the file cannot be read or downloaded
	 */
	private <T> T readCachedJson(URI sourceUri, Path cacheFile, Class<T> type, boolean refresh) throws IOException
	{
		ensureCached(sourceUri, cacheFile, refresh);

		try (InputStream inputStream = Files.newInputStream(cacheFile))
		{
			return _mapper.readValue(inputStream, type);
		}
	}

	/**
	 * Ensures a cache file exists and contains the latest requested document.
	 *
	 * @param sourceUri the source URI to fetch
	 * @param cacheFile the local cache file
	 * @param refresh whether to force a fresh download
	 * @throws IOException if the document cannot be downloaded
	 */
	private void ensureCached(URI sourceUri, Path cacheFile, boolean refresh) throws IOException
	{
		if (!refresh && Files.exists(cacheFile))
		{
			return;
		}

		Files.createDirectories(cacheFile.getParent());

		HttpRequest request = HttpRequest.newBuilder(sourceUri).GET().build();

		try
		{
			HttpResponse<Path> response = _httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofFile(cacheFile)
			);

			if (response.statusCode() / 100 != 2)
			{
				throw new IOException("Mojang request failed with HTTP " + response.statusCode() + " for " + sourceUri);
			}
		}
		catch (InterruptedException exception)
		{
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while downloading " + sourceUri, exception);
		}
	}

	/**
	 * Evaluates Mojang library rules for the current runtime environment.
	 *
	 * @param rules the optional rule list
	 * @return {@code true} if the library should be included
	 */
	private boolean isAllowed(java.util.List<MojangRule> rules)
	{
		if (rules == null || rules.isEmpty())
		{
			return true;
		}

		boolean allowed = false;

		for (MojangRule rule : rules)
		{
			if (!matches(rule))
			{
				continue;
			}

			if ("allow".equals(rule.action()))
			{
				allowed = true;
			}
			else if ("disallow".equals(rule.action()))
			{
				allowed = false;
			}
		}

		return allowed;
	}

	/**
	 * Checks whether a rule matches the current runtime environment.
	 *
	 * @param rule the rule to evaluate
	 * @return {@code true} if the rule matches
	 */
	private boolean matches(MojangRule rule)
	{
		if (rule == null || rule.os() == null)
		{
			return true;
		}

		String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
		String osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

		if (rule.os().name() != null)
		{
			String expectedOs = switch (rule.os().name())
			{
				case "windows" -> "windows";
				case "osx" -> "mac";
				case "linux" -> "linux";
				default -> rule.os().name().toLowerCase(Locale.ROOT);
			};

			if (!osName.contains(expectedOs))
			{
				return false;
			}
		}

		if (rule.os().arch() != null)
		{
			String expectedArch = rule.os().arch().toLowerCase(Locale.ROOT);

			if (!osArch.equals(expectedArch))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Summary of downloaded runtime inputs.
	 *
	 * @param libraryCount the number of downloaded runtime libraries
	 * @param assetObjectCount the number of downloaded asset objects
	 * @param librariesRoot the cached libraries root
	 * @param assetsObjectsRoot the cached asset objects root
	 */
	public record RuntimeDownloadResult(
		int libraryCount,
		int assetObjectCount,
		Path librariesRoot,
		Path assetsObjectsRoot
	)
	{
	}
}
