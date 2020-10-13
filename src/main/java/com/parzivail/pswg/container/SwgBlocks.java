package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.block.BlockMoistureVaporator;
import com.parzivail.pswg.block.RotatingBlock;
import com.parzivail.util.block.BlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class SwgBlocks
{
	public static class Crate
	{
		public static final RotatingBlock OctagonOrange = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final RotatingBlock OctagonGray = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final RotatingBlock OctagonBlack = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final RotatingBlock MosEisley = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
	}

	public static class Leaves
	{
		public static final LeavesBlock Sequoia = createLeavesBlock();

		private static LeavesBlock createLeavesBlock()
		{
			return new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().suffocates(BlockUtils::never).blockVision(BlockUtils::never));
		}
	}

	public static class Log
	{
		public static final PillarBlock Sequoia = createLogBlock(MaterialColor.WOOD, MaterialColor.BROWN);

		private static PillarBlock createLogBlock(MaterialColor topMaterialColor, MaterialColor sideMaterialColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, (blockState) -> {
				return blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor;
			}).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		}
	}

	public static class Machine
	{
		public static final RotatingBlock Spoked = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
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

	public static class Sand
	{
		public static final Block Tatooine = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).nonOpaque());
	}

	public static class Vaporator
	{
		public static final BlockMoistureVaporator Gx8 = new BlockMoistureVaporator(FabricBlockSettings.of(Material.METAL).nonOpaque());
	}

	public static void register(Block block, Identifier identifier)
	{
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(Galaxies.Tab)));
	}
}
