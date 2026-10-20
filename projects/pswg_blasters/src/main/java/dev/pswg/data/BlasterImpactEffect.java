package dev.pswg.data;

import com.mojang.serialization.DataResult;
import dev.pswg.Blasters;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.GenerateEnumCodec;
import dev.pswg.entity.BlasterEffectContext;
import dev.pswg.entity.BlasterImpacts;
import dev.pswg.generated.codecs.IBlasterImpactEffectCodec;
import dev.pswg.generated.codecs.IImpactKindCodec;
import net.minecraft.resources.Identifier;

/**
 * Nonlethal blaster impact effects.
 */
@GenerateCodec(strict = true)
public record BlasterImpactEffect(
		BlasterBehaviorProfile.EffectPhase when,
		ImpactKind kind,
		@CodecRange(min = 1, max = 200) int durationTicks
) implements IBlasterImpactEffectCodec, BlasterBehaviorProfile.Effect
{
	/**
	 * Impact kind.
	 */
	@GenerateEnumCodec
	public enum ImpactKind implements IImpactKindCodec
	{
		/**
		 * Stun.
		 */
		STUN,

		/**
		 * Ion.
		 */
		ION,

		/**
		 * Training.
		 */
		TRAINING
	}

	public static final Identifier TYPE = Blasters.id("contact");

	public static void register()
	{
		BlasterBehaviorProfile.registerEffect(TYPE, MAP_CODEC.validate(value ->
				                                                               value.when() == BlasterBehaviorProfile.EffectPhase.ON_ENTITY_HIT
				                                                               || value.kind() == ImpactKind.ION && value.when() == BlasterBehaviorProfile.EffectPhase.ON_BLOCK_HIT
				                                                               ? DataResult.success(value)
				                                                               : DataResult.error(() -> "Contact effects require an entity hit; ion also permits a block hit")
		));
	}

	/**
	 * Identifies this effect's registered codec.
	 */
	@Override
	public Identifier type()
	{
		return TYPE;
	}

	/**
	 * Applies one effect.
	 */
	@Override
	public void apply(BlasterEffectContext context)
	{
		BlasterImpacts.contact(context, this);
	}
}
