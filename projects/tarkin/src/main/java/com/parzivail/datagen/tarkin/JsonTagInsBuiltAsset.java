package com.parzivail.datagen.tarkin;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.tag.TagEntry;
import net.minecraft.tag.TagFile;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class JsonTagInsBuiltAsset extends BuiltAsset
{
	private final TagEntry contents;

	protected JsonTagInsBuiltAsset(Path file, TagEntry contents)
	{
		super(file, null);
		this.contents = contents;
	}

	@Override
	public void write()
	{
		try
		{
			ArrayList<TagEntry> entries;

			if (Files.exists(file))
			{
				try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
				{
					entries = new ArrayList<>(TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseReader(reader))).getOrThrow(false, Tarkin.LOG::error).entries());
				}
			}
			else
			{
				entries = new ArrayList<>();
				Files.createDirectories(file.getParent());
			}

			entries.add(contents);

			try (
					Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
					var jsonWriter = new JsonWriter(writer)
			)
			{
				GSON.toJson(JsonObjKeyInsBuiltAsset.sortElementsRecursively(TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(entries, false)).getOrThrow(false, Tarkin.LOG::error)), jsonWriter);
				writer.write('\n');
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
