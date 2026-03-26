package com.parzivail.toolchain.config;

/**
 * The supported text-configured module convention families.
 */
public enum ConfiguredModuleKind
{
	/**
	 * A conventional Java module with main Java and resource roots.
	 */
	JAVA,

	/**
	 * A Fabric module with shared main and client roots.
	 */
	FABRIC_SPLIT_SOURCES,

	/**
	 * A Fabric module that only contributes resources.
	 */
	FABRIC_RESOURCE_ONLY;

	/**
	 * Parses one configured module kind token.
	 *
	 * @param value the configured token
	 *
	 * @return the parsed kind
	 */
	public static ConfiguredModuleKind parse(String value)
	{
		return switch (value)
		{
			case "java" -> JAVA;
			case "fabric_split_sources" -> FABRIC_SPLIT_SOURCES;
			case "fabric_resource_only" -> FABRIC_RESOURCE_ONLY;
			default -> throw new IllegalArgumentException("Unsupported module kind: " + value);
		};
	}
}
