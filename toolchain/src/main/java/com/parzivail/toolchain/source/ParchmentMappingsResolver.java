package com.parzivail.toolchain.source;

import com.parzivail.toolchain.mojang.MojangMetadataClient;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.util.DigestUtilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Resolves and caches Parchment exports used to overlay official-name sources with docs.
 */
public final class ParchmentMappingsResolver
{
	/**
	 * The Parchment JSON entry embedded in the published zip artifact.
	 */
	private static final String PARCHMENT_JSON_ENTRY = "parchment.json";

	/**
	 * The metadata client used to resolve and download Parchment exports.
	 */
	private final ParchmentMetaClient _parchmentMetaClient;

	/**
	 * The Mojang metadata client used to find fallback versions.
	 */
	private final MojangMetadataClient _mojangClient;

	/**
	 * The local mappings cache root.
	 */
	private final Path _cacheRoot;

	/**
	 * A cache of latest published Parchment releases by Minecraft version.
	 */
	private final Map<String, String> _releaseVersionCache;

	/**
	 * Creates a resolver with default metadata clients.
	 */
	public ParchmentMappingsResolver()
	{
		this(new ParchmentMetaClient(), new MojangMetadataClient(), ToolchainPaths.PARCHMENT_CACHE_ROOT);
	}

	/**
	 * Creates a resolver with explicit collaborators.
	 *
	 * @param parchmentMetaClient the Parchment metadata client
	 * @param mojangClient        the Mojang metadata client
	 * @param cacheRoot           the local mappings cache root
	 */
	public ParchmentMappingsResolver(
			ParchmentMetaClient parchmentMetaClient,
			MojangMetadataClient mojangClient,
			Path cacheRoot
	)
	{
		_parchmentMetaClient = parchmentMetaClient;
		_mojangClient = mojangClient;
		_cacheRoot = cacheRoot;
		_releaseVersionCache = new HashMap<>();
	}

	/**
	 * Resolves a local Parchment JSON file for one Minecraft version.
	 *
	 * @param requestedVersion the Minecraft version being documented
	 * @param refresh          whether cache refresh was requested
	 *
	 * @return the resolved Parchment mappings, or {@code null} when none can be found
	 *
	 * @throws IOException if metadata retrieval, downloading, or extraction fails
	 */
	public ResolvedParchmentMappings resolveMappings(String requestedVersion, boolean refresh) throws IOException
	{
		var release = resolveBestRelease(requestedVersion, refresh);

		if (release == null)
		{
			return null;
		}

		var mappingsDirectory = _cacheRoot.resolve(requestedVersion);
		var parchmentJsonFile = mappingsDirectory.resolve(
				"parchment-" + release.minecraftVersion() + "-" + release.parchmentVersion() + ".json"
		);

		if (!refresh && Files.exists(parchmentJsonFile))
		{
			return new ResolvedParchmentMappings(
					requestedVersion,
					release.minecraftVersion(),
					release.parchmentVersion(),
					parchmentJsonFile
			);
		}

		Files.createDirectories(mappingsDirectory);

		var parchmentZip = mappingsDirectory.resolve(
				"parchment-" + release.minecraftVersion() + "-" + release.parchmentVersion() + ".zip"
		);
		var artifactUri = ParchmentMetaClient.createArtifactUri(release);
		var expectedSha1 = _parchmentMetaClient.fetchTrimmedString(ParchmentMetaClient.createArtifactSha1Uri(release));

		if (Files.notExists(parchmentZip) || !hasMatchingSha1(parchmentZip, expectedSha1))
		{
			_parchmentMetaClient.downloadToFile(artifactUri, parchmentZip);

			if (!hasMatchingSha1(parchmentZip, expectedSha1))
			{
				Files.deleteIfExists(parchmentZip);
				throw new IOException("Checksum mismatch for " + artifactUri);
			}
		}

		extractParchmentJson(parchmentZip, parchmentJsonFile);
		return new ResolvedParchmentMappings(
				requestedVersion,
				release.minecraftVersion(),
				release.parchmentVersion(),
				parchmentJsonFile
		);
	}

