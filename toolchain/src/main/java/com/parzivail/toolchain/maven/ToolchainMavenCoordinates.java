package com.parzivail.toolchain.maven;

/**
 * Shared Maven coordinate bundles used by the PSWG graph.
 */
public final class ToolchainMavenCoordinates
{
	/**
	 * The JetBrains annotations library used throughout PSWG content modules.
	 */
	public static final String JETBRAINS_ANNOTATIONS = "org.jetbrains:annotations:26.0.2";

	/**
	 * The Fabric API aggregate module used by PSWG content modules at compile time.
	 */
	public static final String FABRIC_API = "net.fabricmc.fabric-api:fabric-api:${fabric_version}";

	/**
	 * Prevents construction.
	 */
	private ToolchainMavenCoordinates()
	{
	}
}
