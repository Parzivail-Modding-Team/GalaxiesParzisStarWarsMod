package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.interaction.PlayerAction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Encapsulates a fire-and-forget C2S player action notification
 *
 * @param action The action to invoke
 */
public record GalaxiesPlayerActionC2SPacket(PlayerAction action) implements CustomPayload
{
	public static final Id<GalaxiesPlayerActionC2SPacket> ID = new Id<>(Galaxies.id("stop_using_item_left"));

	public static final PacketCodec<PacketByteBuf, GalaxiesPlayerActionC2SPacket> CODEC = PacketCodec.tuple(
			GalaxiesPacketCodecs.PLAYER_ACTION,
			GalaxiesPlayerActionC2SPacket::action,
			GalaxiesPlayerActionC2SPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
