package dev.pswg.networking;

import dev.pswg.Galaxies;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;

/**
 * Encapsulates a fire-and-forget C2S player use-left item interaction
 *
 * @param hand   The hand that used an item
 * @param yaw    The player's yaw at the time of the interaction
 * @param pitch  The player's pitch at the time of the interaction
 * @param repeat Whether the input event was a repeat event
 */
public record PlayerInteractItemLeftC2SPacket(InteractionHand hand, float yaw, float pitch, boolean repeat) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<PlayerInteractItemLeftC2SPacket> ID = new CustomPacketPayload.Type<>(Galaxies.id("use_item_left"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerInteractItemLeftC2SPacket> CODEC = StreamCodec.composite(
			GalaxiesPacketCodecs.HAND,
			PlayerInteractItemLeftC2SPacket::hand,
			ByteBufCodecs.FLOAT,
			PlayerInteractItemLeftC2SPacket::yaw,
			ByteBufCodecs.FLOAT,
			PlayerInteractItemLeftC2SPacket::pitch,
			ByteBufCodecs.BOOL,
			PlayerInteractItemLeftC2SPacket::repeat,
			PlayerInteractItemLeftC2SPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}
