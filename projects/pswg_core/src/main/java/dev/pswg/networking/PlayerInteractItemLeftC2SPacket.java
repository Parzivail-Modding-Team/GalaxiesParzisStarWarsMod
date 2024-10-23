package dev.pswg.networking;

import dev.pswg.Galaxies;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;

/**
 * Encapsulates a fire-and-forget C2S player use-left item interaction
 *
 * @param hand  The hand that used an item
 * @param yaw   The player's yaw at the time of the interaction
 * @param pitch The player's pitch at the time of the interaction
 */
public record PlayerInteractItemLeftC2SPacket(Hand hand, float yaw, float pitch) implements CustomPayload
{
	public static final CustomPayload.Id<PlayerInteractItemLeftC2SPacket> ID = new CustomPayload.Id<>(Galaxies.id("use_item_left"));

	public static final PacketCodec<PacketByteBuf, PlayerInteractItemLeftC2SPacket> CODEC = PacketCodec.tuple(
			GalaxiesPacketCodecs.HAND,
			PlayerInteractItemLeftC2SPacket::hand,
			PacketCodecs.FLOAT,
			PlayerInteractItemLeftC2SPacket::yaw,
			PacketCodecs.FLOAT,
			PlayerInteractItemLeftC2SPacket::pitch,
			PlayerInteractItemLeftC2SPacket::new
	);

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
