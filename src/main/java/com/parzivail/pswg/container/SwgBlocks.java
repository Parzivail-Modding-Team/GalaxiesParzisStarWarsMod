package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.*;
import com.parzivail.pswg.block.crop.*;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.HashMap;

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
		}, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(2.5F).requiresTool());
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
		public static final TatooineHomeDoorBlock TatooineHomeTop = new TatooineHomeDoorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.0F));
		public static final HashMap<String, TatooineHomeDoorControllerBlock> TatooineHomeBottoms = Util.make(new HashMap<>(), m -> {
			m.put("", new TatooineHomeDoorControllerBlock(null, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.0F)));
			Arrays.stream(DyeColor.values()).forEach(color -> m.put(color.getName(), new TatooineHomeDoorControllerBlock(color, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.0F))));
		});
		@RegistryName("door_tatooine_home")
		public static final BlockEntityType<TatooineHomeDoorBlockEntity> TatooineHomeBlockEntityType = FabricBlockEntityTypeBuilder.create(TatooineHomeDoorBlockEntity::new, TatooineHomeBottoms.values().toArray(new Block[0])).build();
	}

	public static class Machine
	{
		@RegistryName("spoked_machine")
		public static final RotatingBlock Spoked = new RotatingBlockWithBounds(VoxelShapeUtil.getCenteredCube(10, 20), RotatingBlockWithBounds.Substrate.NONE, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(5.0F).requiresTool());
	}

	public static class MoistureVaporator
	{
		@RegistryName("gx8_moisture_vaporator")
		public static final MoistureVaporatorBlock Gx8 = new MoistureVaporatorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(10.0F).requiresTool());
		@RegistryName("gx8_moisture_vaporator")
		public static final BlockEntityType<MoistureVaporatorBlockEntity> Gx8BlockEntityType = FabricBlockEntityTypeBuilder.create(MoistureVaporatorBlockEntity::new, Gx8).build();
	}

	public static class Power
	{
		@RegistryName("power_coupling")
		public static final Block Coupling = new PowerCouplingBlock(RotatingBlockWithBounds.Substrate.BEHIND, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).mapColor(MapColor.GRAY).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("power_coupling")
		public static final BlockEntityType<PowerCouplingBlockEntity> CouplingBlockEntityType = FabricBlockEntityTypeBuilder.create(PowerCouplingBlockEntity::new, Coupling).build();
	}

	public static class Pipe
	{
		@RegistryName("large_pipe")
		public static final Block Large = new SelfConnectingNodeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).mapColor(MapColor.GRAY).nonOpaque().strength(3.5F).requiresTool());
	}

	public static class Tank
	{
		@RegistryName("fusion_fuel_tank")
		public static final Block Fusion = new RotatingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
	}

	public static class Cage
	{
		public static final Block Creature = new WaterloggableCreatureCageBlock(null, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never).strength(3.5F).requiresTool());
		public static final Block CreatureTerrarium = new CreatureCageBlock(null, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never).strength(3.5F).requiresTool());
		public static final RegistryHelper.DyedBlockVariants DyedCreatureTerrarium = new RegistryHelper.DyedBlockVariants(color -> new CreatureCageBlock(color, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never).strength(3.5F).requiresTool()));
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
		public static final Block Air = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());

		@RegistryName("imperial_vent")
		public static final Block Imperial = new AccessibleMetalTrapdoorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool().allowsSpawning((state, world, pos, type) -> false));
		@RegistryName("imperial_grated_vent")
		public static final Block ImperialGrated = new AccessibleMetalTrapdoorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool().allowsSpawning((state, world, pos, type) -> false));
	}

	public static class Grate
	{
		@RegistryName("imperial_opaque_grate_1")
		public static final Block ImperialOpaque1 = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("imperial_opaque_grate_2")
		public static final Block ImperialOpaque2 = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("imperial_opaque_grate_3")
		public static final Block ImperialOpaque3 = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
	}

	public static class Workbench
	{
		@RegistryName("blaster_workbench")
		public static final Block Blaster = new BlasterWorkbenchBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(3.5F).requiresTool());
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
		@RegistryName("vaporator_mushroom_colony")
		public static final Block VaporatorMushroom = new VaporatorMushroomBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
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
		@RegistryName("mossy_sequoia_log")
		@Flammable(burn = 5, spread = 5)
		public static final PillarBlock MossySequoia = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
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

	public static class Planks
	{
		@RegistryName("japor_planks")
		@Flammable(burn = 5, spread = 20)
		public static final Block Japor = new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
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
		public static final ClusterLightBlock WallCluster = new ClusterLightBlock(RotatingBlockWithBounds.Substrate.BEHIND, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().luminance(15).strength(0.5F));
	}

	public static class Panel
	{
		@RegistryName("rusted_metal")
		public static final SelfConnectingBlock RustedMetal = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BROWN).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());

		@RegistryName("imperial_cutout_pipes")
		public static final Block ImperialCutoutPipes = new PillarBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("imperial_cutout_caged_pipes")
		public static final Block ImperialCutoutCagedPipes = new PillarBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("imperial_cutout")
		public static final Block ImperialCutout = new PillarBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("imperial_cutout_caged")
		public static final Block ImperialCutoutCaged = new PillarBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());

		@RegistryName("black_imperial_panel_blank")
		public static final RegistryHelper.BlockStairsSlabWallVariants BlackImperialPanelBlank = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool()));
		@RegistryName("black_imperial_panel_bordered")
		public static final SelfConnectingBlock BlackImperialPanelBordered = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_split")
		public static final SelfConnectingBlock BlackImperialPanelSplit = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_thin_bordered")
		public static final SelfConnectingBlock BlackImperialPanelThinBordered = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("external_imperial_plating")
		public static final SelfConnectingBlock ExternalImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("large_imperial_plating")
		public static final SelfConnectingBlock LargeImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("rusted_large_imperial_plating")
		public static final SelfConnectingBlock RustedLargeImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("mossy_large_imperial_plating")
		public static final SelfConnectingBlock MossyLargeImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("gray_imperial_panel_blank")
		public static final RegistryHelper.BlockStairsSlabWallVariants GrayImperialPanelBlank = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.GRAY).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool()));
		@RegistryName("gray_imperial_panel_pattern_1")
		public static final PillarBlock GrayImperialPanelPattern1 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_2")
		public static final PillarBlock GrayImperialPanelPattern2 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_3")
		public static final PillarBlock GrayImperialPanelPattern3 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_3")
		public static final PillarBlock RustedGrayImperialPanelPattern3 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_3")
		public static final PillarBlock MossyGrayImperialPanelPattern3 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_4")
		public static final PillarBlock GrayImperialPanelPattern4 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_4")
		public static final PillarBlock RustedGrayImperialPanelPattern4 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_4")
		public static final PillarBlock MossyGrayImperialPanelPattern4 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_5")
		public static final PillarBlock GrayImperialPanelPattern5 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_5")
		public static final PillarBlock RustedGrayImperialPanelPattern5 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_5")
		public static final PillarBlock MossyGrayImperialPanelPattern5 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_6")
		public static final PillarBlock GrayImperialPanelPattern6 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_6")
		public static final PillarBlock RustedGrayImperialPanelPattern6 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_6")
		public static final PillarBlock MossyGrayImperialPanelPattern6 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_7")
		public static final PillarBlock GrayImperialPanelPattern7 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_8")
		public static final PillarBlock GrayImperialPanelPattern8 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_9")
		public static final PillarBlock GrayImperialPanelPattern9 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_9")
		public static final PillarBlock RustedGrayImperialPanelPattern9 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_9")
		public static final PillarBlock MossyGrayImperialPanelPattern9 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_10")
		public static final PillarBlock GrayImperialPanelPattern10 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_11")
		public static final PillarBlock GrayImperialPanelPattern11 = createPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);

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
		public static final SelfConnectingBlock ImperialPanelTall1 = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("gray_imperial_tall_panel_2")
		public static final SelfConnectingBlock ImperialPanelTall2 = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());

		@RegistryName("gray_imperial_tall_light_1")
		public static final SelfConnectingBlock ImperialLightTall1 = createLitConnectingPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_tall_light_2")
		public static final SelfConnectingBlock ImperialLightTall2 = createLitConnectingPanel(MapColor.GRAY);

		@RegistryName("lab_wall")
		@TabIgnore
		public static final Block LabWall = new Block(FabricBlockSettings.of(Material.STONE));

		private static PillarBlock createLitPanel(MapColor topMapColor, MapColor sideMapColor, int luminance)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(2.0F).requiresTool().sounds(BlockSoundGroup.COPPER).luminance(value -> luminance));
		}

		private static PillarBlock createLitPanel(MapColor topMapColor, MapColor sideMapColor)
		{
			return createLitPanel(topMapColor, sideMapColor, 15);
		}

		private static SelfConnectingBlock createLitConnectingPanel(MapColor mapColor)
		{
			return new SelfConnectingBlock(AbstractBlock.Settings.of(Material.METAL, mapColor).strength(2.0F).requiresTool().sounds(BlockSoundGroup.COPPER).luminance(value -> 15));
		}

		private static PillarBlock createPanel(MapColor topMapColor, MapColor sideMapColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(1.5F).requiresTool().sounds(BlockSoundGroup.COPPER));
		}
	}

	public static class MaterialBlock
	{
		@RegistryName("beskar_block")
		public static final Block Beskar = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
		@RegistryName("chromium_block")
		public static final Block Chromium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(3.0F).requiresTool());
		@RegistryName("cortosis_block")
		public static final Block Cortosis = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("desh_block")
		public static final Block Desh = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(3.0F).requiresTool());
		@RegistryName("diatium_block")
		public static final Block Diatium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("durasteel_block")
		public static final Block Durasteel = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
		@RegistryName("ionite_block")
		public static final Block Ionite = new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.METAL).strength(5.0F).luminance(3).requiresTool());
		@RegistryName("lommite_block")
		public static final Block Lommite = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
		@RegistryName("plasteel_block")
		public static final SelfConnectingBlock Plasteel = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).requiresTool());
		@RegistryName("titanium_block")
		public static final Block Titanium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
		@RegistryName("zersium_block")
		public static final Block Zersium = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
		@RegistryName("helicite_block")
		public static final Block Helicite = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
		@RegistryName("thorilide_block")
		public static final Block Thorilide = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
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
		@RegistryName("pit_sand")
		public static final Block Pit = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
		@RegistryName("fine_sand")
		public static final Block Fine = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
		@RegistryName("loose_desert_sand")
		public static final Block LooseDesert = new AccumulatingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F), Desert::getPlacementState);
		@RegistryName("desert_canyon_sand")
		public static final Block DesertCanyon = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
	}

	public static class Sandstone
	{
		@RegistryName("desert_sandstone")
		public static final Block Desert = new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool());
		@RegistryName("smooth_desert_sandstone")
		public static final Block SmoothDesert = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("polished_desert_sandstone")
		public static final Block PolishedDesert = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("chiseled_desert_sandstone")
		public static final Block ChiseledDesert = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
	}

	public static class Salt
	{
		@RegistryName("caked_salt")
		public static final Block Caked = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
	}

	public static class Dirt
	{
		@RegistryName("wet_pourstone")
		public static final Block WetPourstone = new RuiningDryingBlock(Stone.Pourstone.block, 10, () -> Dirt.RuinedWetPourstone, FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F).noCollision());
		@RegistryName("wet_pourstone_stairs")
		public static final Block WetPourstoneStairs = new RuiningDryingStairsBlock(WetPourstone.getDefaultState(), Stone.Pourstone.stairs, 10, () -> Dirt.RuinedWetPourstoneStairs, AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("wet_pourstone_slab")
		public static final Block WetPourstoneSlab = new RuiningDryingSlabBlock(Stone.Pourstone.slab, 10, () -> Dirt.RuinedWetPourstoneSlab, AbstractBlock.Settings.copy(WetPourstone));
		@RegistryName("ruined_wet_pourstone")
		public static final Block RuinedWetPourstone = new DryingBlock(Stone.CrackedPourstone.block, 10, FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).noCollision().strength(0.5F));
		@RegistryName("ruined_wet_pourstone_stairs")
		public static final StairsBlock RuinedWetPourstoneStairs = new DryingStairsBlock(RuinedWetPourstone.getDefaultState(), Stone.CrackedPourstone.stairs, 10, AbstractBlock.Settings.copy(RuinedWetPourstone));
		@RegistryName("ruined_wet_pourstone_slab")
		public static final SlabBlock RuinedWetPourstoneSlab = new DryingSlabBlock(Stone.CrackedPourstone.slab, 10, AbstractBlock.Settings.copy(RuinedWetPourstone));
		@RegistryName("desert_loam")
		public static final Block DesertLoam = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F));
	}

	public static class Gravel
	{
		@RegistryName("jundland_gravel")
		public static final Block Jundland = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F));
	}

	public static class Stone
	{
		@RegistryName("canyon_stone")
		public static final Block Canyon = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("canyon_stone_bricks")
		public static final Block CanyonBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("polished_canyon_stone")
		public static final Block PolishedCanyon = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("chiseled_canyon_stone")
		public static final Block ChiseledCanyon = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));

		@RegistryName("canyon_cobblestone")
		public static final RegistryHelper.BlockStairsSlabWallVariants CanyonCobble = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));

		@RegistryName("pourstone")
		public static final RegistryHelper.BlockStairsSlabWallVariants Pourstone = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));
		@RegistryName("smooth_pourstone")
		public static final RegistryHelper.BlockStairsSlabWallVariants SmoothPourstone = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));
		@RegistryName("cracked_pourstone")
		public static final RegistryHelper.BlockStairsSlabWallVariants CrackedPourstone = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.0F).requiresTool()));

		@RegistryName("durasteel_bordered_pourstone")
		public static final SelfConnectingBlock DurasteelConnectedPourstone = new SelfConnectingBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());

		@RegistryName("pourstone")
		public static final RegistryHelper.DyedBlockVariants DyedPourstone = new RegistryHelper.DyedBlockVariants(color -> new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));
		@RegistryName("pourstone_stairs")
		public static final RegistryHelper.DyedBlockVariants DyedPourstoneStairs = new RegistryHelper.DyedBlockVariants(color -> new PStairsBlock(DyedPourstone.get(color).getDefaultState(), AbstractBlock.Settings.copy(DyedPourstone.get(color))));
		@RegistryName("pourstone_slab")
		public static final RegistryHelper.DyedBlockVariants DyedPourstoneSlab = new RegistryHelper.DyedBlockVariants(color -> new SlabBlock(AbstractBlock.Settings.copy(DyedPourstone.get(color))));
		@RegistryName("pourstone_wall")
		public static final RegistryHelper.DyedBlockVariants DyedPourstoneWall = new RegistryHelper.DyedBlockVariants(color -> new WallBlock(AbstractBlock.Settings.copy(DyedPourstone.get(color))));

		@RegistryName("massassi_stone")
		public static final RegistryHelper.BlockStairsSlabWallVariants Massassi = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		@RegistryName("smooth_massassi_stone")
		public static final Block MassassiSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("smooth_massassi_stone_slab")
		public static final SlabBlock MassassiSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiSmooth));
		@RegistryName("massassi_stone_bricks")
		public static final RegistryHelper.BlockStairsSlabWallVariants MassassiBricks = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		//@RegistryName("chiseled_massassi_stone_bricks")
		//public static final Block MassassiChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());

		@RegistryName("mossy_smooth_massassi_stone")
		public static final Block MossyMassassiSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("mossy_smooth_massassi_stone_slab")
		public static final SlabBlock MossyMassassiSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(MossyMassassiSmooth));
		@RegistryName("mossy_massassi_stone_bricks")
		public static final RegistryHelper.BlockStairsSlabWallVariants MossyMassassiBricks = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));

		@RegistryName("ilum_stone")
		public static final RegistryHelper.BlockStairsSlabWallVariants Ilum = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		@RegistryName("smooth_ilum_stone")
		public static final Block IlumSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("smooth_ilum_stone_slab")
		public static final SlabBlock IlumSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumSmooth));
		@RegistryName("ilum_stone_bricks")
		public static final RegistryHelper.BlockStairsSlabWallVariants IlumBricks = new RegistryHelper.BlockStairsSlabWallVariants(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		@RegistryName("chiseled_ilum_stone_bricks")
		public static final Block IlumChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());
	}

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, Block.class, SwgBlocks::registerBlock);
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, RegistryHelper.BlockStairsSlabWallVariants.class, SwgBlocks::registerBlockStabStairs);
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, RegistryHelper.DyedBlockVariants.class, SwgBlocks::registerDyedBlocks);
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, BlockEntityType.class, SwgBlocks::registerBlockEntityType);

		RegistryHelper.registerFlammable(SwgBlocks.class);

		for (var entry : Cage.DyedCreatureTerrarium.entrySet())
		{
			var id = Resources.id(entry.getKey().getName() + "_stained_creature_terrarium");
			Registry.register(Registry.BLOCK, id, entry.getValue());
			Registry.register(Registry.ITEM, id, new CreatureCageBlock.Item((CreatureCageBlock)entry.getValue(), new Item.Settings().group(Galaxies.TabBlocks)));
		}
		Registry.register(Registry.BLOCK, Resources.id("creature_terrarium"), Cage.CreatureTerrarium);
		Registry.register(Registry.ITEM, Resources.id("creature_terrarium"), new CreatureCageBlock.Item((CreatureCageBlock)SwgBlocks.Cage.CreatureTerrarium, new Item.Settings().group(Galaxies.TabBlocks)));

		Registry.register(Registry.BLOCK, Resources.id("creature_cage"), Cage.Creature);
		Registry.register(Registry.ITEM, Resources.id("creature_cage"), new CreatureCageBlock.Item((CreatureCageBlock)SwgBlocks.Cage.Creature, new Item.Settings().group(Galaxies.TabBlocks)));

		Registry.register(Registry.BLOCK, Resources.id("tatooine_home_door"), Door.TatooineHomeTop);

		for (var block : Door.TatooineHomeBottoms.values())
		{
			var color = block.getColor();
			if (color == null)
				Registry.register(Registry.BLOCK, Resources.id("tatooine_home_door_controller"), block);
			else
				Registry.register(Registry.BLOCK, Resources.id("tatooine_home_door_controller_" + color.getName()), block);
		}
	}

	private static void registerBlockStabStairs(RegistryHelper.BlockStairsSlabWallVariants t, Identifier identifier, boolean ignoreTab)
	{
		registerBlock(t.block, identifier, ignoreTab);
		registerBlock(t.stairs, Resources.id(identifier.getPath() + "_stairs"), ignoreTab);
		registerBlock(t.slab, Resources.id(identifier.getPath() + "_slab"), ignoreTab);
		registerBlock(t.wall, Resources.id(identifier.getPath() + "_wall"), ignoreTab);
	}

	private static void registerDyedBlocks(RegistryHelper.DyedBlockVariants t, Identifier identifier, boolean ignoreTab)
	{
		for (var entry : t.entrySet())
			registerBlock(entry.getValue(), Resources.id(entry.getKey().getName() + "_" + identifier.getPath()), ignoreTab);
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
