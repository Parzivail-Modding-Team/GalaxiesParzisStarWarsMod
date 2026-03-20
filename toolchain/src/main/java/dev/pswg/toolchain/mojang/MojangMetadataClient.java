package dev.pswg.toolchain.mojang;

import dev.pswg.toolchain.mojang.model.MojangVersionManifest;
import dev.pswg.toolchain.mojang.model.MojangVersionManifestEntry;
import dev.pswg.toolchain.mojang.model.MojangVersionManifestLatest;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadata;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataAssetIndex;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataDownload;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataDownloads;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataLibrary;
import dev.pswg.toolchain.util.json.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
		Map<String, Object> root = readCachedObject(
			VERSION_MANIFEST_URI,
			_paths.versionManifestFile(),
			refresh
		);

		Map<String, Object> latest = requireObject(root, "latest");
		List<Map<String, Object>> versions = requireObjectList(root, "versions");
		List<MojangVersionManifestEntry> entries = new ArrayList<>();

		for (Map<String, Object> version : versions)
		{
			entries.add(new MojangVersionManifestEntry(
				requireString(version, "id"),
				requireString(version, "type"),
				requireString(version, "url"),
				stringValue(version.get("time")),
				stringValue(version.get("releaseTime")),
				stringValue(version.get("sha1")),
				intValue(version.get("complianceLevel"))
			));
		}

		return new MojangVersionManifest(
			new MojangVersionManifestLatest(
				requireString(latest, "release"),
				requireString(latest, "snapshot")
			),
			entries
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
		Map<String, Object> root = readCachedObject(
			URI.create(version.url()),
			_paths.versionMetadataFile(versionId),
			refresh
		);

		Map<String, Object> assetIndex = requireObject(root, "assetIndex");
		Map<String, Object> downloads = requireObject(root, "downloads");
		Map<String, Object> client = requireObject(downloads, "client");
		List<Map<String, Object>> libraries = requireObjectList(root, "libraries");
		List<MojangVersionMetadataLibrary> libraryEntries = new ArrayList<>();

		for (Map<String, Object> library : libraries)
		{
			libraryEntries.add(new MojangVersionMetadataLibrary(requireString(library, "name")));
		}

		return new MojangVersionMetadata(
			requireString(root, "id"),
			requireString(root, "mainClass"),
			requireString(root, "assets"),
			new MojangVersionMetadataAssetIndex(
				requireString(assetIndex, "id"),
				stringValue(assetIndex.get("sha1")),
				longValue(assetIndex.get("size")),
				longValue(assetIndex.get("totalSize")),
				requireString(assetIndex, "url")
			),
			new MojangVersionMetadataDownloads(
				new MojangVersionMetadataDownload(
					stringValue(client.get("sha1")),
					longValue(client.get("size")),
					requireString(client, "url")
				)
			),
			libraryEntries
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
	private Map<String, Object> readCachedObject(URI sourceUri, Path cacheFile, boolean refresh) throws IOException
	{
		ensureCached(sourceUri, cacheFile, refresh);
		String json = Files.readString(cacheFile, StandardCharsets.UTF_8);
		Object parsed = JsonParser.parse(json);

		if (parsed instanceof Map<?, ?> map)
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> object = (Map<String, Object>) map;
			return object;
		}

		throw new IOException("Expected JSON object in " + cacheFile);
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
	 * Reads a required string field from an object.
	 *
	 * @param object the JSON object
	 * @param key the field name
	 * @return the string value
	 * @throws IOException if the field is missing or invalid
	 */
	private static String requireString(Map<String, Object> object, String key) throws IOException
	{
		Object value = object.get(key);

		if (value instanceof String string)
		{
			return string;
		}

		throw new IOException("Missing or invalid string field: " + key);
	}

	/**
	 * Reads a required nested object field from an object.
	 *
	 * @param object the JSON object
	 * @param key the field name
	 * @return the nested object
	 * @throws IOException if the field is missing or invalid
	 */
	private static Map<String, Object> requireObject(Map<String, Object> object, String key) throws IOException
	{
		Object value = object.get(key);

		if (value instanceof Map<?, ?> map)
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> nested = (Map<String, Object>) map;
			return nested;
		}

		throw new IOException("Missing or invalid object field: " + key);
	}

	/**
	 * Reads a required array of nested objects from an object.
	 *
	 * @param object the JSON object
	 * @param key the field name
	 * @return the nested object list
	 * @throws IOException if the field is missing or invalid
	 */
	private static List<Map<String, Object>> requireObjectList(Map<String, Object> object, String key) throws IOException
	{
		Object value = object.get(key);

		if (!(value instanceof List<?> list))
		{
			throw new IOException("Missing or invalid array field: " + key);
		}

		List<Map<String, Object>> nestedObjects = new ArrayList<>();

		for (Object entry : list)
		{
			if (entry instanceof Map<?, ?> map)
			{
				@SuppressWarnings("unchecked")
				Map<String, Object> nested = (Map<String, Object>) map;
				nestedObjects.add(nested);
				continue;
			}

			throw new IOException("Expected object entry in array field: " + key);
		}

		return nestedObjects;
	}

	/**
	 * Converts an optional value to a string.
	 *
	 * @param value the source value
	 * @return the string value, or {@code null}
	 */
	private static String stringValue(Object value)
	{
		return value instanceof String string ? string : null;
	}

	/**
	 * Converts an optional numeric value to an integer.
	 *
	 * @param value the source value
	 * @return the integer value, or {@code null}
	 */
	private static Integer intValue(Object value)
	{
		return value instanceof Number number ? number.intValue() : null;
	}

	/**
	 * Converts an optional numeric value to a long.
	 *
	 * @param value the source value
	 * @return the long value, or {@code null}
	 */
	private static Long longValue(Object value)
	{
		return value instanceof Number number ? number.longValue() : null;
	}
}
