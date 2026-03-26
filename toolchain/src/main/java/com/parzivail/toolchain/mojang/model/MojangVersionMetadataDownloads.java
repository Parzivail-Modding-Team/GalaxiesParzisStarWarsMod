package com.parzivail.toolchain.mojang.model;

/**
 * Represents the primary download set in Mojang version metadata.
 *
 * @param client the vanilla client jar download
 * @param server the vanilla server jar download
 */
public record MojangVersionMetadataDownloads(
		MojangVersionMetadataDownload client,
		MojangVersionMetadataDownload server
)
{
}
