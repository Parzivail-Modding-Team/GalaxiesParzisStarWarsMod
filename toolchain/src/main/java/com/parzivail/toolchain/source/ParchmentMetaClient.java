package com.parzivail.toolchain.source;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Reads Parchment Maven metadata and downloads published exports.
 */
public final class ParchmentMetaClient
{
	/**
	 * The Parchment Maven base URL used for export resolution.
	 */
	public static final String PARCHMENT_MAVEN_BASE = "https://maven.parchmentmc.org/org/parchmentmc/data/";

	/**
	 * The HTTP client used for Parchment requests.
	 */
	private final HttpClient _httpClient;

	/**
	 * Creates a client with the default HTTP implementation.
	 */
	public ParchmentMetaClient()
	{
		this(
				HttpClient.newBuilder()
				          .followRedirects(HttpClient.Redirect.NORMAL)
				          .build()
		);
	}

	/**
	 * Creates a client with an explicit HTTP implementation.
	 *
	 * @param httpClient the HTTP client used for Parchment requests
	 */
	public ParchmentMetaClient(HttpClient httpClient)
	{
		_httpClient = httpClient;
	}

	/**
	 * Fetches the latest published release version for a Minecraft-targeted Parchment artifact.
	 *
	 * @param minecraftVersion the Minecraft version to resolve
	 *
	 * @return the latest published release version, or {@code null} when none exists
	 *
	 * @throws IOException if the request or XML parsing fails
	 */
	public String fetchLatestReleaseVersion(String minecraftVersion) throws IOException
	{
		var body = fetchOptionalBytes(createMetadataUri(minecraftVersion));

		if (body == null)
		{
			return null;
		}

		return parseReleaseVersion(body);
	}

	/**
	 * Downloads a remote file directly to disk.
	 *
	 * @param uri    the remote file URI
	 * @param target the local output path
	 *
	 * @throws IOException if the request or file write fails
	 */
	public void downloadToFile(URI uri, Path target) throws IOException
	{
		var request = HttpRequest.newBuilder()
		                         .uri(uri)
		                         .GET()
		                         .build();
		HttpResponse<InputStream> response;

		try
		{
			response = _httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
		}
		catch (InterruptedException exception)
		{
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while downloading " + uri, exception);
		}

		if (response.statusCode() != 200)
		{
			throw new IOException("Unexpected download response status: " + response.statusCode() + " for " + uri);
		}

		Files.createDirectories(target.getParent());

		try (var body = response.body())
		{
			Files.copy(body, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * Fetches a remote text resource as a trimmed string.
	 *
	 * @param uri the text resource URI
	 *
	 * @return the trimmed body text
	 *
	 * @throws IOException if the request fails
	 */
	public String fetchTrimmedString(URI uri) throws IOException
	{
		var body = fetchRequiredBytes(uri);
		return new String(body, StandardCharsets.UTF_8).trim();
	}

	/**
	 * Builds the metadata URI for a Minecraft-targeted Parchment artifact.
	 *
	 * @param minecraftVersion the target Minecraft version
	 *
	 * @return the metadata URI
	 */
	public static URI createMetadataUri(String minecraftVersion)
	{
		return URI.create(PARCHMENT_MAVEN_BASE + "parchment-" + minecraftVersion + "/maven-metadata.xml");
	}

	/**
	 * Builds the export artifact URI for a published Parchment release.
	 *
	 * @param release the published Parchment release
	 *
	 * @return the zip artifact URI
	 */
	public static URI createArtifactUri(ParchmentRelease release)
	{
		return URI.create(
				PARCHMENT_MAVEN_BASE
				+ "parchment-"
				+ release.minecraftVersion()
				+ "/"
				+ release.parchmentVersion()
				+ "/parchment-"
				+ release.minecraftVersion()
				+ "-"
				+ release.parchmentVersion()
				+ ".zip"
		);
	}

	/**
	 * Builds the SHA-1 URI for a published Parchment release artifact.
	 *
	 * @param release the published Parchment release
	 *
	 * @return the SHA-1 URI
	 */
	public static URI createArtifactSha1Uri(ParchmentRelease release)
	{
		return URI.create(createArtifactUri(release) + ".sha1");
	}

	/**
	 * Parses the release field from a Maven metadata document.
	 *
	 * @param body the raw metadata document
	 *
	 * @return the release version, or {@code null} when absent
	 *
	 * @throws IOException if XML parsing fails
	 */
	private static String parseReleaseVersion(byte[] body) throws IOException
	{
		try
		{
			var factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setExpandEntityReferences(false);
			var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(body));
			var releaseNodes = document.getElementsByTagName("release");

			if (releaseNodes.getLength() == 0)
			{
				return null;
			}

			var releaseVersion = releaseNodes.item(0).getTextContent();
			return releaseVersion != null && !releaseVersion.isBlank() ? releaseVersion.trim() : null;
		}
		catch (Exception exception)
		{
			throw new IOException("Failed to parse Parchment metadata.", exception);
		}
	}

	/**
	 * Fetches a required remote resource as bytes.
	 *
	 * @param uri the request URI
	 *
	 * @return the response body bytes
	 *
	 * @throws IOException if the request fails
	 */
	private byte[] fetchRequiredBytes(URI uri) throws IOException
	{
		HttpResponse<byte[]> response = fetch(uri);

		if (response.statusCode() != 200)
		{
			throw new IOException("Unexpected response status: " + response.statusCode() + " for " + uri);
		}

		return response.body();
	}

	/**
	 * Fetches an optional remote resource as bytes.
	 *
	 * @param uri the request URI
	 *
	 * @return the response body bytes, or {@code null} when absent
	 *
	 * @throws IOException if the request fails
	 */
	private byte[] fetchOptionalBytes(URI uri) throws IOException
	{
		var response = fetch(uri);

		if (response.statusCode() == 404)
		{
			return null;
		}

		if (response.statusCode() != 200)
		{
			throw new IOException("Unexpected response status: " + response.statusCode() + " for " + uri);
		}

		return response.body();
	}

	private HttpResponse<byte[]> fetch(URI uri) throws IOException
	{
		var request = HttpRequest.newBuilder()
		                         .uri(uri)
		                         .GET()
		                         .build();

		HttpResponse<byte[]> response;

		try
		{
			response = _httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
		}
		catch (InterruptedException exception)
		{
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while requesting " + uri, exception);
		}

		return response;
	}
}
