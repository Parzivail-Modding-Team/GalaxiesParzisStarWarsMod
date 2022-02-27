package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.*;
import com.parzivail.pswg.block.crop.AridPlant;
import com.parzivail.pswg.block.crop.ChasukaCrop;
import com.parzivail.pswg.block.crop.HkakBushBlock;
import com.parzivail.pswg.block.crop.MoloShrubBlock;
import com.parzivail.pswg.blockentity.*;
import com.parzivail.pswg.container.registry.Flammable;
import com.parzivail.pswg.container.registry.RegistryHelper;
import com.parzivail.pswg.container.registry.RegistryName;
import com.parzivail.pswg.container.registry.TabIgnore;
import com.parzivail.util.block.*;
import com.parzivail.util.block.connecting.SelfConnectingBlock;
import com.parzivail.util.block.connecting.SelfConnectingGlassBlock;
import com.parzivail.util.block.connecting.SelfConnectingNodeBlock;
import com.parzivail.util.block.connecting.SelfConnectingStainedGlassBlock;
import com.parzivail.util.block.mutating.*;
import com.parzivail.util.block.rotating.RotatingBlock;
import com.parzivail.util.block.rotating.RotatingBlockWithBounds;
import com.parzivail.util.block.rotating.RotatingBlockWithBoundsGuiEntity;
import com.parzivail.util.block.rotating.RotatingBlockWithGuiEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShapes;

