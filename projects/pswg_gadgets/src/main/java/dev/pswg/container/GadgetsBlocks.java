package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.block.*;
import dev.pswg.blockEntity.CrateCorrugatedBlockEntity;
import dev.pswg.blockEntity.WaterloggableRotatingBlockWithBoundsGuiEntity;
import dev.pswg.datagen.DataGenBlock;
import dev.pswg.datagen.DataGenBlockModel;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.feature.scrapping.ScrappingTableBlock;
import dev.pswg.registry.Registrar;
import dev.pswg.util.BlockUtil;
import net.minecraft.block.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;
import net.minecraft.util.DyeColor;
import net.minecraft.util.shape.VoxelShape;

public class GadgetsBlocks
{
	public static final class Tags
	{
		public static final TagKey<Block> FRAGMENTATION_GRENADE_DESTROY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("fragmentation_destroy"));
		public static final TagKey<Block> DETONATES_GRENADE = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("detonates_grenade"));
		public static final TagKey<Block> BOUNCY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("bouncy"));
		public static final TagKey<Block> GAS_PASS_THROUGH = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("gas_pass_through"));
		public static final TagKey<Block> INFERNO_CHAR = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("inferno_char"));
		public static final TagKey<Block> INFERNO_DESTROY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("inferno_destroy"));
	}

	@DataGenBlock(model = DataGenBlockModel.None)
	public static final ThermalDetonatorBlock THERMAL_DETONATOR_BLOCK = Registrar.block(Gadgets.id("thermal_detonator_block"), ThermalDetonatorBlock::new, Block.Settings.create());
	@DataGenBlock(model = DataGenBlockModel.None)
	public static final FragmentationGrenadeBlock FRAGMENTATION_GRENADE_BLOCK = Registrar.block(Gadgets.id("fragmentation_grenade_block"), FragmentationGrenadeBlock::new, Block.Settings.create());

	@DataGenBlock
	public static final FertileDirt FERTILE_DIRT_BLOCK = Registrar.block(Gadgets.id("fertile_dirt"), FertileDirt::new, Block.Settings.create().ticksRandomly());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CHARRED_BLOCK = Registrar.block(Gadgets.id("charred_block"), Block::new, AbstractBlock.Settings.create().dropsNothing().breakInstantly());

	@DataGenBlock(model = DataGenBlockModel.None)
	public static final Block SCRAPPING_TABLE_BLOCK = Registrar.block(Gadgets.id("scrapping_table"), ScrappingTableBlock::new, AbstractBlock.Settings.create());
	//public static final StairsBlock CHARRED_STAIRS = Registrar.block(Gadgets.id("charred_stairs"), Block::new, Block.Settings.create().dropsNothing().breakInstantly());

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
	public static final SelfConnectingBlock DURASTEEL_CONNECTING_POURSTONE = Registrar.block(Gadgets.id("durasteel_bordered_pourstone"), SelfConnectingBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).strength(1.5F).requiresTool());
	@DataGenBlock
	public static final StoneProducts MASSASSI = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "massassi_stone");
	@DataGenBlock
	public static final Block MASSASSI_SMOOTH = createBlock("smooth_massassi_stone", AbstractBlock.Settings.create().strength(1.5F).requiresTool());
	@DataGenBlock
	public static final VerticalSlabBlock MASSASSI_SMOOTH_SLAB = createSlab("smooth_massassi_stone_slab", AbstractBlock.Settings.copy(MASSASSI_SMOOTH));
	@DataGenBlock
	public static final StoneProducts MASSASSI_BRICKS = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "massassi_stone_bricks");
	@DataGenBlock
	public static final Block MASSASSI_CHISELED_BRICKS = createBlock("chiseled_massassi_stone_bricks", AbstractBlock.Settings.create().strength(1.5F).requiresTool());
	@DataGenBlock
	public static final Block MOSSY_MASSASSI_SMOOTH = createBlock("mossy_smooth_massassi_stone", AbstractBlock.Settings.create().strength(2F).requiresTool());
	@DataGenBlock
	public static final VerticalSlabBlock MOSSY_MASSASSI_SMOOTH_SLAB = createSlab("mossy_smooth_massassi_stone_slab", AbstractBlock.Settings.copy(MOSSY_MASSASSI_SMOOTH));
	@DataGenBlock
	public static final StoneProducts MOSSY_MASSASSI_BRICKS = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "mossy_massassi_stone_bricks");
	@DataGenBlock
	public static final StoneProducts ILUM = new StoneProducts(AbstractBlock.Settings.create().strength(1.5F).requiresTool(), "ilum_stone");
	@DataGenBlock
	public static final Block ILUM_SMOOTH = createBlock("smooth_ilum_stone", AbstractBlock.Settings.create().strength(2.0F).requiresTool());
	@DataGenBlock
	public static final VerticalSlabBlock ILUM_SMOOTH_SLAB = createSlab("smooth_ilum_stone_slab", AbstractBlock.Settings.copy(ILUM_SMOOTH));
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
	// TODO: implement & add Datagen for "LOOSE_DESERT_SAND"
	//@RegistryName("loose_desert_sand")
	//public static final Block LooseDesert = new AccumulatingBlock(FabricBlockSettings.create().sounds(BlockSoundGroup.SAND).strength(0.5F), Desert::getPlacementState);
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final ColoredFallingBlock CANYON_SAND = createFallingBlock("canyon_sand", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F), new ColorCode(0xFFC59572));
	/// SALT
	// TODO: implement "state = TrState.RandomRotation" for "CAKED_SALT"
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CAKED_SALT = createBlock("caked_salt", AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.5F));
	/// GRAVEL
	// TODO: implement "state = TrState.RandomRotation" for "JUNDLAND_GRAVEL"
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final FallingBlock JUNDLAND_GRAVEL = createFallingBlock("jundland_gravel", AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).strength(0.5F), new ColorCode(0xFF7A5346));
	/// DIRT
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final DryingBlock RUINED_WET_POURSTONE = Registrar.block(Gadgets.id("ruined_wet_pourstone"), settings -> new DryingBlock(CRACKED_POURSTONE.block, 10, settings, new ColorCode(0xFF986A39)), AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).noCollision().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final RuiningDryingBlock WET_POURSTONE = createRuiningDryingBlock("wet_pourstone", AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).strength(0.5F).noCollision(), 10, POURSTONE.block, RUINED_WET_POURSTONE, new ColorCode(0xFF9E6E3B));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final DryingStairsBlock RUINED_WET_POURSTONE_STAIRS = Registrar.block(Gadgets.id("ruined_wet_pourstone_stairs"), settings -> new DryingStairsBlock(WET_POURSTONE.getDefaultState(), CRACKED_POURSTONE.stairs, 10, settings), AbstractBlock.Settings.copy(WET_POURSTONE));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final RuiningDryingStairsBlock WET_POURSTONE_STAIRS = Registrar.block(Gadgets.id("wet_pourstone_stairs"), settings -> new RuiningDryingStairsBlock(WET_POURSTONE.getDefaultState(), POURSTONE.stairs, 10, () -> RUINED_WET_POURSTONE_STAIRS, settings), AbstractBlock.Settings.copy(WET_POURSTONE));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final DryingSlabBlock RUINED_WET_POURSTONE_SLAB = Registrar.block(Gadgets.id("ruined_wet_pourstone_slab"), settings -> new DryingSlabBlock(CRACKED_POURSTONE.slab, 10, settings), AbstractBlock.Settings.copy(WET_POURSTONE));
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final RuiningDryingSlabBlock WET_POURSTONE_SLAB = Registrar.block(Gadgets.id("wet_pourstone_slab"), settings -> new RuiningDryingSlabBlock(POURSTONE.slab, 10, () -> RUINED_WET_POURSTONE_SLAB, settings), AbstractBlock.Settings.copy(WET_POURSTONE));
	// TODO: implement "state = TrState.RandomRotation" & tags = { TrBlockTag.PickaxeMineable, TrBlockTag.DeadBushSubstrate } for "DESERT_LOAM"
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block DESERT_LOAM = createBlock("desert_loam", AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRAVEL).strength(0.5F));

	// TODO: Implement tree & plant
	/// Ores
	@DataGenBlock
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
	@DataGenBlock
	// TODO: Implement @TarkinBlock(tags = { TrBlockTag.BlasterReflect })
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
	public static final SelfConnectingBlock PLASTEEL_BLOCK = Registrar.block(Gadgets.id("plasteel_block"), SelfConnectingBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(5.0F).requiresTool());

	/// GLASS

	@DataGenBlock
	public static final SelfConnectingGlassBlock IMPERIAL_GLASS = createSelfConnectingGlass("imperial_glass");
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock WHITE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("white_stained_imperial_glass", DyeColor.WHITE);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock ORANGE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("orange_stained_imperial_glass", DyeColor.ORANGE);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock MAGENTA_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("magenta_stained_imperial_glass", DyeColor.MAGENTA);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock LIGHT_BLUE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("light_blue_stained_imperial_glass", DyeColor.LIGHT_BLUE);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock YELLOW_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("yellow_stained_imperial_glass", DyeColor.YELLOW);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock LIME_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("lime_stained_imperial_glass", DyeColor.LIME);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock PINK_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("pink_stained_imperial_glass", DyeColor.PINK);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock GRAY_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("gray_stained_imperial_glass", DyeColor.GRAY);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock LIGHT_GRAY_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("light_gray_stained_imperial_glass", DyeColor.LIGHT_GRAY);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock CYAN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("cyan_stained_imperial_glass", DyeColor.CYAN);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock PURPLE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("purple_stained_imperial_glass", DyeColor.PURPLE);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock BLUE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("blue_stained_imperial_glass", DyeColor.BLUE);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock BROWN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("brown_stained_imperial_glass", DyeColor.BROWN);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock GREEN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("green_stained_imperial_glass", DyeColor.GREEN);
	@DataGenBlock
	public static final SelfConnectingStainedGlassBlock RED_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("red_stained_imperial_glass", DyeColor.RED);
	@DataGenBlock
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

	// TODO: Implement lighting panels
	/*@RegistryName("gray_imperial_tall_light_1")
	@TarkinBlock(state = TrState.None, model = TrModel.None)
	public static final InteractableConnectingInvertedLampBlock IMPERIAL_LIGHT_TALL_1 = createLitConnectingPanel(MapColor.GRAY, 14);
	@RegistryName("gray_imperial_tall_light_2")
	@TarkinBlock(state = TrState.None, model = TrModel.None)
	public static final InteractableConnectingInvertedLampBlock IMPERIAL_LIGHT_TALL_2 = createLitConnectingPanel(MapColor.GRAY, 14);
	@RegistryName("gray_imperial_light_panel_1")
	public static final Block GRAY_IMPERIAL_LIGHT_PANEL_1 = createLightingPanelBlock(11);
	@RegistryName("gray_imperial_light_panel_2")
	public static final Block GRAY_IMPERIAL_LIGHT_PANEL_2 = createLightingPanelBlock(9);
	@RegistryName("gray_imperial_light_panel_3")
	public static final Block GRAY_IMPERIAL_LIGHT_PANEL_3 = createLightingPanelBlock(14);
	@RegistryName("gray_imperial_light_1")
	public static final Block GRAY_IMPERIAL_LIGHT_1 = createLightingPanelBlock(15);
	@RegistryName("gray_imperial_light_2")
	public static final Block GRAY_IMPERIAL_LIGHT_2 = createLightingPanelBlock(15);*/

	@DataGenBlock(itemGroup = DataGenItemGroup.None)
	public static final Block LAB_WALL = createBlock("lab_wall", AbstractBlock.Settings.create());

	/// CRATES
	public static final VoxelShape CRATE_SHAPE = VoxelShapeUtil.getCenteredCube(14, 16);
	public static final AbstractBlock.Settings CORRUGATED_CRATE_SETTINGS = AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).nonOpaque().strength(2.5F);

	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.DataGenModel)
	public static final Block IMPERIAL_CORRUGATED_CRATE = createCorrugatedCrate("imperial_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.DataGenModel)
	public static final Block MEDICAL_CORRUGATED_CRATE = createCorrugatedCrate("medical_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.DataGenModel)
	public static final Block MINING_CORRUGATED_CRATE = createCorrugatedCrate("mining_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.DataGenModel)
	public static final DyedBlocks CORRUGATED_CRATE = new DyedBlocks(color -> createCorrugatedCrate(color.name().toLowerCase() + "_corrugated_crate"));

	public static Block createBlock(String key, AbstractBlock.Settings settings)
	{
		return Registrar.block(Gadgets.id(key), Block::new, settings);
	}

	public static WaterloggableRotatingBlockWithBoundsGuiEntity createCorrugatedCrate(String key)
	{
		return Registrar.block(Gadgets.id(key), blockSettings -> new WaterloggableRotatingBlockWithBoundsGuiEntity(CRATE_SHAPE, blockSettings, CrateCorrugatedBlockEntity::new), CORRUGATED_CRATE_SETTINGS);
	}
	public static SelfConnectingBlock createSelfConnectingBlock(String key, AbstractBlock.Settings settings)
	{
		return Registrar.block(Gadgets.id(key), SelfConnectingBlock::new, settings);
	}

	public static RuiningDryingBlock createRuiningDryingBlock(String key, AbstractBlock.Settings blockSettings, int transitionTime, Block dryingTarget, Block ruinedBlock, ColorCode colorCode)
	{
		return Registrar.block(Gadgets.id(key), settings -> new RuiningDryingBlock(dryingTarget, transitionTime, () -> ruinedBlock, settings, colorCode), blockSettings);
	}

	public static ColoredFallingBlock createFallingBlock(String key, AbstractBlock.Settings blockSettings, ColorCode colorCode)
	{
		return Registrar.block(Gadgets.id(key), settings -> new ColoredFallingBlock(colorCode, settings), blockSettings);
	}

	public static VerticalSlabBlock createSlab(String key, AbstractBlock.Settings settings)
	{
		return Registrar.block(Gadgets.id(key), VerticalSlabBlock::new, settings);
	}
	public static void register()
	{
	}

	private static SelfConnectingGlassBlock createSelfConnectingGlass(String key)
	{
		return Registrar.block(Gadgets.id(key), SelfConnectingGlassBlock::new, AbstractBlock.Settings.create().strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never));
	}

	private static SelfConnectingStainedGlassBlock createSelfConnectingStainedGlass(String key, DyeColor color)
	{
		return Registrar.block(Gadgets.id(key), settings -> new SelfConnectingStainedGlassBlock(color, settings), AbstractBlock.Settings.create().strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(BlockUtil::never).solidBlock(BlockUtil::never).suffocates(BlockUtil::never).blockVision(BlockUtil::never));
	}

	private static Block createPanel(String key, MapColor topMapColor)
	{
		return createBlock(key, AbstractBlock.Settings.create().strength(1.5F).requiresTool().sounds(BlockSoundGroup.COPPER).mapColor(topMapColor));
	}

}
