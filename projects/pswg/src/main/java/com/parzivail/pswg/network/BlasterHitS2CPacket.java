package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.data.MorePacketCodecs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record BlasterHitS2CPacket(Vec3d pos, Vec3d incident, Vec3d normal) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, BlasterHitS2CPacket> CODEC = PacketCodec.tuple(
			MorePacketCodecs.VEC3D, BlasterHitS2CPacket::pos,
			MorePacketCodecs.VEC3D, BlasterHitS2CPacket::incident,
			MorePacketCodecs.VEC3D, BlasterHitS2CPacket::normal,
			BlasterHitS2CPacket::new);

	@Override
	public Id<BlasterHitS2CPacket> getId()
	{
		return SwgPackets.S2C.BlasterHit;
	}
}
