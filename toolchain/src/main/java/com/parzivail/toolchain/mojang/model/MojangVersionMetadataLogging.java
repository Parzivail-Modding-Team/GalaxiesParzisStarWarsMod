package com.parzivail.toolchain.mojang.model;

/**
 * Represents logging metadata attached to a version document.
 *
 * @param client the client logging configuration
 */
public record MojangVersionMetadataLogging(
		MojangVersionMetadataLoggingClient client
)
{
}
