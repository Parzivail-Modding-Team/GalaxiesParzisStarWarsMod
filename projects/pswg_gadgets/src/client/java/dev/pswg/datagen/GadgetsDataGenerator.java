package dev.pswg.datagen;

import dev.pswg.Gadgets;
import dev.pswg.block.collection.NumberedBlocks;
import dev.pswg.block.collection.StoneProducts;
import dev.pswg.container.*;
import dev.pswg.Galaxies;
import dev.pswg.data.CodecDataLoader;
import dev.pswg.data.IdentifierUtil;
import dev.pswg.feature.scrapping.cutter.LaserCuttingRecipeJsonBuilder;
import dev.pswg.feature.scrapping.table.ScrappingRecipeJsonBuilder;
import dev.pswg.feature.scrapping.table.ScrappingToolType;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.rendering.models.GalaxiesModelBakery;
import dev.pswg.rendering.models.GqbIntermediary;
import dev.pswg.util.gen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static dev.pswg.rendering.models.GqbIntermediary.GQB_INTERMEDIARY_LOADER;

/**
 * The gadget data generator
 */
public class GadgetsDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		Galaxies.LOGGER.info("Running Gadgets Data Generator");

		DataGenResourceHelper.loadResources(PackType.CLIENT_RESOURCES, GQB_INTERMEDIARY_LOADER);

		pack.addProvider(LangGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(RecipesGenerator::new);
		pack.addProvider(GqdCompiledModelGenerator::new);
	}

	/**
	 * The gadget model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricPackOutput output)
		{
			super(output, Gadgets.MODID);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator)
		{
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, Block.class, (genBlock, dataGenBlock) -> {
				registerDataGenBlock(genBlock, dataGenBlock, blockStateModelGenerator);
			});

		}

		private static void registerDataGenBlock(Block block, DataGenBlock dataGenBlock, BlockModelGenerators generator)
		{
			switch (dataGenBlock.model())
			{
				case CUBE_ALL -> registerCubeWithRotation(block, dataGenBlock, TexturedModel.CUBE, generator);
				case ACCUMULATING -> registerAccumulatingBlock(block, generator);
				case COLUMN -> registerCubeWithRotation(block, dataGenBlock, TexturedModel.COLUMN_ALT, generator);
				case CROSS -> generator.createCrossBlockWithDefaultItem(block, BlockModelGenerators.PlantType.NOT_TINTED);
				case CUSTOM ->
				{
					switch (dataGenBlock.dataGenModelKey())
					{
						case null, default:
					}
				}
				case JAPOR_LEAVES -> registerJaporLeaves(block, generator);
				case LOG, LOG_WITH_WOOD -> registerLog(block, dataGenBlock, generator);
				case LIGHTING_PANEL -> registerLightingPanel(block, generator);
				case SLAB -> registerVerticalSlab(block, generator);
				case STAIRS -> registerStairs(block, generator);
			}
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerator)
		{
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, Item.class, (item, dataGenItem) -> registerItem(itemModelGenerator, item, dataGenItem));
		}

		public static Identifier createItemKey(Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.textureOverride().equals(""))
				return item.builtInRegistryHolder().key().identifier().withPrefix("item/");
			return Gadgets.id(dataGenItem.textureOverride()).withPrefix("item/");
		}

		public void registerItem(ItemModelGenerators generator, Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.model() != ItemModel.NONE)
			{
				if (dataGenItem.wiz())
					register(generator, item, Galaxies.id("item/wizard"), ModelTemplates.FLAT_ITEM);
				else
					switch (dataGenItem.model())
					{
						case GENERATED -> register(generator, item, createItemKey(item, dataGenItem), ModelTemplates.FLAT_ITEM);
						case HANDHELD -> register(generator, item, createItemKey(item, dataGenItem), ModelTemplates.FLAT_HANDHELD_ITEM);
						case DRINK -> registerDrink(generator, item, dataGenItem);
					}
			}
		}
	}

	/**
	 * The gadget language file generator. All language entries should be
	 * added through this generator.
	 */
	private static class LangGenerator extends FabricLanguageProvider
	{
		protected LangGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup)
		{
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder)
		{
			GadgetsGenUtil.consumeAnnotatedGadgetsItems(DataGenItem.class, (item, dataGenItem) -> addDatagenItem(translationBuilder, item, dataGenItem));
			GadgetsGenUtil.consumeAnnotatedGadgetsBlocks(DataGenBlock.class, (block, dataGenBlock) -> addDataGenBlock(translationBuilder, block, dataGenBlock));

			translationBuilder.add(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY, "Fragmenetation Grenade Destroy");
			translationBuilder.add(GadgetsBlocks.Tags.DETONATES_GRENADE, "Detonates Grenade");
			translationBuilder.add(GadgetsBlocks.Tags.INFERNO_CHAR, "Inferno Grenade Char");
			translationBuilder.add(GadgetsBlocks.Tags.INFERNO_DESTROY, "Inferno Grenade Destroy");
			translationBuilder.add(GadgetsBlocks.Tags.GAS_PASS_THROUGH, "Gas Pass Through");
			translationBuilder.add(GadgetsItems.Tags.GRENADES_TAG, "Grenades");
			translationBuilder.add(GadgetsItems.Tags.MINES_TAG, "Mines");
			translationBuilder.add(GadgetsItems.Tags.MIXER_FOOD_TAG, "Mixer food component");

			translationBuilder.add("subtitle.pswg_gadgets.grenade_throw", "Grenade thrown");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_arm", "Grenade armed");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_disarm", "Grenade disarmed");
			translationBuilder.add("subtitle.pswg_gadgets.fragmentationgrenade.explode1", "Explosion");
			translationBuilder.add("subtitle.pswg_gadgets.fragmentationgrenade.explode2", "Explosion");
			translationBuilder.add("subtitle.pswg_gadgets.fragmentationgrenade.explode3", "Explosion");
			translationBuilder.add("subtitle.pswg_gadgets.fragmentationgrenade.explode4", "Explosion");
			translationBuilder.add("subtitle.pswg_gadgets.thermaldetonator.explode", "Explosion");

			translationBuilder.add("effect.pswg_gadgets.intoxicated", "Intoxicated");

			translationBuilder.add(GadgetsItemGroups.DEMOLITIONS_ITEMS_GROUP_KEY, "PSWG - Demolitions Gadgets");

		}

		public void addDatagenItem(TranslationBuilder translationBuilder, Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.langOverride().isEmpty())
				translationBuilder.add(item, generateDefaultLang(item.builtInRegistryHolder().key().identifier()));
			else
				translationBuilder.add(item, dataGenItem.langOverride());
		}

		public void addDataGenBlock(TranslationBuilder translationBuilder, Block block, DataGenBlock dataGenBlock)
		{
			if (!Objects.equals(dataGenBlock.langOverride(), ""))
			{
				translationBuilder.add(block, dataGenBlock.langOverride());
				translationBuilder.add(block.asItem(), dataGenBlock.langOverride());
			}else
			{
				translationBuilder.add(block, generateDefaultLang(block.builtInRegistryHolder().key().identifier()));
				translationBuilder.add(block.asItem(), generateDefaultLang(block.asItem().builtInRegistryHolder().key().identifier()));
			}
		}
	}

	/**
	 * The gadget item tag generator. All item tags should be added
	 * through this generator.
	 */
	private static class ItemTagGenerator extends FabricTagsProvider.ItemTagsProvider
	{
		public ItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
		{
			super(output, completableFuture);
		}

		public static TagEntry itemId(Item item)
		{
			return TagEntry.element(BuiltInRegistries.ITEM.getKey(item));
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup)
		{
			addItemsToTag(GadgetsItems.Tags.GRENADES_TAG, DataGenGadgetsItemTag.GRENADE, this);
			addItemsToTag(GadgetsItems.Tags.MINES_TAG, DataGenGadgetsItemTag.MINE, this);
			addItemsToTag(GadgetsItems.Tags.MIXER_FOOD_TAG, DataGenGadgetsItemTag.MIXABLE_FOOD, this);

			getOrCreateRawBuilder(GadgetsItems.Tags.MIXER_FOOD_TAG)
					.add(itemId(GalaxiesItems.JOGAN_FRUIT))
					.add(itemId(GalaxiesItems.MEILOORUN))
					.add(itemId(GalaxiesItems.BLUE_MILK))
					.add(itemId(GalaxiesItems.BLACK_MELON))
					.add(itemId(GalaxiesItems.DESERT_PLUMS))
					.add(itemId(GalaxiesItems.PALLIE_FRUIT))
					.add(itemId(GalaxiesItems.PIKA_FRUIT))
					.add(itemId(GalaxiesItems.DEB_DEB))
					.add(itemId(Items.APPLE))
					.add(itemId(Items.BEETROOT))
					.add(itemId(Items.CARROT))
					.add(itemId(Items.GLOW_BERRIES))
					.add(itemId(Items.MELON_SLICE))
					.add(itemId(Items.SWEET_BERRIES));
		}
		private static void addItemsToTag(TagKey<Item> tag, DataGenGadgetsItemTag datagenTag, ItemTagGenerator generator){

			GadgetsGenUtil.consumeAnnotatedGadgetsBlocks(GadgetsItemTag.class, (block, gadgetsItemTag) -> {
				if(Arrays.stream(gadgetsItemTag.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getOrCreateRawBuilder(tag).add(itemId(block.asItem()));

			});
			GadgetsGenUtil.consumeAnnotatedGadgetsItems(GadgetsItemTag.class, (item, gadgetsItemTag) -> {
				if(Arrays.stream(gadgetsItemTag.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getOrCreateRawBuilder(tag).add(itemId(item));
			});
		}
	}
	/**
	 * The gadget block tag generator. All block tags should be added
	 * through this generator.
	 */
	private static class BlockTagGenerator extends FabricTagsProvider.BlockTagsProvider
	{
		public BlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
		{
			super(output, completableFuture);
		}

		public static TagEntry blockId(Block block)
		{
			return TagEntry.element(BuiltInRegistries.BLOCK.getKey(block));
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup)
		{
			getOrCreateRawBuilder(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY)
					.addOptionalTag(BlockTags.LEAVES.location())
					.addOptionalTag(BlockTags.CAVE_VINES.location())
					.addOptionalTag(BlockTags.CROPS.location())
					.addOptionalTag(BlockTags.FLOWERS.location())
					.addOptionalTag(BlockTags.SAPLINGS.location())
					.addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS.location())
					.addOptionalTag(BlockTags.ICE.location())
					.add(blockId(Blocks.FERN))
					.add(blockId(Blocks.LARGE_FERN))
					.add(blockId(Blocks.BROWN_MUSHROOM))
					.add(blockId(Blocks.RED_MUSHROOM))
					.add(blockId(Blocks.DEAD_BUSH))
					.add(blockId(Blocks.SHORT_GRASS))
					.add(blockId(Blocks.TALL_GRASS))
					.add(blockId(Blocks.SNOW));

			getOrCreateRawBuilder(GadgetsBlocks.Tags.DETONATES_GRENADE)
					.add(blockId(Blocks.REDSTONE_BLOCK))
					.add(blockId(Blocks.REDSTONE_TORCH))
					.add(blockId(Blocks.REDSTONE_WALL_TORCH))
					.add(blockId(Blocks.FIRE))
					.add(blockId(Blocks.SOUL_FIRE));

			getOrCreateRawBuilder(GadgetsBlocks.Tags.GAS_PASS_THROUGH)
					.addOptionalTag(BlockTags.LEAVES.location());
					// TODO: copper grates are a collection now, do they have a tag ID?
					// .add(blockId(Blocks.COPPER_GRATE));

			getOrCreateRawBuilder(GadgetsBlocks.Tags.INFERNO_CHAR)
					.add(blockId(Blocks.MOSS_BLOCK))
					.addOptionalTag(BlockTags.LOGS.location())
					.addOptionalTag(BlockTags.PLANKS.location())
					.addOptionalTag(BlockTags.BAMBOO_BLOCKS.location())
					.addOptionalTag(BlockTags.WOOL.location())
					.addOptionalTag(BlockTags.WOODEN_FENCES.location())
					.addOptionalTag(BlockTags.WOODEN_SLABS.location())
					.addOptionalTag(BlockTags.WOODEN_STAIRS.location())
					.addOptionalTag(BlockTags.WOODEN_TRAPDOORS.location())
					.addOptionalTag(ConventionalBlockTags.BOOKSHELVES.location())
			;

			getOrCreateRawBuilder(GadgetsBlocks.Tags.INFERNO_DESTROY)
					.addOptionalTag(BlockTags.LEAVES.location())
					.addOptionalTag(BlockTags.CAVE_VINES.location())
					.addOptionalTag(BlockTags.FLOWERS.location())
					.addOptionalTag(BlockTags.CROPS.location())
					.addOptionalTag(BlockTags.CRIMSON_STEMS.location())
					.addOptionalTag(BlockTags.ALL_SIGNS.location())
					.addOptionalTag(BlockTags.BANNERS.location())
					.addOptionalTag(BlockTags.FLOWER_POTS.location())
					.addOptionalTag(BlockTags.WOOL_CARPETS.location())
					.addOptionalTag(BlockTags.WOODEN_BUTTONS.location())
					.addOptionalTag(BlockTags.WARPED_STEMS.location())
					.addOptionalTag(BlockTags.SNOW.location())
					.addOptionalTag(BlockTags.ICE.location())
					.add(blockId(Blocks.BAMBOO))
					.add(blockId(Blocks.VINE))
					.add(blockId(Blocks.FERN))
					.add(blockId(Blocks.LARGE_FERN))
					.add(blockId(Blocks.DEAD_BUSH))
					.add(blockId(Blocks.TALL_GRASS))
					.add(blockId(Blocks.SHORT_GRASS))
					.add(blockId(Blocks.CACTUS))
					.add(blockId(Blocks.LEAF_LITTER))
			;

			addBlocksToGadgetsTag(GadgetsBlocks.Tags.DETONATES_GRENADE, DataGenGadgetsBlockTag.DETONATES_GRENADE, this);
			addBlocksToGadgetsTag(GadgetsBlocks.Tags.INFERNO_CHAR, DataGenGadgetsBlockTag.INFERNO_CHAR, this);
			addBlocksToGadgetsTag(GadgetsBlocks.Tags.INFERNO_DESTROY, DataGenGadgetsBlockTag.INFERNO_DESTROY, this);
			addBlocksToGadgetsTag(GadgetsBlocks.Tags.GAS_PASS_THROUGH, DataGenGadgetsBlockTag.GAS_PASS_THROUGH, this);
			addBlocksToGadgetsTag(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY, DataGenGadgetsBlockTag.FRAGMENTATION_GRENADE_DESTROY, this);
			addBlocksToTag(BlockTags.LEAVES, DataGenBlockTag.LEAVES, this);
			addBlocksToTag(BlockTags.LOGS, DataGenBlockTag.LOGS, this);
			addBlocksToTag(BlockTags.MINEABLE_WITH_AXE, DataGenBlockTag.AXE_MINEABLE, this);
			addBlocksToTag(BlockTags.MINEABLE_WITH_PICKAXE, DataGenBlockTag.PICKAXE_MINEABLE, this);
			addBlocksToTag(BlockTags.SAND, DataGenBlockTag.SAND, this);
			addBlocksToTag(BlockTags.MINEABLE_WITH_SHOVEL, DataGenBlockTag.SHOVEL_MINEABLE, this);
			addBlocksToTag(BlockTags.LOGS_THAT_BURN, DataGenBlockTag.LOGS_THAT_BURN, this);
			addBlocksToTag(BlockTags.STAIRS, DataGenBlockTag.STAIRS, this);


		}
		private static void addBlocksToTag(TagKey<Block> tag, DataGenBlockTag datagenTag, BlockTagGenerator generator){

			GadgetsGenUtil.consumeAnnotatedGadgetsBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if(Arrays.stream(dataGenBlock.blockTags()).anyMatch(dataGenBlockTag -> dataGenBlockTag == datagenTag)){
					generator.getOrCreateRawBuilder(tag).add(blockId(block));
				}
			});
		}
		private static void addBlocksToGadgetsTag(TagKey<Block> tag, DataGenGadgetsBlockTag datagenTag, BlockTagGenerator generator){

			GadgetsGenUtil.consumeAnnotatedGadgetsBlocks(GadgetsBlockTag.class, (block, dataGenBlock) -> {
				if(Arrays.stream(dataGenBlock.blockTags()).anyMatch(dataGenBlockTag -> dataGenBlockTag == datagenTag)){
					generator.getOrCreateRawBuilder(tag).add(blockId(block));
				}
			});
		}
	}

	private static class RecipesGenerator extends FabricRecipeProvider
	{
		public RecipesGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture)
		{
			super(output, registriesFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter)
		{
			return new RecipeProvider(registryLookup, exporter)
			{

				@Override
				public void buildRecipes()
				{
					createPanelStoneProductsCuttingRecipes(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_BLANK);
					createPanelStoneProductsCuttingRecipes(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_BLANK);
					createPanelStoneProductsCuttingRecipes(GalaxiesBlocks.WHITE_IMPERIAL_PANEL_BLANK);
					createPanelStoneProductsCuttingRecipes(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_PANEL_BLANK);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_TILE);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_SECTIONAL);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_SECTIONAL_1);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_SECTIONAL_2);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_SECTIONAL);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_SECTIONAL_1);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_SECTIONAL_2);
					createPanelCuttingRecipe(GalaxiesBlocks.IMPERIAL_PANEL_TALL_1);
					createPanelCuttingRecipe(GalaxiesBlocks.IMPERIAL_PANEL_TALL_2);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_HALF_1);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_HALF_2);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_HALF_3);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_HALF_4);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_HALF_5);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHTING_SLAB);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_PANEL_1);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_PANEL_2);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_PANEL_3);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_1);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_LIGHT_2);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL_1);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_PANEL_SECTIONAL_2);
					createPanelCuttingRecipe(GalaxiesBlocks.WHITE_IMPERIAL_PANEL_SECTIONAL);
					createPanelCuttingRecipe(GalaxiesBlocks.WHITE_IMPERIAL_PANEL_SECTIONAL_1);
					createPanelCuttingRecipe(GalaxiesBlocks.WHITE_IMPERIAL_PANEL_SECTIONAL_2);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_BORDERED);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_SPLIT);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_THIN_BORDERED);
					createPanelCuttingRecipe(GalaxiesBlocks.EXTERNAL_IMPERIAL_PLATING);
					createPanelCuttingRecipe(GalaxiesBlocks.LARGE_IMPERIAL_PLATING);
					createPanelCuttingRecipe(GalaxiesBlocks.RUSTED_LARGE_IMPERIAL_PLATING);
					createPanelCuttingRecipe(GalaxiesBlocks.MOSSY_LARGE_IMPERIAL_PLATING);
					createPanelCuttingRecipe(GalaxiesBlocks.LARGE_LIGHT_GRAY_IMPERIAL_PLATING);
					createNumberedPanelsRecipes(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_PATTERN_A);
					createNumberedPanelsRecipes(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_PATTERN_B);
					createNumberedPanelsRecipes(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_PATTERN_C);
					createNumberedPanelsRecipes(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_PATTERN_D);
					createNumberedPanelsRecipes(GalaxiesBlocks.BLACK_IMPERIAL_PANEL_PATTERN_E);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_3);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_4);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_PANEL_PATTERN_5);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_3);
					createPanelCuttingRecipe(GalaxiesBlocks.RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_3);
					createPanelCuttingRecipe(GalaxiesBlocks.MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_3);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_4);
					createPanelCuttingRecipe(GalaxiesBlocks.RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_4);
					createPanelCuttingRecipe(GalaxiesBlocks.MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_4);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_5);
					createPanelCuttingRecipe(GalaxiesBlocks.RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_5);
					createPanelCuttingRecipe(GalaxiesBlocks.MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_5);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_6);
					createPanelCuttingRecipe(GalaxiesBlocks.RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_6);
					createPanelCuttingRecipe(GalaxiesBlocks.MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_6);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_7);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_8);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_9);
					createPanelCuttingRecipe(GalaxiesBlocks.RUSTED_GRAY_IMPERIAL_PANEL_PATTERN_9);
					createPanelCuttingRecipe(GalaxiesBlocks.MOSSY_GRAY_IMPERIAL_PANEL_PATTERN_9);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_10);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_11);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_12);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_13);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_FLOORING_0);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_FLOORING_3);
					createPanelCuttingRecipe(GalaxiesBlocks.GRAY_IMPERIAL_FLOORING_4);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_FLOORING_0);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_FLOORING_3);
					createPanelCuttingRecipe(GalaxiesBlocks.LIGHT_GRAY_IMPERIAL_FLOORING_4);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_FLOORING_3);
					createPanelCuttingRecipe(GalaxiesBlocks.BLACK_IMPERIAL_FLOORING_4);
					createPanelCuttingRecipe(GalaxiesBlocks.WHITE_IMPERIAL_FLOORING_3);
					createPanelCuttingRecipe(GalaxiesBlocks.WHITE_IMPERIAL_FLOORING_4);
					createPanelCuttingRecipe(GalaxiesBlocks.IMPERIAL_FLOORING_PATTERN_1);
					createPanelCuttingRecipe(GalaxiesBlocks.IMPERIAL_FLOORING_PATTERN_2);
					createPanelCuttingRecipe(GalaxiesBlocks.LAB_WALL);

					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DURASTEEL_ROD, stack(GalaxiesItems.DURASTEEL_NUGGET, 6), stack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.PLASTEEL_ROD, stack(GalaxiesItems.PLASTEEL_NUGGET, 6), stack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.BALL_BEARING, stack(GalaxiesItems.DURASTEEL_INGOT), stack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DESH_CUP, stack(GalaxiesItems.DESH_NUGGET, 6), stack(GalaxiesItems.DESH_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DURASTEEL_CUP, stack(GalaxiesItems.DURASTEEL_NUGGET, 6), stack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DESH_WIRE, stack(GalaxiesItems.DESH_NUGGET, 6), stack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DESH_COIL, stack(GalaxiesItems.DESH_INGOT, 2), stack(GalaxiesItems.DESH_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.DESH_WIRE, stack(GalaxiesItems.DESH_INGOT), stack(GalaxiesItems.PLASTEEL_NUGGET, 6), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.BROKEN_SMALL_POWER_PACK_ITEM, stack(GalaxiesItems.DESH_INGOT), stack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.BROKEN_SMALL_POWER_PACK_ITEM, stack(GalaxiesItems.DESH_WIRE), stack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.BROKEN_SMALL_POWER_PACK_ITEM, stack(GalaxiesItems.IONITE_INGOT), stack(GalaxiesItems.IONITE_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DISPLAY_PANEL, stack(GalaxiesItems.PLASTEEL_INGOT), stack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.DISPLAY_PANEL, stack(GalaxiesItems.CHROMIUM_INGOT), stack(GalaxiesItems.CHROMIUM_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.DISPLAY_PANEL, stack(GalaxiesItems.DESH_WIRE), stack(GalaxiesItems.DESH_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.ELECTRIC_MOTOR, stack(GalaxiesItems.TURBINE), stack(GalaxiesItems.DURASTEEL_ROD, 2), 0.25f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.ELECTRIC_MOTOR, stack(GalaxiesItems.DESH_COIL), stack(GalaxiesItems.DESH_WIRE, 3), 0.25f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.LIGHT_PANEL, stack(GalaxiesItems.PLASTEEL_INGOT), stack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.LIGHT_PANEL, stack(GalaxiesItems.DESH_WIRE), stack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.TURBINE, stack(GalaxiesItems.DURASTEEL_INGOT, 2), stack(GalaxiesItems.DURASTEEL_INGOT), 0.25f);

				}

				public void createNumberedPanelsRecipes(NumberedBlocks numberedPanels)
				{
					for (Block panel : numberedPanels)
						createPanelCuttingRecipe(panel);
				}

				public void createPanelStoneProductsCuttingRecipes(StoneProducts panelProducts)
				{
					createPanelCuttingRecipe(panelProducts.block);
					createPanelCuttingRecipe(panelProducts.stairs);
					createPanelCuttingRecipe(panelProducts.slab);
					createPanelCuttingRecipe(panelProducts.wall);
				}

				public void createPanelCuttingRecipe(ItemLike panel)
				{
					createLaserCuttingRecipe(panel, stack(GalaxiesItems.DURASTEEL_INGOT), stack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
				}

				private ItemStackTemplate stack(ItemLike item)
				{
					return new ItemStackTemplate(item.asItem());
				}

				private ItemStackTemplate stack(ItemLike item, int count)
				{
					return new ItemStackTemplate(item.asItem(), count);
				}

				public void createLaserCuttingRecipe(ItemLike input, ItemStackTemplate primaryOutput, ItemStackTemplate secondaryOutput, float secondaryChance)
				{
					HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
					LaserCuttingRecipeJsonBuilder.create(itemLookup, Ingredient.of(input), primaryOutput, secondaryOutput, secondaryChance).offerTo(output, ResourceKey.create(Registries.RECIPE, Identifier.parse(input.asItem().toString().replaceAll(Galaxies.MODID, Gadgets.MODID) + "_cutting")));
				}

				public void createScrappingRecipe(ScrappingToolType tool, ItemLike input, ItemStackTemplate primaryOutput, ItemStackTemplate secondaryOutput, float secondaryChance)
				{
					HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
					Ingredient toolIngredient = null;
					String suffix = "";
					switch (tool)
					{
						case Cutter ->
						{
							toolIngredient = Ingredient.of(GadgetsItems.CUTTER_ITEM);
							suffix = "_cutter";
						}
						case Spanner ->
						{
							toolIngredient = Ingredient.of(GadgetsItems.SPANNER_ITEM);
							suffix = "_spanner";
						}
						case Calibrator ->
						{
							toolIngredient = Ingredient.of(GadgetsItems.CALIBRATOR_ITEM);
							suffix = "_calibrator";
						}
					}
					ScrappingRecipeJsonBuilder.create(itemLookup, toolIngredient, Ingredient.of(input), primaryOutput, secondaryOutput, secondaryChance).offerTo(output, ResourceKey.create(Registries.RECIPE, Identifier.parse(input.asItem().toString().replaceAll(Galaxies.MODID, Gadgets.MODID) + "_scrapping" + suffix)));
				}
			};
		}

		@Override
		public String getName()
		{
			return "PSWGGadgetsRecipeProvider";
		}
	}
	/**
	 * The GQD compiled model generator. All models should be compiled through
	 * this generator.
	 */
	private static class GqdCompiledModelGenerator implements DataProvider
	{
		private final PackOutput.PathProvider resolver;

		public GqdCompiledModelGenerator(FabricPackOutput output)
		{
			this.resolver = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
		}

		@Override
		public CompletableFuture<?> run(CachedOutput writer)
		{
			var completables = new ArrayList<CompletableFuture<?>>();

			for (var entry : GQB_INTERMEDIARY_LOADER.getDefinitions().entrySet())
			{
				if (!entry.getKey().getNamespace().equals(Gadgets.MODID))
					continue;

				completables.add(compile(writer, entry));
			}

			return CompletableFuture.allOf(completables.toArray(CompletableFuture[]::new));
		}

		/**
		 * Compile the given GQB intermediary model
		 *
		 * @param writer The writer to add the generated data to
		 * @param entry  The entry to compile
		 *
		 * @return A future that completes when the data is written
		 */
		private CompletableFuture<?> compile(CachedOutput writer, Map.Entry<Identifier, GqbIntermediary> entry)
		{
			var completables = new ArrayList<CompletableFuture<?>>();

			if (entry.getValue().files().isPresent())
			{
				// Split the geometry and model into multiple files
				for (var fileEntry : entry.getValue().files().get().entrySet())
				{
					var nonDatagenId = entry.getKey().withPath(GalaxiesDataProvider.getNonDatagenPath(entry.getKey().getPath(), Optional.of(fileEntry.getKey())));
					var quadsOutputPath = resolver.file(nonDatagenId, "gqb");
					var jsonOutputPath = resolver.file(nonDatagenId, "json");

					completables.add(DataProvider.saveStable(writer, entry.getValue().createModelDef(), jsonOutputPath));
					completables.add(GalaxiesDataProvider.writeToPath(
							writer,
							quadsOutputPath,
							GalaxiesModelBakery.GQuadGeometry.PACKET_CODEC,
							entry.getValue().createGeometry(Optional.of(new HashSet<>(fileEntry.getValue())))
					));
				}
			}
			else
			{
				var nonDatagenId = entry.getKey().withPath(GalaxiesDataProvider.getNonDatagenPath(entry.getKey().getPath(), Optional.empty()));
				var quadsOutputPath = resolver.file(nonDatagenId, "gqb");
				var jsonOutputPath = resolver.file(nonDatagenId, "json");

				completables.add(DataProvider.saveStable(writer, entry.getValue().createModelDef(), jsonOutputPath));
				completables.add(GalaxiesDataProvider.writeToPath(
						writer,
						quadsOutputPath,
						GalaxiesModelBakery.GQuadGeometry.PACKET_CODEC,
						entry.getValue().createGeometry(Optional.empty())
				));
			}

			return CompletableFuture.allOf(completables.toArray(CompletableFuture[]::new));
		}

		@Override
		public String getName()
		{
			return "Gadgets GQD Compiled Models";
		}
	}

	private static String generateDefaultLang(Identifier reg)
	{
		var path = reg.getPath();
		return Arrays.stream(path.split("_"))
		             .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
		             .collect(Collectors.joining(" "));
	}
}
