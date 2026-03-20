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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	 * The standard request timeout for Mojang downloads.
	 */
	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

	/**
	 * The maximum number of download retry attempts.
	 */
	private static final int MAX_DOWNLOAD_ATTEMPTS = 3;

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
	 * The local toolchain cache paths.
	 */
	private final MojangPaths _paths;

	/**
	 * Creates a metadata client with default runtime services.
	 */
	public MojangMetadataClient()
	{
		_mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		_httpClient = HttpClient.newBuilder()
		                        .connectTimeout(REQUEST_TIMEOUT)
		                        .build();
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
		download(URI.create(metadata.assetIndex().url()), target, refresh);
		return target;
	}

	/**
	 * Downloads a single file into the toolchain cache.
	 *
	 * @param sourceUri the source URI
	 * @param targetFile the target cache file
	 * @param refresh whether to force a fresh download
	 * @throws IOException if the file cannot be downloaded
	 */
	public void download(URI sourceUri, Path targetFile, boolean refresh) throws IOException
	{
		ensureCached(sourceUri, targetFile, refresh);
	}

	/**
	 * Checks whether a runtime library should be included for the current host platform.
	 *
	 * @param library the library to evaluate
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
			if (!isLibraryAllowed(library))
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

		int assetObjectCount = downloadAssetObjects(assetIndex, refresh);

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

		downloadToFile(sourceUri, cacheFile);
	}

	/**
	 * Downloads asset objects concurrently with bounded parallelism, retries, and progress reporting.
	 *
	 * @param assetIndex the resolved asset index
	 * @param refresh whether to force fresh downloads
	 * @return the number of processed asset objects
	 * @throws IOException if one or more downloads fail
	 */
	private int downloadAssetObjects(MojangAssetIndex assetIndex, boolean refresh) throws IOException
	{
		List<AssetDownload> downloads = new ArrayList<>();

		for (Map.Entry<String, MojangAssetObject> entry : assetIndex.objects().entrySet())
		{
			MojangAssetObject object = entry.getValue();
			Path target = _paths.assetObjectFile(object.hash());

			if (!refresh && Files.exists(target))
			{
				continue;
			}

			String prefix = object.hash().substring(0, 2);
			URI source = URI.create("https://resources.download.minecraft.net/" + prefix + "/" + object.hash());
			downloads.add(new AssetDownload(entry.getKey(), source, target));
		}

		if (downloads.isEmpty())
		{
			return assetIndex.objects().size();
		}

		int workerCount = Math.max(1, Math.min(ASSET_DOWNLOAD_CONCURRENCY, downloads.size()));
		ExecutorService executor = Executors.newFixedThreadPool(workerCount);
		ExecutorCompletionService<AssetDownloadResult> completionService = new ExecutorCompletionService<>(executor);
		List<String> failures = new ArrayList<>();

		try
		{
			for (AssetDownload download : downloads)
			{
				completionService.submit(new AssetDownloadTask(download));
			}

			int completed = 0;

			while (completed < downloads.size())
			{
				Future<AssetDownloadResult> future = completionService.take();
				completed++;

				try
				{
					AssetDownloadResult result = future.get();

					if (completed == downloads.size() || completed % 250 == 0)
					{
						System.out.println(
							"Asset objects: " + completed + "/" + downloads.size() + " downloaded"
						);
					}

					if (!result.success())
					{
						failures.add(result.assetName() + ": " + result.message());
					}
				}
				catch (ExecutionException exception)
				{
					Throwable cause = exception.getCause();
					failures.add(cause == null ? exception.getMessage() : cause.getMessage());
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
			StringBuilder message = new StringBuilder("Failed asset object downloads: ").append(failures.size());
			int sampleCount = Math.min(5, failures.size());

			for (int i = 0; i < sampleCount; i++)
			{
				message.append(System.lineSeparator()).append(" - ").append(failures.get(i));
			}

			throw new IOException(message.toString());
		}

		return assetIndex.objects().size();
	}

	/**
	 * Downloads a single file with retries and atomic replacement.
	 *
	 * @param sourceUri the source URI
	 * @param targetFile the target cache file
	 * @throws IOException if the download fails after all retries
	 */
	private void downloadToFile(URI sourceUri, Path targetFile) throws IOException
	{
		Files.createDirectories(targetFile.getParent());
		Path temporaryFile = targetFile.resolveSibling(targetFile.getFileName() + ".part");
		IOException lastFailure = null;

		for (int attempt = 1; attempt <= MAX_DOWNLOAD_ATTEMPTS; attempt++)
		{
			HttpRequest request = HttpRequest.newBuilder(sourceUri)
			                                 .timeout(REQUEST_TIMEOUT)
			                                 .GET()
			                                 .build();

			try
			{
				HttpResponse<Path> response = _httpClient.send(
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
	 * Checks whether a library classifier matches the current host platform.
	 *
	 * @param library the library to inspect
	 * @return {@code true} if the library matches the current host platform
	 */
	private boolean matchesLibraryPlatform(MojangVersionMetadataLibrary library)
	{
		if (library.name() == null)
		{
			return true;
		}

		String[] parts = library.name().split(":");

		if (parts.length < 4)
		{
			return true;
		}

		String classifier = parts[3].toLowerCase(Locale.ROOT);
		String currentOs = currentOs();
		String currentArch = currentArch();

		if (classifier.contains("windows"))
		{
			if (!"windows".equals(currentOs))
			{
				return false;
			}

			if (classifier.contains("arm64"))
			{
				return "arm64".equals(currentArch);
			}

			if (classifier.contains("x86"))
			{
				return "x86".equals(currentArch);
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
				return "arm64".equals(currentArch);
			}

			if (classifier.contains("x86_64") || classifier.contains("amd64"))
			{
				return "x86_64".equals(currentArch);
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
				return "arm64".equals(currentArch);
			}

			return true;
		}

		return true;
	}

	/**
	 * Resolves the current host operating system to Mojang's canonical names.
	 *
	 * @return the current host operating system
	 */
	private String currentOs()
	{
		String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

		if (osName.contains("win"))
		{
			return "windows";
		}

		if (osName.contains("mac"))
		{
			return "osx";
		}

		if (osName.contains("linux"))
		{
			return "linux";
		}

		return osName;
	}

	/**
	 * Resolves the current host architecture to normalized classifier names.
	 *
	 * @return the current host architecture
	 */
	private String currentArch()
	{
		String osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

		if ("amd64".equals(osArch) || "x86_64".equals(osArch))
		{
			return "x86_64";
		}

		if ("x86".equals(osArch) || "i386".equals(osArch))
		{
			return "x86";
		}

		if ("aarch64".equals(osArch) || "arm64".equals(osArch))
		{
			return "arm64";
		}

		return osArch;
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

	/**
	 * Immutable description of an asset object download.
	 *
	 * @param assetName the logical asset path
	 * @param sourceUri the source URI
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
	 * @param success whether the download succeeded
	 * @param message the failure message when unsuccessful
	 */
	private record AssetDownloadResult(
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
				return new AssetDownloadResult(_download.assetName(), true, null);
			}
			catch (IOException exception)
			{
				return new AssetDownloadResult(_download.assetName(), false, exception.getMessage());
			}
		}
	}
}
