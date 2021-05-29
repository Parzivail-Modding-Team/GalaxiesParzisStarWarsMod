package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.*;
import com.parzivail.pswg.block.crop.ChasukaCrop;
import com.parzivail.pswg.blockentity.*;
import com.parzivail.pswg.container.registry.RegistryHelper;
import com.parzivail.pswg.container.registry.RegistryName;
import com.parzivail.pswg.container.registry.TabIgnore;
import com.parzivail.util.block.*;
import com.parzivail.util.block.connecting.SelfConnectingBlock;
import com.parzivail.util.block.connecting.SelfConnectingGlassBlock;
import com.parzivail.util.block.connecting.SelfConnectingNodeBlock;
import com.parzivail.util.block.connecting.SelfConnectingStainedGlassBlock;
import com.parzivail.util.block.mutating.DryingBlock;
import com.parzivail.util.block.mutating.DryingSlabBlock;
import com.parzivail.util.block.mutating.DryingStairsBlock;
import com.parzivail.util.block.rotating.RotatingBlock;
import com.parzivail.util.block.rotating.RotatingBlockWithBounds;
import com.parzivail.util.block.rotating.RotatingBlockWithBoundsGuiEntity;
import com.parzivail.util.block.rotating.RotatingBlockWithGuiEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
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
			r.setSeed(MathHelper.hashCode(pos));

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
		public static final BlockEntityType<CrateOctagonBlockEntity> KyberCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateOctagonBlockEntity::new, OrangeKyber, GrayKyber, BlackKyber).build();

		@RegistryName("toolbox")
		public static final RotatingBlockWithGuiEntity Toolbox = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateMosEisleyBlockEntity::new);
		@RegistryName("toolbox")
		public static final BlockEntityType<CrateMosEisleyBlockEntity> ToolboxBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateMosEisleyBlockEntity::new, Toolbox).build();

		@RegistryName("imperial_crate")
		public static final RotatingBlockWithGuiEntity Imperial = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateImperialCubeBlockEntity::new);
		@RegistryName("imperial_crate")
		public static final BlockEntityType<CrateImperialCubeBlockEntity> ImperialCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateImperialCubeBlockEntity::new, Imperial).build();

		@RegistryName("segmented_crate")
		public static final RotatingBlockWithBoundsGuiEntity Segmented = new RotatingBlockWithBoundsGuiEntity(VoxelShapeUtil.getCentered(28, 14, 14), FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).breakByTool(FabricToolTags.PICKAXES, 0), CrateSegmentedBlockEntity::new);
		@RegistryName("segmented_crate")
		public static final BlockEntityType<CrateSegmentedBlockEntity> SegmentedCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateSegmentedBlockEntity::new, Segmented).build();
	}

	public static class Door
	{
		public static final BlockTatooineHomeDoor TatooineHomeFiller = new BlockTatooineHomeDoor(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 0));
		@RegistryName("tatooine_home_door_controller")
		@TabIgnore
		public static final BlockTatooineHomeDoor TatooineHomeController = new BlockTatooineHomeDoorController(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 0));
		@RegistryName("door_tatooine_home")
		public static final BlockEntityType<TatooineHomeDoorBlockEntity> TatooineHomeBlockEntityType = FabricBlockEntityTypeBuilder.create(TatooineHomeDoorBlockEntity::new, TatooineHomeController).build();
	}

	public static class Machine
	{
		@RegistryName("spoked_machine")
		public static final RotatingBlock Spoked = new RotatingBlockWithBounds(VoxelShapeUtil.getCenteredCube(10, 20), RotatingBlockWithBounds.Substrate.NONE, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(5.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
	}

	public static class MoistureVaporator
	{
		@RegistryName("gx8_moisture_vaporator")
		public static final BlockMoistureVaporator Gx8 = new BlockMoistureVaporator(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(10.0F).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool());
		@RegistryName("gx8_moisture_vaporator")
		public static final BlockEntityType<MoistureVaporatorBlockEntity> Gx8BlockEntityType = FabricBlockEntityTypeBuilder.create(MoistureVaporatorBlockEntity::new, Gx8).build();
	}

	public static class Pipe
	{
		@RegistryName("large_pipe")
		public static final Block Large = new SelfConnectingNodeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).materialColor(MapColor.GRAY).nonOpaque().strength(3.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static class Tank
	{
		@RegistryName("fusion_fuel_tank")
		public static final Block Fusion = new RotatingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static class Glass
	{
		@RegistryName("imperial_glass")
		public static final SelfConnectingGlassBlock Imperial = createSelfConnectingGlass();
		@RegistryName("white_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock WhiteStainedImperial = createSelfConnectingStainedGlass(DyeColor.WHITE);
		@RegistryName("orange_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock OrangeStainedImperial = createSelfConnectingStainedGlass(DyeColor.ORANGE);
		@RegistryName("magenta_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock MagentaStainedImperial = createSelfConnectingStainedGlass(DyeColor.MAGENTA);
		@RegistryName("light_blue_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock LightBlueStainedImperial = createSelfConnectingStainedGlass(DyeColor.LIGHT_BLUE);
		@RegistryName("yellow_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock YellowStainedImperial = createSelfConnectingStainedGlass(DyeColor.YELLOW);
		@RegistryName("lime_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock LimeStainedImperial = createSelfConnectingStainedGlass(DyeColor.LIME);
		@RegistryName("pink_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock PinkStainedImperial = createSelfConnectingStainedGlass(DyeColor.PINK);
		@RegistryName("gray_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock GrayStainedImperial = createSelfConnectingStainedGlass(DyeColor.GRAY);
		@RegistryName("light_gray_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock LightGrayStainedImperial = createSelfConnectingStainedGlass(DyeColor.LIGHT_GRAY);
		@RegistryName("cyan_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock CyanStainedImperial = createSelfConnectingStainedGlass(DyeColor.CYAN);
		@RegistryName("purple_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock PurpleStainedImperial = createSelfConnectingStainedGlass(DyeColor.PURPLE);
		@RegistryName("blue_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock BlueStainedImperial = createSelfConnectingStainedGlass(DyeColor.BLUE);
		@RegistryName("brown_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock BrownStainedImperial = createSelfConnectingStainedGlass(DyeColor.BROWN);
		@RegistryName("green_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock GreenStainedImperial = createSelfConnectingStainedGlass(DyeColor.GREEN);
		@RegistryName("red_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock RedStainedImperial = createSelfConnectingStainedGlass(DyeColor.RED);
		@RegistryName("black_stained_imperial_glass")
		public static final SelfConnectingStainedGlassBlock BlackStainedImperial = createSelfConnectingStainedGlass(DyeColor.BLACK);

		private static SelfConnectingGlassBlock createSelfConnectingGlass()
		{
			return new SelfConnectingGlassBlock(AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never));
		}

		private static SelfConnectingStainedGlassBlock createSelfConnectingStainedGlass(DyeColor color)
		{
			return new SelfConnectingStainedGlassBlock(color, AbstractBlock.Settings.of(Material.GLASS, color).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never));
		}
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
		public static final BlockEntityType<BlasterWorkbenchBlockEntity> BlasterBlockEntityType = FabricBlockEntityTypeBuilder.create(BlasterWorkbenchBlockEntity::new, Blaster).build();

		@RegistryName("lightsaber_forge")
		public static final Block Lightsaber = new LightsaberForgeBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2.5F).breakByTool(FabricToolTags.AXES, 0));
		@RegistryName("lightsaber_forge")
		public static final BlockEntityType<LightsaberForgeBlockEntity> LightsaberBlockEntityType = FabricBlockEntityTypeBuilder.create(LightsaberForgeBlockEntity::new, Lightsaber).build();
	}

	public static class Plant
	{
		@RegistryName("funnel_flower")
		public static final Block FunnelFlower = new TatooinePlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("blossoming_funnel_flower")
		public static final Block BlossomingFunnelFlower = new TatooinePlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("poonten_grass")
		public static final Block PoontenGrass = new TatooinePlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("dried_poonten_grass")
		public static final Block DriedPoontenGrass = new TatooinePlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("tuber_stalk")
		public static final Block Tuber = new TatooinePlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("chasuka")
		@TabIgnore
		public static final CropBlock Chasuka = new ChasukaCrop(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
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
		public static final PillarBlock Sequoia = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("japor_log")
		public static final PillarBlock Japor = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("tatooine_log")
		public static final PillarBlock Tatooine = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);

		private static PillarBlock createLogBlock(MapColor topMaterialColor, MapColor sideMaterialColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, blockState ->
					blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		}
	}

	public static class Wood
	{
		@RegistryName("sequoia_wood")
		public static final PillarBlock Sequoia = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		@RegistryName("japor_wood")
		public static final PillarBlock Japor = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		@RegistryName("tatooine_wood")
		public static final PillarBlock Tatooine = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
	}

	public static class Light
	{
		@RegistryName("light_fixture")
		public static final Block Fixture = new InvertedLampBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).luminance(BlockUtil.createLightLevelFromBlockState(15)).strength(0.3F));

		@RegistryName("red_hangar_light")
		public static final Block RedHangar = new RotatingBlockWithBounds(VoxelShapeUtil.getCentered(12, 10, 5), RotatingBlockWithBounds.Substrate.BELOW, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).noCollision().nonOpaque().luminance(15).strength(0.5F));
		@RegistryName("blue_hangar_light")
		public static final Block BlueHangar = new RotatingBlockWithBounds(VoxelShapeUtil.getCentered(12, 10, 5), RotatingBlockWithBounds.Substrate.BELOW, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).noCollision().nonOpaque().luminance(15).strength(0.5F));
		@RegistryName("wall_cluster_light")
		public static final Block WallCluster = new RotatingBlockWithBounds(VoxelShapes.cuboid(0, 0.0625f, 0.0625f, 0.0625f, 0.9375f, 0.9375f), RotatingBlockWithBounds.Substrate.BEHIND, FabricBlockSettings.of(Material.METAL).noCollision().sounds(BlockSoundGroup.METAL).nonOpaque().luminance(15).strength(0.5F));
	}

	public static class Panel
	{
		@RegistryName("black_imperial_panel_blank")
		public static final Block BlackImperialPanelBlank = new Block(FabricBlockSettings.of(Material.METAL).materialColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("gray_imperial_panel_blank")
		public static final Block GrayImperialPanelBlank = new Block(FabricBlockSettings.of(Material.METAL).materialColor(MapColor.GRAY).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("gray_imperial_panel_pattern_1")
		public static final PillarBlock GrayImperialPanelPattern1 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_2")
		public static final PillarBlock GrayImperialPanelPattern2 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_3")
		public static final PillarBlock GrayImperialPanelPattern3 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_4")
		public static final PillarBlock GrayImperialPanelPattern4 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_5")
		public static final PillarBlock GrayImperialPanelPattern5 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);

		@RegistryName("gray_imperial_light_half_1")
		public static final PillarBlock GrayImperialLightHalf1 = createLitPanel(MapColor.GRAY, MapColor.LIGHT_GRAY, 8);
		@RegistryName("gray_imperial_light_half_2")
		public static final PillarBlock GrayImperialLightHalf2 = createLitPanel(MapColor.GRAY, MapColor.LIGHT_GRAY, 8);
		@RegistryName("gray_imperial_light_half_3")
		public static final PillarBlock GrayImperialLightHalf3 = createLitPanel(MapColor.GRAY, MapColor.LIGHT_GRAY, 8);
		@RegistryName("gray_imperial_light_half_4")
		public static final PillarBlock GrayImperialLightHalf4 = createLitPanel(MapColor.GRAY, MapColor.LIGHT_GRAY, 8);
		@RegistryName("gray_imperial_light_half_5")
		public static final PillarBlock GrayImperialLightHalf5 = createLitPanel(MapColor.GRAY, MapColor.LIGHT_GRAY, 8);
		@RegistryName("gray_imperial_light_off_1")
		public static final PillarBlock GrayImperialLightOff1 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_light_off_2")
		public static final PillarBlock GrayImperialLightOff2 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_light_on_1")
		public static final PillarBlock GrayImperialLightOn1 = createLitPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_light_on_2")
		public static final PillarBlock GrayImperialLightOn2 = createLitPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);

		@RegistryName("gray_imperial_tall_light_1")
		public static final SelfConnectingBlock ImperialLightTall1 = createLitConnectingPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_tall_light_2")
		public static final SelfConnectingBlock ImperialLightTall2 = createLitConnectingPanel(MapColor.GRAY);

		@RegistryName("lab_wall")
		public static final Block LabWall = new Block(FabricBlockSettings.of(Material.STONE));

		private static PillarBlock createLitPanel(MapColor topMaterialColor, MapColor sideMaterialColor, int luminance)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> {
				return blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMaterialColor : sideMaterialColor;
			}).strength(2.0F).requiresTool().sounds(BlockSoundGroup.METAL).luminance(value -> luminance));
		}

		private static PillarBlock createLitPanel(MapColor topMaterialColor, MapColor sideMaterialColor)
		{
			return createLitPanel(topMaterialColor, sideMaterialColor, 15);
		}

		private static SelfConnectingBlock createLitConnectingPanel(MapColor materialColor)
		{
			return new SelfConnectingBlock(AbstractBlock.Settings.of(Material.METAL, materialColor).strength(2.0F).requiresTool().sounds(BlockSoundGroup.METAL).luminance(value -> 15));
		}

		private static PillarBlock createPanel(MapColor topMaterialColor, MapColor sideMaterialColor)
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
		public static final SelfConnectingBlock Plasteel = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
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
		public static final Block WetPourstone = new DryingBlock(Stone.Pourstone, 25, FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F).breakByTool(FabricToolTags.SHOVELS));
		@RegistryName("wet_pourstone_stairs")
		public static final Block WetPourstoneStairs = new DryingStairsBlock(WetPourstone.getDefaultState(), Stone.PourstoneStairs, 25, AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("wet_pourstone_slab")
		public static final Block WetPourstoneSlab = new DryingSlabBlock(Stone.PourstoneSlab, 25, AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("desert_loam")
		public static final Block DesertLoam = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F).breakByTool(FabricToolTags.SHOVELS));
	}

	public static class Stone
	{
		@RegistryName("pourstone")
		public static final Block Pourstone = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("pourstone_stairs")
		public static final StairsBlock PourstoneStairs = new PStairsBlock(Pourstone.getDefaultState(), AbstractBlock.Settings.copy(Pourstone));
		@RegistryName("pourstone_slab")
		public static final SlabBlock PourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(Pourstone));
		@RegistryName("light_pourstone")
		public static final Block LightPourstone = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("light_pourstone_stairs")
		public static final StairsBlock LightPourstoneStairs = new PStairsBlock(LightPourstone.getDefaultState(), AbstractBlock.Settings.copy(LightPourstone));
		@RegistryName("light_pourstone_slab")
		public static final SlabBlock LightPourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(Pourstone));

		@RegistryName("massassi_stone")
		public static final Block Massassi = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("massassi_stone_stairs")
		public static final StairsBlock MassassiStairs = new PStairsBlock(Massassi.getDefaultState(), AbstractBlock.Settings.copy(Massassi));
		@RegistryName("massassi_stone_slab")
		public static final SlabBlock MassassiSlab = new SlabBlock(AbstractBlock.Settings.copy(Massassi));
		@RegistryName("smooth_massassi_stone")
		public static final Block MassassiSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("smooth_massassi_stone_slab")
		public static final SlabBlock MassassiSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiSmooth));
		@RegistryName("massassi_stone_bricks")
		public static final Block MassassiBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("massassi_stone_brick_stairs")
		public static final StairsBlock MassassiBrickStairs = new PStairsBlock(MassassiBricks.getDefaultState(), AbstractBlock.Settings.copy(MassassiBricks));
		@RegistryName("massassi_stone_brick_slab")
		public static final SlabBlock MassassiBrickSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiBricks));
		//@RegistryName("chiseled_massassi_stone_bricks")
		//public static final Block MassassiChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());


		@RegistryName("ilum_stone")
		public static final Block Ilum = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("ilum_stone_stairs")
		public static final StairsBlock IlumStairs = new PStairsBlock(Ilum.getDefaultState(), AbstractBlock.Settings.copy(Ilum));
		@RegistryName("ilum_stone_slab")
		public static final SlabBlock IlumSlab = new SlabBlock(AbstractBlock.Settings.copy(Ilum));
		@RegistryName("smooth_ilum_stone")
		public static final Block IlumSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("smooth_ilum_stone_slab")
		public static final SlabBlock IlumSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumSmooth));
		@RegistryName("ilum_stone_bricks")
		public static final Block IlumBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
		@RegistryName("ilum_stone_brick_stairs")
		public static final StairsBlock IlumBrickStairs = new PStairsBlock(IlumBricks.getDefaultState(), AbstractBlock.Settings.copy(IlumBricks));
		@RegistryName("ilum_stone_brick_slab")
		public static final SlabBlock IlumBrickSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumBricks));
		@RegistryName("chiseled_ilum_stone_bricks")
		public static final Block IlumChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).breakByTool(FabricToolTags.PICKAXES, 0).requiresTool());
	}

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, Block.class, SwgBlocks::registerBlock);
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, BlockEntityType.class, SwgBlocks::registerBlockEntityType);

		FlammableBlockRegistry.getDefaultInstance().add(SwgBlocks.Leaves.Sequoia, 30, 60);

		Registry.register(Registry.BLOCK, Resources.identifier("tatooine_home_door"), Door.TatooineHomeFiller);
		Registry.register(Registry.ITEM, Resources.identifier("tatooine_home_door"), new BlockTatooineHomeDoor.Item(Door.TatooineHomeController, new Item.Settings().group(Galaxies.Tab)));
	}

	public static void registerBlock(Block block, Identifier identifier, boolean ignoreTab)
	{
		Item.Settings itemSettings = new Item.Settings();

		if (!ignoreTab)
			itemSettings = itemSettings.group(Galaxies.Tab);

		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, itemSettings));
	}

	public static void registerBlockEntityType(BlockEntityType<?> blockEntityType, Identifier identifier, boolean ignoreTab)
	{
		Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, blockEntityType);
	}
}
