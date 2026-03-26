package com.parzivail.toolchain.mojang;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parzivail.toolchain.mojang.model.*;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.util.DigestUtilities;
import com.parzivail.toolchain.util.HostArchitecture;
import com.parzivail.toolchain.util.HostPlatform;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.jar.JarFile;

/**
 * Resolves and caches Mojang launcher metadata used by the standalone toolchain.
 *
 * <p>The cache behavior is intentionally iteration-friendly: "refresh" means "revalidate and heal
 * stale files when necessary", not "always redownload". That distinction matters because this
 * client feeds both launch preparation and IntelliJ sync, so unnecessary downloads quickly make the
 * whole toolchain feel sluggish.
 */
public final class MojangMetadataClient
{
	/**
	 * The official Mojang version manifest endpoint.
	 */
	public static final URI VERSION_MANIFEST_URI = URI.create("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");

	/**
	 * The standard request timeout for Mojang downloads.
	 */
	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);

	/**
	 * The maximum number of download retry attempts.
	 */
	private static final int MAX_DOWNLOAD_ATTEMPTS = 5;

	/**
	 * The default concurrent asset download worker count.
	 */
	private static final int ASSET_DOWNLOAD_CONCURRENCY = 8;

	/**
	 * The shared JSON object mapper.
	 */
	private final ObjectMapper _mapper;

	/**
	 * The shared HTTP client.
	 */
	private final HttpClient _httpClient;

	/**
	 * Creates a metadata client with default runtime services.
	 */
	public MojangMetadataClient()
	{
		_mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		_httpClient = HttpClient.newBuilder()
		                        .connectTimeout(REQUEST_TIMEOUT)
		                        .build();
	}

	/**
	 * Resolves the version manifest from cache or Mojang.
	 *
	 * @param refresh whether to revalidate the cached manifest before reuse
	 *
	 * @return the resolved version manifest
	 *
	 * @throws IOException if resolution fails
	 */
	public MojangVersionManifest getVersionManifest(boolean refresh) throws IOException
	{
		return readCachedJson(
				VERSION_MANIFEST_URI,
				ToolchainPaths.MOJANG_VERSION_MANIFEST_FILE,
				null,
				MojangVersionManifest.class,
				refresh
		);
	}

	/**
	 * Resolves a specific version entry from the version manifest.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh   whether to revalidate the cached manifest before reuse
	 *
	 * @return the resolved version entry
	 *
	 * @throws IOException if the version is missing or the manifest fails to resolve
	 */
	public MojangVersionManifestEntry getVersion(String versionId, boolean refresh) throws IOException
	{
		var manifest = getVersionManifest(refresh);

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
	 * @param refresh   whether to revalidate the cached metadata before reuse
	 *
	 * @return the resolved version metadata
	 *
	 * @throws IOException if resolution fails
	 */
	public MojangVersionMetadata getVersionMetadata(String versionId, boolean refresh) throws IOException
	{
		var version = getVersion(versionId, refresh);

		return readCachedJson(
				URI.create(version.url()),
				ToolchainPaths.mojangVersionMetadataFile(versionId),
				version.sha1(),
				MojangVersionMetadata.class,
				refresh
		);
	}

	/**
	 * Downloads the vanilla client jar for a resolved Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh   whether to revalidate the cached client jar before reuse
	 *
	 * @return the cached client jar path
	 *
	 * @throws IOException if the jar cannot be downloaded
	 */
	public Path downloadClientJar(String versionId, boolean refresh) throws IOException
	{
		var metadata = getVersionMetadata(versionId, refresh);
		var target = ToolchainPaths.mojangClientJarFile(versionId);
		ensureCached(
				URI.create(metadata.downloads().client().url()),
				target,
				metadata.downloads().client().sha1(),
				refresh
		);
		return target;
	}

	/**
	 * Downloads the vanilla server jar for a resolved Minecraft version.
	 *
	 * <p>Recent Minecraft versions often distribute a small bootstrap server jar that contains the
	 * real dedicated-server jar under `META-INF/versions`. This method transparently extracts that
	 * nested jar and returns the extracted path so callers can compile against actual server classes
	 * instead of the bootstrap launcher wrapper.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh   whether to revalidate the cached server jar before reuse
	 *
	 * @return the cached extracted server jar path when bundled, otherwise the cached raw server jar
	 *
	 * @throws IOException if the jar cannot be downloaded or extracted
	 */
	public Path downloadServerJar(String versionId, boolean refresh) throws IOException
	{
		var metadata = getVersionMetadata(versionId, refresh);

		if (metadata.downloads().server() == null)
		{
			throw new IOException("Minecraft " + versionId + " does not expose a server download");
		}

		var target = ToolchainPaths.mojangServerJarFile(versionId);
		ensureCached(
				URI.create(metadata.downloads().server().url()),
				target,
				metadata.downloads().server().sha1(),
				refresh
		);
		return extractBundledServerJar(versionId, target, refresh);
	}

	/**
	 * Extracts bundled dedicated-server libraries from a bootstrap server jar when present.
	 *
	 * <p>Modern dedicated server downloads package additional runtime dependencies under
	 * `META-INF/libraries`. The split-source-set toolchain still wants the extracted server jar for
	 * compilation, but the actual dedicated-server runtime also needs these bundled libraries on the
	 * classpath. Loom models them explicitly through {@code BundleMetadata}; this helper mirrors that
	 * behavior for the standalone toolchain.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh   whether to force cache refresh
	 *
	 * @return the extracted bundled library paths in declared order
	 *
	 * @throws IOException if extraction fails
	 */
	public List<Path> extractBundledServerLibraries(String versionId, boolean refresh) throws IOException
	{
		var bundledServerJar = ToolchainPaths.mojangServerJarFile(versionId);
		var libraries = bundledServerLibraries(bundledServerJar);

		if (libraries.isEmpty())
		{
			return List.of();
		}

		List<Path> extractedLibraries = new ArrayList<>();

		try (var jarFile = new JarFile(bundledServerJar.toFile()))
		{
			for (var library : libraries)
			{
				var target = ToolchainPaths.mojangLibraryFile(library.artifactPath());

				if (refresh || !Files.isRegularFile(target))
				{
					Files.createDirectories(target.getParent());
					var entry = jarFile.getJarEntry(library.jarEntryPath());

					if (entry == null)
					{
						throw new IOException("Bundled server jar is missing " + library.jarEntryPath() + " in " + bundledServerJar);
					}

					try (var inputStream = jarFile.getInputStream(entry))
					{
						Files.copy(inputStream, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					}
				}

				extractedLibraries.add(target);
			}
		}

		return extractedLibraries;
	}

	/**
	 * Downloads the asset index JSON for a resolved Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh   whether to revalidate the cached asset index before reuse
	 *
	 * @return the cached asset index path
	 *
	 * @throws IOException if the asset index cannot be downloaded
	 */
	public Path downloadAssetIndex(String versionId, boolean refresh) throws IOException
	{
		var metadata = getVersionMetadata(versionId, refresh);
		var target = ToolchainPaths.mojangAssetIndexFile(metadata.assetIndex().id());
		ensureCached(
				URI.create(metadata.assetIndex().url()),
				target,
				metadata.assetIndex().sha1(),
				refresh
		);
		return target;
	}

	/**
	 * Downloads a single file into the toolchain cache.
	 *
	 * @param sourceUri  the source URI
	 * @param targetFile the target cache file
	 * @param refresh    whether to revalidate the cached file before reuse
	 *
	 * @throws IOException if the file cannot be downloaded
	 */
	public void download(URI sourceUri, Path targetFile, boolean refresh) throws IOException
	{
		ensureCached(sourceUri, targetFile, null, refresh);
	}

	/**
	 * Extracts the real dedicated-server jar from a bootstrap bundle when present.
	 *
	 * @param versionId        the Minecraft version identifier
	 * @param bundledServerJar the downloaded server bootstrap jar
	 * @param refresh          whether cache refresh was requested
	 *
	 * @return the extracted jar when bundled metadata is present, otherwise the original jar
	 *
	 * @throws IOException if extraction fails
	 */
	private Path extractBundledServerJar(
			String versionId,
			Path bundledServerJar,
			boolean refresh
	) throws IOException
	{
		var bundledEntryPath = bundledServerEntryPath(bundledServerJar);

		if (bundledEntryPath == null)
		{
			return bundledServerJar;
		}

		var extractedServerJar = ToolchainPaths.mojangExtractedServerJarFile(versionId);

		if (!refresh
		    && Files.isRegularFile(extractedServerJar)
		    && Files.getLastModifiedTime(extractedServerJar).compareTo(Files.getLastModifiedTime(bundledServerJar)) >= 0)
		{
			return extractedServerJar;
		}

		Files.createDirectories(extractedServerJar.getParent());

		try (var jarFile = new JarFile(bundledServerJar.toFile()))
		{
			var entry = jarFile.getJarEntry(bundledEntryPath);

			if (entry == null)
			{
				throw new IOException("Bundled server jar is missing " + bundledEntryPath + " in " + bundledServerJar);
			}

			try (var inputStream = jarFile.getInputStream(entry))
			{
				Files.copy(inputStream, extractedServerJar, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			}
		}

		return extractedServerJar;
	}

	/**
	 * Locates the nested server jar entry inside a bundled server bootstrap jar.
	 *
	 * @param bundledServerJar the downloaded server bootstrap jar
	 *
	 * @return the bundled server jar entry path, or {@code null} when the server jar is already
	 * 		directly usable
	 *
	 * @throws IOException if the bundle metadata cannot be read
	 */
	private String bundledServerEntryPath(Path bundledServerJar) throws IOException
	{
		try (var jarFile = new JarFile(bundledServerJar.toFile()))
		{
			var versionsList = jarFile.getJarEntry("META-INF/versions.list");

			if (versionsList == null)
			{
				return null;
			}

			try (var inputStream = jarFile.getInputStream(versionsList))
			{
				for (var line : new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).split("\n"))
				{
					var trimmed = line.trim();

					if (trimmed.isEmpty())
					{
						continue;
					}

					var parts = trimmed.split("\t");

					if (parts.length == 3)
					{
						return "META-INF/versions/" + parts[2];
					}
				}
			}
		}

		throw new IOException("Bundled server jar metadata is present but no server entry could be resolved from " + bundledServerJar);
	}

	/**
	 * Checks whether a runtime library should be included for the current host platform.
	 *
	 * @param library the library to evaluate
	 *
	 * @return {@code true} if the library should be included
	 */
	public boolean isLibraryAllowed(MojangVersionMetadataLibrary library)
	{
		if (!isAllowed(library.rules()))
		{
			return false;
		}

		return matchesLibraryPlatform(library);
	}

	/**
	 * Checks whether a Mojang rule matches the current host platform.
	 *
	 * @param rule the rule to evaluate
	 *
	 * @return {@code true} if the rule matches
	 */
	public boolean matchesRule(MojangRule rule)
	{
		return matches(rule);
	}

	/**
	 * Downloads the runtime libraries and asset objects required by a selected version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh   whether to revalidate cached runtime artifacts before reuse
	 *
	 * @return the runtime download summary
	 *
	 * @throws IOException if runtime files cannot be downloaded
	 */
	public RuntimeDownloadResult downloadRuntime(String versionId, boolean refresh) throws IOException
	{
		var metadata = getVersionMetadata(versionId, refresh);
		var assetIndexPath = downloadAssetIndex(versionId, refresh);
		var assetIndex = readCachedJson(
				URI.create(metadata.assetIndex().url()),
				assetIndexPath,
				metadata.assetIndex().sha1(),
				MojangAssetIndex.class,
				false
		);

		var libraryCount = 0;

		for (var library : metadata.libraries())
		{
			if (!isLibraryAllowed(library))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null)
			{
				continue;
			}

			var target = ToolchainPaths.mojangLibraryFile(library.downloads().artifact().path());
			ensureCached(
					URI.create(library.downloads().artifact().url()),
					target,
					library.downloads().artifact().sha1(),
					refresh
			);
			libraryCount++;
		}

		var assetObjectCount = downloadAssetObjects(assetIndex, refresh);

		return new RuntimeDownloadResult(
				libraryCount,
				assetObjectCount,
				ToolchainPaths.MOJANG_LIBRARIES_ROOT,
				ToolchainPaths.MOJANG_ASSET_OBJECTS_ROOT
		);
	}

	/**
	 * Reads a JSON object from cache or downloads it into the cache first.
	 *
	 * @param sourceUri    the source URI to fetch
	 * @param cacheFile    the local cache file
	 * @param expectedSha1 the authoritative SHA-1 when one is available
	 * @param type         the JSON payload type
	 * @param refresh      whether to revalidate the cached JSON before reuse
	 *
	 * @return the parsed JSON object
	 *
	 * @throws IOException if the file cannot be read or downloaded
	 */
	private <T> T readCachedJson(
			URI sourceUri,
			Path cacheFile,
			String expectedSha1,
			Class<T> type,
			boolean refresh
	) throws IOException
	{
		ensureCached(sourceUri, cacheFile, expectedSha1, refresh);

		try (var inputStream = Files.newInputStream(cacheFile))
		{
			return _mapper.readValue(inputStream, type);
		}
	}

	/**
	 * Reads bundled dedicated-server library entries from a bootstrap server jar.
	 *
	 * @param bundledServerJar the downloaded server bootstrap jar
	 *
	 * @return the bundled server library entries, or an empty list for legacy non-bundled jars
	 *
	 * @throws IOException if the bundle metadata cannot be read
	 */
	private List<BundledServerLibrary> bundledServerLibraries(Path bundledServerJar) throws IOException
	{
		try (var jarFile = new JarFile(bundledServerJar.toFile()))
		{
			var librariesList = jarFile.getJarEntry("META-INF/libraries.list");

			if (librariesList == null)
			{
				return List.of();
			}

			List<BundledServerLibrary> libraries = new ArrayList<>();

			try (var inputStream = jarFile.getInputStream(librariesList))
			{
				for (var line : new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).split("\n"))
				{
					var trimmed = line.trim();

					if (trimmed.isEmpty())
					{
						continue;
					}

					var parts = trimmed.split("\t");

					if (parts.length != 3)
					{
						continue;
					}

					libraries.add(new BundledServerLibrary(
							parts[1],
							parts[2],
							"META-INF/libraries/" + parts[2]
					));
				}
			}

			return libraries;
		}
	}

	/**
	 * One bundled dedicated-server library entry from {@code META-INF/libraries.list}.
	 *
	 * @param notation     the Maven-like coordinate string
	 * @param artifactPath the relative library artifact path
	 * @param jarEntryPath the entry path inside the bundled server jar
	 */
	private record BundledServerLibrary(
			String notation,
			String artifactPath,
			String jarEntryPath
	)
	{
	}

	/**
	 * Ensures a cache file exists and matches the authoritative content when one is known.
	 *
	 * @param sourceUri    the source URI to fetch
	 * @param cacheFile    the local cache file
	 * @param expectedSha1 the authoritative SHA-1 when one is available
	 * @param refresh      whether to revalidate the cached file before reuse
	 *
	 * @throws IOException if the document cannot be downloaded
	 */
	private void ensureCached(URI sourceUri, Path cacheFile, String expectedSha1, boolean refresh) throws IOException
	{
		if (Files.exists(cacheFile))
		{
			if (!refresh)
			{
				return;
			}

			if (expectedSha1 != null && hasMatchingSha1(cacheFile, expectedSha1))
			{
				// For repeated toolchain sync and launch cycles, refresh means "revalidate against the
				// authoritative hash" rather than "blindly redownload". That keeps iteration fast while
				// still letting the cache self-heal when a local artifact is stale or corrupted.
				return;
			}
		}

		Files.createDirectories(cacheFile.getParent());

		downloadToFile(sourceUri, cacheFile);
	}

	/**
	 * Downloads asset objects concurrently with bounded parallelism, retries, and progress reporting.
	 *
	 * @param assetIndex the resolved asset index
	 * @param refresh    whether to revalidate cached asset objects before reuse
	 *
	 * @return the number of processed asset objects
	 *
	 * @throws IOException if one or more downloads fail
	 */
	private int downloadAssetObjects(MojangAssetIndex assetIndex, boolean refresh) throws IOException
	{
		List<AssetDownload> downloads = new ArrayList<>();

		for (var entry : assetIndex.objects().entrySet())
		{
			var object = entry.getValue();
			var target = ToolchainPaths.mojangAssetObjectFile(object.hash());

			if (Files.exists(target) && (!refresh || hasMatchingSha1(target, object.hash())))
			{
				continue;
			}

			var prefix = object.hash().substring(0, 2);
			var source = URI.create("https://resources.download.minecraft.net/" + prefix + "/" + object.hash());
			downloads.add(new AssetDownload(entry.getKey(), source, target));
		}

		if (downloads.isEmpty())
		{
			return assetIndex.objects().size();
		}

		var workerCount = Math.max(1, Math.min(ASSET_DOWNLOAD_CONCURRENCY, downloads.size()));
		var executor = Executors.newFixedThreadPool(workerCount);
		var completionService = new ExecutorCompletionService<AssetDownloadResult>(executor);
		List<AssetDownload> failures = new ArrayList<>();

		try
		{
			for (var download : downloads)
			{
				completionService.submit(new AssetDownloadTask(download));
			}

			var completed = 0;

			while (completed < downloads.size())
			{
				var future = completionService.take();
				completed++;

				try
				{
					var result = future.get();

					if (completed == downloads.size() || completed % 250 == 0)
					{
						System.out.println(
								"Asset objects: " + completed + "/" + downloads.size() + " downloaded"
						);
					}

					if (!result.success())
					{
						failures.add(result.download());
					}
				}
				catch (ExecutionException exception)
				{
					var cause = exception.getCause();

					if (cause instanceof AssetDownloadException assetDownloadException)
					{
						failures.add(assetDownloadException.download());
					}
					else
					{
						throw new IOException("Unexpected asset download failure", cause == null ? exception : cause);
					}
				}
			}
		}
		catch (InterruptedException exception)
		{
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while downloading asset objects", exception);
		}
		finally
		{
			executor.shutdownNow();

			try
			{
				executor.awaitTermination(5, TimeUnit.SECONDS);
			}
			catch (InterruptedException exception)
			{
				Thread.currentThread().interrupt();
			}
		}

		if (!failures.isEmpty())
		{
			var remainingFailures = retryFailedAssetDownloads(failures);

			if (remainingFailures.isEmpty())
			{
				return assetIndex.objects().size();
			}

			var message = new StringBuilder("Failed asset object downloads: ").append(remainingFailures.size());
			var sampleCount = Math.min(5, remainingFailures.size());

			for (var i = 0; i < sampleCount; i++)
			{
				message.append(System.lineSeparator()).append(" - ").append(remainingFailures.get(i));
			}

			throw new IOException(message.toString());
		}

		return assetIndex.objects().size();
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
	private boolean hasMatchingSha1(Path path, String expectedSha1) throws IOException
	{
		try
		{
			MessageDigest digest = DigestUtilities.computeSha1Digest(path);
			return expectedSha1.equalsIgnoreCase(DigestUtilities.formatHex(digest.digest()));
		}
		catch (NoSuchAlgorithmException exception)
		{
			throw new IOException("SHA-1 hashing is not available", exception);
		}
	}

	/**
	 * Retries failed asset downloads serially to smooth over transient network issues.
	 *
	 * @param failedDownloads the failed asset downloads from the concurrent pass
	 *
	 * @return the remaining failure messages after retry
	 */
	private List<String> retryFailedAssetDownloads(List<AssetDownload> failedDownloads)
	{
		List<String> remainingFailures = new ArrayList<>();

		for (var download : failedDownloads)
		{
			try
			{
				downloadToFile(download.sourceUri(), download.targetFile());
			}
			catch (IOException exception)
			{
				remainingFailures.add(download.assetName() + ": " + exception.getMessage());
			}
		}

		return remainingFailures;
	}

	/**
	 * Downloads a single file with retries and atomic replacement.
	 *
	 * @param sourceUri  the source URI
	 * @param targetFile the target cache file
	 *
	 * @throws IOException if the download fails after all retries
	 */
	private void downloadToFile(URI sourceUri, Path targetFile) throws IOException
	{
		Files.createDirectories(targetFile.getParent());
		var temporaryFile = targetFile.resolveSibling(targetFile.getFileName() + ".part");
		IOException lastFailure = null;

		for (var attempt = 1; attempt <= MAX_DOWNLOAD_ATTEMPTS; attempt++)
		{
			var request = HttpRequest.newBuilder(sourceUri)
			                         .timeout(REQUEST_TIMEOUT)
			                         .GET()
			                         .build();

			try
			{
				var response = _httpClient.send(
						request,
						HttpResponse.BodyHandlers.ofFile(temporaryFile)
				);

				if (response.statusCode() / 100 != 2)
				{
					Files.deleteIfExists(temporaryFile);
					throw new IOException("HTTP " + response.statusCode() + " for " + sourceUri);
				}

				Files.move(
						temporaryFile,
						targetFile,
						java.nio.file.StandardCopyOption.REPLACE_EXISTING,
						java.nio.file.StandardCopyOption.ATOMIC_MOVE
				);
				return;
			}
			catch (InterruptedException exception)
			{
				Thread.currentThread().interrupt();
				throw new IOException("Interrupted while downloading " + sourceUri, exception);
			}
			catch (IOException exception)
			{
				lastFailure = exception;
				Files.deleteIfExists(temporaryFile);

				if (attempt < MAX_DOWNLOAD_ATTEMPTS)
				{
					try
					{
						Thread.sleep(250L * attempt);
					}
					catch (InterruptedException interruptedException)
					{
						Thread.currentThread().interrupt();
						throw new IOException("Interrupted while retrying " + sourceUri, interruptedException);
					}
				}
			}
		}

		throw new IOException("Failed to download " + sourceUri, lastFailure);
	}

	/**
	 * Evaluates Mojang library rules for the current runtime environment.
	 *
	 * @param rules the optional rule list
	 *
	 * @return {@code true} if the library should be included
	 */
	private boolean isAllowed(java.util.List<MojangRule> rules)
	{
		if (rules == null || rules.isEmpty())
		{
			return true;
		}

		var allowed = false;

		for (var rule : rules)
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
	 *
	 * @return {@code true} if the rule matches
	 */
	private boolean matches(MojangRule rule)
	{
		if (rule == null || rule.os() == null)
		{
			return true;
		}

		var osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
		var osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

		if (rule.os().name() != null)
		{
			var expectedOs = HostPlatform.expectedOsNameToken(rule.os().name());

			if (!osName.contains(expectedOs))
			{
				return false;
			}
		}

		if (rule.os().arch() != null)
		{
			var expectedArch = rule.os().arch().toLowerCase(Locale.ROOT);

			if (!osArch.equals(expectedArch))
			{
				return false;
			}
		}

		if (rule.os().versionRange() != null)
		{
			var osVersion = System.getProperty("os.version", "");
			var minVersion = rule.os().versionRange().min();
			var maxVersion = rule.os().versionRange().max();

			if (minVersion != null && compareVersions(osVersion, minVersion) < 0)
			{
				return false;
			}

			return maxVersion == null || compareVersions(osVersion, maxVersion) <= 0;
		}

		return true;
	}

	/**
	 * Compares dotted numeric version strings such as Windows build versions.
	 *
	 * @param left  the first version
	 * @param right the second version
	 *
	 * @return a negative number if {@code left < right}, zero if equal, otherwise positive
	 */
	private int compareVersions(String left, String right)
	{
		var leftParts = left.split("[^0-9]+");
		var rightParts = right.split("[^0-9]+");
		var partCount = Math.max(leftParts.length, rightParts.length);

		for (var i = 0; i < partCount; i++)
		{
			var leftValue = i < leftParts.length ? parseVersionPart(leftParts[i]) : 0;
			var rightValue = i < rightParts.length ? parseVersionPart(rightParts[i]) : 0;

			if (leftValue != rightValue)
			{
				return Integer.compare(leftValue, rightValue);
			}
		}

		return 0;
	}

	/**
	 * Parses a numeric version segment, treating blanks as zero.
	 *
	 * @param value the version segment
	 *
	 * @return the parsed numeric value
	 */
	private int parseVersionPart(String value)
	{
		if (value == null || value.isBlank())
		{
			return 0;
		}

		return Integer.parseInt(value);
	}

	/**
	 * Checks whether a library classifier matches the current host platform.
	 *
	 * @param library the library to inspect
	 *
	 * @return {@code true} if the library matches the current host platform
	 */
	private boolean matchesLibraryPlatform(MojangVersionMetadataLibrary library)
	{
		if (library.name() == null)
		{
			return true;
		}

		var parts = library.name().split(":");

		if (parts.length < 4)
		{
			return true;
		}

		var classifier = parts[3].toLowerCase(Locale.ROOT);
		var currentOs = HostPlatform.current().mojangOsName();
		var currentArch = HostArchitecture.current();

		if (classifier.contains("windows"))
		{
			if (!"windows".equals(currentOs))
			{
				return false;
			}

			if (classifier.contains("arm64"))
			{
				return currentArch.isArm64();
			}

			if (classifier.contains("x86"))
			{
				return currentArch.isX86();
			}

			return true;
		}

		if (classifier.contains("linux"))
		{
			if (!"linux".equals(currentOs))
			{
				return false;
			}

			if (classifier.contains("aarch_64") || classifier.contains("arm64"))
			{
				return currentArch.isArm64();
			}

			if (classifier.contains("x86_64") || classifier.contains("amd64"))
			{
				return currentArch.isX86_64();
			}

			return true;
		}

		if (classifier.contains("macos") || classifier.contains("osx"))
		{
			if (!"osx".equals(currentOs))
			{
				return false;
			}

			if (classifier.contains("arm64"))
			{
				return currentArch.isArm64();
			}

			return true;
		}

		return true;
	}

	/**
	 * Summary of downloaded runtime inputs.
	 *
	 * @param libraryCount      the number of downloaded runtime libraries
	 * @param assetObjectCount  the number of downloaded asset objects
	 * @param librariesRoot     the cached libraries root
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

	/**
	 * Immutable description of an asset object download.
	 *
	 * @param assetName  the logical asset path
	 * @param sourceUri  the source URI
	 * @param targetFile the cache target file
	 */
	private record AssetDownload(
			String assetName,
			URI sourceUri,
			Path targetFile
	)
	{
	}

	/**
	 * Immutable result for a completed asset download.
	 *
	 * @param assetName the logical asset path
	 * @param success   whether the download succeeded
	 * @param message   the failure message when unsuccessful
	 */
	private record AssetDownloadResult(
			AssetDownload download,
			String assetName,
			boolean success,
			String message
	)
	{
	}

	/**
	 * Worker that downloads a single asset object.
	 */
	private final class AssetDownloadTask implements Callable<AssetDownloadResult>
	{
		/**
		 * The asset download work item.
		 */
		private final AssetDownload _download;

		/**
		 * Creates a new asset download task.
		 *
		 * @param download the download work item
		 */
		private AssetDownloadTask(AssetDownload download)
		{
			_download = download;
		}

		/**
		 * Executes the asset object download.
		 *
		 * @return the download result
		 */
		@Override
		public AssetDownloadResult call()
		{
			try
			{
				downloadToFile(_download.sourceUri(), _download.targetFile());
				return new AssetDownloadResult(_download, _download.assetName(), true, null);
			}
			catch (IOException exception)
			{
				return new AssetDownloadResult(_download, _download.assetName(), false, exception.getMessage());
			}
		}
	}

	/**
	 * Wraps an asset download failure with its associated work item.
	 */
	private static final class AssetDownloadException extends Exception
	{
		/**
		 * The failed asset download work item.
		 */
		private final AssetDownload _download;

		/**
		 * Creates a new wrapped asset download failure.
		 *
		 * @param download the failed download
		 * @param cause    the failure cause
		 */
		private AssetDownloadException(AssetDownload download, Throwable cause)
		{
			super(cause);
			_download = download;
		}

		/**
		 * Gets the failed asset download work item.
		 *
		 * @return the failed download
		 */
		private AssetDownload download()
		{
			return _download;
		}
	}
}
