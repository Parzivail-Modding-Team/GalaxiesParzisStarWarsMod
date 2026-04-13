package dev.pswg.datagen;

import dev.pswg.Galaxies;
import dev.pswg.GalaxiesClient;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.block.collection.*;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.GalaxiesItemGroups;
import dev.pswg.container.GalaxiesItems;
import dev.pswg.data.CodecDataLoader;
import dev.pswg.data.IdentifierUtil;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import dev.pswg.rendering.models.GalaxiesModelBakery;
import dev.pswg.rendering.models.GqbIntermediary;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static dev.pswg.rendering.models.GqbIntermediary.GQB_INTERMEDIARY_LOADER;

/**
 * The base data generator
 */
public class GalaxiesDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		Galaxies.LOGGER.info("Running Galaxies Client Data Generator");

		DataGenResourceHelper.loadResources(PackType.CLIENT_RESOURCES, GQB_INTERMEDIARY_LOADER);

		pack.addProvider(LangGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(RecipesGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider((fabricPackOutput, completableFuture) -> new dev.pswg.datagen.GqdCompiledModelGenerator(fabricPackOutput, Galaxies.MODID));
	}

	/**
	 * The galaxies model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricPackOutput output)
		{
			super(output, Galaxies.MODID);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator)
		{
			registerVerticalLightingSlab(blockStateModelGenerator);

			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, Block.class, (genBlock, dataGenBlock) -> {
				registerDataGenBlock(genBlock, dataGenBlock, blockStateModelGenerator);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, DyedBlocks.class, (genDyedBlocks, dataGenBlock) -> {

				for (Block block : genDyedBlocks.values())
					registerDataGenBlock(block, dataGenBlock, blockStateModelGenerator);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, NumberedBlocks.class, (numberedBlocks, dataGenBlock) -> {
				for (Block block : numberedBlocks)
					registerDataGenBlock(block, dataGenBlock, blockStateModelGenerator);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, ReducedDryingRuiningStoneProducts.class, (reducedDryingRuiningStoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.NONE)
				{
					registerReducedDryingRuinedStoneProducts(reducedDryingRuiningStoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, ReducedDryingStoneProducts.class, (reducedDryingStoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.NONE)
				{
					registerReducedDryingStoneProducts(reducedDryingStoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, ReducedStoneProducts.class, (reducedStoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.NONE)
				{
					registerReducedStoneProducts(reducedStoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, StoneProducts.class, (stoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.NONE)
				{
					registerStoneProducts(stoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, WoodProducts.class, (woodProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.NONE)
				{
					registerWoodProducts(woodProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, DyedStoneProducts.class, (dyedStoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.NONE)
				{
					for (StoneProducts stoneProducts : dyedStoneProducts.values())
						registerStoneProducts(stoneProducts, blockStateModelGenerator);
				}
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
				case CROSS_AGE_3 -> registerCrossAge3(block, generator);
				case CROSS_AGE_3_BLOOMING -> registerCrossAge3Blooming(block, generator);
				case CROP_AGE_2 -> generator.createCropBlock(block, BlockStateProperties.AGE_2, 0, 1, 2);
				case CUSTOM ->
				{
					switch (dataGenBlock.dataGenModelKey())
					{
						case "corrugated_crate":
							registerCorrugatedCrate(generator, block);
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
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, Item.class, (item, dataGenItem) -> registerItem(itemModelGenerator, item, dataGenItem));
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, ArmorItems.class, (armorItems, dataGenItem) -> {
				registerItem(itemModelGenerator, armorItems.helmet, dataGenItem);
				registerItem(itemModelGenerator, armorItems.chestplate, dataGenItem);
				registerItem(itemModelGenerator, armorItems.leggings, dataGenItem);
				registerItem(itemModelGenerator, armorItems.boots, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, DyedItems.class, (dyedItems, dataGenItem) -> {
				for (Item item : dyedItems.values())
					registerItem(itemModelGenerator, item, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
				for (Item item : numberedItems.stream().toList())
					registerItem(itemModelGenerator, item, dataGenItem);
			});
		}

		public void registerItem(ItemModelGenerators generator, Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.model() != ItemModel.NONE)
			{
				if (dataGenItem.wiz())
					GalaxiesModelProvider.register(generator, item, Galaxies.id("item/wizard"), ModelTemplates.FLAT_ITEM);
				else
					switch (dataGenItem.model())
					{
						case GENERATED -> GalaxiesModelProvider.register(generator, item, createItemKey(item, dataGenItem), ModelTemplates.FLAT_ITEM);
						case HANDHELD -> GalaxiesModelProvider.register(generator, item, createItemKey(item, dataGenItem), ModelTemplates.FLAT_HANDHELD_ITEM);
						case DRINK -> registerDrink(generator, item, dataGenItem);
					}
			}
		}
	}

	private static String generateDefaultLang(Identifier reg)
	{
		var path = reg.getPath();
		return Arrays.stream(path.split("_"))
		             .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
		             .collect(Collectors.joining(" "));
	}

	/**
	 * The base language file generator. All language entries should be
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
			// Hints
			translationBuilder.add(GalaxiesClient.I18N_KEYBIND_HINT_KEY, "§9[§f%s§9]§r %s");

			// Keybinds
			ClientLangGenHelper.keybindCategory(translationBuilder, GalaxiesKeybinds.CATEGORY, "Galaxies: Parzi's Star Wars Mod");

			ClientLangGenHelper.keybind(translationBuilder, GalaxiesKeybinds.getPrimaryAction(), "Primary Item Action");

			// Attributes
			translationBuilder.add(GalaxiesEntityAttributes.I18N_ATTR_MULTIPLIER, "%sx %s");
			translationBuilder.add(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, "Zoom");

			// Items
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, Item.class, (item, dataGenItem) -> addDatagenItem(translationBuilder, item, dataGenItem));
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, ArmorItems.class, (armorItems, dataGenItem) -> {
				addDatagenItem(translationBuilder, armorItems.helmet, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.chestplate, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.leggings, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.boots, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, DyedItems.class, (dyedItems, dataGenItem) -> {
				for (Item item : dyedItems.values())
					addDatagenItem(translationBuilder, item, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
				for (Item item : numberedItems.stream().toList())
					addDatagenItem(translationBuilder, item, dataGenItem);
			});

			//Blocks
			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> addDataGenBlock(translationBuilder, block, dataGenBlock));

			//Item Groups
			translationBuilder.add(GalaxiesItemGroups.CONSTRUCTION_BLOCK_GROUP_KEY, "PSWG - Construction Blocks");
			translationBuilder.add(GalaxiesItemGroups.WORLDGEN_BLOCK_GROUP_KEY, "PSWG - Worldgen Blocks");
			translationBuilder.add(GalaxiesItemGroups.GENERIC_ITEMS_GROUP_KEY, "PSWG - Items");
			translationBuilder.add(GalaxiesItemGroups.FOOD_ITEMS_GROUP_KEY, "PSWG - Food");

			//Tags
			translationBuilder.add(GalaxiesBlocks.Tags.BOUNCY, "Bouncy");
			translationBuilder.add(GalaxiesBlocks.Tags.SOFT, "Soft");
			translationBuilder.add(GalaxiesBlocks.Tags.BUSH_PLACEABLE, "Bush Placeable");
			translationBuilder.add(GalaxiesBlocks.Tags.ARID_PLANT_PLACEABLE, "Arid Plant Placeable");
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
				if (dataGenBlock.addItemTranslation())
					translationBuilder.add(block.asItem(), dataGenBlock.langOverride());
			}
			else
			{
				translationBuilder.add(block, generateDefaultLang(block.builtInRegistryHolder().key().identifier()));
				if (dataGenBlock.addItemTranslation())
					translationBuilder.add(block.asItem(), generateDefaultLang(block.asItem().builtInRegistryHolder().key().identifier()));
			}
		}
	}

	/**
	 * The galaxies item tag generator. All item tags should be added
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
			addItemsToTag(ItemTags.LEAVES, DataGenItemTag.LEAVES, this);
			addItemsToTag(ItemTags.SAND, DataGenItemTag.SAND, this);
			addItemsToTag(ItemTags.LOGS_THAT_BURN, DataGenItemTag.LOGS_THAT_BURN, this);
			addItemsToTag(ItemTags.LOGS, DataGenItemTag.LOGS, this);

			getOrCreateRawBuilder(GalaxiesItems.Tags.BESKAR_TOOL_MATERIALS_TAG)
					.add(itemId(GalaxiesItems.BESKAR_INGOT));
			getOrCreateRawBuilder(GalaxiesItems.Tags.DURASTEEL_TOOL_MATERIALS_TAG)
					.add(itemId(GalaxiesItems.PLASTEEL_INGOT));
			getOrCreateRawBuilder(GalaxiesItems.Tags.TITANIUM_TOOL_MATERIALS_TAG)
					.add(itemId(GalaxiesItems.TITANIUM_INGOT));
			addItemsToTag(GalaxiesItems.Tags.DRINK_CONTAINER_TAG, DataGenItemTag.DRINK_CONTAINER, this);
		}

		private static void addItemsToTag(TagKey<Item> tag, DataGenItemTag datagenTag, ItemTagGenerator generator)
		{

			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if (Arrays.stream(dataGenBlock.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getOrCreateRawBuilder(tag).add(itemId(block.asItem()));
			});
			AutoGenerateUtil.consumeAnnotatedGalaxiesItems(DataGenItem.class, (item, dataGenItem) -> {
				if (Arrays.stream(dataGenItem.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getOrCreateRawBuilder(tag).add(itemId(item));
			});
		}
	}

	/**
	 * The galaxies block tag generator. All block tags should be added
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
			addBlocksToTag(BlockTags.LEAVES, DataGenBlockTag.LEAVES, this);
			addBlocksToTag(BlockTags.LOGS, DataGenBlockTag.LOGS, this);
			addBlocksToTag(BlockTags.MINEABLE_WITH_AXE, DataGenBlockTag.AXE_MINEABLE, this);
			addBlocksToTag(BlockTags.MINEABLE_WITH_PICKAXE, DataGenBlockTag.PICKAXE_MINEABLE, this);
			addBlocksToTag(BlockTags.SAND, DataGenBlockTag.SAND, this);
			addBlocksToTag(BlockTags.MINEABLE_WITH_SHOVEL, DataGenBlockTag.SHOVEL_MINEABLE, this);
			addBlocksToTag(BlockTags.LOGS_THAT_BURN, DataGenBlockTag.LOGS_THAT_BURN, this);
			addBlocksToTag(BlockTags.STAIRS, DataGenBlockTag.STAIRS, this);
			addBlocksToTag(GalaxiesBlocks.Tags.BUSH_PLACEABLE, DataGenBlockTag.BUSH_PLACEABLE, this);
			getOrCreateRawBuilder(GalaxiesBlocks.Tags.BUSH_PLACEABLE)
					.addOptionalTag(BlockTags.SAND.location())
					.add(blockId(Blocks.GRASS_BLOCK))
					.add(blockId(Blocks.DIRT))
					.add(blockId(Blocks.PODZOL))
					.add(blockId(Blocks.COARSE_DIRT));
			addBlocksToTag(GalaxiesBlocks.Tags.ARID_PLANT_PLACEABLE, DataGenBlockTag.ARID_PLANT_PLACEABLE, this);
			getOrCreateRawBuilder(GalaxiesBlocks.Tags.ARID_PLANT_PLACEABLE)
					.addOptionalTag(BlockTags.SAND.location())
					.addOptionalTag(BlockTags.TERRACOTTA.location())
					.add(blockId(Blocks.GRASS_BLOCK))
					.add(blockId(Blocks.DIRT))
					.add(blockId(Blocks.PODZOL))
					.add(blockId(Blocks.COARSE_DIRT));
			addBlocksToTag(GalaxiesBlocks.Tags.BOUNCY, DataGenBlockTag.BOUNCY, this);
			getOrCreateRawBuilder(GalaxiesBlocks.Tags.BOUNCY)
					.add(blockId(Blocks.HONEY_BLOCK))
					.add(blockId(Blocks.SLIME_BLOCK));
			addBlocksToTag(GalaxiesBlocks.Tags.SOFT, DataGenBlockTag.SOFT, this);
			getOrCreateRawBuilder(GalaxiesBlocks.Tags.SOFT)
					.addOptionalTag(BlockTags.SNOW.location())
					.addOptionalTag(BlockTags.BEDS.location())
					.addOptionalTag(BlockTags.LEAVES.location())
					.addOptionalTag(BlockTags.WOOL.location())
					.addOptionalTag(BlockTags.WOOL_CARPETS.location())
					.add(blockId(Blocks.MOSS_BLOCK))
					.add(blockId(Blocks.MOSS_CARPET))
					.add(blockId(Blocks.PALE_MOSS_BLOCK))
					.add(blockId(Blocks.PALE_MOSS_CARPET));
		}

		private static void addBlocksToTag(TagKey<Block> tag, DataGenBlockTag datagenTag, BlockTagGenerator generator)
		{

			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if (Arrays.stream(dataGenBlock.blockTags()).anyMatch(dataGenBlockTag -> dataGenBlockTag == datagenTag))
				{
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
				}
			};
		}

		@Override
		public String getName()
		{
			return "PSWGGalaxiesRecipeProvider";
		}
	}

}
