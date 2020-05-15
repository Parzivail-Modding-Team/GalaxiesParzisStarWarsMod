package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SwgBlocks
{
	public static class Sand
	{
		public static final Block Tatooine = new FallingBlock(FabricBlockSettings.of(Material.SAND).nonOpaque());
	}

	public static class Crate
	{
		public static final Block Octagon = new Block(FabricBlockSettings.of(Material.METAL).nonOpaque());
	}

	public static class Ore
	{
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
	}

	public static void register(Block block, Identifier identifier)
	{
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(Galaxies.Tab)));
	}
}
