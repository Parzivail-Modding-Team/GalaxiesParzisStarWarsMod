package dev.pswg.codec;

import com.mojang.serialization.Codec;

/**
 * Defines codecs and related utilities for common data types
 */
public final class GalaxiesCodecs
{
	/**
	 * Creates a {@link Codec} for serializing and deserializing an enum type.
	 *
	 * @param <T>   The type of the enum
	 * @param clazz The class of the enum
	 *
	 * @return A {@link Codec} for the specified enum type
	 */
	public static <T extends Enum<T>> Codec<T> forEnum(Class<T> clazz)
	{
		return Codec.STRING.xmap(s -> Enum.valueOf(clazz, s), T::name);
	}
}
