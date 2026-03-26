package com.parzivail.toolchain.mojang.model;

/**
 * Represents the download section for a runtime library.
 *
 * @param artifact the standard artifact download
 */
public record MojangVersionMetadataLibraryDownloads(
		MojangVersionMetadataDownload artifact
)
{
}
