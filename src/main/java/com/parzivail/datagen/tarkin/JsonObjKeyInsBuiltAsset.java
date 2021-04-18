package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonObjKeyInsBuiltAsset extends BuiltAsset
{
	protected JsonObjKeyInsBuiltAsset(Path file, JsonElement contents)
	{
		super(file, contents);
	}

	@Override
	public void write()
	{
		if (Files.exists(file))
		{
			try
			{
				JsonObject json;
				try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
				{
					json = GSON.fromJson(reader, JsonObject.class);
				}

				JsonObject objContents = (JsonObject)contents;

				for (Map.Entry<String, JsonElement> pair : objContents.entrySet())
				{
					if (!json.has(pair.getKey()))
						json.add(pair.getKey(), pair.getValue());
				}

				try (
						Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
						JsonWriter jsonWriter = new JsonWriter(writer)
				)
				{
					jsonWriter.setIndent("\t");
					GSON.toJson(sortKeysRecursively(json), jsonWriter);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Files.createDirectories(file.getParent());

				try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.US_ASCII))
				{
					GSON.toJson(contents, writer);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static JsonObject sortKeysRecursively(JsonObject jsonObject)
	{
		List<String> keySet = jsonObject.entrySet().stream().map(Map.Entry::getKey).sorted().collect(Collectors.toList());
		JsonObject temp = new JsonObject();

		for (String key : keySet)
		{
			JsonElement ele = jsonObject.get(key);
			if (ele.isJsonObject())
			{
				ele = sortKeysRecursively(ele.getAsJsonObject());
			}
			else if (ele.isJsonArray())
			{
				ele = sortKeysRecursively(ele.getAsJsonArray());
			}
			temp.add(key, ele);
		}

		return temp;
	}

	private static JsonArray sortKeysRecursively(JsonArray jsonArray)
	{
		final JsonArray temp = new JsonArray();

		for (JsonElement ele : jsonArray)
		{
			if (ele.isJsonObject())
			{
				ele = sortKeysRecursively(ele.getAsJsonObject());
			}
			else if (ele.isJsonArray())
			{
				ele = sortKeysRecursively(ele.getAsJsonArray());
			}
			temp.add(ele);
		}

		return temp;
	}
}
