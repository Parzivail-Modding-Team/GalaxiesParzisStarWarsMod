package dev.pswg.rendering.ptex;

import org.jetbrains.annotations.Nullable;

/**
 * The rendering domain encoded in a {@code ptex} identifier.
 */
public enum PtexTextureDomain
{
	/**
	 * A texture that resolves into a stitched atlas sprite.
	 */
	BAKED("baked"),

	/**
	 * A texture that resolves into a directly sampled runtime texture.
	 */
	SAMPLER("sampler");

	/**
	 * The serialized name used inside the {@code ptex} namespace.
	 */
	private final String _serializedName;

	PtexTextureDomain(String serializedName)
	{
		_serializedName = serializedName;
	}

	/**
	 * Resolves a texture domain from its serialized name.
	 *
	 * @param value The serialized value to parse.
	 *
	 * @return The parsed domain, or {@code null} if the value is unknown.
	 */
	public static @Nullable PtexTextureDomain parse(String value)
	{
		for (var domain : values())
		{
			if (domain._serializedName.equals(value))
			{
				return domain;
			}
		}

		return null;
	}

	/**
	 * Gets the serialized name for this domain.
	 *
	 * @return The serialized identifier segment.
	 */
	public String getSerializedName()
	{
		return _serializedName;
	}
}
