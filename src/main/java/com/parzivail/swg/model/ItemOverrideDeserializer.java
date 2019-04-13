package com.parzivail.swg.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemOverrideDeserializer implements JsonDeserializer<ItemOverride>
{
	public ItemOverride deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
	{
		JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
		ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "model"));
		Map<ResourceLocation, Float> map = this.makeMapResourceValues(jsonobject);
		return new ItemOverride(resourcelocation, map);
	}

	protected Map<ResourceLocation, Float> makeMapResourceValues(JsonObject p_188025_1_)
	{
		Map<ResourceLocation, Float> map = Maps.newLinkedHashMap();
		JsonObject jsonobject = JsonUtils.getJsonObject(p_188025_1_, "predicate");

		for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet())
		{
			map.put(new ResourceLocation(entry.getKey()), JsonUtils.getFloat(entry.getValue(), entry.getKey()));
		}

		return map;
	}
}
