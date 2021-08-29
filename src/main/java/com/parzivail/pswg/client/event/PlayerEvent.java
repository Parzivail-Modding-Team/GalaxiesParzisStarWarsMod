package com.parzivail.pswg.client.event;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;

public enum PlayerEvent
{
	ACCUMULATE_RECOIL((byte)0);

	public static final HashMap<Byte, PlayerEvent> ID_LOOKUP = new HashMap<>();

	static
	{
		for (var v : values())
			ID_LOOKUP.put(v.id, v);
	}

	private final byte id;

	PlayerEvent(byte id)
	{
		this.id = id;
	}

	public byte getId()
	{
		return id;
	}

	public static PacketByteBuf createBuffer(PlayerEvent event)
	{
		var passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeByte(event.getId());
		return passedData;
	}
}
