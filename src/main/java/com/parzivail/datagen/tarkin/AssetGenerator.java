package com.parzivail.datagen.tarkin;

import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AssetGenerator
{
	private static Path rootDir = Paths.get("..", "src", "main", "resources");

	static Identifier getRegistryName(ItemConvertible item)
	{
		return Registry.ITEM.getId(item.asItem());
	}

//	static Identifier getRegistryName(Block block)
//	{
//		return Registry.BLOCK.getId(block);
//	}

	static Identifier getTextureName(Block block)
	{
		Identifier id = Registry.BLOCK.getId(block);
		return new Identifier(id.getNamespace(), "block/" + id.getPath());
	}

	public static BlockGenerator blockDefaultDrops(Block block)
	{
		return block(block)
				.lootTable(LootTableFile::basic);
	}

	public static BlockGenerator block(Block block)
	{
		return new BlockGenerator(block)
				.state(BlockStateGenerator::basic)
				.model(ModelFile::cube)
				.itemModel(ModelFile::ofBlock);
	}

	public static void setOutputRoot(Path rootDir)
	{
		AssetGenerator.rootDir = rootDir;
	}

	public static Path getRootDir()
	{
		return rootDir;
	}
}
