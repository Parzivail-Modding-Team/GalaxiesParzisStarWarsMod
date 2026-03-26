package com.parzivail.toolchain.mojang.model;

/**
 * Represents the Mojang asset index descriptor for a version.
 *
 * @param id        the asset index identifier
 * @param sha1      the SHA-1 of the asset index
 * @param size      the asset index size
 * @param totalSize the expanded asset size
 * @param url       the asset index URL
 */
public record MojangVersionMetadataAssetIndex(
		String id,
		String sha1,
		long size,
		long totalSize,
		String url
)
{
}
