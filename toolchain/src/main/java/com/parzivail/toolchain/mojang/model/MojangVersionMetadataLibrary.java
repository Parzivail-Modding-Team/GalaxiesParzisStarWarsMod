package com.parzivail.toolchain.mojang.model;

import java.util.List;

/**
 * Represents a declared runtime library from Mojang version metadata.
 *
 * @param name      the Maven coordinate-like library name
 * @param downloads the library downloads section
 * @param rules     the optional allow/disallow rules
 */
public record MojangVersionMetadataLibrary(
		String name,
		MojangVersionMetadataLibraryDownloads downloads,
		List<MojangRule> rules
)
{
}