public class SwgBlocks
{
	public static class Barrel
	{
		@RegistryName("desh_barrel")
		public static final Block Desh = new DisplacingBlock((state, world, pos, context) -> {
			var r = Resources.RANDOM;
			r.setSeed(MathHelper.hashCode(pos));

			float s = 4;
			var dx = r.nextFloat() * s;
			var dz = r.nextFloat() * s;

			return VoxelShapeUtil.getCenteredCube(9.2f, 15.6f, dx, dz);
		}, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F).requiresTool());
	}

	public static class Crate
	{
		@RegistryName("orange_kyber_crate")
		public static final RotatingBlockWithGuiEntity OrangeKyber = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateOctagonBlockEntity::new);
		@RegistryName("gray_kyber_crate")
		public static final RotatingBlockWithGuiEntity GrayKyber = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateOctagonBlockEntity::new);
		@RegistryName("black_kyber_crate")
		public static final RotatingBlockWithGuiEntity BlackKyber = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateOctagonBlockEntity::new);
		@RegistryName("kyber_crate")
		public static final BlockEntityType<CrateOctagonBlockEntity> KyberCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateOctagonBlockEntity::new, OrangeKyber, GrayKyber, BlackKyber).build();

		@RegistryName("toolbox")
		public static final RotatingBlockWithGuiEntity Toolbox = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateMosEisleyBlockEntity::new);
		@RegistryName("toolbox")
		public static final BlockEntityType<CrateMosEisleyBlockEntity> ToolboxBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateMosEisleyBlockEntity::new, Toolbox).build();

		@RegistryName("imperial_crate")
		public static final RotatingBlockWithGuiEntity Imperial = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateImperialCubeBlockEntity::new);
		@RegistryName("imperial_crate")
		public static final BlockEntityType<CrateImperialCubeBlockEntity> ImperialCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateImperialCubeBlockEntity::new, Imperial).build();

		@RegistryName("segmented_crate")
		public static final RotatingBlockWithBoundsGuiEntity Segmented = new RotatingBlockWithBoundsGuiEntity(VoxelShapeUtil.getCentered(28, 14, 14), FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateSegmentedBlockEntity::new);
		@RegistryName("segmented_crate")
		public static final BlockEntityType<CrateSegmentedBlockEntity> SegmentedCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateSegmentedBlockEntity::new, Segmented).build();
	}

	public static class Door
	{
		public static final TatooineHomeDoorBlock TatooineHomeTop = new TatooineHomeDoorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.0F));
		public static final TatooineHomeDoorBlock TatooineHomeBottom = new TatooineHomeDoorControllerBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.0F));
		@RegistryName("door_tatooine_home")
		public static final BlockEntityType<TatooineHomeDoorBlockEntity> TatooineHomeBlockEntityType = FabricBlockEntityTypeBuilder.create(TatooineHomeDoorBlockEntity::new, TatooineHomeBottom).build();
	}

	public static class Machine
	{
		@RegistryName("spoked_machine")
		public static final RotatingBlock Spoked = new RotatingBlockWithBounds(VoxelShapeUtil.getCenteredCube(10, 20), RotatingBlockWithBounds.Substrate.NONE, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(5.0F).requiresTool());
	}

	public static class MoistureVaporator
	{
		@RegistryName("gx8_moisture_vaporator")
		public static final MoistureVaporatorBlock Gx8 = new MoistureVaporatorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(10.0F).requiresTool());
		@RegistryName("gx8_moisture_vaporator")
		public static final BlockEntityType<MoistureVaporatorBlockEntity> Gx8BlockEntityType = FabricBlockEntityTypeBuilder.create(MoistureVaporatorBlockEntity::new, Gx8).build();
	}

	public static class Pipe
	{
		@RegistryName("large_pipe")
		public static final Block Large = new SelfConnectingNodeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).mapColor(MapColor.GRAY).nonOpaque().strength(3.5F).requiresTool());
	}

	public static class Tank
	{
		@RegistryName("fusion_fuel_tank")
		public static final Block Fusion = new RotatingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.5F).requiresTool());
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
		public static final Block Air = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.5F).requiresTool());
	}

	public static class Workbench
	{
		@RegistryName("blaster_workbench")
		public static final Block Blaster = new RotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.5F).requiresTool(), BlasterWorkbenchBlockEntity::new);
		@RegistryName("blaster_workbench")
		public static final BlockEntityType<BlasterWorkbenchBlockEntity> BlasterBlockEntityType = FabricBlockEntityTypeBuilder.create(BlasterWorkbenchBlockEntity::new, Blaster).build();

		@RegistryName("lightsaber_forge")
		public static final Block Lightsaber = new LightsaberForgeBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2.5F));
		@RegistryName("lightsaber_forge")
		public static final BlockEntityType<LightsaberForgeBlockEntity> LightsaberBlockEntityType = FabricBlockEntityTypeBuilder.create(LightsaberForgeBlockEntity::new, Lightsaber).build();
	}

	public static class Plant
	{
		@RegistryName("funnel_flower")
		@Flammable(burn = 60, spread = 100)
		public static final Block FunnelFlower = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("blossoming_funnel_flower")
		@Flammable(burn = 60, spread = 100)
		public static final Block BlossomingFunnelFlower = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("poonten_grass")
		@Flammable(burn = 60, spread = 100)
		public static final Block PoontenGrass = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("dried_poonten_grass")
		@Flammable(burn = 60, spread = 100)
		public static final Block DriedPoontenGrass = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("tuber_stalk")
		@Flammable(burn = 60, spread = 100)
		public static final Block Tuber = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("chasuka")
		@TabIgnore
		@Flammable(burn = 60, spread = 100)
		public static final CropBlock Chasuka = new ChasukaCrop(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
		@RegistryName("hkak_bush")
		@Flammable(burn = 60, spread = 100)
		public static final HkakBushBlock HkakBush = new HkakBushBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
		@RegistryName("molo_shrub")
		@Flammable(burn = 60, spread = 100)
		public static final MoloShrubBlock MoloShrub = new MoloShrubBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.CROP));
	}

	public static class Leaves
	{
		@RegistryName("sequoia_leaves")
		@Flammable(burn = 30, spread = 60)
		public static final LeavesBlock Sequoia = createLeavesBlock();
		@RegistryName("japor_leaves")
		@Flammable(burn = 30, spread = 60)
		public static final BushLeavesBlock Japor = createBushLeavesBlock();

		private static LeavesBlock createLeavesBlock()
		{
			return new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).sounds(BlockSoundGroup.GRASS).nonOpaque().suffocates(BlockUtil::never).blockVision(BlockUtil::never));
		}

		private static BushLeavesBlock createBushLeavesBlock()
		{
			return new BushLeavesBlock(8, 3, AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).sounds(BlockSoundGroup.GRASS).noCollision());
		}
	}

	public static class Log
	{
		@RegistryName("sequoia_log")
		@Flammable(burn = 5, spread = 5)
		public static final PillarBlock Sequoia = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("japor_log")
		@Flammable(burn = 5, spread = 5)
		public static final PillarBlock Japor = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("tatooine_log")
		@Flammable(burn = 5, spread = 5)
		public static final PillarBlock Tatooine = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);

		private static PillarBlock createLogBlock(MapColor topMapColor, MapColor sideMapColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, blockState ->
					blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		}
	}

	public static class Wood
	{
		@RegistryName("sequoia_wood")
		@Flammable(burn = 5, spread = 5)
		public static final PillarBlock Sequoia = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		@RegistryName("japor_wood")
		@Flammable(burn = 5, spread = 5)
		public static final PillarBlock Japor = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		@RegistryName("tatooine_wood")
		@Flammable(burn = 5, spread = 5)
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
		public static final ClusterLightBlock WallCluster = new ClusterLightBlock(VoxelShapes.cuboid(0, 0.0625f, 0.0625f, 0.0625f, 0.9375f, 0.9375f), RotatingBlockWithBounds.Substrate.BEHIND, FabricBlockSettings.of(Material.METAL).noCollision().sounds(BlockSoundGroup.METAL).nonOpaque().luminance(15).strength(0.5F));
	}

	public static class Panel
	{
//		@RegistryName("imperial_cutout")
//		public static final SelfConnectingBlock ImperialCutout = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
//		@RegistryName("imperial_cutout_pipes")
//		public static final SelfConnectingBlock ImperialCutoutPipes = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_blank")
		public static final Block BlackImperialPanelBlank = new Block(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_bordered")
		public static final SelfConnectingBlock BlackImperialPanelBordered = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_split")
		public static final SelfConnectingBlock BlackImperialPanelSplit = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_thin_bordered")
		public static final SelfConnectingBlock BlackImperialPanelThinBordered = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("external_imperial_plating")
		public static final SelfConnectingBlock ExternalImperialPlatingConnectedBorder = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("gray_imperial_panel_blank")
		public static final Block GrayImperialPanelBlank = new Block(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.GRAY).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
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

		@RegistryName("gray_imperial_tall_panel_1")
		public static final SelfConnectingBlock ImperialPanelTall1 = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
		@RegistryName("gray_imperial_tall_panel_2")
		public static final SelfConnectingBlock ImperialPanelTall2 = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());

		@RegistryName("gray_imperial_tall_light_1")
		public static final SelfConnectingBlock ImperialLightTall1 = createLitConnectingPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_tall_light_2")
		public static final SelfConnectingBlock ImperialLightTall2 = createLitConnectingPanel(MapColor.GRAY);

		@RegistryName("lab_wall")
		@TabIgnore
		public static final Block LabWall = new Block(FabricBlockSettings.of(Material.STONE));

		private static PillarBlock createLitPanel(MapColor topMapColor, MapColor sideMapColor, int luminance)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(2.0F).requiresTool().sounds(BlockSoundGroup.METAL).luminance(value -> luminance));
		}

		private static PillarBlock createLitPanel(MapColor topMapColor, MapColor sideMapColor)
		{
			return createLitPanel(topMapColor, sideMapColor, 15);
		}

		private static SelfConnectingBlock createLitConnectingPanel(MapColor mapColor)
		{
			return new SelfConnectingBlock(AbstractBlock.Settings.of(Material.METAL, mapColor).strength(2.0F).requiresTool().sounds(BlockSoundGroup.METAL).luminance(value -> 15));
		}

		private static PillarBlock createPanel(MapColor topMapColor, MapColor sideMapColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(1.5F).requiresTool().sounds(BlockSoundGroup.METAL));
		}
	}

	public static class MaterialBlock
	{
		@RegistryName("beskar_block")
		public static final Block Beskar = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("chromium_block")
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).requiresTool());
		@RegistryName("cortosis_block")
		public static final Block Cortosis = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("desh_block")
		public static final Block Desh = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).requiresTool());
		@RegistryName("diatium_block")
		public static final Block Diatium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("durasteel_block")
		public static final Block Durasteel = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("ionite_block")
		public static final Block Ionite = new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.METAL).strength(5.0F).luminance(3).requiresTool());
		@RegistryName("lommite_block")
		public static final Block Lommite = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("plasteel_block")
		public static final SelfConnectingBlock Plasteel = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).requiresTool());
		@RegistryName("titanium_block")
		public static final Block Titanium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("zersium_block")
		public static final Block Zersium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("helicite_block")
		public static final Block Helicite = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("thorilide_block")
		public static final Block Thorilide = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
	}

	public static class Ore
	{
		@RegistryName("beskar_ore")
		public static final Block Beskar = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F).requiresTool());
		@RegistryName("chromium_ore")
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("cortosis_ore")
		public static final Block Cortosis = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F).requiresTool());
		@RegistryName("desh_ore")
		public static final Block Desh = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("diatium_ore")
		public static final Block Diatium = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("ionite_ore")
		public static final Block Ionite = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("lommite_ore")
		public static final Block Lommite = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("titanium_ore")
		public static final Block Titanium = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0F).requiresTool());
		@RegistryName("zersium_ore")
		public static final Block Zersium = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("thorilide_ore")
		public static final Block Thorilide = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("helicite_ore")
		public static final Block Helicite = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
	}

	public static class Sand
	{
		@RegistryName("salty_desert_sand")
		public static final Block SaltyDesert = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
		@RegistryName("desert_sand")
		public static final Block Desert = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
		@RegistryName("desert_canyon_sand")
		public static final Block DesertCanyon = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
	}

	public static class Salt
	{
		@RegistryName("caked_salt")
		public static final Block Caked = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
	}

	public static class Dirt
	{
		@RegistryName("wet_pourstone")
		public static final Block WetPourstone = new RuiningDryingBlock(Stone.Pourstone, 10, () -> Dirt.RuinedWetPourstone, FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F).noCollision());
		@RegistryName("wet_pourstone_stairs")
		public static final Block WetPourstoneStairs = new RuiningDryingStairsBlock(WetPourstone.getDefaultState(), Stone.PourstoneStairs, 10, () -> Dirt.RuinedWetPourstoneStairs, AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("wet_pourstone_slab")
		public static final Block WetPourstoneSlab = new RuiningDryingSlabBlock(Stone.PourstoneSlab, 10, () -> Dirt.RuinedWetPourstoneSlab, AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("ruined_wet_pourstone")
		public static final Block RuinedWetPourstone = new DryingBlock(Stone.CrackedPourstone, 10, FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).noCollision().strength(0.5F));
		@RegistryName("ruined_wet_pourstone_stairs")
		public static final StairsBlock RuinedWetPourstoneStairs = new DryingStairsBlock(RuinedWetPourstone.getDefaultState(), Stone.CrackedPourstoneStairs, 10, AbstractBlock.Settings.copy(RuinedWetPourstone));
		@RegistryName("ruined_wet_pourstone_slab")
		public static final SlabBlock RuinedWetPourstoneSlab = new DryingSlabBlock(Stone.CrackedPourstoneSlab, 10, AbstractBlock.Settings.copy(RuinedWetPourstone));
		@RegistryName("desert_loam")
		public static final Block DesertLoam = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F));
	}

	public static class Stone
	{
		@RegistryName("desert_sediment")
		public static final Block DesertSediment = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool());

		@RegistryName("pourstone")
		public static final Block Pourstone = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool());
		@RegistryName("pourstone_stairs")
		public static final StairsBlock PourstoneStairs = new PStairsBlock(Pourstone.getDefaultState(), AbstractBlock.Settings.copy(Pourstone));
		@RegistryName("pourstone_slab")
		public static final SlabBlock PourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(Pourstone));
		@RegistryName("cracked_pourstone")
		public static final Block CrackedPourstone = new Block(FabricBlockSettings.of(Material.STONE).strength(1.0F).requiresTool());
		@RegistryName("cracked_pourstone_stairs")
		public static final StairsBlock CrackedPourstoneStairs = new PStairsBlock(CrackedPourstone.getDefaultState(), AbstractBlock.Settings.copy(Pourstone));
		@RegistryName("cracked_pourstone_slab")
		public static final SlabBlock CrackedPourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(CrackedPourstone));
		@RegistryName("light_pourstone")
		public static final Block LightPourstone = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool());
		@RegistryName("light_pourstone_stairs")
		public static final StairsBlock LightPourstoneStairs = new PStairsBlock(LightPourstone.getDefaultState(), AbstractBlock.Settings.copy(LightPourstone));
		@RegistryName("light_pourstone_slab")
		public static final SlabBlock LightPourstoneSlab = new SlabBlock(AbstractBlock.Settings.copy(Pourstone));

		@RegistryName("massassi_stone")
		public static final Block Massassi = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());
		@RegistryName("massassi_stone_stairs")
		public static final StairsBlock MassassiStairs = new PStairsBlock(Massassi.getDefaultState(), AbstractBlock.Settings.copy(Massassi));
		@RegistryName("massassi_stone_slab")
		public static final SlabBlock MassassiSlab = new SlabBlock(AbstractBlock.Settings.copy(Massassi));
		@RegistryName("smooth_massassi_stone")
		public static final Block MassassiSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("smooth_massassi_stone_slab")
		public static final SlabBlock MassassiSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiSmooth));
		@RegistryName("massassi_stone_bricks")
		public static final Block MassassiBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());
		@RegistryName("massassi_stone_brick_stairs")
		public static final StairsBlock MassassiBrickStairs = new PStairsBlock(MassassiBricks.getDefaultState(), AbstractBlock.Settings.copy(MassassiBricks));
		@RegistryName("massassi_stone_brick_slab")
		public static final SlabBlock MassassiBrickSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiBricks));
		//@RegistryName("chiseled_massassi_stone_bricks")
		//public static final Block MassassiChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());


		@RegistryName("ilum_stone")
		public static final Block Ilum = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());
		@RegistryName("ilum_stone_stairs")
		public static final StairsBlock IlumStairs = new PStairsBlock(Ilum.getDefaultState(), AbstractBlock.Settings.copy(Ilum));
		@RegistryName("ilum_stone_slab")
		public static final SlabBlock IlumSlab = new SlabBlock(AbstractBlock.Settings.copy(Ilum));
		@RegistryName("smooth_ilum_stone")
		public static final Block IlumSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("smooth_ilum_stone_slab")
		public static final SlabBlock IlumSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumSmooth));
		@RegistryName("ilum_stone_bricks")
		public static final Block IlumBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());
		@RegistryName("ilum_stone_brick_stairs")
		public static final StairsBlock IlumBrickStairs = new PStairsBlock(IlumBricks.getDefaultState(), AbstractBlock.Settings.copy(IlumBricks));
		@RegistryName("ilum_stone_brick_slab")
		public static final SlabBlock IlumBrickSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumBricks));
		@RegistryName("chiseled_ilum_stone_bricks")
		public static final Block IlumChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());
	}

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, Block.class, SwgBlocks::registerBlock);
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, BlockEntityType.class, SwgBlocks::registerBlockEntityType);

		RegistryHelper.registerFlammable(SwgBlocks.class);

		Registry.register(Registry.BLOCK, Resources.id("tatooine_home_door"), Door.TatooineHomeTop);
		Registry.register(Registry.BLOCK, Resources.id("tatooine_home_door_controller"), Door.TatooineHomeBottom);
	}

	public static void registerBlock(Block block, Identifier identifier, boolean ignoreTab)
	{
		var itemSettings = new Item.Settings();

		if (!ignoreTab)
			itemSettings = itemSettings.group(Galaxies.TabBlocks);

		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, itemSettings));
	}

	public static void registerBlockEntityType(BlockEntityType<?> blockEntityType, Identifier identifier, boolean ignoreTab)
	{
		Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, blockEntityType);
	}
}
