package dev.pswg.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.IBlasterAttachmentDefinitionCodec;
import dev.pswg.generated.codecs.IModifierCodec;
import dev.pswg.generated.codecs.IModifierConditionCodec;
import dev.pswg.math.ModifierOperation;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Data-only attachment definition, shared or inline in a weapon's option catalog.
 *
 * @param translationKey Display translation key.
 * @param slots          Unique compatible slot IDs.
 * @param category       Group used by selection UIs.
 * @param modifiers      Ordered stat modifications; empty for cosmetic-only options.
 * @param grantedModes   Optional unique mode grants.
 * @param stanceProfile  Optional cosmetic pose profile.
 * @param itemModel      Optional cosmetic model root.
	 * @param recoilPattern  Optional prioritized pitch/yaw pattern override.
 */
@GenerateCodec(strict = true)
public record BlasterAttachmentDefinition(
		@UseCodec(codec = GenStandardCodec.NON_EMPTY_STRING) String translationKey,
		@CodecSize(min = 1) @CodecUnique List<Identifier> slots,
		Identifier category,
		@SelfCodec @CodecDefault("java.util.List.of()") List<Modifier> modifiers,
		@SelfCodec @CodecUnique(key = "id") Optional<List<BlasterStats.Mode>> grantedModes,
		Optional<Identifier> stanceProfile,
		Optional<Identifier> itemModel,
		@SelfCodec Optional<BlasterStats.RecoilPattern> recoilPattern
) implements IBlasterAttachmentDefinitionCodec
{
	/**
	 * Optional context axes restricting a modifier; empty lists match any value.
	 */
	@GenerateCodec(strict = true)
	public record ModifierCondition(
			@CodecDefault("java.util.List.of()") @CodecUnique List<Identifier> mode,
			@CodecDefault("java.util.List.of()") @CodecUnique List<Identifier> archetype,
			@CodecDefault("java.util.List.of()") @CodecUnique List<BlasterStanceProfile.WeaponState> stance,
			Optional<Boolean> deployed,
			Optional<Boolean> folded,
			Optional<Boolean> ads
	) implements IModifierConditionCodec
	{
		/**
		 * Context matching every state.
		 */
		public static final ModifierCondition UNCONDITIONAL = new ModifierCondition(List.of(), List.of(), List.of(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	/**
	 * A fixed stat target, operation, literal value and context.
	 */
	@GenerateCodec(strict = true)
	public record Modifier(
			BlasterStatFunction function,
			@CodecDefault("dev.pswg.math.ModifierOperation.MULTIPLY_TOTAL") ModifierOperation operation,
			@CodecRange(min = 0) float value,
			@CodecDefault("0") int priority,
			@SelfCodec @CodecDefault("dev.pswg.data.BlasterAttachmentDefinition.ModifierCondition.UNCONDITIONAL") ModifierCondition modifierCondition
	) implements IModifierCodec
	{
		/**
		 * Zoom and cadence require a positive literal-total factor; zero remains useful for recoil/spread/cooling.
		 */
		public static final Codec<Modifier> CODEC = IModifierCodec.CODEC.validate(
				modifier ->
						modifier.operation() == ModifierOperation.MULTIPLY_TOTAL && modifier.value() == 0
						&& (modifier.function() == BlasterStatFunction.ZOOM_MULTIPLIER || modifier.function() == BlasterStatFunction.FIRE_RATE_MULTIPLIER)
						? DataResult.error(() -> "Zoom and fire-rate multipliers must be positive")
						: DataResult.success(modifier)
		);
	}
}
