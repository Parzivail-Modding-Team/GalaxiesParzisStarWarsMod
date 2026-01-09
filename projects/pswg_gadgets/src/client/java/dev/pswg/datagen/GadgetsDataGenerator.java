package dev.pswg.datagen;

import dev.pswg.Gadgets;
import dev.pswg.block.*;
import dev.pswg.container.*;
import dev.pswg.Galaxies;
import dev.pswg.feature.scrapping.cutter.LaserCuttingRecipeJsonBuilder;
import dev.pswg.feature.scrapping.table.ScrappingRecipeJsonBuilder;
import dev.pswg.feature.scrapping.table.ScrappingToolType;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.item.SwgDrinkTintSource;
import dev.pswg.util.GadgetsGenUtil;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

		pack.addProvider(LangGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(RecipesGenerator::new);
	}

	/**
	 * The gadget model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricDataOutput output)
		{
			super(output);
		}

		public static WeightedVariant createWeightedVariant(Identifier id)
		{
			return new WeightedVariant(Pool.of(new ModelVariant(id)));
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
		{
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, Block.class, (genBlock, dataGenBlock) -> {
				registerDataGenBlock(genBlock, dataGenBlock, blockStateModelGenerator);
			});

		}
		private static void registerAccumulatingBlock(Block block, BlockStateModelGenerator generator) {
			var id = TextureMap.getId(block);

			generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.LAYERS).generate(
						 height -> {
							Identifier modelId = TexturedModel.makeFactory(block1 -> TextureMap.all(id).put(TextureKey.PARTICLE, id), blockModel("template_accumulating_height" + height * 2, TextureKey.ALL, TextureKey.PARTICLE)).upload(block, "_height"+ height * 2, generator.modelCollector);
							 return createWeightedVariant(modelId);//BlockStateVariant.create().put(VariantSettings.MODEL, modelId);
						 }))
					);
			generator.registerParentedItemModel(block, ModelIds.getBlockSubModelId(block, "_height2"));
		}

		private static void registerDataGenBlock(Block block, DataGenBlock dataGenBlock, BlockStateModelGenerator generator)
		{
			switch (dataGenBlock.model())
			{
				case CubeAll -> registerCubeWithRotation(block, dataGenBlock, TexturedModel.CUBE_ALL, generator);
				case Accumulating -> registerAccumulatingBlock(block, generator);
				case Column -> registerCubeWithRotation(block, dataGenBlock, TexturedModel.END_FOR_TOP_CUBE_COLUMN, generator);
				case Cross -> generator.registerTintableCross(block, BlockStateModelGenerator.CrossType.NOT_TINTED);
				case Custom ->
				{
					switch (dataGenBlock.dataGenModelKey())
					{
						case null, default:
					}
				}
				case Log, LogWithWood -> registerLog(block, dataGenBlock, generator);
				case LightingPanel -> registerLightingPanel(block, generator);
				case Slab -> registerVerticalSlab(block, generator);
				case Stairs -> registerStairs(block, generator);
			}
		}

		private static void registerLog(Block block, DataGenBlock dataGenBlock, BlockStateModelGenerator generator)
		{
			String logKey = getBlockKey(block).toString();
			BlockStateModelGenerator.LogTexturePool texturePool = generator.createLogTexturePool(block).log(block);
			if (dataGenBlock.model() == DataGenBlockModel.LogWithWood)
			{
				String woodKey = logKey.substring(0, logKey.indexOf("_log")) + "_wood";
				Block woodBlock = Registries.BLOCK.get(Identifier.of(woodKey));
				texturePool.wood(woodBlock);
			}
		}
		private static void registerCubeWithRotation(Block block, DataGenBlock dataGenBlock, TexturedModel.Factory modelFactory, BlockStateModelGenerator generator){
			switch (dataGenBlock.rotation()){
				case Default -> generator.registerSingleton(block, modelFactory);
				case RandomRotationX -> {
					Identifier id = modelFactory.upload(block, generator.modelCollector);
					var blockStateSupplier = MultipartBlockModelDefinitionCreator.create(block).with(new WeightedVariant(Pool.of(
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R0), 1),
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R90), 1),
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R180), 1),
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R270), 1)
					)));
					generator.blockStateCollector.accept(blockStateSupplier);
				}
				case AxisRotated -> {
					Identifier id = modelFactory.upload(block, generator.modelCollector);
					var blockStateSupplier = VariantsBlockModelDefinitionCreator.of(block, createWeightedVariant(id)).apply(BlockStateVariantMap.operations(Properties.AXIS)
					                                                                                                                            .register(Direction.Axis.Y, ModelVariantOperator.MODEL.withValue(id))
					                                                                                                                            .register(Direction.Axis.Z, ModelVariantOperator.MODEL.withValue(id)
					                                                                                                                                                                                  .then(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)))
					                                                                                                                            .register(Direction.Axis.X, ModelVariantOperator.MODEL.withValue(id)
					                                                                                                                                                                                  .then(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90))
					                                                                                                                                                                                  .then(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
					                                                                                                                            )
					);
					generator.blockStateCollector.accept(blockStateSupplier);
				}
			}
		}
		private static void registerStairs(Block stairs, BlockStateModelGenerator generator){
			Identifier stairsId = Models.STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);
			Identifier stairsOuterId = Models.OUTER_STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);
			Identifier stairsInnerId = Models.INNER_STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);

			generator.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(stairs, createWeightedVariant(stairsInnerId), createWeightedVariant(stairsId), createWeightedVariant(stairsOuterId)));
			generator.registerItemModel(stairs);

		}

		private static void registerVerticalSlab(Block slab, BlockStateModelGenerator generator){
			registerVerticalSlabAllTextures(slab, slab, generator);
		}
		private static void registerVerticalSlabAllTextures(Block textureBase, Block slab, BlockStateModelGenerator generator){
			var textureId = TextureMap.getId(textureBase);
			var textureMap = new TextureMap().put(TextureKey.SIDE, textureId).put(TextureKey.TOP, textureId).put(TextureKey.END, textureId);
			Identifier bottomId = Models.SLAB.upload(slab, textureMap, generator.modelCollector);
			Identifier topId =  Models.SLAB_TOP.upload(slab, "_top", textureMap, generator.modelCollector);
			Identifier doubleId =  Models.CUBE_COLUMN.upload(slab, "_double", textureMap, generator.modelCollector);

			var blockState = VariantsBlockModelDefinitionCreator.of(slab, createWeightedVariant(bottomId)).
			                                                    apply(BlockStateVariantMap.operations(Properties.AXIS)
			                                                                              .register(Direction.Axis.Y, ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R0))
			                                                                              .register(Direction.Axis.Z, ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270).then(ModelVariantOperator.UV_LOCK.withValue(true)))
			                                                                              .register(Direction.Axis.X, ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90).then(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)).then(ModelVariantOperator.UV_LOCK.withValue(true)))
			                                                    ).apply(BlockStateVariantMap.operations(Properties.SLAB_TYPE)
			                                                                                .register(SlabType.BOTTOM, ModelVariantOperator.MODEL.withValue(bottomId))
			                                                                                .register(SlabType.DOUBLE, ModelVariantOperator.MODEL.withValue(doubleId))
			                                                                                .register(SlabType.TOP, ModelVariantOperator.MODEL.withValue(topId))
					);
			generator.blockStateCollector.accept(blockState);

		}
		private static Model blockModel(String parent, TextureKey... requiredTextureKeys)
		{
			return new Model(Optional.of(Gadgets.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
		}

		private static void registerLightingPanel(Block block, BlockStateModelGenerator generator) {
			Identifier identifier = lightingPanelFactory().upload(block, generator.modelCollector);
			Identifier identifier2 = generator.createSubModel(block, "_on", Models.CUBE_COLUMN, ModelGenerator::createLightingPanelTextureMap);

			generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, createWeightedVariant(identifier2), createWeightedVariant(identifier))));
		}
		public static TexturedModel.Factory lightingPanelFactory(){
			return TexturedModel.makeFactory(ModelGenerator::createLightingPanelTextureMap, Models.CUBE_COLUMN);
		}
		public static TextureMap createLightingPanelTextureMap(Block block){
			return createLightingPanelTextureMap(TextureMap.getId(block));
		}
		public static TextureMap createLightingPanelTextureMap(Identifier identifier){
			return new TextureMap().put(TextureKey.SIDE, identifier).put(TextureKey.END, TextureMap.getId(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_3));
		}

		public static Identifier getBlockKey(Block block)
		{
			return block.getRegistryEntry().getKey().get().getValue();
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator)
		{
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, Item.class, (item, dataGenItem) -> registerItem(itemModelGenerator, item, dataGenItem));

		}

		public static Identifier createItemKey(Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.textureOverride().equals(""))
				return item.getRegistryEntry().getKey().get().getValue().withPrefixedPath("item/");
			return Gadgets.id(dataGenItem.textureOverride()).withPrefixedPath("item/");
		}

		public void registerItem(ItemModelGenerator generator, Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.genModel())
			{
				if (dataGenItem.wiz())
					register(generator, item, Galaxies.id("item/wizard"), Models.GENERATED);
				else
					switch (dataGenItem.model())
					{
						case generated -> register(generator, item, createItemKey(item, dataGenItem), Models.GENERATED);
						case handheld -> register(generator, item, createItemKey(item, dataGenItem), Models.HANDHELD);
						case drink -> registerDrink(generator, item, dataGenItem);
					}
			}
		}

		public void registerDrink(ItemModelGenerator generator, Item item, DataGenItem dataGenItem)
		{
			Identifier modelId;
			Identifier overlay = (dataGenItem.overlayTextureOverride().equals("")) ? Identifier.of(createItemKey(item, dataGenItem).withSuffixedPath("_overlay").toString().replace("_filled", "")) : Gadgets.id(dataGenItem.overlayTextureOverride()).withPrefixedPath("item/");
			Identifier base = Identifier.of(ModelIds.getItemModelId(item).toString().replace("_filled", ""));

			if (dataGenItem.invertLayer())
			{
				modelId = generator.uploadTwoLayers(item, base, overlay);
				generator.output.accept(item, ItemModels.tinted(modelId, new ConstantTintSource(16777215), new SwgDrinkTintSource()));
			}
			else
			{
				modelId = generator.uploadTwoLayers(item, overlay, base);
				generator.output.accept(item, ItemModels.tinted(modelId, new SwgDrinkTintSource()));
			}
		}
	}

	/**
	 * The gadget language file generator. All language entries should be
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
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, Item.class, (item, dataGenItem) -> addDatagenItem(translationBuilder, item, dataGenItem));
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, ArmorItems.class, (armorItems, dataGenItem) -> {
				addDatagenItem(translationBuilder, armorItems.helmet, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.chestplate, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.leggings, dataGenItem);
				addDatagenItem(translationBuilder, armorItems.boots, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, DyedItems.class, (dyedItems, dataGenItem) -> {
				for (Item item : dyedItems.values())
					addDatagenItem(translationBuilder, item, dataGenItem);
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
				for (Item item : numberedItems.stream().toList())
					addDatagenItem(translationBuilder, item, dataGenItem);
			});

			GadgetsGenUtil.consumeAnnotatedGadgetsBlocks(DataGenBlock.class, (block, dataGenBlock) -> addDataGenBlock(translationBuilder, block, dataGenBlock));

			translationBuilder.add(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY, "Fragmenetation Grenade Destroy");
			translationBuilder.add(GadgetsBlocks.Tags.DETONATES_GRENADE, "Detonates Grenade");
			translationBuilder.add(GadgetsBlocks.Tags.BOUNCY, "Bouncy");
			translationBuilder.add(GadgetsBlocks.Tags.INFERNO_CHAR, "Inferno Grenade Char");
			translationBuilder.add(GadgetsBlocks.Tags.INFERNO_DESTROY, "Inferno Grenade Destroy");
			translationBuilder.add(GadgetsBlocks.Tags.GAS_PASS_THROUGH, "Gas Pass Through");
			translationBuilder.add(GadgetsItems.Tags.GRENADES_TAG, "Grenades");

			translationBuilder.add("subtitle.pswg_gadgets.grenade_throw", "Grenade thrown");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_arm", "Grenade armed");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_disarm", "Grenade disarmed");
			translationBuilder.add("subtitle.pswg_gadgets.fragmentationgrenade.explode", "C-25 Grenade explosion");
			translationBuilder.add("subtitle.pswg_gadgets.thermaldetonator.explode", "Thermal Detonator explosion");

			translationBuilder.add("effect.pswg_gadgets.intoxicated", "Intoxicated");

			translationBuilder.add(GadgetsItemGroups.DEMOLITIONS_ITEMS_GROUP_KEY, "PSWG - Demolitions Gadgets");

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
			}else
			{
				translationBuilder.add(block, generateDefaultLang(block.getRegistryEntry().registryKey().getValue()));
				translationBuilder.add(block.asItem(), generateDefaultLang(block.asItem().getRegistryEntry().registryKey().getValue()));
			}
		}
	}

	/**
	 * The gadget item tag generator. All item tags should be added
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
			addItemsToTag(GadgetsItems.Tags.GRENADES_TAG, DGItemTag.Grenade, this);
			addItemsToTag(GadgetsItems.Tags.MINES_TAG, DGItemTag.Mine, this);
			addItemsToTag(GadgetsItems.Tags.MIXER_FOOD_TAG, DGItemTag.MixableFood, this);
			addItemsToTag(ItemTags.LEAVES, DGItemTag.Leaves, this);

			getTagBuilder(GadgetsItems.Tags.MIXER_FOOD_TAG)
					.add(itemId(Items.APPLE))
					.add(itemId(Items.BEETROOT))
					.add(itemId(Items.CARROT))
					.add(itemId(Items.GLOW_BERRIES))
					.add(itemId(Items.MELON_SLICE))
					.add(itemId(Items.SWEET_BERRIES));
		}
		private static void addItemsToTag(TagKey<Item> tag, DGItemTag datagenTag, ItemTagGenerator generator){

			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if(Arrays.stream(dataGenBlock.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getTagBuilder(tag).add(itemId(block.asItem()));

			});
			AutoGenerateUtil.consumeAnnotatedGadgetsItems(DataGenItem.class, (item, dataGenItem) -> {
				if(Arrays.stream(dataGenItem.itemTags()).anyMatch(dgItemTag -> dgItemTag == datagenTag))
					generator.getTagBuilder(tag).add(itemId(item));
			});
		}
	}
	/**
	 * The gadget block tag generator. All block tags should be added
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
			getTagBuilder(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY)
					.addOptionalTag(BlockTags.LEAVES.id())
					.addOptionalTag(BlockTags.CAVE_VINES.id())
					.addOptionalTag(BlockTags.CROPS.id())
					.addOptionalTag(BlockTags.FLOWERS.id())
					.addOptionalTag(BlockTags.SAPLINGS.id())
					.addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS.id())
					.addOptionalTag(BlockTags.ICE.id())
					.add(blockId(Blocks.FERN))
					.add(blockId(Blocks.LARGE_FERN))
					.add(blockId(Blocks.BROWN_MUSHROOM))
					.add(blockId(Blocks.RED_MUSHROOM))
					.add(blockId(Blocks.DEAD_BUSH))
					.add(blockId(Blocks.SHORT_GRASS))
					.add(blockId(Blocks.TALL_GRASS))
					.add(blockId(Blocks.SNOW));

			getTagBuilder(GadgetsBlocks.Tags.DETONATES_GRENADE)
					.add(blockId(Blocks.REDSTONE_BLOCK))
					.add(blockId(Blocks.REDSTONE_TORCH))
					.add(blockId(Blocks.REDSTONE_WALL_TORCH))
					.add(blockId(Blocks.FIRE))
					.add(blockId(Blocks.SOUL_FIRE));

			getTagBuilder(GadgetsBlocks.Tags.BOUNCY)
					.add(blockId(Blocks.HONEY_BLOCK))
					.add(blockId(Blocks.SLIME_BLOCK));

			getTagBuilder(GadgetsBlocks.Tags.GAS_PASS_THROUGH)
					.addOptionalTag(BlockTags.LEAVES.id())
					.add(blockId(Blocks.COPPER_GRATE));

			getTagBuilder(GadgetsBlocks.Tags.INFERNO_CHAR)
					.add(blockId(Blocks.MOSS_BLOCK))
					.addOptionalTag(BlockTags.LOGS.id())
					.addOptionalTag(BlockTags.PLANKS.id())
					.addOptionalTag(BlockTags.BAMBOO_BLOCKS.id())
					.addOptionalTag(BlockTags.WOOL.id())
					.addOptionalTag(BlockTags.WOODEN_FENCES.id())
					.addOptionalTag(BlockTags.WOODEN_SLABS.id())
					.addOptionalTag(BlockTags.WOODEN_STAIRS.id())
					.addOptionalTag(BlockTags.WOODEN_TRAPDOORS.id())
					.addOptionalTag(ConventionalBlockTags.BOOKSHELVES.id())
			;

			getTagBuilder(GadgetsBlocks.Tags.INFERNO_DESTROY)
					.addOptionalTag(BlockTags.LEAVES.id())
					.addOptionalTag(BlockTags.CAVE_VINES.id())
					.addOptionalTag(BlockTags.FLOWERS.id())
					.addOptionalTag(BlockTags.CROPS.id())
					.addOptionalTag(BlockTags.CRIMSON_STEMS.id())
					.addOptionalTag(BlockTags.ALL_SIGNS.id())
					.addOptionalTag(BlockTags.BANNERS.id())
					.addOptionalTag(BlockTags.FLOWER_POTS.id())
					.addOptionalTag(BlockTags.WOOL_CARPETS.id())
					.addOptionalTag(BlockTags.WOODEN_BUTTONS.id())
					.addOptionalTag(BlockTags.WARPED_STEMS.id())
					.addOptionalTag(BlockTags.SNOW.id())
					.addOptionalTag(BlockTags.ICE.id())
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

			addBlocksToTag(GadgetsBlocks.Tags.BOUNCY, DGBlockTag.Bouncy, this);
			addBlocksToTag(GadgetsBlocks.Tags.DETONATES_GRENADE, DGBlockTag.DetonatesGrenade, this);
			addBlocksToTag(GadgetsBlocks.Tags.INFERNO_CHAR, DGBlockTag.InfernoChar, this);
			addBlocksToTag(GadgetsBlocks.Tags.INFERNO_DESTROY, DGBlockTag.InfernoDestroy, this);
			addBlocksToTag(GadgetsBlocks.Tags.GAS_PASS_THROUGH, DGBlockTag.GasPassThrough, this);
			addBlocksToTag(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY, DGBlockTag.FragmentationGrenadeDestroy, this);
			addBlocksToTag(BlockTags.LEAVES, DGBlockTag.Leaves, this);
			addBlocksToTag(BlockTags.LOGS, DGBlockTag.Logs, this);
			addBlocksToTag(BlockTags.AXE_MINEABLE, DGBlockTag.AxeMineable, this);
			addBlocksToTag(BlockTags.PICKAXE_MINEABLE, DGBlockTag.PickaxeMineable, this);
			addBlocksToTag(BlockTags.SAND, DGBlockTag.Sand, this);
			addBlocksToTag(BlockTags.SHOVEL_MINEABLE, DGBlockTag.ShovelMineable, this);
			addBlocksToTag(BlockTags.LOGS_THAT_BURN, DGBlockTag.LogsThatBurn, this);
			addBlocksToTag(BlockTags.STAIRS, DGBlockTag.Stairs, this);


		}
		private static void addBlocksToTag(TagKey<Block> tag, DGBlockTag datagenTag, BlockTagGenerator generator){

			AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
				if(Arrays.stream(dataGenBlock.tags()).anyMatch(dgBlockTag -> dgBlockTag == datagenTag)){
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

					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DURASTEEL_ROD, new ItemStack(GalaxiesItems.DURASTEEL_NUGGET, 6), new ItemStack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.PLASTEEL_ROD, new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 6), new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.BALL_BEARING, new ItemStack(GalaxiesItems.DURASTEEL_INGOT), new ItemStack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DESH_CUP, new ItemStack(GalaxiesItems.DESH_NUGGET, 6), new ItemStack(GalaxiesItems.DESH_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DURASTEEL_CUP, new ItemStack(GalaxiesItems.DURASTEEL_NUGGET, 6), new ItemStack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DESH_WIRE, new ItemStack(GalaxiesItems.DESH_NUGGET, 6), new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DESH_COIL, new ItemStack(GalaxiesItems.DESH_INGOT, 2), new ItemStack(GalaxiesItems.DESH_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.DESH_WIRE, new ItemStack(GalaxiesItems.DESH_INGOT), new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 6), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.BROKEN_SMALL_POWER_PACK_ITEM, new ItemStack(GalaxiesItems.DESH_INGOT), new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.BROKEN_SMALL_POWER_PACK_ITEM, new ItemStack(GalaxiesItems.DESH_WIRE), new ItemStack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.BROKEN_SMALL_POWER_PACK_ITEM, new ItemStack(GalaxiesItems.IONITE_INGOT), new ItemStack(GalaxiesItems.IONITE_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.DISPLAY_PANEL, new ItemStack(GalaxiesItems.PLASTEEL_INGOT), new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.DISPLAY_PANEL, new ItemStack(GalaxiesItems.CHROMIUM_INGOT), new ItemStack(GalaxiesItems.CHROMIUM_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.DISPLAY_PANEL, new ItemStack(GalaxiesItems.DESH_WIRE), new ItemStack(GalaxiesItems.DESH_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.ELECTRIC_MOTOR, new ItemStack(GalaxiesItems.TURBINE), new ItemStack(GalaxiesItems.DURASTEEL_ROD, 2), 0.25f);
					createScrappingRecipe(ScrappingToolType.Calibrator, GalaxiesItems.ELECTRIC_MOTOR, new ItemStack(GalaxiesItems.DESH_COIL), new ItemStack(GalaxiesItems.DESH_WIRE, 3), 0.25f);
					createScrappingRecipe(ScrappingToolType.Cutter, GalaxiesItems.LIGHT_PANEL, new ItemStack(GalaxiesItems.PLASTEEL_INGOT), new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.LIGHT_PANEL, new ItemStack(GalaxiesItems.DESH_WIRE), new ItemStack(GalaxiesItems.PLASTEEL_NUGGET, 3), 0.35f);
					createScrappingRecipe(ScrappingToolType.Spanner, GalaxiesItems.TURBINE, new ItemStack(GalaxiesItems.DURASTEEL_INGOT, 2), new ItemStack(GalaxiesItems.DURASTEEL_INGOT), 0.25f);

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

				public void createPanelCuttingRecipe(ItemConvertible panel)
				{
					createLaserCuttingRecipe(panel, new ItemStack(GalaxiesItems.DURASTEEL_INGOT), new ItemStack(GalaxiesItems.DURASTEEL_NUGGET, 3), 0.35f);
				}

				public void createLaserCuttingRecipe(ItemConvertible input, ItemStack primaryOutput, ItemStack secondaryOutput, float secondaryChance)
				{
					RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
					LaserCuttingRecipeJsonBuilder.create(itemLookup, Ingredient.ofItem(input), primaryOutput, secondaryOutput, secondaryChance).offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(input.asItem().toString() + "_cutting")));
				}

				public void createScrappingRecipe(ScrappingToolType tool, ItemConvertible input, ItemStack primaryOutput, ItemStack secondaryOutput, float secondaryChance)
				{
					RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
					Ingredient toolIngredient = null;
					String suffix = "";
					switch (tool)
					{
						case Cutter ->
						{
							toolIngredient = Ingredient.ofItem(GadgetsItems.CUTTER_ITEM);
							suffix = "_cutter";
						}
						case Spanner ->
						{
							toolIngredient = Ingredient.ofItem(GadgetsItems.SPANNER_ITEM);
							suffix = "_spanner";
						}
						case Calibrator ->
						{
							toolIngredient = Ingredient.ofItem(GadgetsItems.CALIBRATOR_ITEM);
							suffix = "_calibrator";
						}
					}
					ScrappingRecipeJsonBuilder.create(itemLookup, toolIngredient, Ingredient.ofItem(input), primaryOutput, secondaryOutput, secondaryChance).offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(input.asItem().toString() + "_scrapping" + suffix)));
				}
			};
		}

		@Override
		public String getName()
		{
			return "PSWGGadgetsRecipeProvider";
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
