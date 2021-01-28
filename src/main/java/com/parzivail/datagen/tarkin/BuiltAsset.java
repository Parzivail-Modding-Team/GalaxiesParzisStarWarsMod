package com.parzivail.datagen.tarkin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.parzivail.util.Lumberjack;
import net.minecraft.util.Identifier;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuiltAsset
{
	static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private final Path file;
	private final JsonElement contents;

	private BuiltAsset(Path file, JsonElement contents)
	{
		this.file = file;
		this.contents = contents;
	}

	private static Path getResourcePath(String slug, Identifier identifier)
	{
		return AssetGenerator.getRootDir().resolve(Paths.get(slug, identifier.getNamespace(), identifier.getPath()));
	}

	private static Path getAssetPath(Identifier identifier)
	{
		return getResourcePath("assets", identifier);
	}

	private static Path getDataPath(Identifier identifier)
	{
		return getResourcePath("data", identifier);
	}

	private static Path getBlockstatePath(Identifier identifier)
	{
		return getAssetPath(IdentifierUtil.concat("blockstates/", identifier, ".json"));
	}

	private static Path getBlockModelPath(Identifier identifier)
	{
		return getAssetPath(IdentifierUtil.concat("models/block/", identifier, ".json"));
	}

	private static Path getItemModelPath(Identifier identifier)
	{
		return getAssetPath(IdentifierUtil.concat("models/item/", identifier, ".json"));
	}

	private static Path getLootTablePath(Identifier identifier)
	{
		return getDataPath(IdentifierUtil.concat("loot_tables/", identifier, ".json"));
	}

	private static Path getRecipePath(Identifier identifier)
	{
		return getDataPath(IdentifierUtil.concat("recipes/", identifier, ".json"));
	}

	public static BuiltAsset blockstate(Identifier identifier, JsonElement contents)
	{
		Lumberjack.log("Created blockstate %s", identifier);
		return new BuiltAsset(getBlockstatePath(identifier), contents);
	}

	public static BuiltAsset blockModel(Identifier identifier, JsonElement contents)
	{
		Lumberjack.log("Created block model %s", identifier);
		return new BuiltAsset(getBlockModelPath(identifier), contents);
	}

	public static BuiltAsset itemModel(Identifier identifier, JsonElement contents)
	{
		Lumberjack.log("Created item model %s", identifier);
		return new BuiltAsset(getItemModelPath(identifier), contents);
	}

	public static BuiltAsset lootTable(Identifier identifier, JsonElement contents)
	{
		Lumberjack.log("Created loot table %s", identifier);
		return new BuiltAsset(getLootTablePath(identifier), contents);
	}

	public static BuiltAsset recipe(Identifier identifier, JsonElement contents)
	{
		Lumberjack.log("Created recipe %s", identifier);
		return new BuiltAsset(getRecipePath(identifier), contents);
	}

	public void write()
	{
		try
		{
			Files.createDirectories(file.getParent());

			String path = getFilename();

			FileWriter writer = new FileWriter(path);
			writer.write(GSON.toJson(contents));
			writer.close();
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
