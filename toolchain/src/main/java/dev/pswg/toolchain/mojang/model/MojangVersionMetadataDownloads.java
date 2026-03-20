package dev.pswg.toolchain.mojang.model;

/**
 * Represents the primary download set in Mojang version metadata.
 *
 * @param client the vanilla client jar download
 */
public record MojangVersionMetadataDownloads(
	MojangVersionMetadataDownload client
)
{
}
