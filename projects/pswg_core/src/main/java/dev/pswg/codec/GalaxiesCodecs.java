package dev.pswg.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

/**
 * Defines codecs and related utilities for common data types
 */
public final class GalaxiesCodecs
{
	/** Minecraft's native array-vector codec, rejecting non-finite axes. */
	public static final Codec<Vector3fc> FINITE_VECTOR3F = ExtraCodecs.VECTOR3F.validate(vector ->
			vector.isFinite()
					? DataResult.success(vector)
					: DataResult.error(() -> "Vector axes must be finite"));

	/**
	 * A codec for serializing and deserializing a list of {@link Identifier}s.
	 */
	public static final Codec<List<Identifier>> IDENTIFIER_LIST = ExtraCodecs.compactListCodec(Identifier.CODEC);

	/**
	 * A codec for serializing and deserializing a map between {@link Identifier}s.
	 */
	public static final Codec<Map<Identifier, Identifier>> IDENTIFIER_MAP = Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC);

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
	 * Creates a codec that rejects map keys not declared by the supplied map codec.
	 *
	 * @param <A>      The decoded value type
	 * @param mapCodec The map codec defining the accepted keys
	 *
	 * @return A codec that validates keys before decoding with the map codec
	 */
	public static <A> Codec<A> strict(MapCodec<A> mapCodec)
	{
		var codec = mapCodec.codec();
		return ExtraCodecs.catchDecoderException(Codec.of(codec, new Decoder<A>()
		{
			@Override
			public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input)
			{
				return ops.getMap(input).flatMap(map -> {
					var allowed = mapCodec.keys(ops).collect(Collectors.toSet());
					for (var entry : map.entries().toList())
					{
						if (!allowed.contains(entry.getFirst()))
						{
							return DataResult.error(() -> "Unknown field: " + entry.getFirst());
						}
					}
					return codec.decode(ops, input);
				});
			}
		}));
	}

	/**
	 * Validates values during codec decoding and encoding, reporting validation failures as codec errors.
	 *
	 * @param <A>       The value type
	 * @param codec     The codec to validate
	 * @param validator The validation to apply
	 *
	 * @return A codec that reports {@link IllegalArgumentException}s as data errors
	 */
	public static <A> Codec<A> validate(Codec<A> codec, Consumer<A> validator)
	{
		return codec.validate(value -> {
			try
			{
				validator.accept(value);
				return DataResult.success(value);
			}
			catch (IllegalArgumentException exception)
			{
				return DataResult.error(exception::getMessage);
			}
		});
	}

	/**
	 * Dispatches registered map codecs with native DFU dispatch and strict branch fields.
	 * Branch codecs describe their own fields; this method owns the {@code type} discriminator.
	 * Unlike ordinary dispatch, unknown branch fields and mismatched returned type IDs are rejected.
	 */
	public static <K, A> Codec<A> typedDispatch(
			Codec<K> keyCodec,
			Map<K, MapCodec<? extends A>> codecs,
			Function<? super A, K> typeGetter,
			String description
	)
	{
		Codec<A> dispatch = keyCodec.partialDispatch(
				"type",
				value -> DataResult.success(typeGetter.apply(value)),
				id -> codecs.containsKey(id)
						? DataResult.success(codecs.get(id))
						: DataResult.error(() -> "Unknown " + description + " type " + id)
		);
		return ExtraCodecs.catchDecoderException(Codec.of(dispatch, new Decoder<A>()
		{
			@Override
			public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input)
			{
				return ops.getMap(input).flatMap(map -> {
					var rawType = map.get("type");
					if (rawType == null)
					{
						return DataResult.error(() -> "Missing " + description + " discriminator 'type'");
					}
					return keyCodec.parse(ops, rawType).flatMap(id -> {
						var branch = codecs.get(id);
						if (branch == null)
						{
							return DataResult.error(() -> "Unknown " + description + " type " + id);
						}
						return GalaxiesCodecs.<A>strictBranch(branch).decode(ops, ops.remove(input, "type")).flatMap(pair ->
								id.equals(typeGetter.apply(pair.getFirst()))
										? DataResult.success(pair)
										: DataResult.error(() -> "Mismatched " + description + " type " + id));
					});
				});
			}
		}));
	}

	/** Narrows a branch only after its registered discriminator has selected its value type. */
	@SuppressWarnings("unchecked")
	private static <A> Codec<A> strictBranch(MapCodec<? extends A> branch)
	{
		return strict((MapCodec<A>)branch);
	}

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
		return ExtraCodecs.catchDecoderException(Codec.STRING.xmap(s -> Enum.valueOf(clazz, s), T::name));
	}
}
