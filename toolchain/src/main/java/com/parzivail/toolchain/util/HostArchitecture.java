package com.parzivail.toolchain.util;

import java.util.Locale;

/**
 * Canonical host-architecture identities used across the toolchain.
 */
public enum HostArchitecture
{
	/**
	 * 64-bit x86 hosts.
	 */
	X86_64("x86_64"),

	/**
	 * 32-bit x86 hosts.
	 */
	X86("x86"),

	/**
	 * 64-bit ARM hosts.
	 */
	ARM64("arm64"),

	/**
	 * Any unrecognized host architecture.
	 */
	UNKNOWN("unknown");

	/**
	 * The normalized classifier identifier.
	 */
	private final String _id;

	/**
	 * Creates one host-architecture value.
	 *
	 * @param id the normalized classifier identifier
	 */
	HostArchitecture(String id)
	{
		_id = id;
	}

	/**
	 * Detects the current host architecture.
	 *
	 * @return the current host architecture
	 */
	public static HostArchitecture current()
	{
		var osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

		return switch (osArch)
		{
			case "amd64", "x86_64" -> X86_64;
			case "x86", "i386" -> X86;
			case "aarch64", "arm64" -> ARM64;
			default -> UNKNOWN;
		};
	}

	/**
	 * Gets the normalized classifier identifier.
	 *
	 * @return the normalized classifier identifier
	 */
	public String id()
	{
		return _id;
	}

	/**
	 * Checks whether this architecture is 64-bit ARM.
	 *
	 * @return whether this architecture is 64-bit ARM
	 */
	public boolean isArm64()
	{
		return this == ARM64;
	}

	/**
	 * Checks whether this architecture is 32-bit x86.
	 *
	 * @return whether this architecture is 32-bit x86
	 */
	public boolean isX86()
	{
		return this == X86;
	}

	/**
	 * Checks whether this architecture is 64-bit x86.
	 *
	 * @return whether this architecture is 64-bit x86
	 */
	public boolean isX86_64()
	{
		return this == X86_64;
	}
}
