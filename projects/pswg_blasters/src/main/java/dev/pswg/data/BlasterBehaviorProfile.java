package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blaster behavior profile.
 *
 * @param delivery    Registered delivery strategy.
 * @param damageType  Native namespaced damage-type ID.
 * @param effects     Ordered effect definitions, empty when omitted.
 * @param muzzles     Optional authored muzzle sockets and emission policy.
 * @param chargedShot Optional bounded charged-shot scaling configuration.
 */
@GenerateCodec(packetCodec = false, strict = true)
public record BlasterBehaviorProfile(
		@SelfCodec DamageDeliveryStrategy delivery,
		Identifier damageType,
		@SelfCodec
		@CodecDefault("java.util.List.of()")
		List<Effect> effects,
		@SelfCodec Optional<Muzzles> muzzles,
		@SelfCodec Optional<ChargedShot> chargedShot
) implements IBlasterBehaviorProfileCodec
{
	/**
	 * Damage delivery strategy (e.g., projectile or hitscan).
	 */
	public interface DamageDeliveryStrategy
	{
		/**
		 * Returns this delivery variant's registered discriminator.
		 */
		Identifier type();

		Codec<DamageDeliveryStrategy> CODEC = BlasterBehaviorProfile.DELIVERY_CODEC;
	}

	/**
	 * Hitscan delivery strategy.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record HitscanDamageDeliveryStrategy() implements DamageDeliveryStrategy, IHitscanDamageDeliveryStrategyCodec
	{
		/**
		 * Returns the built-in hitscan discriminator.
		 */
		@Override
		public Identifier type()
		{
			return DELIVERY_HITSCAN;
		}
	}

	/**
	 * Projectile delivery strategy.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ProjectileDamageDeliveryStrategy() implements DamageDeliveryStrategy, IProjectileDamageDeliveryStrategyCodec
	{
		/**
		 * Returns the built-in projectile discriminator.
		 */
		@Override
		public Identifier type()
		{
			return DELIVERY_PROJECTILE;
		}
	}

	/**
	 * Typed, registered effect definition.
	 */
	public interface Effect
	{
		/**
		 * Returns this effect variant's registered discriminator.
		 */
		Identifier type();

		/**
		 * Returns the closed application phase for this effect.
		 */
		EffectPhase when();

		/**
		 * Strict codec for registered effect variants.
		 */
		Codec<Effect> CODEC = BlasterBehaviorProfile.EFFECT_CODEC;
	}

	/**
	 * Phases when an effect may be applied.
	 */
	public enum EffectPhase implements StringRepresentable
	{
		/**
		 * When the blaster is fired.
		 */
		ON_FIRE("on_fire"),

		/**
		 * When the bolt hits a block.
		 */
		ON_BLOCK_HIT("on_block_hit"),

		/**
		 * When the bolt hits an entity.
		 */
		ON_ENTITY_HIT("on_entity_hit"),

		/**
		 * When the bolt detonates.
		 */
		ON_DETONATE("on_detonate");

		public static final Codec<EffectPhase> CODEC = StringRepresentable.fromEnum(EffectPhase::values);

		/**
		 * Serialized effect-phase identifier.
		 */
		private final String _id;

		EffectPhase(String id)
		{
			_id = id;
		}

		@Override
		public @NonNull String getSerializedName()
		{
			return _id;
		}

		/**
		 * Returns the serialized effect-phase identifier.
		 */
		public String id()
		{
			return _id;
		}
	}

	/**
	 * Existing muzzle sockets on the blaster.
	 *
	 * @param sockets    Distinct non-empty G3D socket IDs in emission order.
	 * @param selection  Selection policy.
	 * @param costPolicy Cost policy; the codec defaults this to {@code per_emission} except when {@code all} is selected.
	 */
	public record Muzzles(List<String> sockets, Selection selection, CostPolicy costPolicy)
	{
		public static final Codec<Muzzles> CODEC = createMuzzlesCodec();
	}

	/**
	 * Muzzle socket selection policy.
	 */
	public enum Selection implements StringRepresentable
	{
		/**
		 * Use the first configured socket.
		 */
		SINGLE("single"),

		/**
		 * Use one socket per shot, rotating after commit.
		 */
		ROUND_ROBIN("round_robin"),

		/**
		 * Use every socket for each shot.
		 */
		ALL("all");

		public static final Codec<Selection> CODEC = StringRepresentable.fromEnum(Selection::values);

		/**
		 * Serialized selection identifier.
		 */
		private final String _id;

		Selection(String id)
		{
			_id = id;
		}

		@Override
		public String getSerializedName()
		{
			return _id;
		}

		/**
		 * Returns the serialized selection identifier.
		 */
		public String id()
		{
			return _id;
		}
	}

	/**
	 * Shot cost policy.
	 */
	public enum CostPolicy implements StringRepresentable
	{
		/**
		 * Charge one cost per trigger, even if multiple emissions are from one trigger.
		 */
		PER_TRIGGER("per_trigger"),

		/**
		 * Charge one cost per emission, even if multiple emissions are from one trigger.
		 */
		PER_EMISSION("per_emission");

		public static final Codec<CostPolicy> CODEC = StringRepresentable.fromEnum(CostPolicy::values);

		/**
		 * Serialized cost-policy identifier.
		 */
		private final String _id;

		CostPolicy(String id)
		{
			_id = id;
		}

		@Override
		public String getSerializedName()
		{
			return _id;
		}

		/**
		 * Returns the serialized cost-policy identifier.
		 */
		public String id()
		{
			return _id;
		}
	}

	/**
	 * Shot charging policy.
	 *
	 * @param source                  {@code held_duration} or {@code loaded_component_charge}.
	 * @param maximumDamageMultiplier Maximum damage scale.
	 * @param consume                 {@code one_round} or {@code all_remaining_component_charge}.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ChargedShot(
			@UseCodec(customCodec = @CodecSource(source = BlasterBehaviorProfile.class, member = "CHARGED_SHOT_SOURCE_CODEC"))
			String source,
			@CodecRange(min = 1)
			float maximumDamageMultiplier,
			@UseCodec(customCodec = @CodecSource(source = BlasterBehaviorProfile.class, member = "CHARGED_SHOT_CONSUME_CODEC"))
			String consume
	) implements IChargedShotCodec
	{
	}

	/**
	 * Existing muzzles.
	 *
	 * @param sockets    Socket IDs.
	 * @param selection  Selection or its default.
	 * @param costPolicy Optional cost policy.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record MuzzlesFields(
			@CodecSize(min = 1)
			List<String> sockets,
			@SelfCodec
			@CodecDefault("dev.pswg.data.BlasterBehaviorProfile.Selection.SINGLE")
			Selection selection,
			@SelfCodec Optional<CostPolicy> costPolicy
	) implements IMuzzlesFieldsCodec
	{
	}

	/**
	 * Registers a delivery codec before behavior-profile types are frozen. The discriminator is added by this class.
	 */
	public static void registerDelivery(Identifier id, MapCodec<? extends DamageDeliveryStrategy> codec)
	{
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(codec, "codec");
		synchronized (_typeLock)
		{
			Preconditions.checkArgument(!_typesFrozen, "Behavior-profile types are frozen; cannot register delivery %s", id);
			Preconditions.checkArgument(_deliveryCodecs.putIfAbsent(id, codec) == null,
			                            "Delivery codec already registered: %s", id);
		}
	}

	/**
	 * Registers an effect codec before behavior-profile types are frozen. The discriminator is added by this class.
	 */
	public static void registerEffect(Identifier id, MapCodec<? extends Effect> codec)
	{
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(codec, "codec");
		synchronized (_typeLock)
		{
			Preconditions.checkArgument(!_typesFrozen, "Behavior-profile types are frozen; cannot register effect %s", id);
			Preconditions.checkArgument(_effectCodecs.putIfAbsent(id, codec) == null,
			                            "Effect codec already registered: %s", id);
		}
	}

	/**
	 * Freezes both type registries after module and addon registration has completed. Repeated calls are harmless.
	 */
	public static void freezeTypes()
	{
		synchronized (_typeLock)
		{
			_typesFrozen = true;
		}
	}

	/**
	 * Creates the selection-dependent muzzle codec over its generated, strict wire-record codec.
	 */
	private static Codec<Muzzles> createMuzzlesCodec()
	{
		return MuzzlesFields.CODEC.flatXmap(BlasterBehaviorProfile::fromMuzzlesFields, BlasterBehaviorProfile::toMuzzlesFields);
	}

	/**
	 * Populates a muzzle from its optional fields.
	 */
	private static DataResult<Muzzles> fromMuzzlesFields(MuzzlesFields fields)
	{
		try
		{
			Preconditions.checkArgument(fields.selection() != Selection.ALL || fields.costPolicy().isPresent(),
			                            "muzzles.costPolicy is required when selection is all");
			return DataResult.success(new Muzzles(
					fields.sockets(),
					fields.selection(),
					fields.costPolicy().orElse(CostPolicy.PER_EMISSION)
			));
		}
		catch (IllegalArgumentException exception)
		{
			return DataResult.error(exception::getMessage);
		}
	}

	/**
	 * Populates on-default fields from a muzzle.
	 */
	private static DataResult<MuzzlesFields> toMuzzlesFields(Muzzles muzzles)
	{
		try
		{
			var costPolicy = muzzles.selection() == Selection.ALL || muzzles.costPolicy() != CostPolicy.PER_EMISSION
			                 ? Optional.of(muzzles.costPolicy())
			                 : Optional.<CostPolicy>empty();
			return DataResult.success(new MuzzlesFields(muzzles.sockets(), muzzles.selection(), costPolicy));
		}
		catch (IllegalArgumentException exception)
		{
			return DataResult.error(exception::getMessage);
		}
	}

	/**
	 * Returns whether a value is one of the closed charged-shot source names.
	 */
	private static boolean isChargedShotSource(String source)
	{
		return source.equals("held_duration") || source.equals("loaded_component_charge");
	}

	/**
	 * Returns whether a value is one of the closed charged-shot consume names.
	 */
	private static boolean isChargedShotConsume(String consume)
	{
		return consume.equals("one_round") || consume.equals("all_remaining_component_charge");
	}

	/**
	 * Creates the module-namespaced identifier used by this profile.
	 */
	private static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath("pswg_blasters", path);
	}

	/**
	 * Hitscan delivery strategy ID.
	 */
	public static final Identifier DELIVERY_HITSCAN = id("hitscan");

	/**
	 * Projectile delivery strategy ID.
	 */
	public static final Identifier DELIVERY_PROJECTILE = id("projectile");

	/**
	 * Codec for the closed charged-shot source strings.
	 */
	public static final Codec<String> CHARGED_SHOT_SOURCE_CODEC = GalaxiesCodecs.validate(
			Codec.STRING,
			source -> Preconditions.checkArgument(isChargedShotSource(source),
			                                      "Expected held_duration or loaded_component_charge")
	);

	/**
	 * Codec for the closed charged-shot consumption strings.
	 */
	public static final Codec<String> CHARGED_SHOT_CONSUME_CODEC = GalaxiesCodecs.validate(
			Codec.STRING,
			consume -> Preconditions.checkArgument(isChargedShotConsume(consume),
			                                       "Expected one_round or all_remaining_component_charge")
	);

	/**
	 * Mutable registered delivery codec table, populated before {@link #freezeTypes()}.
	 */
	private static final Map<Identifier, MapCodec<? extends DamageDeliveryStrategy>> _deliveryCodecs = new ConcurrentHashMap<>();

	/**
	 * Mutable registered effect codec table, populated before {@link #freezeTypes()}.
	 */
	private static final Map<Identifier, MapCodec<? extends Effect>> _effectCodecs = new ConcurrentHashMap<>();

	/**
	 * Lock shared by registration and type freezing.
	 */
	private static final Object _typeLock = new Object();

	/**
	 * Whether addon type registration has been frozen.
	 */
	private static volatile boolean _typesFrozen;

	static
	{
		_deliveryCodecs.put(DELIVERY_HITSCAN, HitscanDamageDeliveryStrategy.MAP_CODEC);
		_deliveryCodecs.put(DELIVERY_PROJECTILE, ProjectileDamageDeliveryStrategy.MAP_CODEC);
	}

	/**
	 * Delivery codec.
	 */
	public static final Codec<DamageDeliveryStrategy> DELIVERY_CODEC = GalaxiesCodecs.typedDispatch(
			Identifier.CODEC,
			_deliveryCodecs,
			DamageDeliveryStrategy::type,
			"delivery"
	);

	/**
	 * Effect codec.
	 */
	public static final Codec<Effect> EFFECT_CODEC = GalaxiesCodecs.typedDispatch(
			Identifier.CODEC,
			_effectCodecs,
			Effect::type,
			"effect"
	);
}
