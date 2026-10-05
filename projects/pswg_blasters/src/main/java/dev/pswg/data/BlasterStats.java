package dev.pswg.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.*;
import dev.pswg.generated.recordbuilders.IAmmoBuilder;
import dev.pswg.generated.recordbuilders.IBlasterStatsBuilder;
import dev.pswg.item.BlasterItem;
import dev.pswg.mutablerecord.MutableRecord;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Normalized blaster statistics.
 *
 * @param damage Base direct-hit damage.
 * @param range Maximum trace distance in blocks.
 * @param automaticRepeatDelay Minimum accepted-shot interval in ticks.
 * @param fireSound Optional sound override.
 * @param heat Heat values, zero when omitted.
 * @param cooling Optional cooling windows; explicit zero windows remain present.
 * @param damageRange Distance over which falloff is sampled.
 * @param ammo Required feed and consumption options.
 * @param configuration Required archetype and item model.
 * @param modes Ordered firing modes.
 * @param recoil Server aim impulse.
 * @param spread Cone spread.
 * @param falloff Ordered normalized damage curve.
 */
@MutableRecord
public record BlasterStats(
		float damage,
		int range,
		int automaticRepeatDelay,
		Optional<Identifier> fireSound, HeatDefinition heat, Optional<BlasterItem.Cooling> cooling,
		float damageRange,
		Ammo ammo,
		BlasterConfiguration configuration,
		Modes modes,
		Recoil recoil,
		Spread spread,
		List<FalloffPoint> falloff
) implements IBlasterStatsBuilder
{
	/**
	 * Supported ammunition feeds.
	 */
	@GenerateEnumCodec
	public enum Feed implements IFeedCodec
	{
		/**
		 * Inventory ammunition consumed per shot.
		 */
		PER_SHOT,

		/**
		 * Discrete rounds loaded into a magazine.
		 */
		MAGAZINE,

		/**
		 * Charge units loaded from component-backed packs.
		 */
		CHARGE_STORE
	}

	/**
	 * Fixed source-ammunition consumption choices.
	 */
	@GenerateEnumCodec
	public enum ConsumptionType implements IConsumptionTypeCodec
	{
		/**
		 * Consume matching item counts.
		 */
		ITEM_COUNT,

		/**
		 * Debit a persistent charge component.
		 */
		COMPONENT_CHARGE
	}

	/**
	 * Fixed firing trigger choices.
	 */
	@GenerateEnumCodec
	public enum TriggerType implements ITriggerTypeCodec
	{
		/**
		 * Fire once on a press edge.
		 */
		SEMI,

		/**
		 * Repeat while held.
		 */
		AUTO,

		/**
		 * Schedule a bounded burst.
		 */
		BURST,

		/**
		 * Charge while held.
		 */
		CHARGE
	}

	/**
	 * Fixed field-conversion predicates.
	 */
	@GenerateEnumCodec
	public enum FieldConditionType implements IFieldConditionTypeCodec
	{
		/**
		 * Require a selected mode.
		 */
		MODE_IS,

		/**
		 * Require an installed attachment.
		 */
		HAS_ATTACHMENT,

		/**
		 * Require loaded charge.
		 */
		LOADED_CHARGE_MIN
	}

	/**
	 * Supported creative ammunition rules.
	 */
	@GenerateEnumCodec
	public enum CreativeAmmoPolicy implements ICreativeAmmoPolicyCodec
	{
		/**
		 * Creative players bypass ammunition debits.
		 */
		FREE_AMMO
	}

	/**
	 * Charge-trigger release behavior.
	 */
	@GenerateEnumCodec
	public enum ReleasePolicy implements IReleasePolicyCodec
	{
		/**
		 * Fire on release after minimum charge.
		 */
		FIRE_ON_RELEASE,

		/**
		 * Fire once when fully charged.
		 */
		FIRE_AT_FULL
	}

	/**
	 * Heat values in units and ticks.
	 */
	@GenerateCodec(strict = true)
	public record HeatDefinition(
			@CodecRange(min = 0) int capacity,
			@CodecRange(min = 0) int perRound,
			@CodecRange(min = 0) float drainSpeed,
			@CodecRange(min = 0) int overheatPenalty,
			@CodecRange(min = 0) float overheatDrainSpeed,
			@CodecRange(min = 0) int passiveCooldownDelay,
			@CodecRange(min = 0) int overchargeBonus
	) implements IHeatDefinitionCodec
	{
		/**
		 * Disabled heat.
		 */
		public static final HeatDefinition ZERO = new HeatDefinition(0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Required ammo settings; each feed's closed shape carries only its own parameters.
	 */
	@GenerateCodec(strict = true)
	@MutableRecord
	public record Ammo(
			@SelfCodec FeedSettings feed,
			Ingredient ingredient, @SelfCodec Consumption consumption,
			@CodecDefault("dev.pswg.data.BlasterStats.CreativeAmmoPolicy.FREE_AMMO") CreativeAmmoPolicy creativePolicy
	) implements IAmmoCodec, IAmmoBuilder
	{
	}

	/**
	 * Closed feed configuration selected by the feed enum.
	 */
	public sealed interface FeedSettings permits PerShotFeed, MagazineFeed, ChargeStoreFeed
	{
		/**
		 * Selected built-in feed.
		 */
		Feed type();

		Codec<FeedSettings> CODEC = GalaxiesCodecs.typedDispatch(Feed.CODEC, Map.of(Feed.PER_SHOT, PerShotFeed.MAP_CODEC, Feed.MAGAZINE, MagazineFeed.MAP_CODEC, Feed.CHARGE_STORE, ChargeStoreFeed.MAP_CODEC), FeedSettings::type, "feed");

		StreamCodec<RegistryFriendlyByteBuf, FeedSettings> PACKET_CODEC = Feed.PACKET_CODEC.<RegistryFriendlyByteBuf>cast().dispatch(FeedSettings::type, type -> switch (type)
		{
			case PER_SHOT -> IPerShotFeedCodec.PACKET_CODEC;
			case MAGAZINE -> IMagazineFeedCodec.PACKET_CODEC;
			case CHARGE_STORE -> IChargeStoreFeedCodec.PACKET_CODEC;
		});
	}

	/**
	 * Inventory-fed shots have no magazine/reload settings.
	 */
	@GenerateCodec(strict = true)
	public record PerShotFeed() implements FeedSettings, IPerShotFeedCodec
	{
		@Override
		public Feed type()
		{
			return Feed.PER_SHOT;
		}
	}

	/**
	 * Discrete magazine capacity and reload duration.
	 */
	@GenerateCodec(strict = true)
	public record MagazineFeed(
			@CodecRange(min = 1) int magazineSize,
			@CodecRange(min = 1) int reloadTicks
	) implements FeedSettings, IMagazineFeedCodec
	{
		@Override
		public Feed type()
		{
			return Feed.MAGAZINE;
		}
	}

	/**
	 * Component-charge capacity and reload duration.
	 */
	@GenerateCodec(strict = true)
	public record ChargeStoreFeed(
			@CodecRange(min = 1, max = 1_000_000) int chargeCapacityUnits,
			@CodecRange(min = 1, max = 12000) int reloadTicks
	) implements FeedSettings, IChargeStoreFeedCodec
	{
		@Override
		public Feed type()
		{
			return Feed.CHARGE_STORE;
		}
	}

	/**
	 * Closed ammunition consumption configurations.
	 */
	public sealed interface Consumption permits ItemCountConsumption, ComponentChargeConsumption
	{
		/**
		 * Selected built-in consumption rule.
		 */
		ConsumptionType type();

		Codec<Consumption> CODEC = GalaxiesCodecs.typedDispatch(ConsumptionType.CODEC, Map.of(ConsumptionType.ITEM_COUNT, ItemCountConsumption.MAP_CODEC, ConsumptionType.COMPONENT_CHARGE, ComponentChargeConsumption.MAP_CODEC), Consumption::type, "consumption");

		StreamCodec<RegistryFriendlyByteBuf, Consumption> PACKET_CODEC = ConsumptionType.PACKET_CODEC.<RegistryFriendlyByteBuf>cast().dispatch(Consumption::type, type -> switch (type)
		{
			case ITEM_COUNT -> IItemCountConsumptionCodec.PACKET_CODEC;
			case COMPONENT_CHARGE -> IComponentChargeConsumptionCodec.PACKET_CODEC;
		});
	}

	/**
	 * Item count debited for each round.
	 */
	@GenerateCodec(strict = true)
	public record ItemCountConsumption(
			@CodecRange(min = 1) int itemsPerRound
	) implements Consumption, IItemCountConsumptionCodec
	{
		@Override
		public ConsumptionType type()
		{
			return ConsumptionType.ITEM_COUNT;
		}
	}

	/**
	 * Charge pack source.
	 */
	@GenerateCodec(strict = true)
	public record ComponentChargeConsumption(
			Identifier component,
			@CodecRange(min = 1) int unitsPerRound,
			Optional<ItemStackTemplate> emptyContainer
	) implements Consumption, IComponentChargeConsumptionCodec
	{
		@Override
		public ConsumptionType type()
		{
			return ConsumptionType.COMPONENT_CHARGE;
		}
	}

	/**
	 * Archetype/model configuration and optional data-defined conversion destinations.
	 */
	@GenerateCodec(strict = true)
	public record BlasterConfiguration(
			Identifier archetype, Identifier itemModel, Optional<Identifier> stanceProfile,
			@SelfCodec Optional<FieldConversion> fieldConversion
	) implements IBlasterConfigurationCodec
	{
	}

	/**
	 * Available conversions with unique option IDs.
	 */
	@GenerateCodec(strict = true)
	public record FieldConversion(
			@SelfCodec @CodecSize(min = 1) @CodecUnique(key = "id") List<FieldConversionOption> options
	) implements IFieldConversionCodec
	{
	}

	/**
	 * A destination and the supported predicates which gate it.
	 */
	@GenerateCodec(strict = true)
	public record FieldConversionOption(
			Identifier id, Identifier targetBlasterId,
			@CodecRange(min = 0) @CodecDefault("0") int durationTicks,
			@CodecDefault("java.util.List.of()") @SelfCodec List<FieldConversionCondition> conditions
	) implements IFieldConversionOptionCodec
	{
	}

	/**
	 * Closed conversion predicate configurations.
	 */
	public sealed interface FieldConversionCondition permits ModeIsCondition, HasAttachmentCondition, LoadedChargeMinCondition
	{
		/**
		 * Supported predicate choice.
		 */
		FieldConditionType type();

		Codec<FieldConversionCondition> CODEC = GalaxiesCodecs.typedDispatch(FieldConditionType.CODEC, Map.of(FieldConditionType.MODE_IS, ModeIsCondition.MAP_CODEC, FieldConditionType.HAS_ATTACHMENT, HasAttachmentCondition.MAP_CODEC, FieldConditionType.LOADED_CHARGE_MIN, LoadedChargeMinCondition.MAP_CODEC), FieldConversionCondition::type, "conversion condition");

		StreamCodec<RegistryFriendlyByteBuf, FieldConversionCondition> PACKET_CODEC = FieldConditionType.PACKET_CODEC.<RegistryFriendlyByteBuf>cast().dispatch(FieldConversionCondition::type, type -> switch (type)
		{
			case MODE_IS -> IModeIsConditionCodec.PACKET_CODEC;
			case HAS_ATTACHMENT -> IHasAttachmentConditionCodec.PACKET_CODEC;
			case LOADED_CHARGE_MIN -> ILoadedChargeMinConditionCodec.PACKET_CODEC;
		});
	}

	/**
	 * Require a selected mode.
	 */
	@GenerateCodec(strict = true)
	public record ModeIsCondition(Identifier modeId) implements FieldConversionCondition, IModeIsConditionCodec
	{
		@Override
		public FieldConditionType type()
		{
			return FieldConditionType.MODE_IS;
		}
	}

	/**
	 * Require an installed, slot-compatible attachment.
	 */
	@GenerateCodec(strict = true)
	public record HasAttachmentCondition(
			Identifier attachmentSlot,
			Identifier attachmentId
	) implements FieldConversionCondition, IHasAttachmentConditionCodec
	{
		@Override
		public FieldConditionType type()
		{
			return FieldConditionType.HAS_ATTACHMENT;
		}
	}

	/**
	 * Require a minimum loaded charge.
	 */
	@GenerateCodec(strict = true)
	public record LoadedChargeMinCondition(
			@CodecRange(min = 1) int units
	) implements FieldConversionCondition, ILoadedChargeMinConditionCodec
	{
		@Override
		public FieldConditionType type()
		{
			return FieldConditionType.LOADED_CHARGE_MIN;
		}
	}

	/**
	 * Ordered modes and one default from that list.
	 */
	@GenerateCodec(strict = true)
	public record Modes(
			@CodecName("default") Identifier defaultMode,
			@CodecSize(min = 1) @CodecUnique(key = "id") @SelfCodec List<Mode> options
	) implements IModesCodec
	{
		public static final Codec<Modes> CODEC = IModesCodec.CODEC.validate(
				value ->
						value.options().stream().anyMatch(mode -> mode.id().equals(value.defaultMode()))
						? DataResult.success(value)
						: DataResult.error(() -> "Default mode is not listed: " + value.defaultMode())
		);
	}

	/**
	 * One trigger choice and referenced behavior profile.
	 */
	@GenerateCodec(strict = true)
	public record Mode(
			Identifier id,
			@SelfCodec Trigger trigger,
			Identifier behaviorProfile
	) implements IModeCodec
	{
	}

	/**
	 * Closed trigger configurations; data cannot register additional scheduling behavior.
	 */
	public sealed interface Trigger permits SemiTrigger, AutoTrigger, BurstTrigger, ChargeTrigger
	{
		/**
		 * Supported trigger choice.
		 */
		TriggerType type();

		Codec<Trigger> CODEC = GalaxiesCodecs.<TriggerType, Trigger>typedDispatch(TriggerType.CODEC, Map.of(TriggerType.SEMI, SemiTrigger.MAP_CODEC, TriggerType.AUTO, AutoTrigger.MAP_CODEC, TriggerType.BURST, BurstTrigger.MAP_CODEC, TriggerType.CHARGE, ChargeTrigger.MAP_CODEC), Trigger::type, "trigger").validate(trigger -> trigger instanceof ChargeTrigger charge && charge.minimumChargeTicks() > charge.maximumChargeTicks() ? DataResult.error(() -> "minimumChargeTicks exceeds maximumChargeTicks") : DataResult.success(trigger));

		StreamCodec<RegistryFriendlyByteBuf, Trigger> PACKET_CODEC = TriggerType.PACKET_CODEC.<RegistryFriendlyByteBuf>cast().dispatch(Trigger::type, type -> switch (type)
		{
			case SEMI -> ISemiTriggerCodec.PACKET_CODEC;
			case AUTO -> IAutoTriggerCodec.PACKET_CODEC;
			case BURST -> IBurstTriggerCodec.PACKET_CODEC;
			case CHARGE -> IChargeTriggerCodec.PACKET_CODEC;
		});
	}

	/**
	 * Press-edge trigger.
	 */
	@GenerateCodec(strict = true)
	public record SemiTrigger() implements Trigger, ISemiTriggerCodec
	{
		@Override
		public TriggerType type()
		{
			return TriggerType.SEMI;
		}
	}

	/**
	 * Held automatic trigger.
	 */
	@GenerateCodec(strict = true)
	public record AutoTrigger() implements Trigger, IAutoTriggerCodec
	{
		@Override
		public TriggerType type()
		{
			return TriggerType.AUTO;
		}
	}

	/**
	 * Burst-specific rounds and scheduling.
	 */
	@GenerateCodec(strict = true)
	public record BurstTrigger(
			@CodecRange(min = 1, max = 99) int rounds,
			@CodecRange(min = 1) int intervalTicks,
			@CodecRange(min = 1) Optional<Integer> interburstCooldownTicks
	) implements Trigger, IBurstTriggerCodec
	{
		@Override
		public TriggerType type()
		{
			return TriggerType.BURST;
		}

		/**
		 * Resolves the containing weapon's default cooldown.
		 */
		public int effectiveInterburstCooldownTicks(int automaticRepeatDelay)
		{
			return interburstCooldownTicks.orElse(automaticRepeatDelay);
		}
	}

	/**
	 * Held charge timing and release policy.
	 */
	@GenerateCodec(strict = true)
	public record ChargeTrigger(
			@CodecRange(min = 1) int maximumChargeTicks,
			@CodecRange(min = 1) @CodecDefault("1") int minimumChargeTicks,
			ReleasePolicy releasePolicy
	) implements Trigger, IChargeTriggerCodec
	{
		public static final Codec<ChargeTrigger> CODEC = IChargeTriggerCodec.CODEC.validate(value -> value.minimumChargeTicks() <= value.maximumChargeTicks() ? DataResult.success(value) : DataResult.error(() -> "minimumChargeTicks exceeds maximumChargeTicks"));

		@Override
		public TriggerType type()
		{
			return TriggerType.CHARGE;
		}
	}

	/**
	 * Server aim impulses in degrees and recovery duration in ticks.
	 */
	@GenerateCodec(strict = true)
	public record Recoil(
			@CodecRange(min = 0) float hipPitchDegrees,
			@CodecRange(min = 0) float hipYawDegrees,
			@CodecRange(min = 0) float aimPitchDegrees,
			@CodecRange(min = 0) float aimYawDegrees,
			@CodecRange(min = 0) int recoveryTicks
	) implements IRecoilCodec
	{
		/**
		 * No server aim recoil.
		 */
		public static final Recoil ZERO = new Recoil(0, 0, 0, 0, 0);
	}

	/**
	 * Cone half-angles and movement multipliers.
	 */
	@GenerateCodec(strict = true)
	public record Spread(
			@CodecRange(min = 0) float hipDegrees,
			@CodecRange(min = 0) float aimDegrees,
			@CodecRange(min = 0) @CodecDefault("1.0f") float movingMultiplier,
			@CodecRange(min = 0) @CodecDefault("1.0f") float sprintingMultiplier
	) implements ISpreadCodec
	{
		/**
		 * No cone spread.
		 */
		public static final Spread DEFAULT = new Spread(0, 0, 1, 1);
	}

	/**
	 * One linear falloff sample in normalized distance and damage.
	 */
	@GenerateCodec(strict = true)
	public record FalloffPoint(
			@CodecRange(min = 0, max = 1) float distanceFraction,
			@CodecRange(min = 0, max = 1) float multiplier
	) implements IFalloffPointCodec
	{
	}

	/**
	 * Generated authoring shape preserving optional fields before sibling-dependent normalization.
	 */
	@GenerateCodec(strict = true)
	public record BlasterStatsFields(
			@CodecRange(min = 0) float damage,
			@CodecRange(min = 1) int range,
			@CodecRange(min = 1) int automaticRepeatDelay,
			Optional<Identifier> fireSound,
			@SelfCodec Optional<HeatDefinition> heat,
			@SelfCodec Optional<BlasterItem.Cooling> cooling,
			@CodecRange(min = 0) Optional<Float> damageRange,
			@SelfCodec Ammo ammo,
			@SelfCodec BlasterConfiguration configuration,
			@SelfCodec Modes modes,
			@SelfCodec Optional<Recoil> recoil,
			@SelfCodec Optional<Spread> spread,
			@CodecSize(min = 2) @SelfCodec Optional<List<FalloffPoint>> falloff
	) implements IBlasterStatsFieldsCodec
	{
	}

	/**
	 * Project fields to stats.
	 */
	private static DataResult<BlasterStats> fromFields(BlasterStatsFields fields)
	{
		var heat = fields.heat().orElse(HeatDefinition.ZERO);

		if (heat.capacity() == 0 && !heat.equals(HeatDefinition.ZERO))
			return DataResult.error(() -> "Disabled heat must have zero costs, rates and penalties");

		if (fields.cooling().isPresent() && heat.capacity() == 0)
			return DataResult.error(() -> "cooling requires nonzero heat capacity");

		if (fields.damageRange().orElse((float)fields.range()) > fields.range())
			return DataResult.error(() -> "damageRange cannot exceed range");

		var falloff = fields.falloff().orElse(_defaultFalloff);
		if (falloff.getFirst().distanceFraction() != 0 || falloff.getLast().distanceFraction() != 1)
			return DataResult.error(() -> "falloff must span distance fractions 0 through 1");

		for (var index = 1; index < falloff.size(); index++)
		{
			if (falloff.get(index).distanceFraction() <= falloff.get(index - 1).distanceFraction())
				return DataResult.error(() -> "falloff distance fractions must be strictly increasing");
		}

		if (fields.ammo().feed() instanceof ChargeStoreFeed && !(fields.ammo().consumption() instanceof ComponentChargeConsumption))
			return DataResult.error(() -> "charge_store requires component_charge consumption");

		return DataResult.success(new BlasterStats(
				fields.damage(),
				fields.range(),
				fields.automaticRepeatDelay(),
				fields.fireSound(),
				heat,
				fields.cooling(),
				fields.damageRange().orElse((float)fields.range()),
				fields.ammo(),
				fields.configuration(),
				fields.modes(),
				fields.recoil().orElse(Recoil.ZERO),
				fields.spread().orElse(Spread.DEFAULT),
				falloff
		));
	}

	/**
	 * Projects the stats values to fields.
	 */
	private static BlasterStatsFields toFields(BlasterStats stats)
	{
		return new BlasterStatsFields(
				stats.damage(),
				stats.range(),
				stats.automaticRepeatDelay(),
				stats.fireSound(),
				GalaxiesCodecs.optionalUnless(stats.heat(), HeatDefinition.ZERO),
				stats.cooling(),
				GalaxiesCodecs.optionalUnless(stats.damageRange(), (float)stats.range()),
				stats.ammo(),
				stats.configuration(),
				stats.modes(),
				GalaxiesCodecs.optionalUnless(stats.recoil(), Recoil.ZERO),
				GalaxiesCodecs.optionalUnless(stats.spread(), Spread.DEFAULT),
				GalaxiesCodecs.optionalUnless(stats.falloff(), _defaultFalloff)
		);
	}

	/**
	 * Default constant-damage curve.
	 */
	private static final List<FalloffPoint> _defaultFalloff = List.of(new FalloffPoint(0, 1), new FalloffPoint(1, 1));

	public static final Codec<BlasterStats> CODEC = BlasterStatsFields.CODEC.comapFlatMap(BlasterStats::fromFields, BlasterStats::toFields);

	public static final StreamCodec<RegistryFriendlyByteBuf, BlasterStats> PACKET_CODEC = BlasterStatsFields.PACKET_CODEC.map(fields -> fromFields(fields).getOrThrow(), BlasterStats::toFields);
}
