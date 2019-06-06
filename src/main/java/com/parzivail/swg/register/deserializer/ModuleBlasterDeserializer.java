package com.parzivail.swg.register.deserializer;

import com.google.gson.*;
import com.parzivail.swg.item.data.BlasterDescriptor;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;

public class ModuleBlasterDeserializer implements JsonDeserializer<BlasterDescriptor>
{
	@Override
	public BlasterDescriptor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject object = json.getAsJsonObject();

		String name = JsonUtils.getString(object, "name", "generic");
		float damage = JsonUtils.getFloat(object, "damage", 0);
		float spread = JsonUtils.getFloat(object, "spread", 0);
		int range = JsonUtils.getInt(object, "range", 0);
		int cost = JsonUtils.getInt(object, "cost", 0);
		float weight = JsonUtils.getFloat(object, "weight", 0);
		String boltColorString = JsonUtils.getString(object, "boltColor", "FF0000");
		int boltColor = Integer.parseInt(boltColorString, 16);
		int clipSize = JsonUtils.getInt(object, "clipSize", 0);
		int roundsBeforeOverheat = JsonUtils.getInt(object, "roundsBeforeOverheat", 1);
		int cooldownTimeTicks = JsonUtils.getInt(object, "cooldownTimeTicks", 20);
		int autofireTimeTicks = JsonUtils.getInt(object, "autofireTimeTicks", 20);

		return new BlasterDescriptor(name, damage, spread, range, cost, weight, clipSize, boltColor, roundsBeforeOverheat, cooldownTimeTicks, autofireTimeTicks);
	}
}
