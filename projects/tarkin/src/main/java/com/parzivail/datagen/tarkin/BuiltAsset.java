package com.parzivail.datagen.tarkin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.parzivail.datagen.FilesystemUtils;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BuiltAsset
{
	static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	protected final Path file;
	protected final JsonElement contents;

	protected BuiltAsset(Path file, JsonElement contents)
	{
		this.file = file;
		this.contents = contents;
	}

	private static Path getLangPath(Identifier identifier)
	{
		// /assets/minecraft/lang/en_us.json
		return FilesystemUtils.getAssetPath(IdentifierUtil.concat("lang/", identifier, ".json"));
	}

	public static BuiltAsset blockstate(Identifier identifier, JsonElement contents)
	{
		Tarkin.LOG.log("Created blockstate %s", identifier);
		return new BuiltAsset(FilesystemUtils.getBlockstatePath(identifier), contents);
	}

	public static BuiltAsset blockModel(Identifier identifier, JsonElement contents)
	{
		Tarkin.LOG.log("Created block model %s", identifier);
		return new BuiltAsset(FilesystemUtils.getBlockModelPath(identifier), contents);
	}

	public static BuiltAsset itemModel(Identifier identifier, JsonElement contents)
	{
		Tarkin.LOG.log("Created item model %s", identifier);
		return new BuiltAsset(FilesystemUtils.getItemModelPath(identifier), contents);
	}

	public static BuiltAsset lang(Identifier identifier, JsonElement contents)
	{
		Tarkin.LOG.log("Created lang entry %s", contents.toString());
		return new JsonObjKeyInsBuiltAsset(getLangPath(identifier), contents);
	}

	public static BuiltAsset lootTable(Identifier identifier, JsonElement contents)
	{
		Tarkin.LOG.log("Created loot table %s", identifier);
		return new BuiltAsset(FilesystemUtils.getLootTablePath(identifier), contents);
	}

	public static BuiltAsset recipe(Identifier identifier, JsonElement contents)
	{
		Tarkin.LOG.log("Created recipe %s", identifier);
		return new BuiltAsset(FilesystemUtils.getRecipePath(identifier), contents);
	}

	public static BuiltAsset tag(Identifier identifier, TagEntry contents)
	{
		Tarkin.LOG.log("Created tag %s", identifier);
		return new JsonTagInsBuiltAsset(FilesystemUtils.getTagPath(identifier), contents);
	}

	public static void mergeLanguageKeys(Identifier localeKeySource, Identifier locateDestination) throws IOException
	{
		updateLanguageKeys(localeKeySource, locateDestination);

		var pathKeySource = getLangPath(localeKeySource);
		Files.delete(pathKeySource);
	}

	public static void updateLanguageKeys(Identifier localeKeySource, Identifier locateDestination) throws IOException
	{
		var pathKeySource = getLangPath(localeKeySource);
		var pathDest = getLangPath(locateDestination);

		if (!Files.exists(pathDest))
		{
			Tarkin.LOG.error("Could not find the merge destination lang file %s", pathDest);
			return;
		}

		JsonObject keySource;
		try (Reader reader = Files.newBufferedReader(pathKeySource, StandardCharsets.UTF_8))
		{
			keySource = GSON.fromJson(reader, JsonObject.class);
		}

		JsonObject valueSource;
		try (Reader reader = Files.newBufferedReader(pathDest, StandardCharsets.UTF_8))
		{
			valueSource = GSON.fromJson(reader, JsonObject.class);
		}

		for (var entry : valueSource.entrySet())
		{
			var newMember = keySource.get(entry.getKey());
			if (newMember != null)
			{
				var oldValue = entry.getValue().getAsString();
				keySource.addProperty(entry.getKey(), oldValue);
			}
		}

		try (
				Writer writer = Files.newBufferedWriter(pathDest, StandardCharsets.UTF_8);
				var jsonWriter = new JsonWriter(writer)
		)
		{
			jsonWriter.setIndent("\t");
			GSON.toJson(keySource, jsonWriter);
			writer.write('\n');
		}
	}

	public void write()
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

	public String getFilename()
	{
		return file.toAbsolutePath().toString();
	}
}
