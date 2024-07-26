package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record PlayerSocketSparksS2CPacket(int playerId, String playerSocket) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, PlayerSocketSparksS2CPacket> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT,
			PlayerSocketSparksS2CPacket::playerId,
			PacketCodecs.STRING,
			PlayerSocketSparksS2CPacket::playerSocket,
			PlayerSocketSparksS2CPacket::new
	);

	@Override
	public Id<PlayerSocketSparksS2CPacket> getId()
	{
		return SwgPackets.S2C.PlayerSocketPyro;
	}
}
