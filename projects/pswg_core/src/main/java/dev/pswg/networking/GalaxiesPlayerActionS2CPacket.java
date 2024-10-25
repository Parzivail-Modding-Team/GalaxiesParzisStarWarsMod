package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.interaction.ServerPlayerAction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Encapsulates a fire-and-forget S2C player action notification
 *
 * @param action The action to invoke
 */
public record GalaxiesPlayerActionS2CPacket(ServerPlayerAction action) implements CustomPayload
{
	public static final Id<GalaxiesPlayerActionS2CPacket> ID = new Id<>(Galaxies.id("server_player_action"));

	public static final PacketCodec<RegistryByteBuf, GalaxiesPlayerActionS2CPacket> CODEC = PacketCodec.tuple(
			GalaxiesPacketCodecs.SERVER_PLAYER_ACTION,
			GalaxiesPlayerActionS2CPacket::action,
			GalaxiesPlayerActionS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
