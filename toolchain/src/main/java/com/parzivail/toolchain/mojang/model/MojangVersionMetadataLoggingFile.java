package com.parzivail.toolchain.mojang.model;

/**
 * Represents the downloadable logging configuration file metadata.
 *
 * @param id   the logical logging file identifier
 * @param sha1 the SHA-1 of the file
 * @param size the file size
 * @param url  the file URL
 */
public record MojangVersionMetadataLoggingFile(
		String id,
		String sha1,
		long size,
		String url
)
{
}
