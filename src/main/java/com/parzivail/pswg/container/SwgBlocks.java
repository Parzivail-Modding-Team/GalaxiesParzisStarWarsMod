package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.block.RotatingBlock;
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
		public static final Block Tatooine = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).nonOpaque());
	}

	public static class Crate
	{
		public static final RotatingBlock OctagonOrange = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final RotatingBlock OctagonGray = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final RotatingBlock OctagonBlack = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
	}

	public static class Vaporator
	{
		public static final RotatingBlock Gx8 = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
	}

	public static class Ore
	{
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Cortosis = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Diatium = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Exonium = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Helicite = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Ionite = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Kelerium = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Rubindum = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Thorolide = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
		public static final Block Titanium = new Block(FabricBlockSettings.of(Material.STONE).nonOpaque());
	}

	public static void register(Block block, Identifier identifier)
	{
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(Galaxies.Tab)));
	}
}
