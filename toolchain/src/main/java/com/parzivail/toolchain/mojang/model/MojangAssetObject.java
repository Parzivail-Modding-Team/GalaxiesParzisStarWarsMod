package com.parzivail.toolchain.mojang.model;

/**
 * Represents a single asset object entry in a Mojang asset index.
 *
 * @param hash the asset object content hash
 * @param size the asset object size
 */
public record MojangAssetObject(
		String hash,
		Long size
)
{
}
