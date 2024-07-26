package com.parzivail.pswg.network;

import com.parzivail.pswg.client.input.JetpackControls;
import com.parzivail.pswg.container.SwgPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.EnumSet;

public record JetpackControlsC2SPacket(EnumSet<JetpackControls> jetpackControls) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, JetpackControlsC2SPacket> PACKET_CODEC = PacketCodec.tuple(
			JetpackControls.PACKET_CODEC,
			JetpackControlsC2SPacket::jetpackControls,
			JetpackControlsC2SPacket::new
	);

	@Override
	public Id<JetpackControlsC2SPacket> getId()
	{
		return SwgPackets.C2S.JetpackControls;
	}
}
