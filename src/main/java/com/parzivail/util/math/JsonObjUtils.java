package com.parzivail.util.math;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonObjUtils
{
	public static String[] getStringArray(JsonObject json, String memberName, String[] fallback)
	{
		if (!json.has(memberName))
			return fallback;

		JsonArray arr = json.getAsJsonArray(memberName);
		String[] strings = new String[arr.size()];

		for (int i = 0; i < arr.size(); i++)
			strings[i] = arr.get(i).getAsString();

		return strings;
	}
}
