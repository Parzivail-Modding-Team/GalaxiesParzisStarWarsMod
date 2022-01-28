package com.parzivail.pswg.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
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
	public static final Identifier ID = Resources.id("blaster_manager");

	private static class IdentifierDeserializer implements JsonDeserializer<Identifier>
	{
		@Override
		public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			return new Identifier(json.getAsString());
		}
	}

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
				var bit = Integer.parseInt(pair.getKey());
				var data = pair.getValue().getAsJsonObject();

				short mutexData = 0;
				if (data.has("mutex"))
					mutexData = data.get("mutex").getAsShort();

				byte icon = 0;
				if (data.has("icon"))
					icon = data.get("icon").getAsByte();

				String visComp = null;
				if (data.has("visualComponent"))
					visComp = data.get("visualComponent").getAsString();

				Identifier texture = null;
				if (data.has("texture"))
					texture = new Identifier(data.get("texture").getAsString());

				map.put(bit, new BlasterAttachmentDescriptor(bit, mutexData, icon, data.get("id").getAsString(), visComp, texture));
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
						.registerTypeAdapter(Identifier.class, new IdentifierDeserializer())
						.registerTypeAdapter(TypeToken.getParameterized(ArrayList.class, BlasterFiringMode.class).getType(), new BlasterFiringModesAdapter())
						.registerTypeAdapter(TypeToken.getParameterized(HashMap.class, Integer.class, BlasterAttachmentDescriptor.class).getType(), new BlasterAttachmentDescriptorDeserializer())
						.create(),
				"items/blasters"
		);
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
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
		buf.writeString(value.sound.toString());

		buf.writeByte(value.type.getId());
		buf.writeShort(BlasterFiringMode.pack(value.firingModes));

		buf.writeFloat(value.damage);
		buf.writeFloat(value.range);
		buf.writeFloat(value.weight);
		buf.writeFloat(value.boltColor);
		buf.writeInt(value.magazineSize);
		buf.writeShort(value.automaticRepeatTime);
		buf.writeShort(value.burstRepeatTime);

		buf.writeShort(value.burstSize);

		buf.writeFloat(value.recoil.horizontal);
		buf.writeFloat(value.recoil.vertical);

		buf.writeFloat(value.spread.horizontal);
		buf.writeFloat(value.spread.vertical);

		buf.writeInt(value.heat.capacity);
		buf.writeShort(value.heat.perRound);
		buf.writeShort(value.heat.drainSpeed);
		buf.writeShort(value.heat.overheatPenalty);
		buf.writeShort(value.heat.overheatDrainSpeed);
		buf.writeShort(value.heat.passiveCooldownDelay);
		buf.writeShort(value.heat.overchargeBonus);

		buf.writeFloat(value.cooling.primaryBypassTime);
		buf.writeFloat(value.cooling.primaryBypassTolerance);
		buf.writeFloat(value.cooling.secondaryBypassTime);
		buf.writeFloat(value.cooling.secondaryBypassTolerance);

		buf.writeInt(value.attachmentDefault);
		buf.writeInt(value.attachmentMinimum);

		if (value.attachmentMap == null)
			buf.writeByte(0);
		else
		{
			buf.writeByte(value.attachmentMap.size());

			for (var entry : value.attachmentMap.entrySet())
			{
				buf.writeInt(entry.getKey());
				buf.writeShort(entry.getValue().mutex);
				buf.writeByte(entry.getValue().icon);
				buf.writeString(entry.getValue().id);
				PacketByteBufHelper.writeNullable(buf, entry.getValue().texture, (buf1, value1) -> buf1.writeString(value1.toString()));
				PacketByteBufHelper.writeNullable(buf, entry.getValue().visualComponent, PacketByteBuf::writeString);
			}
		}
	}

	protected BlasterDescriptor readDataEntry(Identifier key, PacketByteBuf buf)
	{
		var sound = new Identifier(buf.readString());

		var archetype = BlasterArchetype.ID_LOOKUP.get(buf.readByte());
		var firingModes = BlasterFiringMode.unpack(buf.readShort());

		var damage = buf.readFloat();
		var range = buf.readFloat();
		var weight = buf.readFloat();
		var boltColor = buf.readFloat();
		var magazineSize = buf.readInt();
		var automaticRepeatTime = buf.readShort();
		var burstRepeatTime = buf.readShort();

		var burstSize = buf.readShort();

		var recoil_horizontal = buf.readFloat();
		var recoil_vertical = buf.readFloat();

		var spread_horizontal = buf.readFloat();
		var spread_vertical = buf.readFloat();

		var heat_capacity = buf.readInt();
		var heat_perRound = buf.readShort();
		var heat_drainSpeed = buf.readShort();
		var heat_cooldownDelay = buf.readShort();
		var heat_overheatDrainSpeed = buf.readShort();
		var heat_passiveCooldownDelay = buf.readShort();
		var heat_overchargeBonus = buf.readShort();

		var cooling_primaryBypassTime = buf.readFloat();
		var cooling_primaryBypassTolerance = buf.readFloat();
		var cooling_secondaryBypassTime = buf.readFloat();
		var cooling_secondaryBypassTolerance = buf.readFloat();

		var attachmentDefault = buf.readInt();
		var attachmentMinimum = buf.readInt();

		var attachmentMap = new HashMap<Integer, BlasterAttachmentDescriptor>();

		var numAttachments = buf.readByte();

		for (var attIdx = 0; attIdx < numAttachments; attIdx++)
		{
			var attachmentBit = buf.readInt();
			var attachmentMutex = buf.readShort();
			var attachmentIcon = buf.readByte();
			var attachmentId = buf.readString();
			var attachmentTexture = PacketByteBufHelper.readNullable(buf, buf1 -> new Identifier(buf1.readString()));
			var attachmentVisualComponent = PacketByteBufHelper.readNullable(buf, PacketByteBuf::readString);

			attachmentMap.put(attachmentBit, new BlasterAttachmentDescriptor(attachmentBit, attachmentMutex, attachmentIcon, attachmentId, attachmentVisualComponent, attachmentTexture));
		}

		return new BlasterDescriptor(
				key,
				sound,
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
				attachmentMinimum,
				attachmentMap
		);
	}

	protected BlasterDescriptor deserializeDataEntry(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		var value = GSON
				.fromJson(jsonObject, BlasterDescriptor.class);
		value.id = identifier;
		return value;
	}
}
