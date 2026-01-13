package dev.pswg.container;

import dev.pswg.Galaxies;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.autoreg.ClientBlockRegistryData;
import dev.pswg.autoreg.ServerBlockRegistryData;
import dev.pswg.block.*;
import dev.pswg.blockEntity.CrateCorrugatedBlockEntity;
import dev.pswg.blockEntity.WaterloggableRotatingBlockWithBoundsGuiEntity;
import dev.pswg.datagen.*;
import dev.pswg.registry.Registrar;
import dev.pswg.util.BlockUtil;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ColorCode;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.function.ToIntFunction;

public class GalaxiesBlocks
{
	/// STONE
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final StoneProducts CANYON = new StoneProducts(AbstractBlock.Settings.create().strength(0.5F), "canyon_stone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CANYON_BRICKS = createBlock("canyon_stone_bricks", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block POLISHED_CANYON = createBlock("polished_canyon_stone", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CHISELED_CANYON = createBlock("chiseled_canyon_stone", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final StoneProducts CANYON_COBBLE = new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), "canyon_cobblestone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final StoneProducts POURSTONE = new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), "pourstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final StoneProducts SMOOTH_POURSTONE = new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), "smooth_pourstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final StoneProducts CRACKED_POURSTONE = new StoneProducts(AbstractBlock.Settings.create().strength(1.0F).requiresTool(), "cracked_pourstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final DyedStoneProducts DYED_POURSTONE = new DyedStoneProducts(color -> new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), color.name().toLowerCase() + "_pourstone"));
	@DataGenBlock
	// TODO: find a way implement "connecting" blocks
	public static final SelfConnectingBlock DURASTEEL_CONNECTING_POURSTONE = Registrar.block(Galaxies.id("durasteel_bordered_pourstone"), SelfConnectingBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
	@DataGenBlock
	public static final StoneProducts MASSASSI = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "massassi_stone");
	@DataGenBlock
	public static final ReducedStoneProducts MASSASSI_SMOOTH = new ReducedStoneProducts(AbstractBlock.Settings.create().strength(2F).requiresTool(), "smooth_massassi_stone");
	@DataGenBlock
	public static final StoneProducts MASSASSI_BRICKS = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "massassi_stone_bricks");
	@DataGenBlock
	public static final ReducedStoneProducts MOSSY_MASSASSI_SMOOTH = new ReducedStoneProducts(AbstractBlock.Settings.create().strength(2F).requiresTool(), "mossy_smooth_massassi_stone");
	@DataGenBlock
	public static final StoneProducts MOSSY_MASSASSI_BRICKS = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "mossy_massassi_stone_bricks");
	@DataGenBlock
	public static final StoneProducts ILUM = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "ilum_stone");
	@DataGenBlock
	public static final ReducedStoneProducts ILUM_SMOOTH = new ReducedStoneProducts(AbstractBlock.Settings.create().strength(2.0F).requiresTool(), "smooth_ilum_stone");
	@DataGenBlock
	public static final StoneProducts ILUM_BRICKS = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "ilum_stone_bricks");
	@DataGenBlock
	public static final Block ILUM_CHISELED_BRICKS = createBlock("chiseled_ilum_stone_bricks", AbstractBlock.Settings.create().strength(1.5F).requiresTool());
	/// SANDSTONE
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final StoneProducts DESERT_SANDSTONE = new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), "desert_sandstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block DUNESTONE = createBlock("dunestone", AbstractBlock.Settings.create().strength(1.25F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block SMOOTH_DESERT_SANDSTONE = createBlock("smooth_desert_sandstone", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block POLISHED_DESERT_SANDSTONE = createBlock("polished_desert_sandstone", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CHISELED_DESERT = createBlock("chiseled_desert_sandstone", AbstractBlock.Settings.create().strength(0.5F));
	/// SAND
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final ColoredFallingBlock DESERT_SAND = createFallingBlock("desert_sand", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F), new ColorCode(0xFFEDBB8A));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final ColoredFallingBlock PIT_SAND = createFallingBlock("pit_sand", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F), new ColorCode(0xFFEAC795));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final ColoredFallingBlock FINE_SAND = createFallingBlock("fine_sand", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F), new ColorCode(0xFFE9C490));
	@DataGenBlock(model =  DataGenBlockModel.Accumulating)
	public static final AccumulatingBlock LOOSE_DESERT_SAND = createAccumulatingBlock("loose_desert_sand", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F), DESERT_SAND);
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final ColoredFallingBlock CANYON_SAND = createFallingBlock("canyon_sand", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F), new ColorCode(0xFFC59572));
	/// SALT
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock, rotation = DGBlockRotation.RandomRotationX)
	public static final Block CAKED_SALT = createBlock("caked_salt", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F));
	/// GRAVEL
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock, rotation = DGBlockRotation.RandomRotationX)
	public static final FallingBlock JUNDLAND_GRAVEL = createFallingBlock("jundland_gravel", AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).strength(0.5F), new ColorCode(0xFF7A5346));
	/// DIRT
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final ReducedDryingStoneProducts RUINED_WET_POURSTONE = new ReducedDryingStoneProducts(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).noCollision().strength(0.5F), "ruined_wet_pourstone", CRACKED_POURSTONE.block, 10, new ColorCode(0xFF986A39));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final ReducedDryingRuiningStoneProducts  WET_POURSTONE = new ReducedDryingRuiningStoneProducts(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).strength(0.5F).noCollision(), "wet_pourstone", POURSTONE.block, RUINED_WET_POURSTONE.block, 10, new ColorCode(0xFF9E6E3B));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock, rotation = DGBlockRotation.RandomRotationX, tags = {DGBlockTag.PickaxeMineable, DGBlockTag.DeadBushSubstrate})
	public static final Block DESERT_LOAM = createBlock("desert_loam", AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).strength(0.5F));

	// TODO: Implement tree & plants
	/// Tree

	@ServerBlockRegistryData(fireBurn = 30, fireSpread = 60)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CutoutMipped)
	@DataGenBlock(tags = DGBlockTag.Leaves, itemTags = DGItemTag.Leaves )
	public static final LeavesBlock SEQUOIA_LEAVES = createLeavesBlock("sequoia_leaves");

	@DataGenBlock(model = DataGenBlockModel.None)
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	public static final Block SEQUOIA_WOOD = createWoodBlock("sequoia_wood", MapColor.BROWN);

	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DGBlockRotation.AxisRotated, model = DataGenBlockModel.LogWithWood, tags = { DGBlockTag.Logs }, itemTags = { DGItemTag.Logs })
	public static final PillarBlock SEQUOIA_LOG = createLogBlock("sequoia_log", MapColor.OAK_TAN, MapColor.BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DGBlockRotation.AxisRotated, model = DataGenBlockModel.Log, tags = { DGBlockTag.Logs }, itemTags = { DGItemTag.Logs })
	public static final PillarBlock STRIPPED_SEQUOIA_LOG = createLogBlock("stripped_sequoia_log", MapColor.OAK_TAN, MapColor.BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DGBlockRotation.AxisRotated, model = DataGenBlockModel.Log, tags = { DGBlockTag.Logs }, itemTags = { DGItemTag.Logs })
	public static final PillarBlock MOSSY_SEQUOIA_LOG = createLogBlock("mossy_sequoia_log", MapColor.OAK_TAN, MapColor.BROWN);
	@DataGenBlock
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 20)
	public static final WoodProducts SEQUOIA_PRODUCTS = new WoodProducts("sequoia", AbstractBlock.Settings.create().strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
	//TODO: Add Japor leaves

	@DataGenBlock(model = DataGenBlockModel.None)
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	public static final Block JAPOR_WOOD = createWoodBlock("japor_wood", MapColor.BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DGBlockRotation.AxisRotated, model = DataGenBlockModel.LogWithWood, tags = DGBlockTag.Logs, itemTags = DGItemTag.Logs)
	public static final PillarBlock JAPOR_LOG = createLogBlock("japor_log", MapColor.OAK_TAN, MapColor.BROWN);
	@DataGenBlock
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 20)
	public static final WoodProducts JAPOR_PRODUCTS = new WoodProducts("japor", AbstractBlock.Settings.create().strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));

	@DataGenBlock(model = DataGenBlockModel.None)
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	public static final Block TATOOINE_WOOD = createWoodBlock("tatooine_wood", MapColor.BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DGBlockRotation.AxisRotated, model = DataGenBlockModel.LogWithWood, tags = DGBlockTag.Logs, itemTags = DGItemTag.Logs)
	public static final PillarBlock TATOOINE_LOG = createLogBlock("tatooine_log", MapColor.OAK_TAN, MapColor.BROWN);


	/// Ores
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block BESKAR_ORE = createBlock("beskar_ore", AbstractBlock.Settings.create().strength(5.0F).requiresTool());
	@DataGenBlock
	public static final Block BESKAR_BLOCK = createBlock("beskar_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CHROMIUM_ORE = createBlock("chromium_ore", AbstractBlock.Settings.create().strength(3.0F).requiresTool());
	@DataGenBlock
	public static final Block CHROMIUM_BLOCK = createBlock("chromium_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(3.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CORTOSIS_ORE = createBlock("cortosis_ore", AbstractBlock.Settings.create().strength(5.0F).requiresTool());
	@DataGenBlock
	public static final Block CORTOSIS_BLOCK = createBlock("cortosis_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block DESH_ORE = createBlock("desh_ore", AbstractBlock.Settings.create().strength(3.0F).requiresTool());
	@DataGenBlock
	public static final Block DESH_BLOCK = createBlock("desh_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(3.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block DIATIUM_ORE = createBlock("diatium_ore", AbstractBlock.Settings.create().strength(5.0F).requiresTool());
	@DataGenBlock(tags = DGBlockTag.BlasterReflect)
	public static final Block DIATIUM_BLOCK = createBlock("diatium_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block IONITE_ORE = createBlock("ionite_ore", AbstractBlock.Settings.create().strength(5.0F).requiresTool());
	@DataGenBlock
	public static final Block IONITE_BLOCK = createBlock("ionite_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).strength(5.0F).luminance(value -> 3).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block LOMMITE_ORE = createBlock("lommite_ore", AbstractBlock.Settings.create().strength(3.0F).requiresTool());
	@DataGenBlock
	public static final Block LOMMITE_BLOCK = createBlock("lommite_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block TITANIUM_ORE = createBlock("titanium_ore", AbstractBlock.Settings.create().strength(4.0F).requiresTool());
	@DataGenBlock
	public static final Block TITANIUM_BLOCK = createBlock("titanium_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block ZERSIUM_ORE = createBlock("zersium_ore", AbstractBlock.Settings.create().strength(3.0F).requiresTool());
	@DataGenBlock
	public static final Block ZERSIUM_BLOCK = createBlock("zersium_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block THORILIDE_ORE = createBlock("thorilide_ore", AbstractBlock.Settings.create().strength(3.0F).requiresTool());
	@DataGenBlock
	public static final Block THORILIDE_BLOCK = createBlock("thorilide_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block HELICITE_ORE = createBlock("helicite_ore", AbstractBlock.Settings.create().strength(3.0F).requiresTool());
	@DataGenBlock
	public static final Block HELICITE_BLOCK = createBlock("helicite_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).strength(5.0F).requiresTool());

	/// COMPOSITE

	@DataGenBlock
	public static final Block DURASTEEL_BLOCK = createBlock("durasteel_block", AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());
	//TODO: implement connecting for PLASTEEL_BLOCK
	@DataGenBlock
	public static final SelfConnectingBlock PLASTEEL_BLOCK = Registrar.block(Galaxies.id("plasteel_block"), SelfConnectingBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

	/// GLASS

	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	@DataGenBlock
	public static final SelfConnectingGlassBlock IMPERIAL_GLASS = createSelfConnectingGlass("imperial_glass");
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock WHITE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("white_stained_imperial_glass", DyeColor.WHITE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock ORANGE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("orange_stained_imperial_glass", DyeColor.ORANGE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock MAGENTA_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("magenta_stained_imperial_glass", DyeColor.MAGENTA);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock LIGHT_BLUE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("light_blue_stained_imperial_glass", DyeColor.LIGHT_BLUE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock YELLOW_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("yellow_stained_imperial_glass", DyeColor.YELLOW);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock LIME_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("lime_stained_imperial_glass", DyeColor.LIME);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock PINK_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("pink_stained_imperial_glass", DyeColor.PINK);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock GRAY_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("gray_stained_imperial_glass", DyeColor.GRAY);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock LIGHT_GRAY_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("light_gray_stained_imperial_glass", DyeColor.LIGHT_GRAY);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock CYAN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("cyan_stained_imperial_glass", DyeColor.CYAN);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock PURPLE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("purple_stained_imperial_glass", DyeColor.PURPLE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock BLUE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("blue_stained_imperial_glass", DyeColor.BLUE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock BROWN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("brown_stained_imperial_glass", DyeColor.BROWN);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock GREEN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("green_stained_imperial_glass", DyeColor.GREEN);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock RED_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("red_stained_imperial_glass", DyeColor.RED);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.Transparent)
	public static final SelfConnectingStainedGlassBlock BLACK_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("black_stained_imperial_glass", DyeColor.BLACK);

	/// PANEL

	private static final AbstractBlock.Settings IMPERIAL_PANEL_SETTINGS = AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool();
	@DataGenBlock
	public static final SelfConnectingBlock RUSTED_METAL = createSelfConnectingBlock("rusted_metal", AbstractBlock.Settings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.COPPER).strength(1.5F).requiresTool());
	// TODO: Implement "imperial cutout" blocks
	@DataGenBlock
	public static final StoneProducts BLACK_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS, "black_imperial_panel_blank");
	@DataGenBlock
	public static final StoneProducts GRAY_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY), "gray_imperial_panel_blank");
	@DataGenBlock
	public static final StoneProducts LIGHT_GRAY_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.LIGHT_GRAY), "light_gray_imperial_panel_blank");
	@DataGenBlock
	public static final StoneProducts WHITE_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS, "white_imperial_panel_blank");
	@DataGenBlock
	public static final Block BLACK_IMPERIAL_PANEL_TILE = createPanel("black_imperial_panel_tile", MapColor.GRAY);
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("black_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("black_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("black_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock GRAY_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("gray_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock GRAY_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("gray_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock GRAY_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("gray_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock IMPERIAL_PANEL_TALL_1 = createSelfConnectingBlock("gray_imperial_tall_panel_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock IMPERIAL_PANEL_TALL_2 = createSelfConnectingBlock("gray_imperial_tall_panel_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	// TODO: Implement connecting lighting panels
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_1 = createLightingPanelBlock("gray_imperial_light_half_1", 13);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_2 = createLightingPanelBlock("gray_imperial_light_half_2", 13);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_3 = createLightingPanelBlock("gray_imperial_light_half_3", 13);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_4 = createLightingPanelBlock("gray_imperial_light_half_4", 13);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_5 = createLightingPanelBlock("gray_imperial_light_half_5", 13);
	@DataGenBlock(model = DataGenBlockModel.None)
	public static final InteractableInvertedLampSlab GRAY_IMPERIAL_LIGHTING_SLAB = createLightingPanelSlab("gray_imperial_lighting_panel_slab", 15, 12);

	/*@RegistryName("gray_imperial_tall_light_1")
	@TarkinBlock(state = TrState.None, model = TrModel.None)
	public static final InteractableConnectingInvertedLampBlock ImperialLightTall1 = createLitConnectingPanel(MapColor.GRAY, 14);
	@RegistryName("gray_imperial_tall_light_2")
	@TarkinBlock(state = TrState.None, model = TrModel.None)
	public static final InteractableConnectingInvertedLampBlock ImperialLightTall2 = createLitConnectingPanel(MapColor.GRAY, 14);*/
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_PANEL_1 = createLightingPanelBlock("gray_imperial_light_panel_1", 11);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_PANEL_2 = createLightingPanelBlock("gray_imperial_light_panel_2", 9);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_PANEL_3 = createLightingPanelBlock("gray_imperial_light_panel_3", 14);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_1 = createLightingPanelBlock("gray_imperial_light_1", 15);
	@DataGenBlock(model = DataGenBlockModel.LightingPanel)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_2 = createLightingPanelBlock("gray_imperial_light_2", 15);

	@DataGenBlock
	public static final SelfConnectingBlock LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("light_gray_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("light_gray_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("light_gray_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock WHITE_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("white_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.OFF_WHITE));
	@DataGenBlock
	public static final SelfConnectingBlock WHITE_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("white_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.OFF_WHITE));
	@DataGenBlock
	public static final SelfConnectingBlock WHITE_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("white_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.OFF_WHITE));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_BORDERED = createSelfConnectingBlock("black_imperial_panel_bordered", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SPLIT = createSelfConnectingBlock("black_imperial_panel_split", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_THIN_BORDERED = createSelfConnectingBlock("black_imperial_panel_thin_bordered", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock EXTERNAL_IMPERIAL_PLATING = createSelfConnectingBlock("external_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LARGE_IMPERIAL_PLATING = createSelfConnectingBlock("large_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock RUSTED_LARGE_IMPERIAL_PLATING = createSelfConnectingBlock("rusted_large_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock MOSSY_LARGE_IMPERIAL_PLATING = createSelfConnectingBlock("mossy_large_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LARGE_LIGHT_GRAY_IMPERIAL_PLATING = createSelfConnectingBlock("large_light_gray_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.GRAY));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_A = createNumberedBlocks("black_imperial_panel_pattern_a", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_B = createNumberedBlocks("black_imperial_panel_pattern_b", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_C = createNumberedBlocks("black_imperial_panel_pattern_c", 2, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_D = createNumberedBlocks("black_imperial_panel_pattern_d", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_E = createNumberedBlocks("black_imperial_panel_pattern_e", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.BLACK));
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("gray_imperial_panel_pattern_3", MapColor.GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("light_gray_imperial_panel_pattern_3", MapColor.LIGHT_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("light_gray_imperial_panel_pattern_4", MapColor.LIGHT_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("light_gray_imperial_panel_pattern_5", MapColor.LIGHT_GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("rusted_gray_imperial_panel_pattern_3", MapColor.GRAY);
	//@RegistryName("rusted_gray_imperial_panel_pattern_3_stairs")
	//public static final Block RustedGrayImperialPanelPattern3Stairs = new StairsBlock(RustedGrayImperialPanelPattern3.getDefaultState(), FabricBlockSettings.copy(RustedGrayImperialPanelPattern3));
	//@RegistryName("rusted_gray_imperial_panel_pattern_3_slab")
	//public static final Block RustedGrayImperialPanelPattern3Slab = new VerticalSlabBlock(FabricBlockSettings.copy(RustedGrayImperialPanelPattern3));
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("mossy_gray_imperial_panel_pattern_3", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("gray_imperial_panel_pattern_4", MapColor.GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("rusted_gray_imperial_panel_pattern_4", MapColor.GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("mossy_gray_imperial_panel_pattern_4", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("gray_imperial_panel_pattern_5", MapColor.GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("rusted_gray_imperial_panel_pattern_5", MapColor.GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("mossy_gray_imperial_panel_pattern_5", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_6 = createPanel("gray_imperial_panel_pattern_6", MapColor.GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_6 = createPanel("rusted_gray_imperial_panel_pattern_6", MapColor.GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_6 = createPanel("mossy_gray_imperial_panel_pattern_6", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_7 = createPanel("gray_imperial_panel_pattern_7", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_8 = createPanel("gray_imperial_panel_pattern_8", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_9 = createPanel("gray_imperial_panel_pattern_9", MapColor.GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_9 = createPanel("rusted_gray_imperial_panel_pattern_9", MapColor.GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_9 = createPanel("mossy_gray_imperial_panel_pattern_9", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_10 = createPanel("gray_imperial_panel_pattern_10", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_11 = createPanel("gray_imperial_panel_pattern_11", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_12 = createPanel("gray_imperial_panel_pattern_12", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_13 = createPanel("gray_imperial_panel_pattern_13", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_FLOORING_0 = createPanel("gray_imperial_flooring_0", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_FLOORING_3 = createPanel("gray_imperial_flooring_3", MapColor.GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_FLOORING_4 = createPanel("gray_imperial_flooring_4", MapColor.GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_FLOORING_0 = createPanel("light_gray_imperial_flooring_0", MapColor.GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_FLOORING_3 = createPanel("light_gray_imperial_flooring_3", MapColor.GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_FLOORING_4 = createPanel("light_gray_imperial_flooring_4", MapColor.GRAY);
	@DataGenBlock
	public static final Block BLACK_IMPERIAL_FLOORING_3 = createPanel("black_imperial_flooring_3", MapColor.GRAY);
	@DataGenBlock
	public static final Block BLACK_IMPERIAL_FLOORING_4 = createPanel("black_imperial_flooring_4", MapColor.GRAY);
	@DataGenBlock
	public static final Block WHITE_IMPERIAL_FLOORING_3 = createPanel("white_imperial_flooring_3", MapColor.GRAY);
	@DataGenBlock
	public static final Block WHITE_IMPERIAL_FLOORING_4 = createPanel("white_imperial_flooring_4", MapColor.GRAY);
	@DataGenBlock
	public static final Block IMPERIAL_FLOORING_PATTERN_1 = createPanel("imperial_flooring_pattern_1", MapColor.GRAY);
	@DataGenBlock
	public static final Block IMPERIAL_FLOORING_PATTERN_2 = createPanel("imperial_flooring_pattern_2", MapColor.GRAY);



	@DataGenBlock(itemGroup = DataGenItemGroup.None)
	public static final Block LAB_WALL = createBlock("lab_wall", AbstractBlock.Settings.create());

	/// CRATES
	public static final VoxelShape CRATE_SHAPE = VoxelShapeUtil.getCenteredCube(14, 16);
	public static final AbstractBlock.Settings CORRUGATED_CRATE_SETTINGS = AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F);

	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.Custom)
	public static final Block IMPERIAL_CORRUGATED_CRATE = createCorrugatedCrate("imperial_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.Custom)
	public static final Block MEDICAL_CORRUGATED_CRATE = createCorrugatedCrate("medical_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.Custom)
	public static final Block MINING_CORRUGATED_CRATE = createCorrugatedCrate("mining_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.Custom)
	public static final DyedBlocks CORRUGATED_CRATE = new DyedBlocks(color -> createCorrugatedCrate(color.name().toLowerCase() + "_corrugated_crate"));

	private static Block createBlock(String key, AbstractBlock.Settings settings)
	{
		return Registrar.block(Galaxies.id(key), Block::new, settings);
	}
	private static PillarBlock createLogBlock(String key, MapColor topMapColor, MapColor sideMapColor)
	{
		return Registrar.block(Galaxies.id(key), PillarBlock::new, AbstractBlock.Settings.create().mapColor(blockState -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(2.0F).sounds(BlockSoundGroup.WOOD));
	}

	private static PillarBlock createWoodBlock(String key, MapColor mapColor)
	{
		return createLogBlock(key, mapColor, mapColor);
	}
	private static LeavesBlock createLeavesBlock(String key)
	{
		return Registrar.block(Galaxies.id(key), settings -> new TintedParticleLeavesBlock(0.01F, settings), AbstractBlock.Settings.create().strength(0.2F).sounds(BlockSoundGroup.GRASS).nonOpaque().suffocates(BlockUtil::never).blockVision(BlockUtil::never));
	}
	private static AccumulatingBlock createAccumulatingBlock(String key, AbstractBlock.Settings settings, Block fullBlock){
		return Registrar.block(Galaxies.id(key), blockSettings -> new AccumulatingBlock(settings, fullBlock::getPlacementState), settings);
	}
	private static InteractableInvertedLampBlock createLightingPanelBlock(String key, int luminosity)
	{
		return Registrar.block(Galaxies.id(key), InteractableInvertedLampBlock::new, IMPERIAL_PANEL_SETTINGS.luminance(createLightLevelFromLit(luminosity)));
	}

	public static ToIntFunction<BlockState> createLightLevelFromLit(int litLevel)
	{
		return state -> state.contains(Properties.LIT) && state.get(Properties.LIT) ? litLevel : 0;
	}

	public static ToIntFunction<BlockState> createLightLevelFromLitAndSlab(int litLevelSingle, int litLevelDouble)
	{
		return state -> state.contains(Properties.LIT) && state.get(Properties.LIT) ? (state.contains(Properties.SLAB_TYPE) && state.get(Properties.SLAB_TYPE) == SlabType.DOUBLE ? litLevelDouble : litLevelSingle) : 0;
	}

	private static InteractableInvertedLampSlab createLightingPanelSlab(String key, int luminositySingle, int luminosityDouble)
	{
		return Registrar.block(Galaxies.id(key), InteractableInvertedLampSlab::new, IMPERIAL_PANEL_SETTINGS.luminance(createLightLevelFromLitAndSlab(luminositySingle, luminosityDouble)));
	}

	private static NumberedBlocks createNumberedBlocks(String key, int count, AbstractBlock.Settings settings)
	{
		return new NumberedBlocks(count, integer -> Registrar.block(Galaxies.id(key + "_" + integer), Block::new, settings));
	}

	private static WaterloggableRotatingBlockWithBoundsGuiEntity createCorrugatedCrate(String key)
	{
		return Registrar.block(Galaxies.id(key), blockSettings -> new WaterloggableRotatingBlockWithBoundsGuiEntity(CRATE_SHAPE, blockSettings, CrateCorrugatedBlockEntity::new), CORRUGATED_CRATE_SETTINGS);
	}
	private static SelfConnectingBlock createSelfConnectingBlock(String key, AbstractBlock.Settings settings)
	{
		return Registrar.block(Galaxies.id(key), SelfConnectingBlock::new, settings);
	}

	private static RuiningDryingBlock createRuiningDryingBlock(String key, AbstractBlock.Settings blockSettings, int transitionTime, Block dryingTarget, Block ruinedBlock, ColorCode colorCode)
	{
		return Registrar.block(Galaxies.id(key), settings -> new RuiningDryingBlock(dryingTarget, transitionTime, () -> ruinedBlock, settings, colorCode), blockSettings);
	}

	private static ColoredFallingBlock createFallingBlock(String key, AbstractBlock.Settings blockSettings, ColorCode colorCode)
	{
		return Registrar.block(Galaxies.id(key), settings -> new ColoredFallingBlock(colorCode, settings), blockSettings);
	}

	private static VerticalSlabBlock createSlab(String key, AbstractBlock.Settings settings)
	{
		return Registrar.block(Galaxies.id(key), VerticalSlabBlock::new, settings);
	}


	private static SelfConnectingGlassBlock createSelfConnectingGlass(String key)
	{
		return Registrar.block(Galaxies.id(key), SelfConnectingGlassBlock::new, AbstractBlock.Settings.create().strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never));
	}

	private static SelfConnectingStainedGlassBlock createSelfConnectingStainedGlass(String key, DyeColor color)
	{
		return Registrar.block(Galaxies.id(key), settings -> new SelfConnectingStainedGlassBlock(color, settings), AbstractBlock.Settings.create().strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never));
	}

	private static Block createPanel(String key, MapColor topMapColor)
	{
		return createBlock(key, AbstractBlock.Settings.create().strength(1.5F).requiresTool().sounds(BlockSoundGroup.COPPER).mapColor(topMapColor));
	}
	public static void register()
	{

		AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(ServerBlockRegistryData.class, GalaxiesBlocks::registerServerDataBlock);

	}
	private static void registerServerDataBlock(Block block, ServerBlockRegistryData serverData)
	{
		if(serverData.fireBurn() != 0 || serverData.fireSpread() !=0)
			FlammableBlockRegistry.getDefaultInstance().add(block, serverData.fireBurn(), serverData.fireSpread());
	}

}
