package dev.pswg.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.generated.codecs.IStoredChargeCodec;

/**
 * Codec for some amount of charge.
 *
 * @param current  Current stored charge units.
 * @param capacity Maximum stored charge units.
 */
@GenerateCodec(strict = true)
public record StoredCharge(
		@CodecRange(min = 0) int current,
		@CodecRange(min = 1) int capacity
) implements IStoredChargeCodec
{
	public static final Codec<StoredCharge> CODEC = IStoredChargeCodec.CODEC.validate(
			value -> value.current() <= value.capacity()
			         ? DataResult.success(value)
			         : DataResult.error(() -> "current charge cannot exceed capacity")
	);
}