	/**
	 * Resolves the best available Parchment export for a requested Minecraft version.
	 *
	 * @param requestedVersion the Minecraft version being documented
	 * @param refresh          whether cache refresh was requested
	 *
	 * @return the selected Parchment release, or {@code null} when none exists
	 *
	 * @throws IOException if metadata retrieval fails
	 */
	private ParchmentRelease resolveBestRelease(String requestedVersion, boolean refresh) throws IOException
	{
		var exactReleaseVersion = fetchLatestReleaseVersion(requestedVersion);

		if (exactReleaseVersion != null)
		{
			return new ParchmentRelease(requestedVersion, exactReleaseVersion);
		}

		var manifest = _mojangClient.getVersionManifest(refresh);
		var targetIndex = -1;

		for (var i = 0; i < manifest.versions().size(); i++)
		{
			if (manifest.versions().get(i).id().equals(requestedVersion))
			{
				targetIndex = i;
				break;
			}
		}

		if (targetIndex < 0)
		{
			return null;
		}

		for (var i = targetIndex + 1; i < manifest.versions().size(); i++)
		{
			var candidate = manifest.versions().get(i);
			var releaseVersion = fetchLatestReleaseVersion(candidate.id());

			if (releaseVersion != null)
			{
				return new ParchmentRelease(candidate.id(), releaseVersion);
			}
		}

		return null;
	}

	/**
	 * Fetches the latest published Parchment release version for a Minecraft version with memoization.
	 *
	 * @param minecraftVersion the target Minecraft version
	 *
	 * @return the published release version, or {@code null} when unavailable
	 *
	 * @throws IOException if metadata retrieval fails
	 */
	private String fetchLatestReleaseVersion(String minecraftVersion) throws IOException
	{
		if (_releaseVersionCache.containsKey(minecraftVersion))
		{
			return _releaseVersionCache.get(minecraftVersion);
		}

		var releaseVersion = _parchmentMetaClient.fetchLatestReleaseVersion(minecraftVersion);
		_releaseVersionCache.put(minecraftVersion, releaseVersion);
		return releaseVersion;
	}

	/**
	 * Extracts the Parchment JSON payload from a published zip artifact.
	 *
	 * @param parchmentZip      the downloaded Parchment zip file
	 * @param parchmentJsonFile the extracted JSON destination
	 *
	 * @throws IOException if the zip cannot be read or the entry is missing
	 */
	private static void extractParchmentJson(Path parchmentZip, Path parchmentJsonFile) throws IOException
	{
		try (var jarFile = new JarFile(parchmentZip.toFile()))
		{
			var jarEntry = jarFile.getJarEntry(PARCHMENT_JSON_ENTRY);

			if (jarEntry == null)
			{
				throw new IOException("Missing " + PARCHMENT_JSON_ENTRY + " in " + parchmentZip);
			}

			try (var inputStream = jarFile.getInputStream(jarEntry))
			{
				Files.copy(inputStream, parchmentJsonFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	/**
	 * Checks whether a local file already matches an expected SHA-1 hash.
	 *
	 * @param path         the local file path
	 * @param expectedSha1 the expected SHA-1 string
	 *
	 * @return {@code true} if the local file already matches the expected hash
	 *
	 * @throws IOException if the file cannot be hashed
	 */
	private static boolean hasMatchingSha1(Path path, String expectedSha1) throws IOException
	{
		try
		{
			var digest = DigestUtilities.computeSha1Digest(path);
			return expectedSha1.equalsIgnoreCase(DigestUtilities.formatHex(digest.digest()));
		}
		catch (NoSuchAlgorithmException exception)
		{
			throw new IOException("SHA-1 hashing is not available", exception);
		}
	}
}
