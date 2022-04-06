package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.*;
import com.parzivail.pswg.block.crop.*;
import com.parzivail.pswg.blockentity.*;
import com.parzivail.pswg.container.registry.*;
import com.parzivail.util.block.*;
import com.parzivail.util.block.connecting.SelfConnectingBlock;
import com.parzivail.util.block.connecting.SelfConnectingGlassBlock;
import com.parzivail.util.block.connecting.SelfConnectingNodeBlock;
import com.parzivail.util.block.connecting.SelfConnectingStainedGlassBlock;
import com.parzivail.util.block.mutating.*;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithBounds;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithBoundsGuiEntity;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithGuiEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
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
	@RegistryOrder(0)
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

	@RegistryOrder(1)
	public static class Stone
	{
		@RegistryName("canyon_stone")
		public static final RegistryHelper.StoneProducts Canyon = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F)));
		@RegistryName("canyon_stone_bricks")
		public static final Block CanyonBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("polished_canyon_stone")
		public static final Block PolishedCanyon = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("chiseled_canyon_stone")
		public static final Block ChiseledCanyon = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));

		@RegistryName("canyon_cobblestone")
		public static final RegistryHelper.StoneProducts CanyonCobble = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));

		@RegistryName("pourstone")
		public static final RegistryHelper.StoneProducts Pourstone = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));
		@RegistryName("smooth_pourstone")
		public static final RegistryHelper.StoneProducts SmoothPourstone = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));
		@RegistryName("cracked_pourstone")
		public static final RegistryHelper.StoneProducts CrackedPourstone = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.0F).requiresTool()));

		@RegistryName("durasteel_bordered_pourstone")
		@ClientBlockRegistryData(isConnected = true)
		public static final SelfConnectingBlock DurasteelConnectedPourstone = new SelfConnectingBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());

		@RegistryName("pourstone")
		public static final RegistryHelper.DyedStoneProducts DyedPourstone = new RegistryHelper.DyedStoneProducts(color -> new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool())));

		@RegistryName("massassi_stone")
		public static final RegistryHelper.StoneProducts Massassi = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		@RegistryName("smooth_massassi_stone")
		public static final Block MassassiSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("smooth_massassi_stone_slab")
		public static final SlabBlock MassassiSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(MassassiSmooth));
		@RegistryName("massassi_stone_bricks")
		public static final RegistryHelper.StoneProducts MassassiBricks = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		//@RegistryName("chiseled_massassi_stone_bricks")
		//public static final Block MassassiChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());

		@RegistryName("mossy_smooth_massassi_stone")
		public static final Block MossyMassassiSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("mossy_smooth_massassi_stone_slab")
		public static final SlabBlock MossyMassassiSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(MossyMassassiSmooth));
		@RegistryName("mossy_massassi_stone_bricks")
		public static final RegistryHelper.StoneProducts MossyMassassiBricks = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));

		@RegistryName("ilum_stone")
		public static final RegistryHelper.StoneProducts Ilum = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		@RegistryName("smooth_ilum_stone")
		public static final Block IlumSmooth = new Block(FabricBlockSettings.of(Material.STONE).strength(2.0F).requiresTool());
		@RegistryName("smooth_ilum_stone_slab")
		public static final SlabBlock IlumSmoothSlab = new SlabBlock(AbstractBlock.Settings.copy(IlumSmooth));
		@RegistryName("ilum_stone_bricks")
		public static final RegistryHelper.StoneProducts IlumBricks = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool()));
		@RegistryName("chiseled_ilum_stone_bricks")
		public static final Block IlumChiseledBricks = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool());
	}

	@RegistryOrder(2)
	public static class Sandstone
	{
		@RegistryName("desert_sandstone")
		public static final RegistryHelper.StoneProducts Desert = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.STONE).strength(1.25F).requiresTool()));
		@RegistryName("smooth_desert_sandstone")
		public static final Block SmoothDesert = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("polished_desert_sandstone")
		public static final Block PolishedDesert = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
		@RegistryName("chiseled_desert_sandstone")
		public static final Block ChiseledDesert = new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F));
	}

	@RegistryOrder(3)
	public static class Sand
	{
		@RegistryName("desert_sand")
		public static final Block Desert = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
		@RegistryName("pit_sand")
		public static final Block Pit = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
		@RegistryName("fine_sand")
		public static final Block Fine = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
		@RegistryName("loose_desert_sand")
		public static final Block LooseDesert = new AccumulatingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F), Desert::getPlacementState);
		@RegistryName("canyon_sand")
		public static final Block Canyon = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
	}

	@RegistryOrder(4)
	public static class Salt
	{
		@RegistryName("caked_salt")
		public static final Block Caked = new Block(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.SAND).strength(0.5F));
	}

	@RegistryOrder(5)
	public static class Gravel
	{
		@RegistryName("jundland_gravel")
		public static final FallingBlock Jundland = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).sounds(BlockSoundGroup.GRAVEL).strength(0.5F));
	}

	@RegistryOrder(6)
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

	@RegistryOrder(7)
	public static class Plant
	{
		@RegistryName("funnel_flower")
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block FunnelFlower = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("blossoming_funnel_flower")
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block BlossomingFunnelFlower = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("poonten_grass")
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block PoontenGrass = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("dried_poonten_grass")
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block DriedPoontenGrass = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("tuber_stalk")
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block Tuber = new AridPlant(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
		@RegistryName("chasuka")
		@TabIgnore
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final CropBlock Chasuka = new ChasukaCrop(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
		@RegistryName("hkak_bush")
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final HkakBushBlock HkakBush = new HkakBushBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
		@RegistryName("molo_shrub")
		@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final MoloShrubBlock MoloShrub = new MoloShrubBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.CROP));
		@RegistryName("vaporator_mushroom_colony")
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block VaporatorMushroom = new VaporatorMushroomBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
	}

	@RegistryOrder(8)
	public class Tree
	{
		@RegistryName("sequoia_leaves")
		@ServerBlockRegistryData(fireBurn = 30, fireSpread = 60)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT_MIPPED)
		public static final LeavesBlock SequoiaLeaves = createLeavesBlock();
		@RegistryName("sequoia_wood")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final Block SequoiaWood = new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		@RegistryName("sequoia_log")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final PillarBlock SequoiaLog = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("stripped_sequoia_log")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final PillarBlock StrippedSequoiaLog = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("mossy_sequoia_log")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final PillarBlock MossySequoiaLog = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("sequoia")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 20)
		public static final RegistryHelper.WoodProducts SequoiaProducts = new RegistryHelper.WoodProducts(new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)));

		@RegistryName("japor_leaves")
		@ServerBlockRegistryData(fireBurn = 30, fireSpread = 60)
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT_MIPPED)
		public static final BushLeavesBlock JaporLeaves = createBushLeavesBlock();
		@RegistryName("japor_wood")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final Block JaporWood = new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		@RegistryName("japor_log")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final PillarBlock JaporLog = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);
		@RegistryName("japor")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 20)
		public static final RegistryHelper.WoodProducts JaporProducts = new RegistryHelper.WoodProducts(new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)));

		@RegistryName("tatooine_wood")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final Block TatooineWood = new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		@RegistryName("tatooine_log")
		@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
		public static final PillarBlock TatooineLog = createLogBlock(MapColor.OAK_TAN, MapColor.BROWN);

		private static LeavesBlock createLeavesBlock()
		{
			return new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).sounds(BlockSoundGroup.GRASS).nonOpaque().suffocates(BlockUtil::never).blockVision(BlockUtil::never));
		}

		private static BushLeavesBlock createBushLeavesBlock()
		{
			return new BushLeavesBlock(8, 3, AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).sounds(BlockSoundGroup.GRASS).noCollision());
		}

		private static PillarBlock createLogBlock(MapColor topMapColor, MapColor sideMapColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, blockState ->
					blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(2.0F).sounds(BlockSoundGroup.WOOD));
		}
	}

	@RegistryOrder(9)
	public static class Ore
	{
		@RegistryName("beskar_ore")
		public static final Block BeskarOre = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F).requiresTool());
		@RegistryName("beskar_block")
		public static final Block BeskarBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

		@RegistryName("chromium_ore")
		public static final Block ChromiumOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("chromium_block")
		public static final Block ChromiumBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(3.0F).requiresTool());

		@RegistryName("cortosis_ore")
		public static final Block CortosisOre = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F).requiresTool());
		@RegistryName("cortosis_block")
		public static final Block CortosisBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());

		@RegistryName("desh_ore")
		public static final Block DeshOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("desh_block")
		public static final Block DeshBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(3.0F).requiresTool());

		@RegistryName("diatium_ore")
		public static final Block DiatiumOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("diatium_block")
		public static final Block DiatiumBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());

		@RegistryName("ionite_ore")
		public static final Block IoniteOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("ionite_block")
		public static final Block IoniteBlock = new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.METAL).strength(5.0F).luminance(3).requiresTool());

		@RegistryName("lommite_ore")
		public static final Block LommiteOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("lommite_block")
		public static final Block LommiteBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

		@RegistryName("titanium_ore")
		public static final Block TitaniumOre = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0F).requiresTool());
		@RegistryName("titanium_block")
		public static final Block TitaniumBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

		@RegistryName("zersium_ore")
		public static final Block ZersiumOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("zersium_block")
		public static final Block ZersiumBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

		@RegistryName("thorilide_ore")
		public static final Block ThorilideOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("thorilide_block")
		public static final Block ThorilideBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

		@RegistryName("helicite_ore")
		public static final Block HeliciteOre = new Block(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool());
		@RegistryName("helicite_block")
		public static final Block HeliciteBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
	}

	@RegistryOrder(10)
	public static class Composite
	{
		@RegistryName("durasteel_block")
		public static final Block DurasteelBlock = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
		@RegistryName("plasteel_block")
		@ClientBlockRegistryData(isConnected = true)
		public static final SelfConnectingBlock PlasteelBlock = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3.0F).requiresTool());
	}

	@RegistryOrder(11)
	public static class Glass
	{
		@RegistryName("imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.CUTOUT)
		public static final SelfConnectingGlassBlock Imperial = createSelfConnectingGlass();
		@RegistryName("white_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock WhiteStainedImperial = createSelfConnectingStainedGlass(DyeColor.WHITE);
		@RegistryName("orange_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock OrangeStainedImperial = createSelfConnectingStainedGlass(DyeColor.ORANGE);
		@RegistryName("magenta_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock MagentaStainedImperial = createSelfConnectingStainedGlass(DyeColor.MAGENTA);
		@RegistryName("light_blue_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock LightBlueStainedImperial = createSelfConnectingStainedGlass(DyeColor.LIGHT_BLUE);
		@RegistryName("yellow_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock YellowStainedImperial = createSelfConnectingStainedGlass(DyeColor.YELLOW);
		@RegistryName("lime_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock LimeStainedImperial = createSelfConnectingStainedGlass(DyeColor.LIME);
		@RegistryName("pink_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock PinkStainedImperial = createSelfConnectingStainedGlass(DyeColor.PINK);
		@RegistryName("gray_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock GrayStainedImperial = createSelfConnectingStainedGlass(DyeColor.GRAY);
		@RegistryName("light_gray_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock LightGrayStainedImperial = createSelfConnectingStainedGlass(DyeColor.LIGHT_GRAY);
		@RegistryName("cyan_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock CyanStainedImperial = createSelfConnectingStainedGlass(DyeColor.CYAN);
		@RegistryName("purple_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock PurpleStainedImperial = createSelfConnectingStainedGlass(DyeColor.PURPLE);
		@RegistryName("blue_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock BlueStainedImperial = createSelfConnectingStainedGlass(DyeColor.BLUE);
		@RegistryName("brown_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock BrownStainedImperial = createSelfConnectingStainedGlass(DyeColor.BROWN);
		@RegistryName("green_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock GreenStainedImperial = createSelfConnectingStainedGlass(DyeColor.GREEN);
		@RegistryName("red_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final SelfConnectingStainedGlassBlock RedStainedImperial = createSelfConnectingStainedGlass(DyeColor.RED);
		@RegistryName("black_stained_imperial_glass")
		@ClientBlockRegistryData(isConnected = true, renderLayer = RenderLayerHint.TRANSLUCENT)
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

	@RegistryOrder(12)
	public static class Panel
	{
		@RegistryName("rusted_metal")
		@ClientBlockRegistryData(isConnected = true)
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
		public static final RegistryHelper.StoneProducts BlackImperialPanelBlank = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool()));
		@RegistryName("black_imperial_panel_bordered")
		public static final SelfConnectingBlock BlackImperialPanelBordered = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_split")
		public static final SelfConnectingBlock BlackImperialPanelSplit = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("black_imperial_panel_thin_bordered")
		public static final SelfConnectingBlock BlackImperialPanelThinBordered = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("external_imperial_plating")
		@ClientBlockRegistryData(isConnected = true)
		public static final SelfConnectingBlock ExternalImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("large_imperial_plating")
		@ClientBlockRegistryData(isConnected = true)
		public static final SelfConnectingBlock LargeImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("rusted_large_imperial_plating")
		@ClientBlockRegistryData(isConnected = true)
		public static final SelfConnectingBlock RustedLargeImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("mossy_large_imperial_plating")
		@ClientBlockRegistryData(isConnected = true)
		public static final SelfConnectingBlock MossyLargeImperialPlatingConnected = new SelfConnectingBlock(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.BLACK).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
		@RegistryName("gray_imperial_panel_blank")
		public static final RegistryHelper.StoneProducts GrayImperialPanelBlank = new RegistryHelper.StoneProducts(new Block(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.GRAY).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool()));

		@RegistryName("gray_imperial_panel_pattern_3")
		public static final Block GrayImperialPanelPattern3 = createPanel(MapColor.GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_3")
		public static final Block RustedGrayImperialPanelPattern3 = createPanel(MapColor.GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_3")
		public static final Block MossyGrayImperialPanelPattern3 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_4")
		public static final Block GrayImperialPanelPattern4 = createPanel(MapColor.GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_4")
		public static final Block RustedGrayImperialPanelPattern4 = createPanel(MapColor.GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_4")
		public static final Block MossyGrayImperialPanelPattern4 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_5")
		public static final Block GrayImperialPanelPattern5 = createPanel(MapColor.GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_5")
		public static final Block RustedGrayImperialPanelPattern5 = createPanel(MapColor.GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_5")
		public static final Block MossyGrayImperialPanelPattern5 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_6")
		public static final Block GrayImperialPanelPattern6 = createPanel(MapColor.GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_6")
		public static final Block RustedGrayImperialPanelPattern6 = createPanel(MapColor.GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_6")
		public static final Block MossyGrayImperialPanelPattern6 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_7")
		public static final Block GrayImperialPanelPattern7 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_8")
		public static final Block GrayImperialPanelPattern8 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_9")
		public static final Block GrayImperialPanelPattern9 = createPanel(MapColor.GRAY);
		@RegistryName("rusted_gray_imperial_panel_pattern_9")
		public static final Block RustedGrayImperialPanelPattern9 = createPanel(MapColor.GRAY);
		@RegistryName("mossy_gray_imperial_panel_pattern_9")
		public static final Block MossyGrayImperialPanelPattern9 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_10")
		public static final Block GrayImperialPanelPattern10 = createPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_panel_pattern_11")
		public static final Block GrayImperialPanelPattern11 = createPanel(MapColor.GRAY);

		@RegistryName("gray_imperial_light_on_1")
		public static final Block GrayImperialLightOn1 = createLitPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_light_on_2")
		public static final Block GrayImperialLightOn2 = createLitPanel(MapColor.GRAY);
		@RegistryName("gray_imperial_light_half_1")
		public static final Block GrayImperialLightHalf1 = createLitPanel(MapColor.GRAY, 8);
		@RegistryName("gray_imperial_light_half_2")
		public static final Block GrayImperialLightHalf2 = createLitPanel(MapColor.GRAY, 8);
		@RegistryName("gray_imperial_light_half_3")
		public static final Block GrayImperialLightHalf3 = createLitPanel(MapColor.GRAY, 8);
		@RegistryName("gray_imperial_light_half_4")
		public static final Block GrayImperialLightHalf4 = createLitPanel(MapColor.GRAY, 8);
		@RegistryName("gray_imperial_light_half_5")
		public static final Block GrayImperialLightHalf5 = createLitPanel(MapColor.GRAY, 8);
		@RegistryName("gray_imperial_light_off_1")
		public static final Block GrayImperialLightOff1 = createPillarPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_light_off_2")
		public static final Block GrayImperialLightOff2 = createPillarPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_1")
		public static final PillarBlock GrayImperialPanelPattern1 = createPillarPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);
		@RegistryName("gray_imperial_panel_pattern_2")
		public static final PillarBlock GrayImperialPanelPattern2 = createPillarPanel(MapColor.GRAY, MapColor.LIGHT_GRAY);

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

		private static PillarBlock createLitPanel(MapColor topMapColor, int luminance)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, topMapColor).strength(2.0F).requiresTool().sounds(BlockSoundGroup.COPPER).luminance(value -> luminance));
		}

		private static PillarBlock createLitPanel(MapColor topMapColor)
		{
			return createLitPanel(topMapColor, 15);
		}

		private static SelfConnectingBlock createLitConnectingPanel(MapColor mapColor)
		{
			return new SelfConnectingBlock(AbstractBlock.Settings.of(Material.METAL, mapColor).strength(2.0F).requiresTool().sounds(BlockSoundGroup.COPPER).luminance(value -> 15));
		}

		private static Block createPanel(MapColor topMapColor)
		{
			return new Block(AbstractBlock.Settings.of(Material.METAL, topMapColor).strength(1.5F).requiresTool().sounds(BlockSoundGroup.COPPER));
		}

		private static PillarBlock createPillarPanel(MapColor topMapColor, MapColor sideMapColor)
		{
			return new PillarBlock(AbstractBlock.Settings.of(Material.METAL, (blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(1.5F).requiresTool().sounds(BlockSoundGroup.COPPER));
		}
	}

	@RegistryOrder(13)
	public static class Grate
	{
		@RegistryName("imperial_opaque_grate_1")
		public static final Block ImperialOpaque1 = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("imperial_opaque_grate_2")
		public static final Block ImperialOpaque2 = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("imperial_opaque_grate_3")
		public static final Block ImperialOpaque3 = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
	}

	@RegistryOrder(14)
	public static class Vent
	{
		@RegistryName("air_vent")
		public static final Block Air = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());

		@RegistryName("imperial_vent")
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block Imperial = new AccessibleMetalTrapdoorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool().allowsSpawning((state, world, pos, type) -> false));
		@RegistryName("imperial_grated_vent")
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block ImperialGrated = new AccessibleMetalTrapdoorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool().allowsSpawning((state, world, pos, type) -> false));
	}

	@RegistryOrder(15)
	public static class Light
	{
		@RegistryName("light_fixture")
		public static final Block Fixture = new InvertedLampBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).luminance(BlockUtil.createLightLevelFromBlockState(15)).strength(0.3F));

		@RegistryName("red_hangar_light")
		public static final Block RedHangar = new WaterloggableRotatingBlockWithBounds(VoxelShapeUtil.getCentered(12, 10, 5), WaterloggableRotatingBlockWithBounds.Substrate.NONE, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).noCollision().nonOpaque().luminance(15).strength(0.5F));
		@RegistryName("blue_hangar_light")
		public static final Block BlueHangar = new WaterloggableRotatingBlockWithBounds(VoxelShapeUtil.getCentered(12, 10, 5), WaterloggableRotatingBlockWithBounds.Substrate.NONE, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).noCollision().nonOpaque().luminance(15).strength(0.5F));
		@RegistryName("wall_cluster_light")
		public static final ClusterLightBlock WallCluster = new ClusterLightBlock(WaterloggableRotatingBlockWithBounds.Substrate.NONE, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().luminance(15).strength(0.5F));
	}

	@RegistryOrder(16)
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

	@RegistryOrder(17)
	public static class Crate
	{
		@RegistryName("orange_kyber_crate")
		public static final WaterloggableRotatingBlockWithGuiEntity OrangeKyber = new WaterloggableRotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateOctagonBlockEntity::new);
		@RegistryName("gray_kyber_crate")
		public static final WaterloggableRotatingBlockWithGuiEntity GrayKyber = new WaterloggableRotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateOctagonBlockEntity::new);
		@RegistryName("black_kyber_crate")
		public static final WaterloggableRotatingBlockWithGuiEntity BlackKyber = new WaterloggableRotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateOctagonBlockEntity::new);
		@RegistryName("kyber_crate")
		public static final BlockEntityType<CrateOctagonBlockEntity> KyberCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateOctagonBlockEntity::new, OrangeKyber, GrayKyber, BlackKyber).build();

		@RegistryName("toolbox")
		public static final WaterloggableRotatingBlockWithGuiEntity Toolbox = new WaterloggableRotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateMosEisleyBlockEntity::new);
		@RegistryName("toolbox")
		public static final BlockEntityType<CrateMosEisleyBlockEntity> ToolboxBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateMosEisleyBlockEntity::new, Toolbox).build();

		@RegistryName("imperial_crate")
		public static final WaterloggableRotatingBlockWithGuiEntity Imperial = new WaterloggableRotatingBlockWithGuiEntity(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateImperialCubeBlockEntity::new);
		@RegistryName("imperial_crate")
		public static final BlockEntityType<CrateImperialCubeBlockEntity> ImperialCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateImperialCubeBlockEntity::new, Imperial).build();

		@RegistryName("segmented_crate")
		public static final WaterloggableRotatingBlockWithBoundsGuiEntity Segmented = new WaterloggableRotatingBlockWithBoundsGuiEntity(VoxelShapeUtil.getCentered(28, 14, 14), FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F), CrateSegmentedBlockEntity::new);
		@RegistryName("segmented_crate")
		public static final BlockEntityType<CrateSegmentedBlockEntity> SegmentedCrateBlockEntityType = FabricBlockEntityTypeBuilder.create(CrateSegmentedBlockEntity::new, Segmented).build();
	}

	@RegistryOrder(18)
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

	@RegistryOrder(19)
	public static class Machine
	{
		@RegistryName("spoked_machine")
		public static final WaterloggableRotatingBlock Spoked = new WaterloggableRotatingBlockWithBounds(VoxelShapeUtil.getCenteredCube(10, 20), WaterloggableRotatingBlockWithBounds.Substrate.NONE, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(5.0F).requiresTool());
	}

	@RegistryOrder(20)
	public static class MoistureVaporator
	{
		@RegistryName("gx8_moisture_vaporator")
		public static final MoistureVaporatorBlock Gx8 = new MoistureVaporatorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(10.0F).requiresTool());
		@RegistryName("gx8_moisture_vaporator")
		public static final BlockEntityType<MoistureVaporatorBlockEntity> Gx8BlockEntityType = FabricBlockEntityTypeBuilder.create(MoistureVaporatorBlockEntity::new, Gx8).build();
	}

	@RegistryOrder(21)
	public static class Power
	{
		@RegistryName("power_coupling")
		public static final Block Coupling = new PowerCouplingBlock(WaterloggableRotatingBlockWithBounds.Substrate.BEHIND, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).mapColor(MapColor.GRAY).nonOpaque().strength(3.5F).requiresTool());
		@RegistryName("power_coupling")
		public static final BlockEntityType<PowerCouplingBlockEntity> CouplingBlockEntityType = FabricBlockEntityTypeBuilder.create(PowerCouplingBlockEntity::new, Coupling).build();
	}

	@RegistryOrder(22)
	public static class Pipe
	{
		@RegistryName("large_pipe")
		public static final Block Large = new SelfConnectingNodeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).mapColor(MapColor.GRAY).nonOpaque().strength(3.5F).requiresTool());
	}

	@RegistryOrder(23)
	public static class Tank
	{
		@RegistryName("fusion_fuel_tank")
		public static final Block Fusion = new WaterloggableRotatingBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().strength(3.5F).requiresTool());
	}

	@RegistryOrder(24)
	public static class Cage
	{
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block Creature = new WaterloggableCreatureCageBlock(null, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never).strength(3.5F).requiresTool());
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.CUTOUT)
		public static final Block CreatureTerrarium = new CreatureCageBlock(null, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never).strength(3.5F).requiresTool());
		@ClientBlockRegistryData(renderLayer = RenderLayerHint.TRANSLUCENT)
		public static final RegistryHelper.DyedBlocks DyedCreatureTerrarium = new RegistryHelper.DyedBlocks(color -> new CreatureCageBlock(color, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never).strength(3.5F).requiresTool()));
	}

	public static void register()
	{
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, Object.class, SwgBlocks::tryRegisterBlock);
		RegistryHelper.registerAnnotatedFields(SwgBlocks.class, BlockEntityType.class, SwgBlocks::registerBlockEntityType);

		RegistryHelper.register(SwgBlocks.class, ServerBlockRegistryData.class, Block.class, SwgBlocks::registerServerData);

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

	private static void registerServerData(ServerBlockRegistryData data, Block block)
	{
		if (data.fireBurn() != 0 || data.fireSpread() != 0)
			FlammableBlockRegistry.getDefaultInstance().add(block, data.fireBurn(), data.fireSpread());
	}

	private static void tryRegisterBlock(Object o, Identifier identifier, boolean ignoreTab)
	{
		if (o instanceof Block block)
			registerBlock(block, identifier, ignoreTab);
		else if (o instanceof RegistryHelper.StoneProducts variants)
			registerStoneProducts(variants, identifier, ignoreTab);
		else if (o instanceof RegistryHelper.DyedStoneProducts variants)
			registerDyedStoneProducts(variants, identifier, ignoreTab);
		else if (o instanceof RegistryHelper.WoodProducts variants)
			registerWoodProducts(variants, identifier, ignoreTab);
		else if (o instanceof RegistryHelper.DyedBlocks blocks)
			registerDyedBlocks(blocks, identifier, ignoreTab);
	}

	private static void registerStoneProducts(RegistryHelper.StoneProducts t, Identifier identifier, boolean ignoreTab)
	{
		registerBlock(t.block, identifier, ignoreTab);
		registerBlock(t.stairs, Resources.id(identifier.getPath() + "_stairs"), ignoreTab);
		registerBlock(t.slab, Resources.id(identifier.getPath() + "_slab"), ignoreTab);
		registerBlock(t.wall, Resources.id(identifier.getPath() + "_wall"), ignoreTab);
	}

	private static void registerDyedStoneProducts(RegistryHelper.DyedStoneProducts t, Identifier identifier, boolean ignoreTab)
	{
		for (var entry : t.entrySet())
			registerStoneProducts(entry.getValue(), Resources.id(entry.getKey().getName() + "_" + identifier.getPath()), ignoreTab);
	}

	private static void registerWoodProducts(RegistryHelper.WoodProducts t, Identifier identifier, boolean ignoreTab)
	{
		registerBlock(t.plank, Resources.id(identifier.getPath() + "_planks"), ignoreTab);
		registerBlock(t.stairs, Resources.id(identifier.getPath() + "_stairs"), ignoreTab);
		registerBlock(t.slab, Resources.id(identifier.getPath() + "_slab"), ignoreTab);
		registerBlock(t.fence, Resources.id(identifier.getPath() + "_fence"), ignoreTab);
		registerBlock(t.gate, Resources.id(identifier.getPath() + "_fence_gate"), ignoreTab);
		registerBlock(t.trapdoor, Resources.id(identifier.getPath() + "_trapdoor"), ignoreTab);
		registerBlock(t.door, Resources.id(identifier.getPath() + "_door"), ignoreTab);
	}

	private static void registerDyedBlocks(RegistryHelper.DyedBlocks t, Identifier identifier, boolean ignoreTab)
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
