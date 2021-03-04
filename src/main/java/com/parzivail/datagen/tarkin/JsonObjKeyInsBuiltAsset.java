package com.parzivail.datagen.tarkin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		File fFile = file.toFile();
		String path = getFilename();

		if (fFile.exists())
		{
			try
			{
				FileReader reader = new FileReader(path);
				JsonObject json = GSON.fromJson(reader, JsonObject.class);

				JsonObject objContents = (JsonObject)contents;

				for (Map.Entry<String, JsonElement> pair : objContents.entrySet())
				{
					if (!json.has(pair.getKey()))
						json.add(pair.getKey(), pair.getValue());
				}

				JsonWriter jsonWriter = new JsonWriter(new FileWriter(path));
				jsonWriter.setIndent("\t");
				GSON.toJson(getSorted(json), jsonWriter);
				jsonWriter.close();
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

				FileWriter writer = new FileWriter(path);
				writer.write(GSON.toJson(contents));
				writer.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static JsonObject getSorted(JsonObject jsonObject)
	{
		List<String> keySet = jsonObject.entrySet().stream().map(Map.Entry::getKey).sorted().collect(Collectors.toList());
		JsonObject temp = new JsonObject();

		for (String key : keySet)
		{
			JsonElement ele = jsonObject.get(key);
			if (ele.isJsonObject())
			{
				ele = getSorted(ele.getAsJsonObject());
				temp.add(key, ele);
			}
			else if (ele.isJsonArray())
				temp.add(key, ele.getAsJsonArray());
			else
				temp.add(key, ele.getAsJsonPrimitive());
		}

		return temp;
	}
}
