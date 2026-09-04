package dev.pswg.container;

import dev.pswg.Galaxies;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.autoreg.ClientBlockRegistryData;
import dev.pswg.autoreg.ServerBlockRegistryData;
import dev.pswg.block.*;
import dev.pswg.block.collection.*;
import dev.pswg.block.plant.*;
import dev.pswg.blockEntity.CrateCorrugatedBlockEntity;
import dev.pswg.blockEntity.WaterloggableRotatingBlockWithBoundsGuiEntity;
import dev.pswg.datagen.*;
import dev.pswg.registry.Registrar;
import dev.pswg.util.BlockUtil;
import dev.pswg.util.VoxelShapeUtil;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.function.ToIntFunction;

public class GalaxiesBlocks
{
	public static final class Tags
	{
		public static final TagKey<Block> BOUNCY = TagKey.create(Registries.BLOCK, Galaxies.id("bouncy"));
		public static final TagKey<Block> SOFT = TagKey.create(Registries.BLOCK, Galaxies.id("soft"));
		public static final TagKey<Block> BUSH_PLACEABLE = TagKey.create(Registries.BLOCK, Galaxies.id("bush_placeable"));
		public static final TagKey<Block> ARID_PLANT_PLACEABLE = TagKey.create(Registries.BLOCK, Galaxies.id("arid_plant_placeable"));
	}
	/// STONE
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final StoneProducts CANYON = new StoneProducts(BlockBehaviour.Properties.of().strength(0.5F), "canyon_stone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block CANYON_BRICKS = createBlock("canyon_stone_bricks", BlockBehaviour.Properties.of().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block POLISHED_CANYON = createBlock("polished_canyon_stone", BlockBehaviour.Properties.of().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block CHISELED_CANYON = createBlock("chiseled_canyon_stone", BlockBehaviour.Properties.of().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final StoneProducts CANYON_COBBLE = new StoneProducts(BlockBehaviour.Properties.of().strength(1.25F).requiresCorrectToolForDrops(), "canyon_cobblestone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final StoneProducts POURSTONE = new StoneProducts(BlockBehaviour.Properties.of().strength(1.25F).requiresCorrectToolForDrops(), "pourstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final StoneProducts SMOOTH_POURSTONE = new StoneProducts(BlockBehaviour.Properties.of().strength(1.25F).requiresCorrectToolForDrops(), "smooth_pourstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final StoneProducts CRACKED_POURSTONE = new StoneProducts(BlockBehaviour.Properties.of().strength(1.0F).requiresCorrectToolForDrops(), "cracked_pourstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final DyedStoneProducts DYED_POURSTONE = new DyedStoneProducts(color -> new StoneProducts(BlockBehaviour.Properties.of().strength(1.25F).requiresCorrectToolForDrops(), color.name().toLowerCase() + "_pourstone"));
	@DataGenBlock
	// TODO: find a way implement "connecting" blocks
	public static final SelfConnectingBlock DURASTEEL_CONNECTING_POURSTONE = Registrar.block(Galaxies.id("durasteel_bordered_pourstone"), SelfConnectingBlock::new, BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1.5F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final StoneProducts MASSASSI = new StoneProducts(BlockBehaviour.Properties.of().strength(1.5F).requiresCorrectToolForDrops(), "massassi_stone");
	@DataGenBlock
	public static final ReducedStoneProducts MASSASSI_SMOOTH = new ReducedStoneProducts(BlockBehaviour.Properties.of().strength(2F).requiresCorrectToolForDrops(), "smooth_massassi_stone");
	@DataGenBlock
	public static final StoneProducts MASSASSI_BRICKS = new StoneProducts(BlockBehaviour.Properties.of().strength(1.5F).requiresCorrectToolForDrops(), "massassi_stone_bricks");
	@DataGenBlock
	public static final ReducedStoneProducts MOSSY_MASSASSI_SMOOTH = new ReducedStoneProducts(BlockBehaviour.Properties.of().strength(2F).requiresCorrectToolForDrops(), "mossy_smooth_massassi_stone");
	@DataGenBlock
	public static final StoneProducts MOSSY_MASSASSI_BRICKS = new StoneProducts(BlockBehaviour.Properties.of().strength(1.5F).requiresCorrectToolForDrops(), "mossy_massassi_stone_bricks");
	@DataGenBlock
	public static final StoneProducts ILUM = new StoneProducts(BlockBehaviour.Properties.of().strength(1.5F).requiresCorrectToolForDrops(), "ilum_stone");
	@DataGenBlock
	public static final ReducedStoneProducts ILUM_SMOOTH = new ReducedStoneProducts(BlockBehaviour.Properties.of().strength(2.0F).requiresCorrectToolForDrops(), "smooth_ilum_stone");
	@DataGenBlock
	public static final StoneProducts ILUM_BRICKS = new StoneProducts(BlockBehaviour.Properties.of().strength(1.5F).requiresCorrectToolForDrops(), "ilum_stone_bricks");
	@DataGenBlock
	public static final Block ILUM_CHISELED_BRICKS = createBlock("chiseled_ilum_stone_bricks", BlockBehaviour.Properties.of().strength(1.5F).requiresCorrectToolForDrops());
	/// SANDSTONE
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final StoneProducts DESERT_SANDSTONE = new StoneProducts(BlockBehaviour.Properties.of().strength(1.25F).requiresCorrectToolForDrops(), "desert_sandstone");
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block DUNESTONE = createBlock("dunestone", BlockBehaviour.Properties.of().strength(1.25F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block SMOOTH_DESERT_SANDSTONE = createBlock("smooth_desert_sandstone", BlockBehaviour.Properties.of().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block POLISHED_DESERT_SANDSTONE = createBlock("polished_desert_sandstone", BlockBehaviour.Properties.of().strength(0.5F));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block CHISELED_DESERT = createBlock("chiseled_desert_sandstone", BlockBehaviour.Properties.of().strength(0.5F));
	/// SAND
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final ColoredFallingBlock DESERT_SAND = createFallingBlock("desert_sand", BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(0.5F), new ColorRGBA(0xFFEDBB8A));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final ColoredFallingBlock PIT_SAND = createFallingBlock("pit_sand", BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(0.5F), new ColorRGBA(0xFFEAC795));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final ColoredFallingBlock FINE_SAND = createFallingBlock("fine_sand", BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(0.5F), new ColorRGBA(0xFFE9C490));
	@DataGenBlock(model = DataGenBlockModel.ACCUMULATING)
	public static final AccumulatingBlock LOOSE_DESERT_SAND = createAccumulatingBlock("loose_desert_sand", BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(0.5F), DESERT_SAND);
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final ColoredFallingBlock CANYON_SAND = createFallingBlock("canyon_sand", BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(0.5F), new ColorRGBA(0xFFC59572));
	/// SALT
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, rotation = DataGenBlockRotation.RANDOM_ROTATION_X, blockTags = { DataGenBlockTag.BUSH_PLACEABLE, DataGenBlockTag.ARID_PLANT_PLACEABLE })
	public static final Block CAKED_SALT = createBlock("caked_salt", BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(0.5F));
	/// GRAVEL
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, rotation = DataGenBlockRotation.RANDOM_ROTATION_X)
	public static final FallingBlock JUNDLAND_GRAVEL = createFallingBlock("jundland_gravel", BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(0.5F), new ColorRGBA(0xFF7A5346));
	/// DIRT
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final ReducedDryingStoneProducts RUINED_WET_POURSTONE = new ReducedDryingStoneProducts(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).noCollision().strength(0.5F), "ruined_wet_pourstone", CRACKED_POURSTONE.block, 10, new ColorRGBA(0xFF986A39));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final ReducedDryingRuiningStoneProducts WET_POURSTONE = new ReducedDryingRuiningStoneProducts(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(0.5F).noCollision(), "wet_pourstone", POURSTONE.block, RUINED_WET_POURSTONE.block, 10, new ColorRGBA(0xFF9E6E3B));
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, rotation = DataGenBlockRotation.RANDOM_ROTATION_X, blockTags = { DataGenBlockTag.PICKAXE_MINEABLE, DataGenBlockTag.DEAD_BUSH_SUBSTRATE, DataGenBlockTag.BUSH_PLACEABLE, DataGenBlockTag.ARID_PLANT_PLACEABLE })
	public static final Block DESERT_LOAM = createBlock("desert_loam", BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(0.5F));

	/// Plants
	//TODO: vaporator mushroom colony & molo shrub

	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final AridPlant FUNNEL_FLOWER = createAridPlantBlock("funnel_flower");
	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final AridPlant BLOSSOMING_FUNNEL_FLOWER = createAridPlantBlock("blossoming_funnel_flower");
	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final AridPlant POONTEN_GRASS = createAridPlantBlock("poonten_grass");
	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final AridPlant DRIED_POONTEN_GRASS = createAridPlantBlock("dried_poonten_grass");
	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final AridPlant TUBER_STALK = createAridPlantBlock("tuber_stalk");
	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROP_AGE_2, itemGroup = DataGenItemGroup.NONE, addItemTranslation = false)
	public static final ChasukaCrop CHASUKA = Registrar.blockWithoutItem(Galaxies.id("chasuka"), ChasukaCrop::new, BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));
	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS_AGE_3, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final HkakBushBlock HKAK_BUSH = Registrar.block(Galaxies.id("hkak_bush"), HkakBushBlock::new, BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));
	@ServerBlockRegistryData(fireBurn = 60, fireSpread = 100)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS_AGE_3_BLOOMING, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final MoloShrubBlock MOLO_SHRUB = Registrar.block(Galaxies.id("molo_shrub"), MoloShrubBlock::new, BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.CROSS, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final VaporatorMushroomBlock VAPORATOR_MUSHROOM_COLONY = Registrar.block(Galaxies.id("vaporator_mushroom_colony"), VaporatorMushroomBlock::new, BlockBehaviour.Properties.of().noCollision().instabreak().sound(SoundType.GRASS));

	/// Tree

	@ServerBlockRegistryData(fireBurn = 30, fireSpread = 60)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(blockTags = DataGenBlockTag.LEAVES, itemTags = DataGenItemTag.LEAVES, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final LeavesBlock SEQUOIA_LEAVES = createLeavesBlock("sequoia_leaves");

	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(model = DataGenBlockModel.NONE, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block SEQUOIA_WOOD = createWoodBlock("sequoia_wood", MapColor.COLOR_BROWN);

	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DataGenBlockRotation.AXIS_ROTATED, model = DataGenBlockModel.LOG_WITH_WOOD, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = { DataGenBlockTag.LOGS }, itemTags = { DataGenItemTag.LOGS })
	public static final RotatedPillarBlock SEQUOIA_LOG = createLogBlock("sequoia_log", MapColor.WOOD, MapColor.COLOR_BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DataGenBlockRotation.AXIS_ROTATED, model = DataGenBlockModel.LOG, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = { DataGenBlockTag.LOGS }, itemTags = { DataGenItemTag.LOGS })
	public static final RotatedPillarBlock STRIPPED_SEQUOIA_LOG = createLogBlock("stripped_sequoia_log", MapColor.WOOD, MapColor.COLOR_BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DataGenBlockRotation.AXIS_ROTATED, model = DataGenBlockModel.LOG, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = { DataGenBlockTag.LOGS }, itemTags = { DataGenItemTag.LOGS })
	public static final RotatedPillarBlock MOSSY_SEQUOIA_LOG = createLogBlock("mossy_sequoia_log", MapColor.WOOD, MapColor.COLOR_BROWN);
	@DataGenBlock
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 20)
	public static final WoodProducts SEQUOIA_PRODUCTS = new WoodProducts("sequoia", BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD));
	@ServerBlockRegistryData(fireBurn = 30, fireSpread = 60)
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.CUTOUT_MIPPED)
	@DataGenBlock(model = DataGenBlockModel.JAPOR_LEAVES, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = { DataGenBlockTag.LEAVES, DataGenBlockTag.SHEARS_MINEABLE }, itemTags = { DataGenItemTag.LEAVES })
	public static final BushLeavesBlock JAPOR_LEAVES = createBushLeavesBlock("japor_leaves");
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(model = DataGenBlockModel.NONE, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block JAPOR_WOOD = createWoodBlock("japor_wood", MapColor.COLOR_BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DataGenBlockRotation.AXIS_ROTATED, model = DataGenBlockModel.LOG_WITH_WOOD, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = DataGenBlockTag.LOGS, itemTags = DataGenItemTag.LOGS)
	public static final RotatedPillarBlock JAPOR_LOG = createLogBlock("japor_log", MapColor.WOOD, MapColor.COLOR_BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 20)
	@DataGenBlock
	public static final WoodProducts JAPOR_PRODUCTS = new WoodProducts("japor", BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD));

	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(model = DataGenBlockModel.NONE, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block TATOOINE_WOOD = createWoodBlock("tatooine_wood", MapColor.COLOR_BROWN);
	@ServerBlockRegistryData(fireBurn = 5, fireSpread = 5)
	@DataGenBlock(rotation = DataGenBlockRotation.AXIS_ROTATED, model = DataGenBlockModel.LOG_WITH_WOOD, itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = DataGenBlockTag.LOGS, itemTags = DataGenItemTag.LOGS)
	public static final RotatedPillarBlock TATOOINE_LOG = createLogBlock("tatooine_log", MapColor.WOOD, MapColor.COLOR_BROWN);


	/// Ores
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block BESKAR_ORE = createBlock("beskar_ore", BlockBehaviour.Properties.of().strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block BESKAR_BLOCK = createBlock("beskar_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(5.0F).requiresCorrectToolForDrops());

	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block CHROMIUM_ORE = createBlock("chromium_ore", BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block CHROMIUM_BLOCK = createBlock("chromium_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block CORTOSIS_ORE = createBlock("cortosis_ore", BlockBehaviour.Properties.of().strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block CORTOSIS_BLOCK = createBlock("cortosis_block", BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block DESH_ORE = createBlock("desh_ore", BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block DESH_BLOCK = createBlock("desh_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block DIATIUM_ORE = createBlock("diatium_ore", BlockBehaviour.Properties.of().strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock(blockTags = DataGenBlockTag.BLASTER_REFLECT)
	public static final Block DIATIUM_BLOCK = createBlock("diatium_block", BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block IONITE_ORE = createBlock("ionite_ore", BlockBehaviour.Properties.of().strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block IONITE_BLOCK = createBlock("ionite_block", BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(5.0F).lightLevel(value -> 3).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block LOMMITE_ORE = createBlock("lommite_ore", BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block LOMMITE_BLOCK = createBlock("lommite_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block TITANIUM_ORE = createBlock("titanium_ore", BlockBehaviour.Properties.of().strength(4.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block TITANIUM_BLOCK = createBlock("titanium_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block ZERSIUM_ORE = createBlock("zersium_ore", BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block ZERSIUM_BLOCK = createBlock("zersium_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block THORILIDE_ORE = createBlock("thorilide_ore", BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block THORILIDE_BLOCK = createBlock("thorilide_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(5.0F).requiresCorrectToolForDrops());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK, blockTags = {DataGenBlockTag.ORES, DataGenBlockTag.ORES_REPLACING_STONE, DataGenBlockTag.PICKAXE_MINEABLE})
	public static final Block HELICITE_ORE = createBlock("helicite_ore", BlockBehaviour.Properties.of().strength(3.0F).requiresCorrectToolForDrops());
	@DataGenBlock
	public static final Block HELICITE_BLOCK = createBlock("helicite_block", BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(5.0F).requiresCorrectToolForDrops());

	/// COMPOSITE

	@DataGenBlock
	public static final Block DURASTEEL_BLOCK = createBlock("durasteel_block", BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(5.0F).requiresCorrectToolForDrops());
	//TODO: implement connecting for PLASTEEL_BLOCK
	@DataGenBlock
	public static final SelfConnectingBlock PLASTEEL_BLOCK = Registrar.block(Galaxies.id("plasteel_block"), SelfConnectingBlock::new, BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(5.0F).requiresCorrectToolForDrops());

	/// GLASS

	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	@DataGenBlock
	public static final SelfConnectingGlassBlock IMPERIAL_GLASS = createSelfConnectingGlass("imperial_glass");
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock WHITE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("white_stained_imperial_glass", DyeColor.WHITE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock ORANGE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("orange_stained_imperial_glass", DyeColor.ORANGE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock MAGENTA_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("magenta_stained_imperial_glass", DyeColor.MAGENTA);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock LIGHT_BLUE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("light_blue_stained_imperial_glass", DyeColor.LIGHT_BLUE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock YELLOW_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("yellow_stained_imperial_glass", DyeColor.YELLOW);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock LIME_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("lime_stained_imperial_glass", DyeColor.LIME);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock PINK_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("pink_stained_imperial_glass", DyeColor.PINK);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock GRAY_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("gray_stained_imperial_glass", DyeColor.GRAY);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock LIGHT_GRAY_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("light_gray_stained_imperial_glass", DyeColor.LIGHT_GRAY);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock CYAN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("cyan_stained_imperial_glass", DyeColor.CYAN);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock PURPLE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("purple_stained_imperial_glass", DyeColor.PURPLE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock BLUE_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("blue_stained_imperial_glass", DyeColor.BLUE);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock BROWN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("brown_stained_imperial_glass", DyeColor.BROWN);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock GREEN_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("green_stained_imperial_glass", DyeColor.GREEN);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock RED_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("red_stained_imperial_glass", DyeColor.RED);
	@DataGenBlock
	@ClientBlockRegistryData(renderLayer = BlockRenderLayer.TRANSPARENT)
	public static final SelfConnectingStainedGlassBlock BLACK_STAINED_IMPERIAL_GLASS = createSelfConnectingStainedGlass("black_stained_imperial_glass", DyeColor.BLACK);

	/// PANEL

	private static final BlockBehaviour.Properties IMPERIAL_PANEL_SETTINGS = BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(1.5F).requiresCorrectToolForDrops();
	@DataGenBlock
	public static final SelfConnectingBlock RUSTED_METAL = createSelfConnectingBlock("rusted_metal", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.COPPER).strength(1.5F).requiresCorrectToolForDrops());
	// TODO: Implement "imperial cutout" blocks
	@DataGenBlock
	public static final StoneProducts BLACK_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS, "black_imperial_panel_blank");
	@DataGenBlock
	public static final StoneProducts GRAY_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY), "gray_imperial_panel_blank");
	@DataGenBlock
	public static final StoneProducts LIGHT_GRAY_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_LIGHT_GRAY), "light_gray_imperial_panel_blank");
	@DataGenBlock
	public static final StoneProducts WHITE_IMPERIAL_PANEL_BLANK = new StoneProducts(IMPERIAL_PANEL_SETTINGS, "white_imperial_panel_blank");
	@DataGenBlock
	public static final Block BLACK_IMPERIAL_PANEL_TILE = createPanel("black_imperial_panel_tile", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("black_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("black_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("black_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock GRAY_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("gray_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock GRAY_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("gray_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock GRAY_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("gray_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock IMPERIAL_PANEL_TALL_1 = createSelfConnectingBlock("gray_imperial_tall_panel_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock IMPERIAL_PANEL_TALL_2 = createSelfConnectingBlock("gray_imperial_tall_panel_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	// TODO: Implement connecting lighting panels
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_1 = createLightingPanelBlock("gray_imperial_light_half_1", 13);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_2 = createLightingPanelBlock("gray_imperial_light_half_2", 13);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_3 = createLightingPanelBlock("gray_imperial_light_half_3", 13);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_4 = createLightingPanelBlock("gray_imperial_light_half_4", 13);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_HALF_5 = createLightingPanelBlock("gray_imperial_light_half_5", 13);
	@DataGenBlock(model = DataGenBlockModel.NONE)
	public static final InteractableInvertedLampSlab GRAY_IMPERIAL_LIGHTING_SLAB = createLightingPanelSlab("gray_imperial_lighting_panel_slab", 15, 12);

	/*@RegistryName("gray_imperial_tall_light_1")
	@TarkinBlock(state = TrState.None, model = TrModel.None)
	public static final InteractableConnectingInvertedLampBlock ImperialLightTall1 = createLitConnectingPanel(MapColor.GRAY, 14);
	@RegistryName("gray_imperial_tall_light_2")
	@TarkinBlock(state = TrState.None, model = TrModel.None)
	public static final InteractableConnectingInvertedLampBlock ImperialLightTall2 = createLitConnectingPanel(MapColor.GRAY, 14);*/
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_PANEL_1 = createLightingPanelBlock("gray_imperial_light_panel_1", 11);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_PANEL_2 = createLightingPanelBlock("gray_imperial_light_panel_2", 9);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_PANEL_3 = createLightingPanelBlock("gray_imperial_light_panel_3", 14);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_1 = createLightingPanelBlock("gray_imperial_light_1", 15);
	@DataGenBlock(model = DataGenBlockModel.LIGHTING_PANEL)
	public static final InteractableInvertedLampBlock GRAY_IMPERIAL_LIGHT_2 = createLightingPanelBlock("gray_imperial_light_2", 15);

	@DataGenBlock
	public static final SelfConnectingBlock LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("light_gray_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("light_gray_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("light_gray_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock WHITE_IMPERIAL_PANEL_SECTIONAL = createSelfConnectingBlock("white_imperial_panel_sectional", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.QUARTZ));
	@DataGenBlock
	public static final SelfConnectingBlock WHITE_IMPERIAL_PANEL_SECTIONAL_1 = createSelfConnectingBlock("white_imperial_panel_sectional_1", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.QUARTZ));
	@DataGenBlock
	public static final SelfConnectingBlock WHITE_IMPERIAL_PANEL_SECTIONAL_2 = createSelfConnectingBlock("white_imperial_panel_sectional_2", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.QUARTZ));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_BORDERED = createSelfConnectingBlock("black_imperial_panel_bordered", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_SPLIT = createSelfConnectingBlock("black_imperial_panel_split", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock BLACK_IMPERIAL_PANEL_THIN_BORDERED = createSelfConnectingBlock("black_imperial_panel_thin_bordered", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final SelfConnectingBlock EXTERNAL_IMPERIAL_PLATING = createSelfConnectingBlock("external_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LARGE_IMPERIAL_PLATING = createSelfConnectingBlock("large_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock RUSTED_LARGE_IMPERIAL_PLATING = createSelfConnectingBlock("rusted_large_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock MOSSY_LARGE_IMPERIAL_PLATING = createSelfConnectingBlock("mossy_large_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final SelfConnectingBlock LARGE_LIGHT_GRAY_IMPERIAL_PLATING = createSelfConnectingBlock("large_light_gray_imperial_plating", IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_GRAY));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_A = createNumberedBlocks("black_imperial_panel_pattern_a", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_B = createNumberedBlocks("black_imperial_panel_pattern_b", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_C = createNumberedBlocks("black_imperial_panel_pattern_c", 2, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_D = createNumberedBlocks("black_imperial_panel_pattern_d", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final NumberedBlocks BLACK_IMPERIAL_PANEL_PATTERN_E = createNumberedBlocks("black_imperial_panel_pattern_e", 4, IMPERIAL_PANEL_SETTINGS.mapColor(MapColor.COLOR_BLACK));
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("gray_imperial_panel_pattern_3", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("light_gray_imperial_panel_pattern_3", MapColor.COLOR_LIGHT_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("light_gray_imperial_panel_pattern_4", MapColor.COLOR_LIGHT_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("light_gray_imperial_panel_pattern_5", MapColor.COLOR_LIGHT_GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("rusted_gray_imperial_panel_pattern_3", MapColor.COLOR_GRAY);
	//@RegistryName("rusted_gray_imperial_panel_pattern_3_stairs")
	//public static final Block RustedGrayImperialPanelPattern3Stairs = new StairsBlock(RustedGrayImperialPanelPattern3.getDefaultState(), FabricBlockSettings.copy(RustedGrayImperialPanelPattern3));
	//@RegistryName("rusted_gray_imperial_panel_pattern_3_slab")
	//public static final Block RustedGrayImperialPanelPattern3Slab = new VerticalSlabBlock(FabricBlockSettings.copy(RustedGrayImperialPanelPattern3));
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_3 = createPanel("mossy_gray_imperial_panel_pattern_3", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("gray_imperial_panel_pattern_4", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("rusted_gray_imperial_panel_pattern_4", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_4 = createPanel("mossy_gray_imperial_panel_pattern_4", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("gray_imperial_panel_pattern_5", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("rusted_gray_imperial_panel_pattern_5", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_5 = createPanel("mossy_gray_imperial_panel_pattern_5", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_6 = createPanel("gray_imperial_panel_pattern_6", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_6 = createPanel("rusted_gray_imperial_panel_pattern_6", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_6 = createPanel("mossy_gray_imperial_panel_pattern_6", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_7 = createPanel("gray_imperial_panel_pattern_7", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_8 = createPanel("gray_imperial_panel_pattern_8", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_9 = createPanel("gray_imperial_panel_pattern_9", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_9 = createPanel("rusted_gray_imperial_panel_pattern_9", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_9 = createPanel("mossy_gray_imperial_panel_pattern_9", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_10 = createPanel("gray_imperial_panel_pattern_10", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_11 = createPanel("gray_imperial_panel_pattern_11", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_12 = createPanel("gray_imperial_panel_pattern_12", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_PANEL_PATTERN_13 = createPanel("gray_imperial_panel_pattern_13", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_FLOORING_0 = createPanel("gray_imperial_flooring_0", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_FLOORING_3 = createPanel("gray_imperial_flooring_3", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block GRAY_IMPERIAL_FLOORING_4 = createPanel("gray_imperial_flooring_4", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_FLOORING_0 = createPanel("light_gray_imperial_flooring_0", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_FLOORING_3 = createPanel("light_gray_imperial_flooring_3", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block LIGHT_GRAY_IMPERIAL_FLOORING_4 = createPanel("light_gray_imperial_flooring_4", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block BLACK_IMPERIAL_FLOORING_3 = createPanel("black_imperial_flooring_3", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block BLACK_IMPERIAL_FLOORING_4 = createPanel("black_imperial_flooring_4", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block WHITE_IMPERIAL_FLOORING_3 = createPanel("white_imperial_flooring_3", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block WHITE_IMPERIAL_FLOORING_4 = createPanel("white_imperial_flooring_4", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block IMPERIAL_FLOORING_PATTERN_1 = createPanel("imperial_flooring_pattern_1", MapColor.COLOR_GRAY);
	@DataGenBlock
	public static final Block IMPERIAL_FLOORING_PATTERN_2 = createPanel("imperial_flooring_pattern_2", MapColor.COLOR_GRAY);

	@DataGenBlock(itemGroup = DataGenItemGroup.NONE)
	public static final Block LAB_WALL = createBlock("lab_wall", BlockBehaviour.Properties.of());

	/// CRATES
	public static final VoxelShape CRATE_SHAPE = VoxelShapeUtil.getCenteredCube(14, 16);
	public static final BlockBehaviour.Properties CORRUGATED_CRATE_SETTINGS = BlockBehaviour.Properties.of().sound(SoundType.METAL).noOcclusion().strength(2.5F);

	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.CUSTOM)
	public static final Block IMPERIAL_CORRUGATED_CRATE = createCorrugatedCrate("imperial_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.CUSTOM)
	public static final Block MEDICAL_CORRUGATED_CRATE = createCorrugatedCrate("medical_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.CUSTOM)
	public static final Block MINING_CORRUGATED_CRATE = createCorrugatedCrate("mining_corrugated_crate");
	@DataGenBlock(dataGenModelKey = "corrugated_crate", model = DataGenBlockModel.CUSTOM)
	public static final DyedBlocks CORRUGATED_CRATE = new DyedBlocks(color -> createCorrugatedCrate(color.name().toLowerCase() + "_corrugated_crate"));

	/// OTHER


	@DataGenBlock(model = DataGenBlockModel.ROTATING_PLAIN)
	public static final WaterloggableRotatingBlockWithBounds BLACK_HANGAR_LIGHT = Registrar.block(Galaxies.id("black_hangar_light"), properties -> new WaterloggableRotatingBlockWithBounds(VoxelShapeUtil.getCentered(16, 14, 8f), WaterloggableRotatingBlockWithBounds.Substrate.NONE, properties),  BlockBehaviour.Properties.of().sound(SoundType.METAL).noCollision().lightLevel(value -> 15).strength(0.5F).noOcclusion());


	private static Block createBlock(String key, BlockBehaviour.Properties settings)
	{
		return Registrar.block(Galaxies.id(key), Block::new, settings);
	}
	private static RotatedPillarBlock createLogBlock(String key, MapColor topMapColor, MapColor sideMapColor)
	{
		return Registrar.block(Galaxies.id(key), RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(blockState -> blockState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(2.0F).sound(SoundType.WOOD));
	}

	private static AridPlant createAridPlantBlock(String key)
	{
		return Registrar.block(Galaxies.id(key), AridPlant::new, BlockBehaviour.Properties.of().noCollision().instabreak().offsetType(BlockBehaviour.OffsetType.XZ).sound(SoundType.GRASS));
	}

	private static RotatedPillarBlock createWoodBlock(String key, MapColor mapColor)
	{
		return createLogBlock(key, mapColor, mapColor);
	}
	private static LeavesBlock createLeavesBlock(String key)
	{
		return Registrar.block(Galaxies.id(key), settings -> new TintedParticleLeavesBlock(0.01F, settings), BlockBehaviour.Properties.of().strength(0.2F).sound(SoundType.GRASS).noOcclusion().isSuffocating(BlockUtil::never).isViewBlocking(BlockUtil::never));
	}
	private static AccumulatingBlock createAccumulatingBlock(String key, BlockBehaviour.Properties settings, Block fullBlock){
		return Registrar.block(Galaxies.id(key), blockSettings -> new AccumulatingBlock(settings, fullBlock::getStateForPlacement), settings);
	}

	private static BushLeavesBlock createBushLeavesBlock(String key)
	{
		return Registrar.block(Galaxies.id(key), settings -> new BushLeavesBlock(8, 3, settings), BlockBehaviour.Properties.of().strength(0.2F).sound(SoundType.GRASS).noCollision().pushReaction(PushReaction.DESTROY));
	}
	private static InteractableInvertedLampBlock createLightingPanelBlock(String key, int luminosity)
	{
		return Registrar.block(Galaxies.id(key), InteractableInvertedLampBlock::new, IMPERIAL_PANEL_SETTINGS.lightLevel(createLightLevelFromLit(luminosity)));
	}

	public static ToIntFunction<BlockState> createLightLevelFromLit(int litLevel)
	{
		return state -> state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT) ? litLevel : 0;
	}

	public static ToIntFunction<BlockState> createLightLevelFromLitAndSlab(int litLevelSingle, int litLevelDouble)
	{
		return state -> state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT) ? (state.hasProperty(BlockStateProperties.SLAB_TYPE) && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE ? litLevelDouble : litLevelSingle) : 0;
	}

	private static InteractableInvertedLampSlab createLightingPanelSlab(String key, int luminositySingle, int luminosityDouble)
	{
		return Registrar.block(Galaxies.id(key), InteractableInvertedLampSlab::new, IMPERIAL_PANEL_SETTINGS.lightLevel(createLightLevelFromLitAndSlab(luminositySingle, luminosityDouble)));
	}

	private static NumberedBlocks createNumberedBlocks(String key, int count, BlockBehaviour.Properties settings)
	{
		return new NumberedBlocks(count, integer -> Registrar.block(Galaxies.id(key + "_" + integer), Block::new, settings));
	}

	private static WaterloggableRotatingBlockWithBoundsGuiEntity createCorrugatedCrate(String key)
	{
		return Registrar.block(Galaxies.id(key), blockSettings -> new WaterloggableRotatingBlockWithBoundsGuiEntity(CRATE_SHAPE, blockSettings, CrateCorrugatedBlockEntity::new), CORRUGATED_CRATE_SETTINGS);
	}
	private static SelfConnectingBlock createSelfConnectingBlock(String key, BlockBehaviour.Properties settings)
	{
		return Registrar.block(Galaxies.id(key), SelfConnectingBlock::new, settings);
	}

	private static RuiningDryingBlock createRuiningDryingBlock(String key, BlockBehaviour.Properties blockSettings, int transitionTime, Block dryingTarget, Block ruinedBlock, ColorRGBA colorCode)
	{
		return Registrar.block(Galaxies.id(key), settings -> new RuiningDryingBlock(dryingTarget, transitionTime, () -> ruinedBlock, settings, colorCode), blockSettings);
	}

	private static ColoredFallingBlock createFallingBlock(String key, BlockBehaviour.Properties blockSettings, ColorRGBA colorCode)
	{
		return Registrar.block(Galaxies.id(key), settings -> new ColoredFallingBlock(colorCode, settings), blockSettings);
	}

	private static VerticalSlabBlock createSlab(String key, BlockBehaviour.Properties settings)
	{
		return Registrar.block(Galaxies.id(key), VerticalSlabBlock::new, settings);
	}


	private static SelfConnectingGlassBlock createSelfConnectingGlass(String key)
	{
		return Registrar.block(Galaxies.id(key), SelfConnectingGlassBlock::new, BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(BlockUtil::never).isRedstoneConductor(BlockUtil::never).isSuffocating(BlockUtil::never).isViewBlocking(BlockUtil::never));
	}

	private static SelfConnectingStainedGlassBlock createSelfConnectingStainedGlass(String key, DyeColor color)
	{
		return Registrar.block(Galaxies.id(key), settings -> new SelfConnectingStainedGlassBlock(color, settings), BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(BlockUtil::never).isRedstoneConductor(BlockUtil::never).isSuffocating(BlockUtil::never).isViewBlocking(BlockUtil::never));
	}

	private static Block createPanel(String key, MapColor topMapColor)
	{
		return createBlock(key, BlockBehaviour.Properties.of().strength(1.5F).requiresCorrectToolForDrops().sound(SoundType.COPPER).mapColor(topMapColor));
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
