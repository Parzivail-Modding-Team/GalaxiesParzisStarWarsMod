package com.parzivail.datagen.tarkin;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.minecraft.tag.Tag;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonTagInsBuiltAsset extends BuiltAsset
{
	private final Tag.Entry contents;

	protected JsonTagInsBuiltAsset(Path file, Tag.Entry contents)
	{
		super(file, null);
		this.contents = contents;
	}

	@Override
	public void write()
	{
		try
		{
			var tag = Tag.Builder.create();
			if (Files.exists(file))
			{
				try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
				{
					tag.read(JsonHelper.deserialize(GSON, reader, JsonObject.class), "TARKIN");
				}
			}
			else
				Files.createDirectories(file.getParent());

			tag.add(contents, "TARKIN");

			try (
					Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
					var jsonWriter = new JsonWriter(writer)
			)
			{
				GSON.toJson(tag.toJson(), jsonWriter);
				writer.write('\n');
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
