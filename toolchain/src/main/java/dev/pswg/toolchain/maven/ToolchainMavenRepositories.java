package dev.pswg.toolchain.maven;

import java.net.URI;

/**
 * Shared Maven repository constants used by the toolchain model and runtime resolvers.
 */
public final class ToolchainMavenRepositories
{
	/**
	 * The Fabric Maven repository root.
	 */
	public static final URI FABRIC = URI.create("https://maven.fabricmc.net/");

	/**
	 * Prevents construction.
	 */
	private ToolchainMavenRepositories()
	{
	}
}
