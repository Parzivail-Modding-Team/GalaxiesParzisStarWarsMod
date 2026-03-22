package dev.pswg.toolchain.runtime;

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
	 * @param id the stable environment identifier
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
	 * Resolves the environment from a CLI or metadata token.
	 *
	 * @param value the raw token
	 * @return the resolved environment
	 */
	public static LaunchEnvironment fromId(String value)
	{
		if (value == null || value.isBlank())
		{
			return CLIENT;
		}

		String normalized = value.trim().toLowerCase(Locale.ROOT);

		for (LaunchEnvironment candidate : values())
		{
			if (candidate.id().equals(normalized))
			{
				return candidate;
			}
		}

		throw new IllegalArgumentException("Unknown launch environment: " + value);
	}
}
