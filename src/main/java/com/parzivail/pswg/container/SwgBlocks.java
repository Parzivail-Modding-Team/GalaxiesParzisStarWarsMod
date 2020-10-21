package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.block.*;
import com.parzivail.pswg.blockentity.BlasterWorkbenchBlockEntity;
import com.parzivail.pswg.blockentity.MoistureVaporatorBlockEntity;
import com.parzivail.pswg.blockentity.MosEisleyCrateBlockEntity;
import com.parzivail.pswg.blockentity.OctagonCrateBlockEntity;
import com.parzivail.util.block.BlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
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
		public static final OctagonCrateBlock OctagonOrange = new OctagonCrateBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final OctagonCrateBlock OctagonGray = new OctagonCrateBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final OctagonCrateBlock OctagonBlack = new OctagonCrateBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final BlockEntityType<OctagonCrateBlockEntity> OctagonBlockEntityType = BlockEntityType.Builder.create(OctagonCrateBlockEntity::new, OctagonOrange, OctagonGray, OctagonBlack).build(null);

		public static final MosEisleyCrateBlock MosEisley = new MosEisleyCrateBlock(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final BlockEntityType<MosEisleyCrateBlockEntity> MosEisleyBlockEntityType = BlockEntityType.Builder.create(MosEisleyCrateBlockEntity::new, MosEisley).build(null);
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
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Cortosis = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Diatium = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Exonium = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Helicite = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Ionite = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Kelerium = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Rubindum = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Thorolide = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block Titanium = new Block(FabricBlockSettings.of(Material.STONE));
	}

	public static class Sand
	{
		public static final Block Tatooine = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND));
	}

	public static class MoistureVaporator
	{
		public static final BlockMoistureVaporator Gx8 = new BlockMoistureVaporator(FabricBlockSettings.of(Material.METAL).nonOpaque());
		public static final BlockEntityType<MoistureVaporatorBlockEntity> Gx8BlockEntityType = BlockEntityType.Builder.create(MoistureVaporatorBlockEntity::new, Gx8).build(null);
	}

	public static class Workbench
	{
		public static final BlasterWorkbenchBlock Blaster = new BlasterWorkbenchBlock(FabricBlockSettings.of(Material.METAL));
		public static final BlockEntityType<BlasterWorkbenchBlockEntity> BlasterBlockEntityType = BlockEntityType.Builder.create(BlasterWorkbenchBlockEntity::new, Blaster).build(null);
	}

	public static void register(Block block, Identifier identifier)
	{
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(Galaxies.Tab)));
	}

	public static void register(Block block, BlockEntityType<?> blockEntityType, Identifier identifier)
	{
		register(block, identifier);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, blockEntityType);
	}

	public static void register(BlockEntityType<?> blockEntityType, Identifier identifier)
	{
		Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, blockEntityType);
	}
}
