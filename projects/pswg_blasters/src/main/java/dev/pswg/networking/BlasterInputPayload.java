package dev.pswg.networking;

import dev.pswg.Blasters;
import dev.pswg.codecgenerator.GenerateEnumCodec;
import dev.pswg.generated.codecs.IBlasterInputActionCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;

/**
 * Blaster input from a player.
 */
public record BlasterInputPayload(
		BlasterInputAction action,
		InteractionHand hand,
		long sequence
) implements CustomPacketPayload
{
	/**
	 * Supported weapon controls; held input is leased by heartbeats rather than client repeat shots.
	 */
	@GenerateEnumCodec
	public enum BlasterInputAction implements IBlasterInputActionCodec
	{
		/**
		 * A fresh trigger press.
		 */
		PRESS,

		/**
		 * Release the held trigger.
		 */
		RELEASE,

		/**
		 * Renew an existing held-input lease.
		 */
		HEARTBEAT,

		/**
		 * Cycle the current resolved mode list.
		 */
		CYCLE_MODE,

		/**
		 * Begin a timed magazine/charge-store reload.
		 */
		RELOAD,

		/**
		 * Toggle a stock's independent folded state.
		 */
		FOLD,

		/**
		 * Toggle ground-supported deployment.
		 */
		DEPLOY,

		/**
		 * Cycle authored field-conversion options, then return to the base form.
		 */
		CONVERT
	}

	/**
	 * Small native serverbound intent payload.
	 */
	public static final Type<BlasterInputPayload> TYPE = new Type<>(Blasters.id("input"));

	/**
	 * Generated enum and native hand/sequence packet codecs.
	 */
	public static final StreamCodec<RegistryFriendlyByteBuf, BlasterInputPayload> CODEC = StreamCodec.composite(
			BlasterInputAction.PACKET_CODEC,
			BlasterInputPayload::action,
			GalaxiesPacketCodecs.HAND,
			BlasterInputPayload::hand,
			ByteBufCodecs.VAR_LONG,
			BlasterInputPayload::sequence,
			BlasterInputPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
