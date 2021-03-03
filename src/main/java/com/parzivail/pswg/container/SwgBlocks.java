package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.*;
import com.parzivail.pswg.blockentity.*;
import com.parzivail.pswg.container.registry.RegistryHelper;
import com.parzivail.pswg.container.registry.RegistryName;
import com.parzivail.util.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Random;

public class SwgBlocks
{
	public static class Barrel
	{
		@RegistryName("desh_barrel")
		public static final Block Desh = new DisplacingBlock((state, world, pos, context) -> {
			Random r = Resources.RANDOM;
			r.setSeed(state.getRenderingSeed(pos));

			float s = 4;
			float dx = r.nextFloat() * s;
			float dz = r.nextFloat() * s;

			return VoxelShapeUtil.getCenteredCube(9.2f, 15.6f, dx, dz);
		}, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static class Crate
	{
		@RegistryName("orange_kyber_crate")
		public static final RotatingBlockWithGuiEntity OrangeKyber = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateOctagonBlockEntity::new);
		@RegistryName("gray_kyber_crate")
		public static final RotatingBlockWithGuiEntity GrayKyber = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateOctagonBlockEntity::new);
		@RegistryName("black_kyber_crate")
		public static final RotatingBlockWithGuiEntity BlackKyber = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateOctagonBlockEntity::new);
		@RegistryName("kyber_crate")
		public static final BlockEntityType<CrateOctagonBlockEntity> KyberCrateBlockEntityType = BlockEntityType.Builder.create(CrateOctagonBlockEntity::new, OrangeKyber, GrayKyber, BlackKyber).build(null);

