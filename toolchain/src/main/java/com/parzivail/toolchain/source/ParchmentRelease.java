package com.parzivail.toolchain.source;

/**
 * A published Parchment export release.
 *
 * @param minecraftVersion the Minecraft version targeted by the export
 * @param parchmentVersion the published Parchment export version
 */
public record ParchmentRelease(
		String minecraftVersion,
		String parchmentVersion
)
{
}
