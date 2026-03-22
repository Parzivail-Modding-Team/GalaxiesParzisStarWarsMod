package dev.pswg.toolchain.util;

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
		String osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

		if ("amd64".equals(osArch) || "x86_64".equals(osArch))
		{
			return X86_64;
		}

		if ("x86".equals(osArch) || "i386".equals(osArch))
		{
			return X86;
		}

		if ("aarch64".equals(osArch) || "arm64".equals(osArch))
		{
			return ARM64;
		}

		return UNKNOWN;
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
