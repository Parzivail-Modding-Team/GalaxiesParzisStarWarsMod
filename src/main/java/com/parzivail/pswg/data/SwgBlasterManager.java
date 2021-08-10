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
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;

public class SwgBlasterManager extends TypedDataLoader<BlasterDescriptor>
{
	private static class BlasterArchetypeAdapter extends TypeAdapter<BlasterArchetype>
	{
		@Override
		public void write(JsonWriter out, BlasterArchetype value) throws IOException
		{
			out.value(value.getValue());
		}

		@Override
		public BlasterArchetype read(JsonReader in) throws IOException
		{
			return BlasterArchetype.VALUE_LOOKUP.get(in.nextString());
		}
	}

	private static class BlasterFiringModesAdapter extends TypeAdapter<ArrayList<BlasterFiringMode>>
	{
		@Override
		public void write(JsonWriter out, ArrayList<BlasterFiringMode> value) throws IOException
		{
			out.beginArray();

			for (var v : value)
				out.value(v.getValue());

			out.endArray();
		}

		@Override
		public ArrayList<BlasterFiringMode> read(JsonReader in) throws IOException
		{
			in.beginArray();

			var modes = new ArrayList<BlasterFiringMode>();

			while (in.hasNext())
				modes.add(BlasterFiringMode.VALUE_LOOKUP.get(in.nextString()));

			in.endArray();

			return modes;
		}
	}

	public SwgBlasterManager()
	{
		super(
				new GsonBuilder()
						.registerTypeAdapter(BlasterArchetype.class, new BlasterArchetypeAdapter())
						.registerTypeAdapter(TypeToken.getParameterized(ArrayList.class, BlasterFiringMode.class).getType(), new BlasterFiringModesAdapter())
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
		buf.writeByte(value.type.getId());
		buf.writeShort(BlasterFiringMode.pack(value.firingModes));

		buf.writeFloat(value.damage);
		buf.writeFloat(value.range);
		buf.writeFloat(value.weight);
		buf.writeFloat(value.boltColor);
		buf.writeInt(value.magazineSize);
		buf.writeInt(value.automaticRepeatTime);

		buf.writeBoolean(value.foreGripPos != null);
		if (value.foreGripPos != null)
		{
			buf.writeFloat((float)value.foreGripPos.x);
			buf.writeFloat((float)value.foreGripPos.y);
			buf.writeFloat((float)value.foreGripPos.z);
		}

		buf.writeBoolean(value.foreGripHandAngle != null);
		if (value.foreGripHandAngle != null)
		{
			buf.writeFloat(value.foreGripHandAngle.getPitch());
			buf.writeFloat(value.foreGripHandAngle.getYaw());
			buf.writeFloat(value.foreGripHandAngle.getRoll());
		}

		buf.writeInt(value.burstSize);

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
		var archetype = BlasterArchetype.ID_LOOKUP.get(buf.readByte());
		var firingModes = BlasterFiringMode.unpack(buf.readShort());

		var damage = buf.readFloat();
		var range = buf.readFloat();
		var weight = buf.readFloat();
		var boltColor = buf.readFloat();
		var magazineSize = buf.readInt();
		var automaticRepeatTime = buf.readInt();

		var hasForeGripPos = buf.readBoolean();
		Vec3d foreGripPos = null;
		if (hasForeGripPos)
		{
			foreGripPos = new Vec3d(
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat()
			);
		}

		var hasForeGripHandAngle = buf.readBoolean();
		EulerAngle foreGripHandAngle = null;
		if (hasForeGripHandAngle)
		{
			foreGripHandAngle = new EulerAngle(
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat()
			);
		}

		var burstSize = buf.readInt();

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
				foreGripPos,
				foreGripHandAngle,
				burstSize,
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
