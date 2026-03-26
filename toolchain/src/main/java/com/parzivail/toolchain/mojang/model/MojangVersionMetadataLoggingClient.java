package com.parzivail.toolchain.mojang.model;

/**
 * Represents client logging configuration metadata.
 *
 * @param argument the JVM argument template
 * @param file     the logging configuration file
 * @param type     the logging configuration type
 */
public record MojangVersionMetadataLoggingClient(
		String argument,
		MojangVersionMetadataLoggingFile file,
		String type
)
{
}
