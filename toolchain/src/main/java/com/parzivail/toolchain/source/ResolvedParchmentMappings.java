package com.parzivail.toolchain.source;

import java.nio.file.Path;

/**
 * A resolved Parchment export cached locally for one requested Minecraft version.
 *
 * @param requestedVersion  the Minecraft version requested by the toolchain
 * @param mappedVersion     the Minecraft version actually used for the Parchment export
 * @param parchmentVersion  the selected Parchment export version
 * @param parchmentJsonFile the extracted Parchment JSON file used for documentation
 */
public record ResolvedParchmentMappings(
		String requestedVersion,
		String mappedVersion,
		String parchmentVersion,
		Path parchmentJsonFile
)
{
}
