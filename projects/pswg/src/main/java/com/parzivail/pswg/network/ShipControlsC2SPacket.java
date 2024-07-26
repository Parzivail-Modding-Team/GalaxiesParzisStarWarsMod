package com.parzivail.pswg.network;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.EnumSet;

public record ShipControlsC2SPacket(EnumSet<ShipControls> shipControls) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, ShipControlsC2SPacket> PACKET_CODEC = PacketCodec.tuple(
			ShipControls.PACKET_CODEC,
			ShipControlsC2SPacket::shipControls,
			ShipControlsC2SPacket::new
	);

	@Override
	public Id<ShipControlsC2SPacket> getId()
	{
		return SwgPackets.C2S.ShipControls;
	}
}
