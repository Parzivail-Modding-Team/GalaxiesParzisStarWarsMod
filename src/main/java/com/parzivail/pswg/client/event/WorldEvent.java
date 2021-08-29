package com.parzivail.pswg.client.event;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;

public enum WorldEvent
{
	SLUG_FIRED((byte)0);

	public static final HashMap<Byte, WorldEvent> ID_LOOKUP = new HashMap<>();

	static
	{
		for (var v : values())
			ID_LOOKUP.put(v.id, v);
	}

	private final byte id;

	WorldEvent(byte id)
	{
		this.id = id;
	}

	public byte getId()
	{
		return id;
	}

	public static PacketByteBuf createBuffer(WorldEvent event)
	{
		var passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeByte(event.getId());
		return passedData;
	}
}

