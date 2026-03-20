package dev.pswg.toolchain.mojang.model;

/**
 * Represents a declared runtime library from Mojang version metadata.
 *
 * @param name the Maven coordinate-like library name
 */
public record MojangVersionMetadataLibrary(
	String name
)
{
}
