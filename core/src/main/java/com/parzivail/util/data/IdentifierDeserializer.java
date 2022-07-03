package com.parzivail.util.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;

public class IdentifierDeserializer implements JsonDeserializer<Identifier>
{
	@Override
	public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return new Identifier(json.getAsString());
	}
}
