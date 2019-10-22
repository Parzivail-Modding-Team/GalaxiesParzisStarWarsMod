package com.parzivail.swg.register.deserializer;

import com.google.gson.*;
import com.parzivail.swg.item.data.LightsaberDescriptor;
import com.parzivail.util.math.JsonObjUtils;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;

public class ModuleLightsaberDeserializer implements JsonDeserializer<LightsaberDescriptor>
{
	public static final ModuleLightsaberDeserializer INSTANCE = new ModuleLightsaberDeserializer();

	private ModuleLightsaberDeserializer()
	{
	}

	@Override
	public LightsaberDescriptor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject object = json.getAsJsonObject();

		String name = JsonUtils.getString(object, "name", "generic");
		boolean waterproof = JsonUtils.getBoolean(object, "waterproof", false);
		boolean unstable = JsonUtils.getBoolean(object, "unstable", false);

		String[] model = JsonObjUtils.getStringArray(object, "model", new String[0]);

		LightsaberDescriptor.Blade blade = getBlade(object, LightsaberDescriptor.Blade.DEFAULT);
		LightsaberDescriptor.Sounds sounds = getSounds(object, LightsaberDescriptor.Sounds.DEFAULT);

		return new LightsaberDescriptor(name, waterproof, unstable, model, blade, sounds);
	}

	private LightsaberDescriptor.Blade getBlade(JsonObject json, LightsaberDescriptor.Blade fallback)
	{
		if (!json.has("blade"))
			return fallback;

		json = json.getAsJsonObject("blade");

		float length = JsonUtils.getFloat(json, "length", 1);
		int glowColor = JsonUtils.getInt(json, "glowColor", 0xFFFFFF);
		int bladeColor = JsonUtils.getInt(json, "coreColor", 0x00FF00);

		return new LightsaberDescriptor.Blade(length, glowColor, bladeColor);
	}

	private LightsaberDescriptor.Sounds getSounds(JsonObject json, LightsaberDescriptor.Sounds fallback)
	{
		if (!json.has("sounds"))
			return fallback;

		json = json.getAsJsonObject("sounds");

		String start = JsonUtils.getString(json, "start", "default");
		String idle = JsonUtils.getString(json, "idle", "default");
		String swing = JsonUtils.getString(json, "swing", "default");
		String stop = JsonUtils.getString(json, "stop", "default");

		return new LightsaberDescriptor.Sounds(start, idle, swing, stop);
	}
}
