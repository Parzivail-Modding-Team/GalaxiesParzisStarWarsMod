package dev.pswg.data;

import com.mojang.serialization.DataResult;
import dev.pswg.Blasters;
import dev.pswg.codecgenerator.CodecDefault;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.GenerateEnumCodec;
import dev.pswg.entity.BlasterEffectContext;
import dev.pswg.entity.BlasterImpacts;
import dev.pswg.generated.codecs.IAreaKindCodec;
import dev.pswg.generated.codecs.IBlasterAreaEffectCodec;
import net.minecraft.resources.Identifier;

/**
 * A bounded on-fire cone. Each visible target is affected once.
 */
@GenerateCodec(strict = true)
public record BlasterAreaEffect(
		BlasterBehaviorProfile.EffectPhase when,
		AreaKind kind,
		@CodecRange(min = 0.1, max = 64) float range,
		@CodecRange(min = 0, max = 90) float coneDegrees,
		@CodecRange(min = 1) int maximumTargets,
		@CodecRange(min = 1, max = 200) int durationTicks,
		@CodecRange(min = 0) float strength,
		@CodecDefault("true") boolean blockFluids
) implements IBlasterAreaEffectCodec, BlasterBehaviorProfile.Effect
{
	/**
	 * Closed cone effects.
	 */
	@GenerateEnumCodec
	public enum AreaKind implements IAreaKindCodec
	{
		/**
		 * Stops movement and attacks for the authored duration.
		 */
		STUN,

		/**
		 * Disrupts tagged living targets and addon powered objects.
		 */
		ION,

		/**
		 * Pushes visible targets away from the shooter.
		 */
		REPULSOR,

		/**
		 * Applies ordinary damage and a short stun.
		 */
		SHOCK
	}

	public static final Identifier TYPE = Blasters.id("area");

	public static void register()
	{
		BlasterBehaviorProfile.registerEffect(TYPE, MAP_CODEC.validate(value ->
				                                                               value.when() == BlasterBehaviorProfile.EffectPhase.ON_FIRE
				                                                               ? DataResult.success(value)
				                                                               : DataResult.error(() -> "Area effects require on_fire")
		));
	}

	@Override
	public Identifier type()
	{
		return TYPE;
	}

	/**
	 * Runs the server cone query.
	 */
	@Override
	public void apply(BlasterEffectContext context)
	{
		BlasterImpacts.area(context, this);
	}
}
