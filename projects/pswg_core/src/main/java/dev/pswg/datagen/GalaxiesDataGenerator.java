package dev.pswg.datagen;

import dev.pswg.Galaxies;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.block.*;
import dev.pswg.container.GalaxiesItemGroups;
import dev.pswg.container.GalaxiesItems;
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
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
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

		pack.addProvider(LangGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(RecipesGenerator::new);
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
			// Attributes
			translationBuilder.add(GalaxiesEntityAttributes.I18N_ATTR_MULTIPLIER, "%sx %s");
			translationBuilder.add(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, "Zoom");

			// Items
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, Galaxies.class, Item.class, (item, dataGenItem) -> addDatagenItem(translationBuilder, item, dataGenItem));
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, Galaxies.class, ArmorItems.class, (armorItems, dataGenItem) -> {
				addDatagenItem(translationBuilder, armorItems.helmet, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.chestplate, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.leggings, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.boots, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, Galaxies.class, DyedItems.class, (dyedItems, dataGenItem) -> {
				for (Item item : dyedItems.values())
					addDatagenItem(translationBuilder, item, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, Galaxies.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
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
				translationBuilder.add(block.asItem(), dataGenBlock.langOverride());
			}
			else
			{
				translationBuilder.add(block, generateDefaultLang(block.getRegistryEntry().registryKey().getValue()));
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
			addItemsToTag(ItemTags.LEAVES, DGItemTag.Leaves, this);

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
			addBlocksToTag(BlockTags.LEAVES, DGBlockTag.Leaves, this);
			addBlocksToTag(BlockTags.LOGS, DGBlockTag.Logs, this);
			addBlocksToTag(BlockTags.AXE_MINEABLE, DGBlockTag.AxeMineable, this);
			addBlocksToTag(BlockTags.PICKAXE_MINEABLE, DGBlockTag.PickaxeMineable, this);
			addBlocksToTag(BlockTags.SAND, DGBlockTag.Sand, this);
			addBlocksToTag(BlockTags.SHOVEL_MINEABLE, DGBlockTag.ShovelMineable, this);
			addBlocksToTag(BlockTags.LOGS_THAT_BURN, DGBlockTag.LogsThatBurn, this);
			addBlocksToTag(BlockTags.STAIRS, DGBlockTag.Stairs, this);
		}

		private static void addBlocksToTag(TagKey<Block> tag, DGBlockTag datagenTag, BlockTagGenerator generator)
		{

			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if (Arrays.stream(dataGenBlock.tags()).anyMatch(dgBlockTag -> dgBlockTag == datagenTag))
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
