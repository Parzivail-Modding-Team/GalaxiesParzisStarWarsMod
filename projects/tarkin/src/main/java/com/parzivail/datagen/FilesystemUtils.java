package com.parzivail.datagen;

import com.parzivail.datagen.tarkin.IdentifierUtil;
import com.parzivail.datagen.tarkin.Tarkin;
import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesystemUtils
{
	private static Path rootDir = null;

	public static void setOutputRoot(Path rootDir)
	{
		FilesystemUtils.rootDir = rootDir;
		Tarkin.LOG.info(String.format("Set output directory to: %s", rootDir.toAbsolutePath()));
	}

	public static Path getRootDir()
	{
		if (rootDir == null)
			throw new RuntimeException("Output directory cannot be null");

		return rootDir;
	}

	private static Path getResourcePath(String slug, Identifier identifier)
	{
		return getRootDir().resolve(Paths.get(slug, identifier.getNamespace(), identifier.getPath()));
	}

	public static Path getAssetPath(Identifier identifier)
	{
		return getResourcePath("assets", identifier);
	}

	private static Path getDataPath(Identifier identifier)
	{
		return getResourcePath("data", identifier);
	}

	public static Path getBlockstatePath(Identifier identifier)
	{
		return getAssetPath(IdentifierUtil.concat("blockstates/", identifier, ".json"));
	}

	public static Path getBlockModelPath(Identifier identifier)
	{
		return getAssetPath(IdentifierUtil.concat("models/block/", identifier, ".json"));
	}

	public static Path getItemModelPath(Identifier identifier)
	{
		return getAssetPath(IdentifierUtil.concat("models/item/", identifier, ".json"));
	}

	public static Path getTagPath(Identifier identifier)
	{
		return getDataPath(IdentifierUtil.concat(identifier, ".json"));
	}

	public static Path getLootTablePath(Identifier identifier)
	{
		return getDataPath(IdentifierUtil.concat("loot_tables/", identifier, ".json"));
	}

	public static Path getRecipePath(Identifier identifier)
	{
		return getDataPath(IdentifierUtil.concat("recipes/tarkin/", identifier, ".json"));
	}

	public static void nukeRecipeDir() throws IOException
	{
		var dummyAsset = getRecipePath(Resources.id("dummy"));

		var parentDir = dummyAsset.getParent();

		if (!Files.exists(parentDir))
			return;

		FileUtils.cleanDirectory(parentDir.toFile());
	}

	public static void nukeBlockModelJsons() throws IOException
	{
		var dummyAsset = getBlockModelPath(Resources.id("dummy"));

		var parentDir = dummyAsset.getParent();

		if (!Files.exists(parentDir))
			return;

		cleanDirectoryOf(parentDir.toFile(), "json");
	}

	public static void nukeItemModelJsons() throws IOException
	{
		var dummyAsset = getItemModelPath(Resources.id("dummy"));

		var parentDir = dummyAsset.getParent();

		if (!Files.exists(parentDir))
			return;

		cleanDirectoryOf(parentDir.toFile(), "json");
	}

	public static void nukeBlockLootTables() throws IOException
	{
		var dummyAsset = getLootTablePath(Resources.id("blocks/dummy"));

		var parentDir = dummyAsset.getParent();

		if (!Files.exists(parentDir))
			return;

		cleanDirectoryOf(parentDir.toFile(), "json");
	}

	public static void nukeTags() throws IOException
	{
		var namespaces = new String[] { "minecraft", "fabric", Resources.MODID };
		for (var namespace : namespaces)
		{
			nukeTags(new Identifier(namespace, "tags/blocks/dummy"));
			nukeTags(new Identifier(namespace, "tags/blocks/mineable/dummy"));
			nukeTags(new Identifier(namespace, "tags/items/dummy"));
		}

		nukeTags(new Identifier("trinkets", "tags/items/chest/dummy"));
	}

	public static void nukeTags(Identifier dummyAssetId) throws IOException
	{
		var dummyAsset = getTagPath(dummyAssetId);

		var parentDir = dummyAsset.getParent();

		if (!Files.exists(parentDir))
			return;

		cleanDirectoryOf(parentDir.toFile(), "json");
	}

	public static void nukeBlockstateDir() throws IOException
	{
		var dummyAsset = getBlockstatePath(Resources.id("dummy"));

		var parentDir = dummyAsset.getParent();

		if (!Files.exists(parentDir))
			return;

		FileUtils.cleanDirectory(parentDir.toFile());
	}

	public static void cleanDirectoryOf(final File directory, String extension) throws IOException
	{
		final File[] files = directory.listFiles();

		for (final File file : files)
		{
			if (FilenameUtils.isExtension(file.getName(), extension))
				FileUtils.forceDelete(file);
		}
	}
}
