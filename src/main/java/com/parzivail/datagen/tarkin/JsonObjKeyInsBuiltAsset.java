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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

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

				var objContents = (JsonObject)contents;

				for (var pair : objContents.entrySet())
				{
					if (!json.has(pair.getKey()))
						json.add(pair.getKey(), pair.getValue());
				}

				try (
						Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
						var jsonWriter = new JsonWriter(writer)
				)
				{
					jsonWriter.setIndent("\t");
					GSON.toJson(sortKeysRecursively(json), jsonWriter);
					writer.write('\n');
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

				try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8))
				{
					GSON.toJson(contents, writer);
					writer.write('\n');
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static JsonObject sortKeysRecursively(JsonObject jsonObject)
	{
		var keySet = jsonObject.entrySet().stream().map(Map.Entry::getKey).sorted().toList();
		var temp = new JsonObject();

		for (var key : keySet)
		{
			var ele = jsonObject.get(key);
			if (ele.isJsonObject())
			{
				ele = sortKeysRecursively(ele.getAsJsonObject());
			}
			else if (ele.isJsonArray())
			{
				ele = sortElementsRecursively(ele.getAsJsonArray());
			}
			temp.add(key, ele);
		}

		return temp;
	}

	private static JsonArray sortElementsRecursively(JsonArray jsonArray)
	{
		final var temp = new JsonArray();

		final var data = new ArrayList<JsonElement>();
		jsonArray.forEach(data::add);
		final var sortedData = data.stream().sorted(JsonObjKeyInsBuiltAsset::tryCompareElement).toList();

		for (var ele : sortedData)
		{
			if (ele.isJsonObject())
			{
				ele = sortKeysRecursively(ele.getAsJsonObject());
			}
			else if (ele.isJsonArray())
			{
				ele = sortElementsRecursively(ele.getAsJsonArray());
			}
			temp.add(ele);
		}

		return temp;
	}

	private static int tryCompareElement(JsonElement o1, JsonElement o2)
	{
		if (o1.isJsonPrimitive() && o2.isJsonPrimitive())
		{
			var p1 = o1.getAsJsonPrimitive();
			var p2 = o2.getAsJsonPrimitive();

			if (p1.isNumber() && p2.isNumber())
				return Comparator.<Double>naturalOrder().compare(p1.getAsDouble(), p2.getAsDouble());
			else
				return Comparator.<String>naturalOrder().compare(p1.getAsString(), p2.getAsString());
		}

		return Comparator.<Integer>naturalOrder().compare(o1.hashCode(), o2.hashCode());
	}
}
