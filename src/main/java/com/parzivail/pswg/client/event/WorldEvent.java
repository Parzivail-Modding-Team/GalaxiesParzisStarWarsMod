package com.parzivail.pswg.client.event;

import com.parzivail.util.generics.MappingEventBus;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;

public enum WorldEvent
{
	SLUG_FIRED((byte)0),
	BLASTER_BOLT_HIT((byte)1);

	public static final HashMap<Byte, WorldEvent> ID_LOOKUP = new HashMap<>();
	public static final MappingEventBus<WorldEvent, ClientPlayNetworking.PlayChannelHandler> EVENT_BUS = new MappingEventBus<>();

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

