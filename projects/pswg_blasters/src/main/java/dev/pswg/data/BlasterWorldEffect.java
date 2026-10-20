package dev.pswg.data;

import com.mojang.serialization.DataResult;
import dev.pswg.Blasters;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.entity.BlasterEffectContext;
import dev.pswg.entity.BlasterImpacts;
import dev.pswg.generated.codecs.IBlasterWorldEffectCodec;
import net.minecraft.resources.Identifier;

/**
 * Breaks a tagged impact block only when server permissions allow it.
 */
@GenerateCodec(strict = true)
public record BlasterWorldEffect(
		BlasterBehaviorProfile.EffectPhase when,
		Identifier blocks
) implements IBlasterWorldEffectCodec, BlasterBehaviorProfile.Effect
{
	public static final Identifier TYPE = Blasters.id("world_damage");

	public static void register()
	{
		BlasterBehaviorProfile.registerEffect(TYPE, MAP_CODEC.validate(value ->
				                                                               value.when() == BlasterBehaviorProfile.EffectPhase.ON_BLOCK_HIT
				                                                               ? DataResult.success(value)
				                                                               : DataResult.error(() -> "World damage requires on_block_hit")
		));
	}

	@Override
	public Identifier type()
	{
		return TYPE;
	}

	/**
	 * Checks the impact block and its permissions.
	 */
	@Override
	public void apply(BlasterEffectContext context)
	{
		BlasterImpacts.breakBlock(context, blocks);
	}
}
