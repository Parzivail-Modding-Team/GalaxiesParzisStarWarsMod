package com.parzivail.pswg.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.blaster.data.BlasterCoolingBypassProfile;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterHeatInfo;
import com.parzivail.pswg.item.blaster.data.BlasterSpreadInfo;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SwgBlasterManager extends JsonDataLoader
{
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).create();

	private Map<Identifier, BlasterDescriptor> blasters = ImmutableMap.of();

	public SwgBlasterManager()
	{
		super(GSON, "items/blasters");
	}

	public static SwgBlasterManager get(MinecraftServer server)
	{
		return Galaxies.ResourceManagers.get(server).getBlasterLoader();
	}

	public static SwgBlasterManager get(World world)
	{
		if (world.isClient)
			return Client.ResourceManagers.getBlasterLoader();

		return SwgBlasterManager.get(world.getServer());
	}

	public PacketByteBuf createPacket()
	{
		var passedData = new PacketByteBuf(Unpooled.buffer());

		passedData.writeInt(blasters.size());

		for (var entry : blasters.entrySet())
		{
			passedData.writeIdentifier(entry.getKey());
			writeBlasterDescriptor(passedData, entry.getValue());
		}

		return passedData;
	}

	private void writeBlasterDescriptor(PacketByteBuf buf, BlasterDescriptor value)
	{
		buf.writeBoolean(value.oneHanded);
		buf.writeFloat(value.damage);
		buf.writeFloat(value.range);
		buf.writeFloat(value.weight);
		buf.writeInt(value.boltColor);
		buf.writeInt(value.magazineSize);
		buf.writeInt(value.automaticRepeatTime);

		buf.writeFloat(value.spread.horizontal);
		buf.writeFloat(value.spread.vertical);

		buf.writeInt(value.heat.capacity);
		buf.writeInt(value.heat.perRound);
		buf.writeInt(value.heat.drainSpeed);
		buf.writeInt(value.heat.overheatPenalty);
		buf.writeInt(value.heat.overheatDrainSpeed);
		buf.writeInt(value.heat.passiveCooldownDelay);

		buf.writeFloat(value.cooling.primaryBypassTime);
		buf.writeFloat(value.cooling.primaryBypassTolerance);
		buf.writeFloat(value.cooling.secondaryBypassTime);
		buf.writeFloat(value.cooling.secondaryBypassTolerance);
	}

	public void handlePacket(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender)
	{
		var size = packetByteBuf.readInt();

		var map = new HashMap<Identifier, BlasterDescriptor>();

		for (var i = 0; i < size; i++)
		{
			var key = packetByteBuf.readIdentifier();
			var value = readBlasterDescriptor(key, packetByteBuf);
			map.put(key, value);
		}

		this.blasters = ImmutableMap.copyOf(map);
	}

	private BlasterDescriptor readBlasterDescriptor(Identifier key, PacketByteBuf buf)
	{
		var oneHanded = buf.readBoolean();
		var damage = buf.readFloat();
		var range = buf.readFloat();
		var weight = buf.readFloat();
		var boltColor = buf.readInt();
		var magazineSize = buf.readInt();
		var automaticRepeatTime = buf.readInt();

		var spread_horizontal = buf.readFloat();
		var spread_vertical = buf.readFloat();

		var heat_capacity = buf.readInt();
		var heat_perRound = buf.readInt();
		var heat_drainSpeed = buf.readInt();
		var heat_cooldownDelay = buf.readInt();
		var heat_overheatDrainSpeed = buf.readInt();
		var heat_passiveCooldownDelay = buf.readInt();

		var cooling_primaryBypassTime = buf.readFloat();
		var cooling_primaryBypassTolerance = buf.readFloat();
		var cooling_secondaryBypassTime = buf.readFloat();
		var cooling_secondaryBypassTolerance = buf.readFloat();

		return new BlasterDescriptor(
				key,
				oneHanded,
				damage,
				range,
				weight,
				boltColor,
				magazineSize,
				automaticRepeatTime,
				new BlasterSpreadInfo(spread_horizontal, spread_vertical),
				new BlasterHeatInfo(heat_capacity, heat_perRound, heat_drainSpeed, heat_cooldownDelay, heat_overheatDrainSpeed, heat_passiveCooldownDelay),
				new BlasterCoolingBypassProfile(cooling_primaryBypassTime, cooling_primaryBypassTolerance, cooling_secondaryBypassTime, cooling_secondaryBypassTolerance)
		);
	}

	protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler)
	{
		Map<Identifier, BlasterDescriptor> blasterDescriptorMap = new HashMap<>();

		map.forEach((identifier, jsonElement) -> {
			try
			{
				var obj = deserializeBlasterDescriptor(identifier, jsonElement);
				blasterDescriptorMap.put(identifier, obj);
			}
			catch (IllegalArgumentException | JsonParseException ex)
			{
				LOGGER.error("Parsing error loading custom blaster {}: {}", identifier, ex.getMessage());
			}
		});

		blasters = ImmutableMap.copyOf(blasterDescriptorMap);
	}

	public Map<Identifier, BlasterDescriptor> getBlasters()
	{
		return blasters;
	}

	private BlasterDescriptor deserializeBlasterDescriptor(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		return new BlasterDescriptor(identifier, GSON.fromJson(jsonObject, BlasterDescriptor.class));
	}

	public BlasterDescriptor getBlaster(Identifier bdId)
	{
		return blasters.get(bdId);
	}
}
