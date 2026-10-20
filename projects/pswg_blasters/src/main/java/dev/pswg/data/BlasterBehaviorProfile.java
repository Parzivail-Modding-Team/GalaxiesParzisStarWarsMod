package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.entity.BlasterEffectContext;
import dev.pswg.generated.codecs.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Supported shot delivery, explicit effects and optional muzzle/charge configuration.
 *
 * @param delivery       Built-in damage delivery choice.
 * @param damageType     Native damage-type resource ID.
 * @param effects        Ordered module-defined effects, omitted for direct-only shots.
 * @param muzzles        Optional authored muzzle sockets.
 * @param chargedShot    Optional charged damage scaling.
 * @param scatter        Optional pellet count, damage scale and additional spread.
 * @param flight         Optional projectile speed, gravity, fuse and reflection policy.
 * @param allowAds       Whether this mode permits ADS.
 * @param activateBlocks Whether an impact invokes native projectile block callbacks.
 */
@GenerateCodec(strict = true)
public record BlasterBehaviorProfile(
		Delivery delivery,
		Identifier damageType,
		@SelfCodec @CodecDefault("java.util.List.of()") List<Effect> effects,
		@SelfCodec Optional<Muzzles> muzzles,
		@SelfCodec Optional<ChargedShot> chargedShot,
		@SelfCodec Optional<Scatter> scatter,
		@SelfCodec Optional<Flight> flight,
		@CodecDefault("true") boolean allowAds,
		@CodecDefault("false") boolean activateBlocks
) implements IBlasterBehaviorProfileCodec
{
	/**
	 * Fixed shot-delivery choices.
	 */
	@GenerateEnumCodec
	public enum Delivery implements IDeliveryCodec
	{
		/**
		 * Server raycast, also used by slugs.
		 */
		HITSCAN,

		/**
		 * Server projectile simulation.
		 */
		PROJECTILE,

		/**
		 * Runs on-fire effects without a ray or bolt.
		 */
		PULSE
	}

	/**
	 * Phases in which explicit effects may run.
	 */
	@GenerateEnumCodec
	public enum EffectPhase implements IEffectPhaseCodec
	{
		/**
		 * Accepted shot.
		 */
		ON_FIRE,

		/**
		 * Block impact.
		 */
		ON_BLOCK_HIT,

		/**
		 * Entity impact.
		 */
		ON_ENTITY_HIT,

		/**
		 * Detonation.
		 */
		ON_DETONATE
	}

	/**
	 * Supported muzzle selection policies.
	 */
	@GenerateEnumCodec
	public enum Selection implements ISelectionCodec
	{
		/**
		 * First socket.
		 */
		SINGLE,

		/**
		 * Advance one socket after each committed shot.
		 */
		ROUND_ROBIN,

		/**
		 * Emit from every socket.
		 */
		ALL
	}

	/**
	 * Shot-cost accounting policies.
	 */
	@GenerateEnumCodec
	public enum CostPolicy implements ICostPolicyCodec
	{
		/**
		 * One cost for the accepted trigger.
		 */
		PER_TRIGGER,

		/**
		 * One cost for each emission.
		 */
		PER_EMISSION
	}

	/**
	 * Supported charge sources.
	 */
	@GenerateEnumCodec
	public enum ChargeSource implements IChargeSourceCodec
	{
		/**
		 * Time spent holding a charge trigger.
		 */
		HELD_DURATION,

		/**
		 * Stored component-backed loaded units.
		 */
		LOADED_COMPONENT_CHARGE
	}

	/**
	 * Supported charged-shot consumption choices.
	 */
	@GenerateEnumCodec
	public enum ChargeConsumer implements IChargeConsumerCodec
	{
		/**
		 * Standard round cost.
		 */
		ONE_ROUND,

		/**
		 * Spend all loaded component-backed units.
		 */
		ALL_REMAINING_COMPONENT_CHARGE
	}

	/**
	 * Explicit module-provided effect with a required application phase.
	 */
	public interface Effect
	{
		/**
		 * Module-registered effect type.
		 */
		Identifier type();

		/**
		 * Closed application phase.
		 */
		EffectPhase when();

		/**
		 * Applies this code-defined effect on the server.
		 */
		void apply(BlasterEffectContext context);

		/**
		 * Module effect dispatch; data cannot define executable handlers.
		 */
		Codec<Effect> CODEC = GalaxiesCodecs.typedDispatch(Identifier.CODEC, _effectCodecs, Effect::type, "effect");

		/**
		 * Native NBT fallback for module effect codecs that have no common binary schema.
		 */
		StreamCodec<RegistryFriendlyByteBuf, Effect> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
	}

	/**
	 * Ordered unique socket IDs, selection and explicit/default cost policy.
	 */
	@GenerateCodec(strict = true)
	public record Muzzles(
			@CodecSize(min = 1) @CodecUnique @UseCodec(codec = GenStandardCodec.NON_EMPTY_STRING) List<String> sockets,
			@CodecDefault("dev.pswg.data.BlasterBehaviorProfile.Selection.SINGLE") Selection selection,
			@CodecDefault("dev.pswg.data.BlasterBehaviorProfile.CostPolicy.PER_EMISSION") CostPolicy costPolicy,
			@SelfCodec @CodecDefault("java.util.Map.of()") Map<String, MuzzleOffset> offsets
	) implements IMuzzlesCodec
	{
		/**
		 * Returns emissions before scatter expansion.
		 */
		public int emissionCount()
		{
			return selection == Selection.ALL ? sockets.size() : 1;
		}

		/**
		 * Returns the resource and heat cost.
		 */
		public int costCount()
		{
			return costPolicy == CostPolicy.PER_TRIGGER ? 1 : emissionCount();
		}

		/**
		 * Selects one socket.
		 */
		public int socketIndex(int cursor, int emission)
		{
			return switch (selection)
			{
				case SINGLE -> 0;
				case ROUND_ROBIN -> Math.floorMod(cursor, sockets.size());
				case ALL -> emission;
			};
		}

		/**
		 * Advances a round-robin shot.
		 */
		public int nextCursor(int cursor)
		{
			return selection == Selection.ROUND_ROBIN ? (Math.floorMod(cursor, sockets.size()) + 1) % sockets.size() : cursor;
		}
	}

	/**
	 * Pellets emitted by one shot.
	 *
	 * @param pelletCount      Number of traces or bolts.
	 * @param damageMultiplier Damage scale for each pellet, after charged-shot scaling.
	 * @param coneDegrees      Extra cone half-angle, added to effective weapon spread and capped at 90 degrees.
	 */
	@GenerateCodec(strict = true)
	public record Scatter(
			@CodecRange(min = 1) int pelletCount,
			@CodecRange(min = 0, max = 1) float damageMultiplier,
			@CodecRange(min = 0, max = 90) float coneDegrees
	) implements IScatterCodec
	{
	}

	/**
	 * Authoritative muzzle offset in view-local blocks. Missing offsets use the eye origin.
	 */
	@GenerateCodec(strict = true)
	public record MuzzleOffset(
			@CodecRange(min = -2, max = 2) float x,
			@CodecRange(min = -2, max = 2) float y,
			@CodecRange(min = -2, max = 2) float z
	) implements IMuzzleOffsetCodec
	{
	}

	/**
	 * Projectile motion and lifetime. Fuse zero detonates only on impact or range expiry.
	 */
	@GenerateCodec(strict = true)
	public record Flight(
			@CodecRange(min = 0.05, max = 8) float speed,
			@CodecRange(min = 0, max = 1) float gravity,
			@CodecRange(min = 0, max = 200) int fuseTicks,
			@CodecDefault("true") boolean reflectable
	) implements IFlightCodec
	{
		/**
		 * Straight energy-bolt motion.
		 */
		public static final Flight ENERGY = new Flight(5, 0, 0, true);
	}

	/**
	 * Charged damage source, multiplier and spending rule.
	 */
	@GenerateCodec(strict = true)
	public record ChargedShot(
			ChargeSource source,
			@CodecRange(min = 1) float maximumDamageMultiplier,
			ChargeConsumer consume
	) implements IChargedShotCodec
	{
		/**
		 * Spending all units only makes sense for loaded component charge.
		 */
		public static final Codec<ChargedShot> CODEC = IChargedShotCodec.CODEC.validate(
				value ->
						value.consume() == ChargeConsumer.ONE_ROUND || value.source() == ChargeSource.LOADED_COMPONENT_CHARGE
						? DataResult.success(value)
						: DataResult.error(() -> "All-charge consumption requires loaded_component_charge")
		);
	}

	/**
	 * Checks relations between decoded fields.
	 */
	private static DataResult<BlasterBehaviorProfile> validate(BlasterBehaviorProfile profile)
	{
		if (profile.flight().isPresent() && profile.delivery() != Delivery.PROJECTILE)
		{
			return DataResult.error(() -> "Flight settings require projectile delivery");
		}

		if (profile.delivery() == Delivery.PULSE && (profile.scatter().isPresent() || profile.effects().stream().anyMatch(effect -> effect.when() != EffectPhase.ON_FIRE)))
		{
			return DataResult.error(() -> "Pulse delivery permits only on_fire effects and no scatter");
		}

		if (profile.muzzles().isPresent())
		{
			var muzzles = profile.muzzles().orElseThrow();
			if (!muzzles.sockets().containsAll(muzzles.offsets().keySet()))
			{
				return DataResult.error(() -> "Muzzle offsets must name declared sockets");
			}

			var allCharge = profile.chargedShot().map(value -> value.consume() == ChargeConsumer.ALL_REMAINING_COMPONENT_CHARGE).orElse(false);
			if (allCharge && muzzles.selection() == Selection.ALL && muzzles.costPolicy() == CostPolicy.PER_EMISSION && muzzles.sockets().size() > 1)
			{
				return DataResult.error(() -> "All-charge shots cannot pay multiple independent muzzle costs");
			}
		}

		if (profile.effects().stream().filter(BlasterExplosionEffect.class::isInstance).count() > 1)
		{
			return DataResult.error(() -> "A profile may contain only one explosion effect");
		}

		return DataResult.success(profile);
	}

	/**
	 * Registers an effect codec before the type table freezes.
	 */
	public static synchronized void registerEffect(Identifier id, MapCodec<? extends Effect> codec)
	{
		Preconditions.checkState(!_typesFrozen, "Behavior effect types are frozen");
		Preconditions.checkArgument(_effectCodecs.putIfAbsent(id, codec) == null, "Effect codec already registered: %s", id);
	}

	/**
	 * Freezes the code-defined effect vocabulary after module initialization.
	 */
	public static synchronized void freezeTypes()
	{
		_typesFrozen = true;
	}

	/**
	 * Module-provided effect codec table.
	 */
	private static final Map<Identifier, MapCodec<? extends Effect>> _effectCodecs = new ConcurrentHashMap<>();

	/**
	 * Whether effect registration has completed.
	 */
	private static volatile boolean _typesFrozen;

	/**
	 * Checks profile relations after field decoding. The effect table must be initialized first.
	 */
	public static final Codec<BlasterBehaviorProfile> CODEC = IBlasterBehaviorProfileCodec.CODEC.validate(BlasterBehaviorProfile::validate);
}
