package dev.pswg.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.generated.codecs.IStoredChargeCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

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
	/**
	 * Forces registration during common initialization.
	 */
	public static void register()
	{
	}

	public static final Codec<StoredCharge> CODEC = IStoredChargeCodec.CODEC.validate(
			value -> value.current() <= value.capacity()
			         ? DataResult.success(value)
			         : DataResult.error(() -> "current charge cannot exceed capacity")
	);

	/**
	 * Generic persistent, synchronized charge storage for packs and other powered items.
	 */
	public static final DataComponentType<StoredCharge> COMPONENT = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Galaxies.id("stored_charge"),
			DataComponentType.<StoredCharge>builder().persistent(StoredCharge.CODEC).networkSynchronized(StoredCharge.PACKET_CODEC).build()
	);
}
