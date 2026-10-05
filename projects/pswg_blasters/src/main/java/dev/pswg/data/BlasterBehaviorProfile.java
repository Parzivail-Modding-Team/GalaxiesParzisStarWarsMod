package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
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
 * @param delivery Built-in damage delivery choice.
 * @param damageType Native damage-type resource ID.
 * @param effects Ordered module-defined effects, omitted for direct-only shots.
 * @param muzzles Optional authored muzzle sockets.
 * @param chargedShot Optional charged damage scaling.
 */
@GenerateCodec(strict = true)
public record BlasterBehaviorProfile(
		Delivery delivery,
		Identifier damageType,
		@SelfCodec @CodecDefault("java.util.List.of()") List<Effect> effects,
		@SelfCodec Optional<Muzzles> muzzles,
		@SelfCodec Optional<ChargedShot> chargedShot
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
		PROJECTILE
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
		/** Module-registered effect type. */
		Identifier type();

		/**
		 * Closed application phase.
		 */
		EffectPhase when();

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
			@CodecDefault("dev.pswg.data.BlasterBehaviorProfile.CostPolicy.PER_EMISSION") CostPolicy costPolicy
	) implements IMuzzlesCodec
	{
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
	 * Registers a code-defined effect codec during module initialization.
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

	/** Module-provided effect codec table. */
	private static final Map<Identifier, MapCodec<? extends Effect>> _effectCodecs = new ConcurrentHashMap<>();

	/** Whether effect registration has completed. */
	private static volatile boolean _typesFrozen;
}
