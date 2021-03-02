package com.parzivail.pswg.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.parzivail.pswg.access.IServerResourceManagerAccess;
import com.parzivail.pswg.item.blaster.data.BlasterCoolingBypassProfile;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterHeatInfo;
import com.parzivail.pswg.item.blaster.data.BlasterSpreadInfo;
import com.parzivail.pswg.mixin.MinecraftServerMixin;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
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
		ServerResourceManager srm = ((MinecraftServerMixin)server).getServerResourceManager();
		return ((IServerResourceManagerAccess)srm).getBlasterLoader();
	}

	public PacketByteBuf createPacket()
	{
		PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());

		passedData.writeInt(blasters.size());

		for (Map.Entry<Identifier, BlasterDescriptor> entry : blasters.entrySet())
		{
			passedData.writeIdentifier(entry.getKey());
			writeBlasterDescriptor(passedData, entry.getValue());
		}

		return passedData;
	}

	private void writeBlasterDescriptor(PacketByteBuf buf, BlasterDescriptor value)
	{
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
		buf.writeInt(value.heat.overheatPenalty);
		buf.writeInt(value.heat.passiveCooldownDelay);

		buf.writeFloat(value.cooling.primaryBypassTime);
		buf.writeFloat(value.cooling.primaryBypassTolerance);
		buf.writeFloat(value.cooling.secondaryBypassTime);
		buf.writeFloat(value.cooling.secondaryBypassTolerance);
	}

	public void handlePacket(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender)
	{
		int size = packetByteBuf.readInt();

		HashMap<Identifier, BlasterDescriptor> map = new HashMap<>();

		for (int i = 0; i < size; i++)
		{
			Identifier key = packetByteBuf.readIdentifier();
			BlasterDescriptor value = readBlasterDescriptor(key, packetByteBuf);
			map.put(key, value);
		}

		this.blasters = ImmutableMap.copyOf(map);
	}

	private BlasterDescriptor readBlasterDescriptor(Identifier key, PacketByteBuf buf)
	{
		float damage = buf.readFloat();
		float range = buf.readFloat();
		float weight = buf.readFloat();
		int boltColor = buf.readInt();
		int magazineSize = buf.readInt();
		int automaticRepeatTime = buf.readInt();

		float spread_horizontal = buf.readFloat();
		float spread_vertical = buf.readFloat();

		int heat_capacity = buf.readInt();
		int heat_perRound = buf.readInt();
		int heat_cooldownDelay = buf.readInt();
		int heat_passiveCooldownDelay = buf.readInt();

		float cooling_primaryBypassTime = buf.readFloat();
		float cooling_primaryBypassTolerance = buf.readFloat();
		float cooling_secondaryBypassTime = buf.readFloat();
		float cooling_secondaryBypassTolerance = buf.readFloat();

		return new BlasterDescriptor(
				key,
				damage,
				range,
				weight,
				boltColor,
				magazineSize,
				automaticRepeatTime,
				new BlasterSpreadInfo(spread_horizontal, spread_vertical),
				new BlasterHeatInfo(heat_capacity, heat_perRound, heat_cooldownDelay, heat_passiveCooldownDelay),
				new BlasterCoolingBypassProfile(cooling_primaryBypassTime, cooling_primaryBypassTolerance, cooling_secondaryBypassTime, cooling_secondaryBypassTolerance)
		);
	}

	protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler)
	{
		Map<Identifier, BlasterDescriptor> blasterDescriptorMap = new HashMap<>();

		map.forEach((identifier, jsonElement) -> {
			try
			{
				BlasterDescriptor obj = deserializeBlasterDescriptor(identifier, jsonElement);
				blasterDescriptorMap.put(identifier, obj);
			}
			catch (IllegalArgumentException | JsonParseException ex)
			{
				LOGGER.error("Parsing error loading custom blaster {}: {}", identifier, ex.getMessage());
			}
		});

		blasters = ImmutableMap.copyOf(blasterDescriptorMap);
	}

	public void reset()
	{
		blasters.clear();
	}

	public Map<Identifier, BlasterDescriptor> getBlasters()
	{
		return blasters;
	}

	public void setBlasters(Map<Identifier, BlasterDescriptor> blasters)
	{
		this.blasters = ImmutableMap.copyOf(blasters);
	}

	private BlasterDescriptor deserializeBlasterDescriptor(Identifier identifier, JsonElement jsonObject)
	{
		int version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		return new BlasterDescriptor(identifier, GSON.fromJson(jsonObject, BlasterDescriptor.class));
	}
}
