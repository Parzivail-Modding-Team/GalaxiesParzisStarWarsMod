package com.parzivail.pswg.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.item.blaster.data.BlasterArchetype;
import com.parzivail.pswg.item.blaster.data.BlasterAttachmentDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterFiringMode;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SwgBlasterManager extends TypedDataLoader<BlasterDescriptor>
{
	public static final Identifier ID = Resources.id("blaster_manager");
	public static final SwgBlasterManager INSTANCE = new SwgBlasterManager();

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

	private SwgBlasterManager()
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
		value.write(buf);
	}

	protected BlasterDescriptor readDataEntry(Identifier key, PacketByteBuf buf)
	{
		return BlasterDescriptor.read(buf, key);
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
