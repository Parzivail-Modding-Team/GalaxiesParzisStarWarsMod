package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.data.MorePacketCodecs;
import com.parzivail.util.item.ItemAction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PlayerItemActionC2SPacket(ItemAction action) implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, PlayerItemActionC2SPacket> PACKET_CODEC = PacketCodec.tuple(
			MorePacketCodecs.ITEM_ACTION,
			PlayerItemActionC2SPacket::action,
			PlayerItemActionC2SPacket::new
	);

	@Override
	public Id<PlayerItemActionC2SPacket> getId()
	{
		return SwgPackets.C2S.PlayerItemAction;
	}
}
