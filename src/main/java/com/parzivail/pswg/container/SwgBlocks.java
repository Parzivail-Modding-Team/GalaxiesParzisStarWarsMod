package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.block.*;
import com.parzivail.pswg.blockentity.*;
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
		public static final RotatingBlockWithGuiEntity OctagonOrange = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).nonOpaque(), CrateOctagonBlockEntity::new);
		public static final RotatingBlockWithGuiEntity OctagonGray = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).nonOpaque(), CrateOctagonBlockEntity::new);
		public static final RotatingBlockWithGuiEntity OctagonBlack = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).nonOpaque(), CrateOctagonBlockEntity::new);
		public static final BlockEntityType<CrateOctagonBlockEntity> OctagonBlockEntityType = BlockEntityType.Builder.create(CrateOctagonBlockEntity::new, OctagonOrange, OctagonGray, OctagonBlack).build(null);

		public static final RotatingBlockWithGuiEntity MosEisley = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).nonOpaque(), CrateMosEisleyBlockEntity::new);
		public static final BlockEntityType<CrateMosEisleyBlockEntity> MosEisleyBlockEntityType = BlockEntityType.Builder.create(CrateMosEisleyBlockEntity::new, MosEisley).build(null);

		public static final RotatingBlockWithGuiEntity ImperialCube = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).nonOpaque(), CrateImperialCubeBlockEntity::new);
		public static final BlockEntityType<CrateImperialCubeBlockEntity> ImperialCubeBlockEntityType = BlockEntityType.Builder.create(CrateImperialCubeBlockEntity::new, ImperialCube).build(null);
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

	public static class Light
	{
		public static final Block FloorWedge = new RotatingBlock(FabricBlockSettings.of(Material.METAL).nonOpaque().luminance(15).dynamicBounds());
	}

	public static class Panel
	{
		public static final Block ImperialBase = new Block(FabricBlockSettings.of(Material.METAL).materialColor(MaterialColor.GRAY).sounds(BlockSoundGroup.METAL));
		public static final Block ImperialBlackBase = new Block(FabricBlockSettings.of(Material.METAL).materialColor(MaterialColor.BLACK).sounds(BlockSoundGroup.METAL));
		public static final PillarBlock Imperial1 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock Imperial2 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock Imperial3 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLight1 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLight2 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLight3 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLight4 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLight5 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLight6 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLightTall1 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLightTall2 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLightTall3 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLightTall4 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		public static final PillarBlock ImperialLightDecoy = createPanel(MaterialColor.GRAY, MaterialColor.GRAY);
		public static final PillarBlock ImperialLightOff = createPanel(MaterialColor.GRAY, MaterialColor.BLACK);

		private static PillarBlock createPanel(MaterialColor topMaterialColor, MaterialColor sideMaterialColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> {
				return blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor;
			}).strength(2.0F).sounds(BlockSoundGroup.METAL));
		}
	}

	public static class Pipe
	{
		public static final Block Thick = new SelfConnectingNodeBlock(FabricBlockSettings.of(Material.METAL).materialColor(MaterialColor.GRAY).nonOpaque().sounds(BlockSoundGroup.METAL));
	}

	public static class Sand
	{
		public static final Block Tatooine = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).strength(0.5F).sounds(BlockSoundGroup.SAND));
	}

	public static class Stone
	{
		public static final Block Temple = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block TempleBricks = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block TempleBricksChisled = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block TempleSlabSideSmooth = new Block(FabricBlockSettings.of(Material.STONE));
		public static final Block TempleSmooth = new Block(FabricBlockSettings.of(Material.STONE));
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
