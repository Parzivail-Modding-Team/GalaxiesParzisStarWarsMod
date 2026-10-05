package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.*;
import dev.pswg.generated.recordbuilders.IAmmoBuilder;
import dev.pswg.generated.recordbuilders.IBlasterStatsBuilder;
import dev.pswg.item.BlasterItem;
import dev.pswg.mutablerecord.MutableRecord;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Statistics for a blaster.
 *
 * @param damage               Base direct-hit damage in Minecraft damage points.
 * @param range                Maximum trace distance in blocks.
 * @param automaticRepeatDelay Minimum accepted-shot interval in ticks.
 * @param fireSound            Optional fire-sound override.
 * @param heat                 Heat values; absence in the definition becomes zero heat.
 * @param cooling              Cooling bypass windows; absence becomes zero bypass windows.
 * @param damageRange          Distance used to evaluate the falloff curve.
 * @param ammo                 Required ammunition feed and consumption definition.
 * @param configuration        Required archetype and native item-model configuration.
 * @param modes                Required ordered firing modes.
 * @param recoil               Configured server aim recoil.
 * @param spread               Configured cone spread.
 * @param falloff              Ordered damage multipliers from distance fraction zero through one.
 */
@MutableRecord
public record BlasterStats(
		float damage,
		int range,
		int automaticRepeatDelay,
		Optional<Identifier> fireSound,
		HeatDefinition heat,
		BlasterItem.Cooling cooling,
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
	 * Shared definition and runtime heat values, preserving fractional drain rates without a second stats adapter.
	 *
	 * @param capacity             Maximum normal heat, in units.
	 * @param perRound             Heat added per accepted shot.
	 * @param drainSpeed           Passive and ordinary-vent drain, in units per tick.
	 * @param overheatPenalty      Heat added on overheat.
	 * @param overheatDrainSpeed   Overheat-vent drain, in units per tick.
	 * @param passiveCooldownDelay Delay before passive cooling begins, in ticks.
	 * @param overchargeBonus      Secondary-bypass overcharge duration, in ticks.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record HeatDefinition(
			int capacity,
			int perRound,
			float drainSpeed,
			int overheatPenalty,
			float overheatDrainSpeed,
			int passiveCooldownDelay,
			int overchargeBonus
	) implements IHeatDefinitionCodec
	{
		/**
		 * Zero heat used when the heat object is absent.
		 */
		public static final HeatDefinition ZERO = new HeatDefinition(0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Required ammunition declaration and feed-specific settings.
	 *
	 * @param feed                Registered feed ID.
	 * @param ingredient          Native Minecraft ingredient selecting ammunition.
	 * @param consumption         Typed source-ammunition consumption policy.
	 * @param magazineSize        Magazine rounds, present only for magazine feeds.
	 * @param reloadTicks         Reload duration, present for magazine and charge-store feeds.
	 * @param chargeCapacityUnits Loaded-charge capacity, present only for charge-store feeds.
	 * @param creativePolicy      Registered creative ammunition policy.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	@MutableRecord
	public record Ammo(
			Identifier feed,
			@SelfCodec Ingredient ingredient,
			@SelfCodec Consumption consumption,
			Optional<Integer> magazineSize,
			Optional<Integer> reloadTicks,
			Optional<Integer> chargeCapacityUnits,
			@CodecDefault("dev.pswg.data.BlasterStats.FREE_AMMO_POLICY") Identifier creativePolicy
	) implements IAmmoCodec, IAmmoBuilder
	{
	}

	/**
	 * Typed ammunition-consumption policy.
	 */
	public interface Consumption
	{
		/**
		 * Returns this consumption variant's registered discriminator.
		 */
		Identifier type();

		/**
		 * Strict codec for the built-in consumption variants.
		 */
		Codec<Consumption> CODEC = CONSUMPTION_CODEC;
	}

	/**
	 * Consumes a positive item count for each round.
	 *
	 * @param itemsPerRound Number of matching items debited for one round.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ItemCountConsumption(
			@CodecRange(min = 1) int itemsPerRound
	) implements Consumption, IItemCountConsumptionCodec
	{
		@Override
		public Identifier type()
		{
			return CONSUMPTION_ITEM_COUNT;
		}
	}

	/**
	 * Consumes charge units from a data component on an ammunition pack.
	 *
	 * @param component      Registered pack charge-component ID.
	 * @param unitsPerRound  Charge units consumed for one round.
	 * @param emptyContainer Optional exact item/count result emitted when the source pack reaches zero.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ComponentChargeConsumption(
			Identifier component,
			@CodecRange(min = 1) int unitsPerRound,
			@SelfCodec Optional<EmptyContainer> emptyContainer
	) implements Consumption, IComponentChargeConsumptionCodec
	{
		@Override
		public Identifier type()
		{
			return CONSUMPTION_COMPONENT_CHARGE;
		}
	}

	/**
	 * Stack to produce when a charge pack is emptied.
	 *
	 * @param item  Output item ID.
	 * @param count Output item count.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record EmptyContainer(
			Identifier item,
			@CodecRange(min = 1, max = 64) int count
	) implements IEmptyContainerCodec
	{
	}

	/**
	 * Required archetype/model configuration and optional stance/conversion profiles.
	 *
	 * @param archetype       Data-defined archetype ID.
	 * @param itemModel       Native item-model root ID.
	 * @param stanceProfile   Optional named stance profile; absence resolves by archetype ID.
	 * @param fieldConversion Optional bounded field-conversion options.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record BlasterConfiguration(
			Identifier archetype,
			Identifier itemModel,
			Optional<Identifier> stanceProfile,
			@SelfCodec Optional<FieldConversion> fieldConversion
	) implements IBlasterConfigurationCodec
	{
	}

	/**
	 * Field-conversion options.
	 *
	 * @param options Conversion options available on this blaster.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record FieldConversion(
			@SelfCodec List<FieldConversionOption> options
	) implements IFieldConversionCodec
	{
		/**
		 * Copies options and rejects duplicate option IDs.
		 */
		public FieldConversion
		{
			var ids = new HashSet<Identifier>();
			for (var option : options)
				Preconditions.checkArgument(ids.add(option.id()), "Duplicate field-conversion option ID: %s", option.id());
		}
	}

	/**
	 * One same-registry target and its optional availability conditions.
	 *
	 * @param id              Stable option ID.
	 * @param targetBlasterId Target definition in the blasters registry.
	 * @param durationTicks   Active duration; zero means manual deactivation.
	 * @param conditions      ANDed conditions; an empty list is always available.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record FieldConversionOption(
			Identifier id,
			Identifier targetBlasterId,
			@CodecRange(min = 0) @CodecDefault("0") int durationTicks,
			@CodecDefault("java.util.List.of()") @SelfCodec List<FieldConversionCondition> conditions
	) implements IFieldConversionOptionCodec
	{
	}

	/**
	 * Typed field-conversion availability condition.
	 */
	public interface FieldConversionCondition
	{
		/**
		 * Returns this condition's registered discriminator.
		 */
		Identifier type();

		/**
		 * Strict codec for the built-in condition variants.
		 */
		Codec<FieldConversionCondition> CODEC = FIELD_CONDITION_CODEC;
	}

	/**
	 * Requires the selected firing mode to match.
	 *
	 * @param modeId Required selected mode ID.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ModeIsCondition(Identifier modeId) implements FieldConversionCondition, IModeIsConditionCodec
	{
		@Override
		public Identifier type()
		{
			return CONDITION_MODE_IS;
		}
	}

	/**
	 * Requires a named attachment to be installed.
	 *
	 * @param attachmentSlot Slot ID to inspect.
	 * @param attachmentId   Required installed attachment ID.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record HasAttachmentCondition(
			Identifier attachmentSlot,
			Identifier attachmentId
	) implements FieldConversionCondition, IHasAttachmentConditionCodec
	{
		@Override
		public Identifier type()
		{
			return CONDITION_HAS_ATTACHMENT;
		}
	}

	/**
	 * Requires at least the declared amount of loaded charge.
	 *
	 * @param units Minimum loaded units.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record LoadedChargeMinCondition(
			@CodecRange(min = 1) int units
	) implements FieldConversionCondition, ILoadedChargeMinConditionCodec
	{
		@Override
		public Identifier type()
		{
			return CONDITION_LOADED_CHARGE_MIN;
		}
	}

	/**
	 * Firing modes and the default mode.
	 *
	 * @param defaultMode ID of one listed mode.
	 * @param options     Ordered unique modes.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Modes(
			@CodecName("default") Identifier defaultMode,
			@CodecSize(min = 1) @SelfCodec List<Mode> options
	) implements IModesCodec
	{
		public Modes
		{
			var ids = new HashSet<Identifier>();
			for (var option : options)
				Preconditions.checkArgument(ids.add(option.id()), "Duplicate mode ID: %s", option.id());

			Preconditions.checkArgument(ids.contains(defaultMode), "Default mode is not listed: %s", defaultMode);
		}
	}

	/**
	 * One firing mode, its typed trigger, and its registered behavior profile.
	 *
	 * @param id              Stable mode ID.
	 * @param trigger         Typed firing trigger.
	 * @param behaviorProfile Registered behavior-profile ID.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Mode(
			Identifier id,
			@SelfCodec Trigger trigger,
			Identifier behaviorProfile
	) implements IModeCodec
	{
	}

	/**
	 * Extensible typed firing trigger.
	 */
	public interface Trigger
	{
		/**
		 * Returns this trigger's registered discriminator.
		 */
		Identifier type();

		/**
		 * Strict codec for built-in and registered triggers.
		 */
		Codec<Trigger> CODEC = TRIGGER_CODEC;
	}

	/**
	 * Built-in trigger that fires once on a valid press edge.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record SemiTrigger() implements Trigger, ISemiTriggerCodec
	{
		/**
		 * Returns the semi trigger ID.
		 */
		@Override
		public Identifier type()
		{
			return TRIGGER_SEMI;
		}
	}

	/**
	 * Built-in trigger that repeats while the server trigger lease remains valid.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record AutoTrigger() implements Trigger, IAutoTriggerCodec
	{
		/**
		 * Returns the automatic trigger ID.
		 */
		@Override
		public Identifier type()
		{
			return TRIGGER_AUTO;
		}
	}

	/**
	 * Schedules a bounded number of independent shots.
	 *
	 * @param rounds                  Number of rounds in one burst.
	 * @param intervalTicks           Ticks between burst rounds.
	 * @param interburstCooldownTicks Optional delay after the burst; absence uses automaticRepeatDelay.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record BurstTrigger(
			@CodecRange(min = 1) int rounds,
			@CodecRange(min = 1) int intervalTicks,
			@CodecRange(min = 1) Optional<Integer> interburstCooldownTicks
	) implements Trigger, IBurstTriggerCodec
	{
		/**
		 * Resolves the default interburst delay against the containing stats definition.
		 *
		 * @param automaticRepeatDelay The containing blaster's required repeat interval.
		 *
		 * @return Explicit interburst cooldown or the containing repeat interval.
		 */
		public int effectiveInterburstCooldownTicks(int automaticRepeatDelay)
		{
			return interburstCooldownTicks.orElse(automaticRepeatDelay);
		}

		/**
		 * Returns the burst trigger ID.
		 */
		@Override
		public Identifier type()
		{
			return TRIGGER_BURST;
		}
	}

	/**
	 * Holds a trigger until minimum charge, maximum charge, or release policy resolves it.
	 *
	 * @param maximumChargeTicks Maximum held duration.
	 * @param minimumChargeTicks Minimum duration; defaults to one tick.
	 * @param releasePolicy      Whether to fire on release or at full charge.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ChargeTrigger(
			@CodecRange(min = 1) int maximumChargeTicks,
			@CodecRange(min = 1) @CodecDefault("1") int minimumChargeTicks,
			@SelfCodec ReleasePolicy releasePolicy
	) implements Trigger, IChargeTriggerCodec
	{
		/**
		 * Returns the charge trigger ID.
		 */
		@Override
		public Identifier type()
		{
			return TRIGGER_CHARGE;
		}
	}

	/**
	 * Charge-trigger release behavior.
	 */
	public enum ReleasePolicy implements StringRepresentable
	{
		/**
		 * Fire once when released after the minimum charge duration.
		 */
		FIRE_ON_RELEASE("fire_on_release"),

		/**
		 * Fire once at maximum charge even if the trigger remains held.
		 */
		FIRE_AT_FULL("fire_at_full");

		public static final Codec<ReleasePolicy> CODEC = StringRepresentable.fromValues(ReleasePolicy::values);

		/**
		 * Serialized release-policy identifier.
		 */
		private final String _id;

		ReleasePolicy(String id)
		{
			_id = id;
		}

		/**
		 * Returns the serialized release-policy identifier.
		 */
		@Override
		public String getSerializedName()
		{
			return _id;
		}
	}

	/**
	 * Configured server-side aim impulse and recovery.
	 *
	 * @param hipPitchDegrees Positive upward hip-fire pitch impulse.
	 * @param hipYawDegrees   Non-negative hip-fire yaw magnitude.
	 * @param aimPitchDegrees Positive upward aimed pitch impulse.
	 * @param aimYawDegrees   Non-negative aimed yaw magnitude.
	 * @param recoveryTicks   Linear return duration in ticks.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Recoil(
			@CodecRange(min = 0, max = 90) float hipPitchDegrees,
			@CodecRange(min = 0, max = 90) float hipYawDegrees,
			@CodecRange(min = 0, max = 90) float aimPitchDegrees,
			@CodecRange(min = 0, max = 90) float aimYawDegrees,
			@CodecRange(min = 0) int recoveryTicks
	) implements IRecoilCodec
	{
		/**
		 * Zero recoil used when the recoil object is absent.
		 */
		public static final Recoil ZERO = new Recoil(0, 0, 0, 0, 0);
	}

	/**
	 * Configured hip/aim cone half-angles and movement multipliers.
	 *
	 * @param hipDegrees          Hip-fire cone half-angle.
	 * @param aimDegrees          Aimed cone half-angle.
	 * @param movingMultiplier    Spread multiplier while moving.
	 * @param sprintingMultiplier Spread multiplier while sprinting.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Spread(
			@CodecRange(min = 0, max = 90) float hipDegrees,
			@CodecRange(min = 0, max = 90) float aimDegrees,
			@CodecRange(min = 0) @CodecDefault("1.0f") float movingMultiplier,
			@CodecRange(min = 0) @CodecDefault("1.0f") float sprintingMultiplier
	) implements ISpreadCodec
	{
		/**
		 * Zero spread and identity movement multipliers used when spread is absent.
		 */
		public static final Spread DEFAULT = new Spread(0, 0, 1, 1);
	}

	/**
	 * One normalized distance sample in a linear damage-falloff curve.
	 *
	 * @param distanceFraction Normalized distance within damageRange.
	 * @param multiplier       Base-damage multiplier at that distance.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record FalloffPoint(
			@CodecRange(min = 0, max = 1) float distanceFraction,
			@CodecRange(min = 0, max = 1) float multiplier
	) implements IFalloffPointCodec
	{
	}

	/**
	 * Generated codec wire shape. Optional fields stay optional here so sibling-dependent defaults can be resolved
	 * into a fully populated {@link BlasterStats} value after decoding.
	 *
	 * @param damage               Base direct-hit damage.
	 * @param range                Maximum trace distance.
	 * @param automaticRepeatDelay Minimum accepted-shot interval.
	 * @param fireSound            Optional fire-sound override.
	 * @param heat                 Optional heat object.
	 * @param cooling              Optional cooling bypass definition.
	 * @param damageRange          Optional falloff distance; defaults to range.
	 * @param ammo                 Required ammunition definition.
	 * @param configuration        Required profile and item-model configuration.
	 * @param modes                Required mode list.
	 * @param recoil               Optional recoil definition.
	 * @param spread               Optional spread definition.
	 * @param falloff              Optional falloff curve.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record BlasterStatsFields(
			@CodecRange(min = 0) float damage,
			@CodecRange(min = 1) int range,
			@CodecRange(min = 1) int automaticRepeatDelay,
			Optional<Identifier> fireSound,
			@SelfCodec Optional<HeatDefinition> heat,
			@UseCodec(customCodec = @CodecSource(source = BlasterStats.class, member = "COOLING_OPTIONAL_CODEC"))
			@CodecDefault("java.util.Optional.empty()") Optional<BlasterItem.Cooling> cooling,
			@CodecRange(min = 0) Optional<Float> damageRange,
			@SelfCodec Ammo ammo,
			@SelfCodec BlasterConfiguration configuration,
			@SelfCodec Modes modes,
			@SelfCodec Optional<Recoil> recoil,
			@SelfCodec Optional<Spread> spread,
			@CodecSize(min = 2) @SelfCodec Optional<List<FalloffPoint>> falloff
	) implements IBlasterStatsFieldsCodec
	{
		/**
		 * Copies an optional falloff list to keep the wire value immutable.
		 */
		public BlasterStatsFields
		{
			falloff = falloff.map(List::copyOf);
		}
	}

	/**
	 * IDs for the supported ammunition feed policies.
	 */
	public static final Identifier FEED_MAGAZINE = id("magazine");

	/**
	 * ID for the discrete per-shot ammunition feed.
	 */
	public static final Identifier FEED_PER_SHOT = id("per_shot");

	/**
	 * ID for the loaded component-charge feed.
	 */
	public static final Identifier FEED_CHARGE_STORE = id("charge_store");

	/**
	 * ID for the default creative ammunition policy.
	 */
	public static final Identifier FREE_AMMO_POLICY = id("free_ammo");

	/**
	 * ID for item-count ammunition consumption.
	 */
	public static final Identifier CONSUMPTION_ITEM_COUNT = id("item_count");

	/**
	 * ID for component-charge ammunition consumption.
	 */
	public static final Identifier CONSUMPTION_COMPONENT_CHARGE = id("component_charge");

	/**
	 * Built-in semi trigger ID.
	 */
	public static final Identifier TRIGGER_SEMI = id("semi");

	/**
	 * Built-in automatic trigger ID.
	 */
	public static final Identifier TRIGGER_AUTO = id("auto");

	/**
	 * Built-in burst trigger ID.
	 */
	public static final Identifier TRIGGER_BURST = id("burst");

	/**
	 * Built-in charge trigger ID.
	 */
	public static final Identifier TRIGGER_CHARGE = id("charge");

	/**
	 * Built-in mode-is field-conversion condition ID.
	 */
	public static final Identifier CONDITION_MODE_IS = id("mode_is");

	/**
	 * Built-in has-attachment field-conversion condition ID.
	 */
	public static final Identifier CONDITION_HAS_ATTACHMENT = id("has_attachment");

	/**
	 * Built-in loaded-charge-minimum field-conversion condition ID.
	 */
	public static final Identifier CONDITION_LOADED_CHARGE_MIN = id("loaded_charge_min");

	/**
	 * Module-namespaced typed trigger map codecs.
	 */
	private static final Map<Identifier, MapCodec<? extends Trigger>> _triggerCodecs = new ConcurrentHashMap<>();

	/**
	 * Closed built-in ammunition-consumption map codecs.
	 */
	private static final Map<Identifier, MapCodec<? extends Consumption>> _consumptionCodecs = new ConcurrentHashMap<>();

	/**
	 * Closed built-in field-conversion condition map codecs.
	 */
	private static final Map<Identifier, MapCodec<? extends FieldConversionCondition>> _conditionCodecs = new ConcurrentHashMap<>();

	/**
	 * Lock shared by addon trigger registration and type freezing.
	 */
	private static final Object _typeLock = new Object();

	/**
	 * Whether addon trigger registration has been frozen.
	 */
	private static volatile boolean _typesFrozen;

	/**
	 * Constant base-damage curve used when falloff is absent.
	 */
	private static final List<FalloffPoint> DEFAULT_FALLOFF = List.of(new FalloffPoint(0, 1), new FalloffPoint(1, 1));

	static
	{
		_triggerCodecs.put(TRIGGER_SEMI, SemiTrigger.MAP_CODEC);
		_triggerCodecs.put(TRIGGER_AUTO, AutoTrigger.MAP_CODEC);
		_triggerCodecs.put(TRIGGER_BURST, BurstTrigger.MAP_CODEC);
		_triggerCodecs.put(TRIGGER_CHARGE, ChargeTrigger.MAP_CODEC);
		_consumptionCodecs.put(CONSUMPTION_ITEM_COUNT, ItemCountConsumption.MAP_CODEC);
		_consumptionCodecs.put(CONSUMPTION_COMPONENT_CHARGE, ComponentChargeConsumption.MAP_CODEC);
		_conditionCodecs.put(CONDITION_MODE_IS, ModeIsCondition.MAP_CODEC);
		_conditionCodecs.put(CONDITION_HAS_ATTACHMENT, HasAttachmentCondition.MAP_CODEC);
		_conditionCodecs.put(CONDITION_LOADED_CHARGE_MIN, LoadedChargeMinCondition.MAP_CODEC);
	}

	/**
	 * Strict codec for typed firing triggers.
	 */
	public static final Codec<Trigger> TRIGGER_CODEC = GalaxiesCodecs.typedDispatch(
			Identifier.CODEC,
			_triggerCodecs,
			Trigger::type,
			"trigger"
	);

	/**
	 * Strict codec for typed ammunition consumption.
	 */
	private static final Codec<Consumption> CONSUMPTION_CODEC = GalaxiesCodecs.typedDispatch(
			Identifier.CODEC,
			_consumptionCodecs,
			Consumption::type,
			"consumption"
	);

	/**
	 * Strict codec for typed field-conversion conditions.
	 */
	private static final Codec<FieldConversionCondition> FIELD_CONDITION_CODEC = GalaxiesCodecs.typedDispatch(
			Identifier.CODEC,
			_conditionCodecs,
			FieldConversionCondition::type,
			"field-conversion condition"
	);

	/**
	 * Strict bounded codec for the retained runtime cooling record.
	 */
	public static final Codec<BlasterItem.Cooling> COOLING_CODEC = GalaxiesCodecs.strict(BlasterItem.Cooling.MAP_CODEC);

	/**
	 * Optional wrapper retains cooling-object presence for heat cross-validation.
	 */
	public static final Codec<Optional<BlasterItem.Cooling>> COOLING_OPTIONAL_CODEC = COOLING_CODEC.flatXmap(
			cooling -> DataResult.success(Optional.of(cooling)),
			cooling -> cooling.map(DataResult::success)
			                  .orElseGet(() -> DataResult.error(() -> "cooling cannot be encoded as an explicit empty value"))
	);

	/**
	 * Strict generated-fields codec plus sibling-dependent normalization to the public stats record.
	 */
	public static final Codec<BlasterStats> CODEC = BlasterStatsFields.CODEC.flatXmap(
			BlasterStats::fromFields,
			BlasterStats::toFields
	);

	/**
	 * Registers an addon trigger before definitions are decoded and before types are frozen.
	 */
	public static void registerTrigger(Identifier id, MapCodec<? extends Trigger> codec)
	{
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(codec, "codec");

		synchronized (_typeLock)
		{
			Preconditions.checkArgument(!_typesFrozen, "Trigger registration has been frozen");
			Preconditions.checkArgument(!_triggerCodecs.containsKey(id), "Trigger codec already registered: %s", id);
			_triggerCodecs.put(id, codec);
		}
	}

	/**
	 * Prevents further addon trigger registration after module initialization has finalized its types.
	 */
	public static void freezeTypes()
	{
		synchronized (_typeLock)
		{
			_typesFrozen = true;
		}
	}

	/**
	 * Converts non-default fields to a stats block.
	 */
	private static DataResult<BlasterStats> fromFields(BlasterStatsFields fields)
	{
		try
		{
			var heat = fields.heat().orElse(HeatDefinition.ZERO);
			var cooling = fields.cooling().orElse(BlasterItem.Cooling.ZERO);
			if (fields.cooling().isPresent())
				Preconditions.checkArgument(heat.capacity() > 0, "cooling requires heat.capacity greater than zero");
			return DataResult.success(new BlasterStats(
					fields.damage(),
					fields.range(),
					fields.automaticRepeatDelay(),
					fields.fireSound(),
					heat,
					cooling,
					fields.damageRange().orElse((float)fields.range()),
					fields.ammo(),
					fields.configuration(),
					fields.modes(),
					fields.recoil().orElse(Recoil.ZERO),
					fields.spread().orElse(Spread.DEFAULT),
					fields.falloff().orElse(DEFAULT_FALLOFF)
			));
		}
		catch (IllegalArgumentException exception)
		{
			return DataResult.error(exception::getMessage);
		}
	}

	/**
	 * Converts non-default stats to a field block.
	 */
	private static DataResult<BlasterStatsFields> toFields(BlasterStats stats)
	{
		return DataResult.success(new BlasterStatsFields(
				stats.damage(),
				stats.range(),
				stats.automaticRepeatDelay(),
				stats.fireSound(),
				optionalUnless(stats.heat(), HeatDefinition.ZERO),
				optionalUnless(stats.cooling(), BlasterItem.Cooling.ZERO),
				Float.compare(stats.damageRange(), stats.range()) == 0 ? Optional.empty() : Optional.of(stats.damageRange()),
				stats.ammo(),
				stats.configuration(),
				stats.modes(),
				optionalUnless(stats.recoil(), Recoil.ZERO),
				optionalUnless(stats.spread(), Spread.DEFAULT),
				stats.falloff().equals(DEFAULT_FALLOFF) ? Optional.empty() : Optional.of(stats.falloff())
		));
	}

	/**
	 * Returns an empty optional when a value equals its canonical default.
	 */
	private static <A> Optional<A> optionalUnless(A value, A defaultValue)
	{
		return Objects.equals(value, defaultValue) ? Optional.empty() : Optional.of(value);
	}

	/**
	 * Creates a namespaced identifier in this module.
	 */
	private static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath("pswg_blasters", path);
	}
}
