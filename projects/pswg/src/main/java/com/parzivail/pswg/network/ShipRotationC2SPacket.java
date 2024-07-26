package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Quaternionf;

public record ShipRotationC2SPacket(Quaternionf rotation) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, ShipRotationC2SPacket> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.QUATERNIONF,
			ShipRotationC2SPacket::rotation,
			ShipRotationC2SPacket::new
	);

	@Override
	public Id<ShipRotationC2SPacket> getId()
	{
		return SwgPackets.C2S.ShipRotation;
	}
}
