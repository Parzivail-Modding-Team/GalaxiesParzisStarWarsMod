package dev.pswg.toolchain.mojang.model;

/**
 * Represents a downloadable artifact in Mojang version metadata.
 *
 * @param sha1 the SHA-1 of the artifact
 * @param size the artifact size
 * @param url the artifact URL
 */
public record MojangVersionMetadataDownload(
	String sha1,
	long size,
	String url
)
{
}
