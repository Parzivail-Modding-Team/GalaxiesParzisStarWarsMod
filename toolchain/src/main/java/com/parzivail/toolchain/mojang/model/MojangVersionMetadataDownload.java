package com.parzivail.toolchain.mojang.model;

/**
 * Represents a downloadable artifact in Mojang version metadata.
 *
 * @param path the relative artifact path when provided by Mojang
 * @param sha1 the SHA-1 of the artifact
 * @param size the artifact size
 * @param url  the artifact URL
 */
public record MojangVersionMetadataDownload(
		String path,
		String sha1,
		long size,
		String url
)
{
}
