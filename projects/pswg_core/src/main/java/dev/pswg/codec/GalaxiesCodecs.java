package dev.pswg.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

/**
 * Defines codecs and related utilities for common data types
 */
public final class GalaxiesCodecs
{
	/**
	 * A codec for serializing and deserializing a list of {@link ResourceLocation}s.
	 */
	public static final Codec<List<ResourceLocation>> IDENTIFIER_LIST = ExtraCodecs.compactListCodec(ResourceLocation.CODEC);

	/**
	 * A codec for serializing and deserializing a map between {@link ResourceLocation}s.
	 */
	public static final Codec<Map<ResourceLocation, ResourceLocation>> IDENTIFIER_MAP = Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC);

	/**
	 * A codec for serializing and deserializing a {@link Vector3f} with named components.
	 */
	public static final Codec<Vector3f> NAMED_VECTOR_3F = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("x").forGetter(Vector3f::x),
			Codec.FLOAT.fieldOf("y").forGetter(Vector3f::y),
			Codec.FLOAT.fieldOf("z").forGetter(Vector3f::z)
	).apply(instance, Vector3f::new));

	/**
	 * A codec for serializing and deserializing a {@link Vector2f} with named components.
	 */
	public static final Codec<Vector2f> NAMED_VECTOR_2F = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("x").forGetter(Vector2f::x),
			Codec.FLOAT.fieldOf("y").forGetter(Vector2f::y)
	).apply(instance, Vector2f::new));

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
