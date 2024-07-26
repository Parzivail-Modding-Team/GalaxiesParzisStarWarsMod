package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record UnitC2SPacket(CustomPayload.Id<UnitC2SPacket> packetId) implements CustomPayload
{
	public static final UnitC2SPacket OpenCharacterCustomizer = new UnitC2SPacket(SwgPackets.C2S.RequestCustomizeSelf);
	public static final UnitC2SPacket ShipFire = new UnitC2SPacket(SwgPackets.C2S.ShipFire);
	public static final UnitC2SPacket TogglePatrolPosture = new UnitC2SPacket(SwgPackets.C2S.TogglePatrolPosture);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return packetId;
	}

	public PacketCodec<ByteBuf, UnitC2SPacket> createPacketCodec() {
		return PacketCodec.unit(this);
	}
}
