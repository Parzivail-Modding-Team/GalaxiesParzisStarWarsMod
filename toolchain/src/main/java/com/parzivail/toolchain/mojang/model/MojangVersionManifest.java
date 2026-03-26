package com.parzivail.toolchain.mojang.model;

import java.util.List;

/**
 * Represents the Mojang launcher version manifest.
 *
 * @param latest   pointers to the latest release and snapshot
 * @param versions all available version entries
 */
public record MojangVersionManifest(
		MojangVersionManifestLatest latest,
		List<MojangVersionManifestEntry> versions
)
{
}
