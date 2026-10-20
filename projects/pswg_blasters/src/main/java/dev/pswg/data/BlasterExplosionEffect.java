package dev.pswg.data;

import com.mojang.serialization.DataResult;
import dev.pswg.Blasters;
import dev.pswg.codecgenerator.CodecDefault;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.entity.BlasterEffectContext;
import dev.pswg.entity.BlasterImpacts;
import dev.pswg.generated.codecs.IBlasterExplosionEffectCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * One native explosion at detonation. Block destruction requires an explicit tag.
 */
@GenerateCodec(strict = true)
public record BlasterExplosionEffect(
		BlasterBehaviorProfile.EffectPhase when,
		@CodecRange(min = 0.1) float radius,
		@CodecDefault("true") boolean damageEntities,
		@CodecRange(min = 0) float knockback,
		Optional<Identifier> destroyBlocks,
		@CodecDefault("true") boolean blockFluids
) implements IBlasterExplosionEffectCodec, BlasterBehaviorProfile.Effect
{
	public static final Identifier TYPE = Blasters.id("explosion");

	public static void register()
	{
		BlasterBehaviorProfile.registerEffect(TYPE, MAP_CODEC.validate(value ->
				                                                               value.when() == BlasterBehaviorProfile.EffectPhase.ON_DETONATE
				                                                               ? DataResult.success(value)
				                                                               : DataResult.error(() -> "Explosion effects require on_detonate")
		));
	}

	@Override
	public Identifier type()
	{
		return TYPE;
	}

	/**
	 * Runs the native server explosion.
	 */
	@Override
	public void apply(BlasterEffectContext context)
	{
		BlasterImpacts.explode(context, this);
	}
}
