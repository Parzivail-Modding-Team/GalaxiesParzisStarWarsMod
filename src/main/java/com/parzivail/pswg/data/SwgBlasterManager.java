package com.parzivail.pswg.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.blaster.data.*;
import com.parzivail.util.data.PacketByteBufHelper;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SwgBlasterManager extends TypedDataLoader<BlasterDescriptor>
{
	private static class EulerAngleDeserializer implements JsonDeserializer<EulerAngle>
	{
		@Override
		public EulerAngle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			var o = json.getAsJsonObject();
			return new EulerAngle(o.get("pitch").getAsFloat(), o.get("yaw").getAsFloat(), o.get("roll").getAsFloat());
		}
	}

	private static class Vec3dDeserializer implements JsonDeserializer<Vec3d>
	{
		@Override
		public Vec3d deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			var o = json.getAsJsonObject();
			return new Vec3d(o.get("x").getAsDouble(), o.get("y").getAsDouble(), o.get("z").getAsDouble());
		}
	}

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

	private static class BlasterAttachmentDescriptorDeserializer implements JsonDeserializer<HashMap<Integer, BlasterAttachmentDescriptor>>
	{
		@Override
		public HashMap<Integer, BlasterAttachmentDescriptor> deserialize(JsonElement e, Type type, JsonDeserializationContext ctx) throws JsonParseException
		{
			var map = new HashMap<Integer, BlasterAttachmentDescriptor>();

			var obj = e.getAsJsonObject();

			for (var pair : obj.entrySet())
			{
				var mask = Integer.parseInt(pair.getKey());
				var data = pair.getValue().getAsJsonObject();

				var mutex = data.get("mutex");

				boolean foundMutex = false;
				short mutexData = 0;

				if (mutex instanceof JsonPrimitive mutexP)
				{
					if (mutexP.isNumber())
					{
						foundMutex = true;
						mutexData = mutexP.getAsShort();
					}
					else if (mutexP.isString())
					{
						mutexData = BlasterAttachmentDescriptor.unpackMutexString(mutexP.getAsString());
						foundMutex = mutexData >= 0;
					}
				}

				if (!foundMutex)
					throw new JsonParseException("Mutex must be a bitmask integer or a string reference to a named category");

				String visComp = null;

				if (data.has("visualComponent"))
					visComp = data.get("visualComponent").getAsString();

				map.put(mask, new BlasterAttachmentDescriptor(mutexData, data.get("id").getAsString(), visComp));
			}

			return map;
		}
	}

	public SwgBlasterManager()
	{
		super(
				new GsonBuilder()
						.registerTypeAdapter(BlasterArchetype.class, new BlasterArchetypeAdapter())
						.registerTypeAdapter(Vec3d.class, new Vec3dDeserializer())
						.registerTypeAdapter(EulerAngle.class, new EulerAngleDeserializer())
						.registerTypeAdapter(TypeToken.getParameterized(ArrayList.class, BlasterFiringMode.class).getType(), new BlasterFiringModesAdapter())
						.registerTypeAdapter(TypeToken.getParameterized(HashMap.class, Integer.class, BlasterAttachmentDescriptor.class).getType(), new BlasterAttachmentDescriptorDeserializer())
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

	public BlasterDescriptor getDataAndAssert(Identifier key)
	{
		var data = super.getData(key);

		if (data != null)
			return data;

		var j = CrashReport.create(new NullPointerException("Cannot get blaster descriptor for unknown key " + key.toString()), "Getting blaster descriptor");

		var k = j.addElement("Blaster Manager Data");
		k.add("Defined keys", this::getDataString);

		throw new CrashException(j);
	}

	private String getDataString()
	{
		var data = getData();
		if (data == null)
			return "null";
		return data.keySet().stream().map(Identifier::toString).collect(Collectors.joining(", "));
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
		buf.writeByte(value.automaticRepeatTime);
		buf.writeByte(value.burstRepeatTime);

		buf.writeByte(value.burstSize);

		buf.writeFloat(value.recoil.horizontal);
		buf.writeFloat(value.recoil.vertical);

		buf.writeFloat(value.spread.horizontal);
		buf.writeFloat(value.spread.vertical);

		buf.writeInt(value.heat.capacity);
		buf.writeByte(value.heat.perRound);
		buf.writeByte(value.heat.drainSpeed);
		buf.writeByte(value.heat.overheatPenalty);
		buf.writeByte(value.heat.overheatDrainSpeed);
		buf.writeByte(value.heat.passiveCooldownDelay);
		buf.writeByte(value.heat.overchargeBonus);

		buf.writeFloat(value.cooling.primaryBypassTime);
		buf.writeFloat(value.cooling.primaryBypassTolerance);
		buf.writeFloat(value.cooling.secondaryBypassTime);
		buf.writeFloat(value.cooling.secondaryBypassTolerance);

		buf.writeInt(value.attachmentDefault);

		if (value.attachmentMap == null)
			buf.writeByte(0);
		else
		{
			buf.writeByte(value.attachmentMap.size());

			for (var entry : value.attachmentMap.entrySet())
			{
				buf.writeInt(entry.getKey());
				buf.writeShort(entry.getValue().mutex);
				buf.writeString(entry.getValue().id);
				PacketByteBufHelper.writeNullable(buf, entry.getValue().visualComponent, PacketByteBuf::writeString);
			}
		}
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
		var automaticRepeatTime = buf.readByte();
		var burstRepeatTime = buf.readByte();

		var burstSize = buf.readByte();

		var recoil_horizontal = buf.readFloat();
		var recoil_vertical = buf.readFloat();

		var spread_horizontal = buf.readFloat();
		var spread_vertical = buf.readFloat();

		var heat_capacity = buf.readInt();
		var heat_perRound = buf.readByte();
		var heat_drainSpeed = buf.readByte();
		var heat_cooldownDelay = buf.readByte();
		var heat_overheatDrainSpeed = buf.readByte();
		var heat_passiveCooldownDelay = buf.readByte();
		var heat_overchargeBonus = buf.readByte();

		var cooling_primaryBypassTime = buf.readFloat();
		var cooling_primaryBypassTolerance = buf.readFloat();
		var cooling_secondaryBypassTime = buf.readFloat();
		var cooling_secondaryBypassTolerance = buf.readFloat();

		var attachmentDefault = buf.readInt();

		var attachmentMap = new HashMap<Integer, BlasterAttachmentDescriptor>();

		var numAttachments = buf.readByte();

		for (var attIdx = 0; attIdx < numAttachments; attIdx++)
		{
			var attachmmentFlag = buf.readInt();
			var attachmmentMutex = buf.readShort();
			var attachmentId = buf.readString();
			var attachmentvisualComponent = PacketByteBufHelper.readNullable(buf, PacketByteBuf::readString);

			attachmentMap.put(attachmmentFlag, new BlasterAttachmentDescriptor(attachmmentMutex, attachmentId, attachmentvisualComponent));
		}

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
				burstRepeatTime,
				burstSize,
				new BlasterAxialInfo(recoil_horizontal, recoil_vertical),
				new BlasterAxialInfo(spread_horizontal, spread_vertical),
				new BlasterHeatInfo(heat_capacity, heat_perRound, heat_drainSpeed, heat_cooldownDelay, heat_overheatDrainSpeed, heat_passiveCooldownDelay, heat_overchargeBonus),
				new BlasterCoolingBypassProfile(cooling_primaryBypassTime, cooling_primaryBypassTolerance, cooling_secondaryBypassTime, cooling_secondaryBypassTolerance),
				attachmentDefault,
				attachmentMap
		);
	}

	protected BlasterDescriptor deserializeDataEntry(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		var value = GSON.fromJson(jsonObject, BlasterDescriptor.class);
		value.id = identifier;
		return value;
	}
}
