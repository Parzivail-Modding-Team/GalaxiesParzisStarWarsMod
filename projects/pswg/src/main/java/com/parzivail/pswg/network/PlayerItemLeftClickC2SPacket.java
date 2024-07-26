package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.data.MorePacketCodecs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;

public record PlayerItemLeftClickC2SPacket(Hand hand, boolean isRepeatEvent) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, PlayerItemLeftClickC2SPacket> PACKET_CODEC = PacketCodec.tuple(
			MorePacketCodecs.HAND,
			PlayerItemLeftClickC2SPacket::hand,
			PacketCodecs.BOOL,
			PlayerItemLeftClickC2SPacket::isRepeatEvent,
			PlayerItemLeftClickC2SPacket::new
	);

	@Override
	public Id<PlayerItemLeftClickC2SPacket> getId()
	{
		return SwgPackets.C2S.PlayerLeftClickItem;
	}
}
