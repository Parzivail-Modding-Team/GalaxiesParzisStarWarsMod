package com.parzivail.pswg.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.blaster.data.*;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.EnumSet;

public class SwgBlasterManager extends TypedDataLoader<BlasterDescriptor>
{
	private static class BlasterArchetypeAdapter extends TypeAdapter<BlasterArchetype>
	{
		@Override
		public void write(JsonWriter out, BlasterArchetype value) throws IOException
		{
			out.value(value.getId());
		}

		@Override
		public BlasterArchetype read(JsonReader in) throws IOException
		{
			var str = in.nextString();
			Lumberjack.debug("%s -> %s", str, BlasterArchetype.ID_LOOKUP.get(str));
			return BlasterArchetype.ID_LOOKUP.get(str);
		}
	}

	private static class BlasterFiringModesAdapter extends TypeAdapter<EnumSet<BlasterFiringMode>>
	{
		@Override
		public void write(JsonWriter out, EnumSet<BlasterFiringMode> value) throws IOException
		{
			out.beginArray();

			for (var v : value)
				out.value(v.getId());

			out.endArray();
		}

		@Override
		public EnumSet<BlasterFiringMode> read(JsonReader in) throws IOException
		{
			in.beginArray();

			var modes = EnumSet.noneOf(BlasterFiringMode.class);

			while (in.hasNext())
				modes.add(BlasterFiringMode.REVERSE_LOOKUP.get(in.nextString()));

			in.endArray();

			return modes;
		}
	}

	public SwgBlasterManager()
	{
		super(
				new GsonBuilder()
						.registerTypeAdapter(BlasterArchetype.class, new BlasterArchetypeAdapter())
						.registerTypeAdapter(TypeToken.getParameterized(EnumSet.class, BlasterFiringMode.class).getType(), new BlasterFiringModesAdapter())
						.create(),
				"items/blasters"
		);
	}

	public static SwgBlasterManager get(MinecraftServer server)
	{
		return Galaxies.ResourceManagers.get(server).getBlasterManager();
	}

	public static SwgBlasterManager get(World world)
	{
		if (world.isClient)
			return Client.ResourceManagers.getBlasterManager();

		return SwgBlasterManager.get(world.getServer());
	}

	protected void writeDataEntry(PacketByteBuf buf, BlasterDescriptor value)
	{
		buf.writeByte(value.type.getOrdinal());
		buf.writeShort(BlasterFiringMode.pack(value.firingModes));

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

	protected BlasterDescriptor readDataEntry(Identifier key, PacketByteBuf buf)
	{
		var archetype = BlasterArchetype.ORDINAL_LOOKUP.get(buf.readByte());
		var firingModes = BlasterFiringMode.unpack(buf.readShort());

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
				archetype,
				firingModes,
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

	protected BlasterDescriptor deserializeDataEntry(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		return new BlasterDescriptor(identifier, GSON.fromJson(jsonObject, BlasterDescriptor.class));
	}
}
