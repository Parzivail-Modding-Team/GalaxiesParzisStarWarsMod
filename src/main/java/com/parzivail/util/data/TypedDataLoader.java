package com.parzivail.util.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class TypedDataLoader<T> extends JsonDataLoader implements IdentifiableResourceReloadListener, IExternalReloadResourceManager<Map<Identifier, JsonElement>>
{
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Gson GSON;

	private Map<Identifier, T> data = ImmutableMap.of();

	public TypedDataLoader(String dataSource)
	{
		this((new GsonBuilder()).create(), dataSource);
	}

	public TypedDataLoader(Gson gson, String dataSource)
	{
		super(gson, dataSource);
		this.GSON = gson;
	}

	public PacketByteBuf createPacket()
	{
		var passedData = new PacketByteBuf(Unpooled.buffer());

		passedData.writeInt(data.size());

		for (var entry : data.entrySet())
		{
			passedData.writeIdentifier(entry.getKey());
			writeDataEntry(passedData, entry.getValue());
		}

		return passedData;
	}

	protected abstract void writeDataEntry(PacketByteBuf buf, T value);

	protected abstract T readDataEntry(Identifier key, PacketByteBuf buf);

	protected abstract T deserializeDataEntry(Identifier identifier, JsonElement jsonObject);

	public void handlePacket(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender)
	{
		var size = packetByteBuf.readInt();

		var map = new HashMap<Identifier, T>();

		for (var i = 0; i < size; i++)
		{
			var key = packetByteBuf.readIdentifier();
			var value = readDataEntry(key, packetByteBuf);
			map.put(key, value);
		}

		this.data = ImmutableMap.copyOf(map);
	}

	public void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler)
	{
		Map<Identifier, T> dataMap = new HashMap<>();

		map.forEach((identifier, jsonElement) -> {
			try
			{
				var obj = deserializeDataEntry(identifier, jsonElement);
				dataMap.put(identifier, obj);
			}
			catch (IllegalArgumentException | JsonParseException ex)
			{
				LOGGER.error("Parsing error loading custom data {}: {}", identifier, ex.getMessage());
			}
		});

		data = ImmutableMap.copyOf(dataMap);
	}

	public Map<Identifier, T> getData()
	{
		return data;
	}

	public T getData(Identifier key)
	{
		return data.get(key);
	}

	@Override
	public Map<Identifier, JsonElement> prepare(ResourceManager manager, Profiler profiler)
	{
		return super.prepare(manager, profiler);
	}
}
