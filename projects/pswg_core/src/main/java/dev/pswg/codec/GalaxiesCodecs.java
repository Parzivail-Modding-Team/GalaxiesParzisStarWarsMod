package dev.pswg.codec;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;

/**
 * Defines codecs and related utilities for common data types
 */
public final class GalaxiesCodecs
{
	/**
	 * A codec for serializing and deserializing a list of {@link Identifier}s.
	 */
	public static final Codec<List<Identifier>> IDENTIFIER_LIST = Codecs.listOrSingle(Identifier.CODEC);

	/**
	 * A codec for serializing and deserializing a map between {@link Identifier}s.
	 */
	public static final Codec<Map<Identifier, Identifier>> IDENTIFIER_MAP = Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC);

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
