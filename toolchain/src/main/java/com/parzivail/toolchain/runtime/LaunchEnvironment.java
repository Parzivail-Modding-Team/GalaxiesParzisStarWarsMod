package com.parzivail.toolchain.runtime;

import java.util.List;
import java.util.Locale;

/**
 * Supported development launch environments.
 */
public enum LaunchEnvironment
{
	/**
	 * The integrated client environment.
	 */
	CLIENT("client", "Client"),

	/**
	 * The dedicated server environment.
	 */
	SERVER("server", "Server");

	/**
	 * The stable environment identifier used in generated metadata.
	 */
	private final String _id;

	/**
	 * The human-readable environment display name.
	 */
	private final String _displayName;

	/**
	 * Creates a launch environment.
	 *
	 * @param id          the stable environment identifier
	 * @param displayName the human-readable display name
	 */
	LaunchEnvironment(String id, String displayName)
	{
		_id = id;
		_displayName = displayName;
	}

	/**
	 * Gets the stable environment identifier.
	 *
	 * @return the identifier
	 */
	public String id()
	{
		return _id;
	}

	/**
	 * Gets the human-readable display name.
	 *
	 * @return the display name
	 */
	public String displayName()
	{
		return _displayName;
	}

	/**
	 * Checks whether this is the client environment.
	 *
	 * @return {@code true} when client
	 */
	public boolean isClient()
	{
		return this == CLIENT;
	}

	/**
	 * Checks whether this is the dedicated server environment.
	 *
	 * @return {@code true} when server
	 */
	public boolean isServer()
	{
		return this == SERVER;
	}

	/**
	 * Gets the standard instance-directory name for the generated Fabric launch bundle.
	 *
	 * @return the Fabric launch instance-directory name
	 */
	public String fabricInstanceDirectoryName()
	{
		return "fabric-" + id();
	}

	/**
	 * Resolves the launch identity that should be embedded for this environment.
	 *
	 * @param clientIdentity the requested client identity
	 *
	 * @return the effective launch identity
	 */
	public LaunchIdentity effectiveIdentity(LaunchIdentity clientIdentity)
	{
		return isClient() ? clientIdentity : LaunchIdentity.defaults();
	}

	/**
	 * Resolves the generated program arguments for this environment.
	 *
	 * @param identity the effective launch identity
	 *
	 * @return the generated program arguments
	 */
	public List<String> programArguments(LaunchIdentity identity)
	{
		return isClient()
		       ? List.of("--username", identity.username(), "--uuid", identity.uuid())
		       : List.of("nogui");
	}

	/**
	 * Resolves the environment from a CLI or metadata token.
	 *
	 * @param value the raw token
	 *
	 * @return the resolved environment
	 */
	public static LaunchEnvironment fromId(String value)
	{
		if (value == null || value.isBlank())
		{
			return CLIENT;
		}

		var normalized = value.trim().toLowerCase(Locale.ROOT);

		for (var candidate : values())
		{
			if (candidate.id().equals(normalized))
			{
				return candidate;
			}
		}

		throw new IllegalArgumentException("Unknown launch environment: " + value);
	}
}