		@RegistryName("toolbox")
		public static final RotatingBlockWithGuiEntity Toolbox = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateMosEisleyBlockEntity::new);
		@RegistryName("toolbox")
		public static final BlockEntityType<CrateMosEisleyBlockEntity> ToolboxBlockEntityType = BlockEntityType.Builder.create(CrateMosEisleyBlockEntity::new, Toolbox).build(null);

		@RegistryName("imperial_crate")
		public static final RotatingBlockWithGuiEntity Imperial = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateImperialCubeBlockEntity::new);
		@RegistryName("imperial_crate")
		public static final BlockEntityType<CrateImperialCubeBlockEntity> ImperialCrateBlockEntityType = BlockEntityType.Builder.create(CrateImperialCubeBlockEntity::new, Imperial).build(null);
	}

	public static class Door
	{
		public static final BlockTatooineHomeDoor TatooineHomeFiller = new BlockTatooineHomeDoor(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 0));
		public static final BlockTatooineHomeDoor TatooineHomeController = new BlockTatooineHomeDoorController(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 0));
		@RegistryName("door_tatooine_home")
		public static final BlockEntityType<TatooineHomeDoorBlockEntity> TatooineHomeBlockEntityType = BlockEntityType.Builder.create(TatooineHomeDoorBlockEntity::new, TatooineHomeController).build(null);
	}

	public static class Machine
	{
		@RegistryName("spoked_machine")
		public static final RotatingBlock Spoked = new RotatingBlockWithBounds(VoxelShapeUtil.getCenteredCube(10, 20), FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
	}

	public static class MoistureVaporator
	{
		@RegistryName("gx8_moisture_vaporator")
		public static final BlockMoistureVaporator Gx8 = new BlockMoistureVaporator(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(10.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("gx8_moisture_vaporator")
		public static final BlockEntityType<MoistureVaporatorBlockEntity> Gx8BlockEntityType = BlockEntityType.Builder.create(MoistureVaporatorBlockEntity::new, Gx8).build(null);
	}

	public static class Pipe
	{
		@RegistryName("large_pipe")
		public static final Block Large = new SelfConnectingNodeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).materialColor(MaterialColor.GRAY).nonOpaque().strength(3.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static class Tank
	{
		@RegistryName("fusion_fuel_tank")
		public static final Block Fusion = new RotatingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static class Vent
	{
		@RegistryName("air_vent")
		public static final Block Air = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static class Workbench
	{
		@RegistryName("blaster_workbench")
		public static final Block Blaster = new BlasterWorkbenchBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("blaster_workbench")
		public static final BlockEntityType<BlasterWorkbenchBlockEntity> BlasterBlockEntityType = BlockEntityType.Builder.create(BlasterWorkbenchBlockEntity::new, Blaster).build(null);

		@RegistryName("lightsaber_forge")
		public static final Block Lightsaber = new LightsaberForgeBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2.5F).breakByTool(FabricToolTags.AXES, 0));
		@RegistryName("lightsaber_forge")
		public static final BlockEntityType<LightsaberForgeBlockEntity> LightsaberBlockEntityType = BlockEntityType.Builder.create(LightsaberForgeBlockEntity::new, Lightsaber).build(null);
	}

	public static class Plant
	{
		@RegistryName("funnel_flower")
		public static final Block FunnelFlower = new FunnelFlowerBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("blossoming_funnel_flower")
		public static final Block BlossomingFunnelFlower = new FunnelFlowerBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
	}

	public static class Leaves
	{
		@RegistryName("sequoia_leaves")
		public static final LeavesBlock Sequoia = createLeavesBlock();

		private static LeavesBlock createLeavesBlock()
		{
			return new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).sounds(BlockSoundGroup.GRASS).nonOpaque().suffocates((state, world, pos) -> false).blockVision((state, world, pos) -> false));
		}
	}

	public static class Log
	{
		@RegistryName("sequoia_log")
		public static final PillarBlock Sequoia = createLogBlock(MaterialColor.WOOD, MaterialColor.BROWN);

		private static PillarBlock createLogBlock(MaterialColor topMaterialColor, MaterialColor sideMaterialColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, (blockState) -> {
				return blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor;
			}).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		}
	}

	public static class Light
	{
		@RegistryName("light_block")
		public static final Block LightBlock = new Block(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).luminance(15).strength(0.3F));

		@RegistryName("floor_wedge_light")
		public static final Block FloorWedge = new RotatingBlockWithBounds(VoxelShapeUtil.getCenteredCube(8, 5), FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().luminance(15).strength(0.5F));
		@RegistryName("wall_cluster_light")
		public static final Block WallCluster = new RotatingBlockWithBounds(VoxelShapes.cuboid(0, 0.0625f, 0.0625f, 0.0625f, 0.9375f, 0.9375f), FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().luminance(15).strength(0.5F));
	}

	public static class Panel
	{
		@RegistryName("panel_imperial_base")
		public static final Block ImperialBase = new Block(FabricBlockSettings.of(Material.METAL).materialColor(MaterialColor.GRAY).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("panel_imperial_black_base")
		public static final Block ImperialBlackBase = new Block(FabricBlockSettings.of(Material.METAL).materialColor(MaterialColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());

		@RegistryName("panel_imperial_1")
		public static final PillarBlock Imperial1 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_2")
		public static final PillarBlock Imperial2 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_3")
		public static final PillarBlock Imperial3 = createPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);

		@RegistryName("panel_imperial_light_1")
		public static final PillarBlock ImperialLight1 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_2")
		public static final PillarBlock ImperialLight2 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_3")
		public static final PillarBlock ImperialLight3 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_4")
		public static final PillarBlock ImperialLight4 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_5")
		public static final PillarBlock ImperialLight5 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_6")
		public static final PillarBlock ImperialLight6 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);

		@RegistryName("panel_imperial_light_tall_1")
		public static final PillarBlock ImperialLightTall1 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_tall_2")
		public static final PillarBlock ImperialLightTall2 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_tall_3")
		public static final PillarBlock ImperialLightTall3 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);
		@RegistryName("panel_imperial_light_tall_4")
		public static final PillarBlock ImperialLightTall4 = createLitPanel(MaterialColor.GRAY, MaterialColor.LIGHT_GRAY);

		@RegistryName("panel_imperial_light_decoy")
		public static final PillarBlock ImperialLightDecoy = createPanel(MaterialColor.GRAY, MaterialColor.GRAY);
		@RegistryName("panel_imperial_light_off")
		public static final PillarBlock ImperialLightOff = createPanel(MaterialColor.GRAY, MaterialColor.BLACK);

		@RegistryName("lab_wall")
		public static final Block LabWall = new Block(FabricBlockSettings.of(Material.STONE));

		private static PillarBlock createLitPanel(MaterialColor topMaterialColor, MaterialColor sideMaterialColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> {
				return blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor;
			}).strength(2.0F).requiresTool().sounds(BlockSoundGroup.METAL).luminance(value -> 15));
		}

		private static PillarBlock createPanel(MaterialColor topMaterialColor, MaterialColor sideMaterialColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> {
				return blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor;
			}).strength(1.5F).requiresTool().sounds(BlockSoundGroup.METAL));
		}
	}

	public static class MaterialBlock
	{
		@RegistryName("beskar_block")
		public static final Block Beskar = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 4).requiresTool());
		@RegistryName("chromium_block")
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("cortosis_block")
		public static final Block Cortosis = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 4).requiresTool());
		@RegistryName("desh_block")
		public static final Block Desh = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
		@RegistryName("diatium_block")
		public static final Block Diatium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("durasteel_block")
		public static final Block Durasteel = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("ionite_block")
		public static final Block Ionite = new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.METAL).strength(5.0F).luminance(3).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("lommite_block")
		public static final Block Lommite = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
		@RegistryName("plasteel_block")
		public static final Block Plasteel = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
		@RegistryName("titanium_block")
		public static final Block Titanium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 3).requiresTool());
		@RegistryName("zersium_block")
		public static final Block Zersium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
	}

	public static class Ore
	{
		@RegistryName("beskar_ore")
		public static final Block Beskar = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 4).requiresTool());
		@RegistryName("chromium_ore")
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("cortosis_ore")
		public static final Block Cortosis = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 4).requiresTool());
		@RegistryName("desh_ore")
		public static final Block Desh = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
		@RegistryName("diatium_ore")
		public static final Block Diatium = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("ionite_ore")
		public static final Block Ionite = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("lommite_ore")
		public static final Block Lommite = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
		@RegistryName("titanium_ore")
		public static final Block Titanium = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0F).breakByTool(FabricToolTags.PICKAXES, 3).requiresTool());
		@RegistryName("zersium_ore")
		public static final Block Zersium = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
	}

	public static class Sand
	{
		@RegistryName("desert_sand")
		public static final Block Desert = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F).breakByTool(FabricToolTags.SHOVELS));
		@RegistryName("desert_canyon_sand")
		public static final Block DesertCanyon = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F).breakByTool(FabricToolTags.SHOVELS));
	}

	public static class Dirt
	{
		@RegistryName("wet_pourstone")
		public static final Block WetPourstone = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F).breakByTool(FabricToolTags.SHOVELS));
		@RegistryName("wet_pourstone_stairs")
		public static final Block WetPourstoneStairs = new PStairsBlock(WetPourstone.getDefaultState(), AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("wet_pourstone_slab")
		public static final Block WetPourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("desert_loam")
		public static final Block DesertLoam = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F).breakByTool(FabricToolTags.SHOVELS));
	}

	public static class Stone
	{
		@RegistryName("pourstone")
		public static final Block Pourstone = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("pourstone_stairs")
		public static final Block PourstoneStairs = new PStairsBlock(Pourstone.getDefaultState(), AbstractBlock.Settings.copy(Pourstone));
		@RegistryName("pourstone_slab")
		public static final Block PourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(Pourstone));
		@RegistryName("light_pourstone")
		public static final Block LightPourstone = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("light_pourstone_stairs")
		public static final Block LightPourstoneStairs = new PStairsBlock(LightPourstone.getDefaultState(), AbstractBlock.Settings.copy(LightPourstone));
		@RegistryName("light_pourstone_slab")
		public static final Block LightPourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(Pourstone));

		@RegistryName("massassi_stone_bricks")
		public static final Block MassassiBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("massassi_stone_brick_stairs")
		public static final Block MassassiBrickStairs = new PStairsBlock(MassassiBricks.getDefaultState(), AbstractBlock.Settings.copy(MassassiBricks));
		@RegistryName("massassi_stone_brick_slab")
		public static final Block MassassiBrickSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiBricks));
		@RegistryName("smooth_massassi_stone")
		public static final Block MassassiSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("smooth_massassi_stone_stairs")
		public static final Block MassassiSmoothStairs = new PStairsBlock(MassassiSmooth.getDefaultState(), AbstractBlock.Settings.copy(MassassiSmooth));
		@RegistryName("smooth_massassi_stone_slab")
		public static final Block MassassiSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiSmooth));

		@RegistryName("ilum_stone")
		public static final Block Ilum = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("ilum_stone_stairs")
		public static final Block IlumStairs = new PStairsBlock(Ilum.getDefaultState(), AbstractBlock.Settings.copy(Ilum));
		@RegistryName("ilum_stone_slab")
		public static final Block IlumSlab = new SlabBlock(AbstractBlock.Settings.copy(Ilum));
		@RegistryName("smooth_ilum_stone")
		public static final Block IlumSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("smooth_ilum_stone_slab")
		public static final Block IlumSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumSmooth));
		@RegistryName("ilum_stone_bricks")
		public static final Block IlumBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("ilum_stone_brick_stairs")
		public static final Block IlumBrickStairs = new PStairsBlock(IlumBricks.getDefaultState(), AbstractBlock.Settings.copy(IlumBricks));
		@RegistryName("ilum_stone_brick_slab")
		public static final Block IlumBrickSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumBricks));
		@RegistryName("chiseled_ilum_stone_bricks")
		public static final Block IlumChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, Block.class, SwgBlocks::registerBlock);
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, BlockEntityType.class, SwgBlocks::registerBlockEntityType);

		FlammableBlockRegistry.getDefaultInstance().add(SwgBlocks.Leaves.Sequoia, 30, 60);

		Registry.register(Registry.BLOCK, Resources.identifier("tatooine_home_door_controller"), Door.TatooineHomeController);
		Registry.register(Registry.BLOCK, Resources.identifier("tatooine_home_door"), Door.TatooineHomeFiller);
		Registry.register(Registry.ITEM, Resources.identifier("tatooine_home_door"), new BlockTatooineHomeDoor.Item(Door.TatooineHomeController, new Item.Settings().group(Galaxies.Tab)));
	}

	public static void registerBlock(Block block, Identifier identifier)
	{
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(Galaxies.Tab)));
	}

	public static void registerBlockEntityType(BlockEntityType<?> blockEntityType, Identifier identifier)
	{
		Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, blockEntityType);
	}
}
