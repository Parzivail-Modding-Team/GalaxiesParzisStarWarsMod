package com.parzivail.toolchain.maven;

import java.net.URI;

/**
 * Shared Maven repository constants used by the toolchain model and runtime resolvers.
 */
public final class ToolchainMavenRepositories
{
	/**
	 * Maven Central.
	 */
	public static final URI MAVEN_CENTRAL = URI.create("https://repo1.maven.org/maven2/");

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
