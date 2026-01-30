package dev.pswg.datagen;

import dev.pswg.Galaxies;
import dev.pswg.GalaxiesClient;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.block.collection.*;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.GalaxiesItemGroups;
import dev.pswg.container.GalaxiesItems;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.*;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

		pack.addProvider(LangGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(RecipesGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
	}

	/**
	 * The galaxies model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricDataOutput output)
		{
			super(output, Galaxies.MODID);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
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
		private static void registerDataGenBlock(Block block, DataGenBlock dataGenBlock, BlockStateModelGenerator generator)
		{
			switch (dataGenBlock.model())
			{
				case CUBE_ALL -> registerCubeWithRotation(block, dataGenBlock, TexturedModel.CUBE_ALL, generator);
				case Accumulating -> registerAccumulatingBlock(block, generator);
				case COLUMN -> registerCubeWithRotation(block, dataGenBlock, TexturedModel.END_FOR_TOP_CUBE_COLUMN, generator);
				case CROSS -> generator.registerTintableCross(block, BlockStateModelGenerator.CrossType.NOT_TINTED);
				case CROSS_AGE_3 -> registerCrossAge3(block, generator);
				case CROSS_AGE_3_BLOOMING -> registerCrossAge3Blooming(block, generator);
				case CROP_AGE_2 -> generator.registerCrop(block, Properties.AGE_2, 0, 1, 2);
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
		public void generateItemModels(ItemModelGenerator itemModelGenerator)
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

		public void registerItem(ItemModelGenerator generator, Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.model() != ItemModel.NONE)
			{
				if (dataGenItem.wiz())
					GalaxiesModelProvider.register(generator, item, Galaxies.id("item/wizard"), Models.GENERATED);
				else
					switch (dataGenItem.model())
					{
						case GENERATED -> GalaxiesModelProvider.register(generator, item, createItemKey(item, dataGenItem), Models.GENERATED);
						case HANDHELD -> GalaxiesModelProvider.register(generator, item, createItemKey(item, dataGenItem), Models.HANDHELD);
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
		protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup)
		{
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder)
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
		}

		public void addDatagenItem(TranslationBuilder translationBuilder, Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.langOverride().isEmpty())
				translationBuilder.add(item, generateDefaultLang(item.getRegistryEntry().registryKey().getValue()));
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
				translationBuilder.add(block, generateDefaultLang(block.getRegistryEntry().registryKey().getValue()));
				if (dataGenBlock.addItemTranslation())
					translationBuilder.add(block.asItem(), generateDefaultLang(block.asItem().getRegistryEntry().registryKey().getValue()));
			}
		}
	}

	/**
	 * The galaxies item tag generator. All item tags should be added
	 * through this generator.
	 */
	private static class ItemTagGenerator extends FabricTagProvider.ItemTagProvider
	{
		public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture)
		{
			super(output, completableFuture);
		}

		public static Identifier itemId(Item item)
		{
			return Registries.ITEM.getId(item);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
		{
			addItemsToTag(ItemTags.LEAVES, DGItemTag.LEAVES, this);

			getTagBuilder(GalaxiesItems.Tags.BESKAR_TOOL_MATERIALS_TAG)
					.add(itemId(GalaxiesItems.BESKAR_INGOT));
			getTagBuilder(GalaxiesItems.Tags.DURASTEEL_TOOL_MATERIALS_TAG)
					.add(itemId(GalaxiesItems.PLASTEEL_INGOT));
			getTagBuilder(GalaxiesItems.Tags.TITANIUM_TOOL_MATERIALS_TAG)
					.add(itemId(GalaxiesItems.TITANIUM_INGOT));
		}

		private static void addItemsToTag(TagKey<Item> tag, DGItemTag datagenTag, ItemTagGenerator generator)
		{

			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if (Arrays.stream(dataGenBlock.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getTagBuilder(tag).add(itemId(block.asItem()));
			});
			AutoGenerateUtil.consumeAnnotatedGadgetsItems(DataGenItem.class, (item, dataGenItem) -> {
				if (Arrays.stream(dataGenItem.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getTagBuilder(tag).add(itemId(item));
			});
		}
	}

	/**
	 * The galaxies block tag generator. All block tags should be added
	 * through this generator.
	 */
	private static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider
	{
		public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture)
		{
			super(output, completableFuture);
		}

		public static Identifier blockId(Block block)
		{
			return Registries.BLOCK.getId(block);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
		{
			addBlocksToTag(BlockTags.LEAVES, DGBlockTag.LEAVES, this);
			addBlocksToTag(BlockTags.LOGS, DGBlockTag.LOGS, this);
			addBlocksToTag(BlockTags.AXE_MINEABLE, DGBlockTag.AXE_MINEABLE, this);
			addBlocksToTag(BlockTags.PICKAXE_MINEABLE, DGBlockTag.PICKAXE_MINEABLE, this);
			addBlocksToTag(BlockTags.SAND, DGBlockTag.SAND, this);
			addBlocksToTag(BlockTags.SHOVEL_MINEABLE, DGBlockTag.SHOVEL_MINEABLE, this);
			addBlocksToTag(BlockTags.LOGS_THAT_BURN, DGBlockTag.LOGS_THAT_BURN, this);
			addBlocksToTag(BlockTags.STAIRS, DGBlockTag.STAIRS, this);
			addBlocksToTag(GalaxiesBlocks.Tags.BUSH_PLACEABLE, DGBlockTag.BUSH_PLACEABLE, this);
			getTagBuilder(GalaxiesBlocks.Tags.BUSH_PLACEABLE)
					.addOptionalTag(BlockTags.SAND.id())
					.add(blockId(Blocks.GRASS_BLOCK))
					.add(blockId(Blocks.DIRT))
					.add(blockId(Blocks.PODZOL))
					.add(blockId(Blocks.COARSE_DIRT));
			addBlocksToTag(GalaxiesBlocks.Tags.ARID_PLANT_PLACEABLE, DGBlockTag.ARID_PLANT_PLACEABLE, this);
			getTagBuilder(GalaxiesBlocks.Tags.ARID_PLANT_PLACEABLE)
					.addOptionalTag(BlockTags.SAND.id())
					.addOptionalTag(BlockTags.TERRACOTTA.id())
					.add(blockId(Blocks.GRASS_BLOCK))
					.add(blockId(Blocks.DIRT))
					.add(blockId(Blocks.PODZOL))
					.add(blockId(Blocks.COARSE_DIRT));
		}

		private static void addBlocksToTag(TagKey<Block> tag, DGBlockTag datagenTag, BlockTagGenerator generator)
		{

			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if (Arrays.stream(dataGenBlock.blockTags()).anyMatch(dgBlockTag -> dgBlockTag == datagenTag))
				{
					generator.getTagBuilder(tag).add(blockId(block));
				}
			});
		}
	}

	private static class RecipesGenerator extends FabricRecipeProvider
	{
		public RecipesGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
		{
			super(output, registriesFuture);
		}

		@Override
		protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter)
		{
			return new RecipeGenerator(registryLookup, exporter)
			{
				@Override
				public void generate()
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
