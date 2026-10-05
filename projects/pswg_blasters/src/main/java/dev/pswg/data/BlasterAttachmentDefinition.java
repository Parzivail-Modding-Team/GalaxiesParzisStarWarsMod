package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.IBlasterAttachmentDefinitionCodec;
import dev.pswg.generated.codecs.IModifierCodec;
import dev.pswg.generated.codecs.IModifierConditionCodec;
import dev.pswg.math.ModifierOperation;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * A blaster attachment definition
 *
 * @param translationKey The translation key.
 * @param slots          Unique slots in which this attachment may be installed.
 * @param function       Optional legacy single-function form; present together with {@code value} only.
 * @param value          Optional literal multiplier for the legacy single-function form.
 * @param category       Attachment category ID.
 * @param modifiers      Multi-stat modifier rows.
 * @param grantedModes   Optional firing modes made available by this attachment.
 * @param stanceProfile  Optional cosmetic stance-profile ID.
 * @param itemModel      Optional cosmetic native item-model ID.
 */
@GenerateCodec(packetCodec = false, strict = true)
public record BlasterAttachmentDefinition(
		@UseCodec(customCodec = @CodecSource(source = BlasterAttachmentDefinition.class, member = "TRANSLATION_KEY_CODEC"))
		String translationKey,
		@SelfCodec
		@CodecSize(min = 1)
		List<Identifier> slots,
		@ApiStatus.Obsolete Optional<Identifier> function,
		@ApiStatus.Obsolete Optional<Float> value,
		Identifier category,
		@SelfCodec
		@CodecDefault("java.util.List.of()")
		List<Modifier> modifiers,
		@SelfCodec
		Optional<List<BlasterStats.Mode>> grantedModes,
		Optional<Identifier> stanceProfile,
		Optional<Identifier> itemModel
) implements IBlasterAttachmentDefinitionCodec
{
	/**
	 * A state that the blaster must be in for the attachment modifiers to be applicable.
	 *
	 * @param mode      Allowed mode IDs, or empty for every mode.
	 * @param archetype Allowed archetype IDs, or empty for every archetype.
	 * @param stance    Allowed closed stance names, or empty for every stance.
	 * @param deployed  Required deployment state when present.
	 * @param ads       Required ADS state when present.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ModifierCondition(
			@SelfCodec
			@CodecDefault("java.util.List.of()")
			List<Identifier> mode,
			@SelfCodec
			@CodecDefault("java.util.List.of()")
			List<Identifier> archetype,
			@CodecDefault("java.util.List.of()")
			List<String> stance,
			Optional<Boolean> deployed,
			Optional<Boolean> ads
	) implements IModifierConditionCodec
	{
		/**
		 * Unconditional context.
		 */
		public static final ModifierCondition UNCONDITIONAL = new ModifierCondition(List.of(), List.of(), List.of(), Optional.empty(), Optional.empty());
	}

	/**
	 * A stats modifier that can apply under a certain condition
	 *
	 * @param function     Registered stat-function ID.
	 * @param operation    Operation accepted by the function descriptor.
	 * @param value        Finite operation value within the function descriptor's range.
	 * @param priority     Ordering priority in {@code -100..100}.
	 * @param modifierCondition Context restriction; defaults to unconditional.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Modifier(
			Identifier function,
			@SelfCodec ModifierOperation operation,
			float value,
			@CodecDefault("0")
			int priority,
			@SelfCodec
			@CodecDefault("dev.pswg.data.BlasterAttachmentDefinition.ModifierCondition.UNCONDITIONAL")
			ModifierCondition modifierCondition
	) implements IModifierCodec
	{
	}

	/**
	 * Existing stances
	 */
	private static final Set<String> _validStances = Set.of(
			"patrol", "hip", "ads", "firing", "venting", "reloading", "deployed", "folded", "holstered"
	);

	/**
	 * Translation key codec
	 */
	public static final Codec<String> TRANSLATION_KEY_CODEC = Codec.sizeLimitedString(256)
	                                                               .validate(value -> !value.isBlank()
	                                                                                  ? DataResult.success(value)
	                                                                                  : DataResult.error(() -> "translationKey must be a non-empty, non-blank string"));
	/**
	 * Root codec
	 */
	public static final Codec<BlasterAttachmentDefinition> CODEC = GalaxiesCodecs.validate(
			IBlasterAttachmentDefinitionCodec.CODEC,
			BlasterAttachmentDefinition::validateDefinition
	);

	/**
	 * Validates an attachment definition's simple or multi-stat wire shape and bounds.
	 */
	private static void validateDefinition(BlasterAttachmentDefinition definition)
	{
		validateDefinition(
				definition.translationKey(),
				definition.slots(),
				definition.function(),
				definition.value(),
				definition.category(),
				definition.modifiers(),
				definition.grantedModes()
		);
	}

	/**
	 * Copies and validates a list of unique IDs or strings.
	 */
	private static <T> void validateUnique(List<T> values, String field)
	{
		Preconditions.checkArgument(new HashSet<>(values).size() == values.size(), "%s entries must be unique", field);
	}

	/**
	 * Gets the effective modifiers for this definition.
	 */
	public List<Modifier> effectiveModifiers()
	{
		if (!modifiers.isEmpty())
			return modifiers;

		return List.of(new Modifier(
				function.orElseThrow(),
				ModifierOperation.MULTIPLY_TOTAL,
				value.orElseThrow(),
				0,
				ModifierCondition.UNCONDITIONAL
		));
	}

	/**
	 * Copies record collections and validates fields for direct construction as well as codec use.
	 */
	public BlasterAttachmentDefinition
	{
		Objects.requireNonNull(translationKey, "translationKey");
		slots = List.copyOf(slots);
		Objects.requireNonNull(function, "function");
		Objects.requireNonNull(value, "value");
		Objects.requireNonNull(category, "category");
		modifiers = List.copyOf(modifiers);
		Objects.requireNonNull(grantedModes, "grantedModes");
		grantedModes = grantedModes.map(List::copyOf);
		Objects.requireNonNull(stanceProfile, "stanceProfile");
		Objects.requireNonNull(itemModel, "itemModel");
		validateDefinition(translationKey, slots, function, value, category, modifiers, grantedModes);
	}

	/**
	 * Validates constructor fields before the record is exposed.
	 */
	private static void validateDefinition(
			String translationKey,
			List<Identifier> slots,
			Optional<Identifier> function,
			Optional<Float> value,
			Identifier category,
			List<Modifier> modifiers,
			Optional<List<BlasterStats.Mode>> grantedModes
	)
	{
		validateUnique(slots, "slots");
		var simpleFunction = function.isPresent();
		var simpleValue = value.isPresent();
		var simpleForm = simpleFunction && simpleValue && modifiers.isEmpty();
		var modifierForm = !simpleFunction && !simpleValue && !modifiers.isEmpty();
		Preconditions.checkArgument(simpleForm || modifierForm,
		                            "Attachment must define either both function/value without modifiers or non-empty modifiers without function/value");
		if (simpleForm)
			BlasterStatFunctions.validate(function.orElseThrow(), ModifierOperation.MULTIPLY_TOTAL, value.orElseThrow());

		grantedModes.ifPresent(modes -> {
			var modeIds = new HashSet<Identifier>();
			for (var mode : modes)
				Preconditions.checkArgument(modeIds.add(mode.id()), "Duplicate granted mode ID: %s", mode.id());
		});
	}
}
