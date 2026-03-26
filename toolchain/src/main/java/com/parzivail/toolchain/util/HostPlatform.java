package com.parzivail.toolchain.util;

import java.util.Locale;

/**
 * Canonical host-platform identities used across the toolchain.
 */
public enum HostPlatform
{
	/**
	 * Windows hosts.
	 */
	WINDOWS("windows", "Windows", "windows"),

	/**
	 * macOS hosts.
	 */
	MACOS("macos", "macOS", "osx"),

	/**
	 * Linux hosts.
	 */
	LINUX("linux", "Linux", "linux"),

	/**
	 * Any unrecognized host.
	 */
	UNKNOWN("unknown", "Unknown", "unknown");

	/**
	 * The toolchain platform identifier.
	 */
	private final String _id;

	/**
	 * The user-facing display name.
	 */
	private final String _displayName;

	/**
	 * Mojang's canonical operating-system identifier.
	 */
	private final String _mojangOsName;

	/**
	 * Creates one host-platform value.
	 *
	 * @param id           the toolchain platform identifier
	 * @param displayName  the user-facing display name
	 * @param mojangOsName Mojang's canonical operating-system name
	 */
	HostPlatform(String id, String displayName, String mojangOsName)
	{
		_id = id;
		_displayName = displayName;
		_mojangOsName = mojangOsName;
	}

	/**
	 * Detects the current host platform.
	 *
	 * @return the current host platform
	 */
	public static HostPlatform current()
	{
		var osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

		if (osName.contains("win"))
		{
			return WINDOWS;
		}

		if (osName.contains("mac"))
		{
			return MACOS;
		}

		if (osName.contains("linux"))
		{
			return LINUX;
		}

		return UNKNOWN;
	}

	/**
	 * Gets the toolchain platform identifier.
	 *
	 * @return the platform identifier
	 */
	public String id()
	{
		return _id;
	}

	/**
	 * Gets the user-facing display name.
	 *
	 * @return the display name
	 */
	public String displayName()
	{
		return _displayName;
	}

	/**
	 * Gets Mojang's canonical operating-system identifier.
	 *
	 * @return the Mojang operating-system identifier
	 */
	public String mojangOsName()
	{
		return _mojangOsName;
	}

	/**
	 * Checks whether this platform is Windows.
	 *
	 * @return whether this platform is Windows
	 */
	public boolean isWindows()
	{
		return this == WINDOWS;
	}

	/**
	 * Resolves a Mojang rule OS name to the substring expected inside Java's `os.name` property.
	 *
	 * @param mojangRuleOsName the Mojang rule OS name
	 *
	 * @return the expected `os.name` token
	 */
	public static String expectedOsNameToken(String mojangRuleOsName)
	{
		if (mojangRuleOsName == null)
		{
			return null;
		}

		return switch (mojangRuleOsName.toLowerCase(Locale.ROOT))
		{
			case "windows" -> "windows";
			case "osx" -> "mac";
			case "linux" -> "linux";
			default -> mojangRuleOsName.toLowerCase(Locale.ROOT);
		};
	}
}
