package com.parzivail.pswg.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.*;
import com.parzivail.util.data.IdentifierDeserializer;
import com.parzivail.util.data.StringInteropAdapter;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SwgBlasterManager extends TypedDataLoader<BlasterDescriptor>
{
	public static final Identifier ID = Resources.id("blaster_manager");
	public static final SwgBlasterManager INSTANCE = new SwgBlasterManager();

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

				var func = BlasterAttachmentFunction.NONE;
				if (data.has("function"))
					func = BlasterAttachmentFunction.VALUE_LOOKUP.get(data.get("function").getAsString());

				String visComp = null;
				if (data.has("visualComponent"))
					visComp = data.get("visualComponent").getAsString();

				Identifier texture = null;
				if (data.has("texture"))
					texture = new Identifier(data.get("texture").getAsString());

				map.put(bit, new BlasterAttachmentDescriptor(bit, mutexData, icon, data.get("id").getAsString(), func, visComp, texture));
			}

			return map;
		}
	}

	private SwgBlasterManager()
	{
		super(
				new GsonBuilder()
						.registerTypeAdapter(Identifier.class, new IdentifierDeserializer())
						.registerTypeAdapter(BlasterArchetype.class, new StringInteropAdapter<>(BlasterArchetype::getValue, BlasterArchetype.VALUE_LOOKUP::get))
						.registerTypeAdapter(BlasterFiringMode.class, new StringInteropAdapter<>(BlasterFiringMode::getValue, BlasterFiringMode.VALUE_LOOKUP::get))
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

		var keyName = key == null ? "[null]" : '"' + key.toString() + '"';
		var j = CrashReport.create(new NullPointerException("Cannot get blaster descriptor for unknown key " + keyName), "Getting blaster descriptor");

		var k = j.addElement("Blaster Manager Data");
		k.add("Defined keys", this::getDataString);

		throw new CrashException(j);
	}

	@Override
	protected void onServerDataLoaded()
	{
		for (var entry : getData().values())
			SwgSounds.registerIfAbsent(BlasterItem.modelIdToSoundId(entry.sound));
	}

	@Override
	protected void onClientDataLoaded()
	{
		for (var entry : getData().values())
			SwgSounds.registerIfAbsent(BlasterItem.modelIdToSoundId(entry.sound));
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
