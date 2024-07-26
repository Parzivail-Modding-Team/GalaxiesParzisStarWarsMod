package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AccumulateRecoilS2CPacket(float vert, float horiz) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, AccumulateRecoilS2CPacket> CODEC = PacketCodec.tuple(
			PacketCodecs.FLOAT, AccumulateRecoilS2CPacket::vert,
			PacketCodecs.FLOAT, AccumulateRecoilS2CPacket::horiz,
			AccumulateRecoilS2CPacket::new);
	@Override
	public Id<AccumulateRecoilS2CPacket> getId()
	{
		return SwgPackets.S2C.AccumulateRecoil;
	}
}
