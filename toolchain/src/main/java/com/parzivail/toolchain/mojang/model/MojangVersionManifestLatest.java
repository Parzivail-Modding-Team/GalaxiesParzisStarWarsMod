package com.parzivail.toolchain.mojang.model;

/**
 * Represents the latest release and snapshot pointers from the Mojang version manifest.
 *
 * @param release  the latest release version identifier
 * @param snapshot the latest snapshot version identifier
 */
public record MojangVersionManifestLatest(
		String release,
		String snapshot
)
{
}
