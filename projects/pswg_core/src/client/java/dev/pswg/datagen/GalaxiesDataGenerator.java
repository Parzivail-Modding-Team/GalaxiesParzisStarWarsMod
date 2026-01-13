package dev.pswg.datagen;

import dev.pswg.Galaxies;
import dev.pswg.GalaxiesClient;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.block.*;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.GalaxiesItemGroups;
import dev.pswg.container.GalaxiesItems;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import dev.pswg.item.SwgDrinkTintSource;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
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
			super(output);
		}

		public static WeightedVariant createWeightedVariant(Identifier id)
		{
			return new WeightedVariant(Pool.of(new ModelVariant(id)));
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
				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					registerReducedDryingRuinedStoneProducts(reducedDryingRuiningStoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, ReducedDryingStoneProducts.class, (reducedDryingStoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					registerReducedDryingStoneProducts(reducedDryingStoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, ReducedStoneProducts.class, (reducedStoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					registerReducedStoneProducts(reducedStoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, StoneProducts.class, (stoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					registerStoneProducts(stoneProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, WoodProducts.class, (woodProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					registerWoodProducts(woodProducts, blockStateModelGenerator);
				}
			});
			AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GalaxiesBlocks.class, DyedStoneProducts.class, (dyedStoneProducts, dataGenBlock) -> {
				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					for (StoneProducts stoneProducts : dyedStoneProducts.values())
						registerStoneProducts(stoneProducts, blockStateModelGenerator);
				}
			});
		}

		private static void registerReducedDryingRuinedStoneProducts(ReducedDryingRuiningStoneProducts dryingRuinedStoneProducts, BlockStateModelGenerator generator)
		{
			generator.registerCubeAllModelTexturePool(dryingRuinedStoneProducts.block)
			         .stairs(dryingRuinedStoneProducts.stairs);
			registerVerticalSlabAllTextures(dryingRuinedStoneProducts.block, dryingRuinedStoneProducts.slab, generator);
		}

		private static void registerReducedDryingStoneProducts(ReducedDryingStoneProducts dryingStoneProducts, BlockStateModelGenerator generator)
		{
			generator.registerCubeAllModelTexturePool(dryingStoneProducts.block)
			         .stairs(dryingStoneProducts.stairs);
			registerVerticalSlabAllTextures(dryingStoneProducts.block, dryingStoneProducts.slab, generator);
		}

		private static void registerReducedStoneProducts(ReducedStoneProducts stoneProducts, BlockStateModelGenerator generator)
		{
			generator.registerCubeAllModelTexturePool(stoneProducts.block)
			         .stairs(stoneProducts.stairs);
			registerVerticalSlabAllTextures(stoneProducts.block, stoneProducts.slab, generator);
		}

		private static void registerStoneProducts(StoneProducts stoneProducts, BlockStateModelGenerator generator)
		{
			generator.registerCubeAllModelTexturePool(stoneProducts.block)
			         .wall(stoneProducts.wall)
			         .stairs(stoneProducts.stairs);
			registerVerticalSlabAllTextures(stoneProducts.block, stoneProducts.slab, generator);
		}

		private static void registerWoodProducts(WoodProducts woodProducts, BlockStateModelGenerator generator)
		{
			generator.registerCubeAllModelTexturePool(woodProducts.plank)
			         .fence(woodProducts.fence)
			         .fenceGate(woodProducts.gate)
			         .stairs(woodProducts.stairs);
			generator.registerDoor(woodProducts.door);
			generator.registerTrapdoor(woodProducts.trapdoor);

			registerVerticalSlabAllTextures(woodProducts.plank, woodProducts.slab, generator);
		}

		private static void registerAccumulatingBlock(Block block, BlockStateModelGenerator generator)
		{
			var id = TextureMap.getId(block);

			generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateVariantMap.models(Properties.LAYERS).generate(
					height -> {
						Identifier modelId = TexturedModel.makeFactory(block1 -> TextureMap.all(id).put(TextureKey.PARTICLE, id), blockModel("template_accumulating_height" + height * 2, TextureKey.ALL, TextureKey.PARTICLE)).upload(block, "_height" + height * 2, generator.modelCollector);
						return createWeightedVariant(modelId);
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
						case "corrugated_crate":
							registerCorrugatedCrate(generator, block);
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

		private static void registerCubeWithRotation(Block block, DataGenBlock dataGenBlock, TexturedModel.Factory modelFactory, BlockStateModelGenerator generator)
		{
			switch (dataGenBlock.rotation())
			{
				case Default -> generator.registerSingleton(block, modelFactory);
				case RandomRotationX ->
				{
					Identifier id = modelFactory.upload(block, generator.modelCollector);
					var blockStateSupplier = MultipartBlockModelDefinitionCreator.create(block).with(new WeightedVariant(Pool.of(
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R0), 1),
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R90), 1),
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R180), 1),
							new Weighted<>(new ModelVariant(id).withRotationX(AxisRotation.R270), 1)
					)));
					generator.blockStateCollector.accept(blockStateSupplier);
				}
				case AxisRotated ->
				{
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

		private static void registerStairs(Block stairs, BlockStateModelGenerator generator)
		{
			Identifier stairsId = Models.STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);
			Identifier stairsOuterId = Models.OUTER_STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);
			Identifier stairsInnerId = Models.INNER_STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);

			generator.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(stairs, createWeightedVariant(stairsInnerId), createWeightedVariant(stairsId), createWeightedVariant(stairsOuterId)));
			generator.registerItemModel(stairs);
		}

		private static void registerVerticalSlab(Block slab, BlockStateModelGenerator generator)
		{
			registerVerticalSlabAllTextures(slab, slab, generator);
		}

		private static void registerVerticalSlabAllTextures(Block textureBase, Block slab, BlockStateModelGenerator generator)
		{
			var textureId = TextureMap.getId(textureBase);
			var textureMap = new TextureMap().put(TextureKey.SIDE, textureId).put(TextureKey.TOP, textureId).put(TextureKey.END, textureId);
			Identifier bottomId = Models.SLAB.upload(slab, textureMap, generator.modelCollector);
			Identifier topId = Models.SLAB_TOP.upload(slab, "_top", textureMap, generator.modelCollector);
			Identifier doubleId = Models.CUBE_COLUMN.upload(slab, "_double", textureMap, generator.modelCollector);

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

		private static void registerVerticalLightingSlab(BlockStateModelGenerator generator)
		{

			var textureIdTopBottom = TextureMap.getId(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_3);
			var slab = GalaxiesBlocks.GRAY_IMPERIAL_LIGHTING_SLAB;
			var textureIdSide = TextureMap.getId(GalaxiesBlocks.GRAY_IMPERIAL_LIGHTING_SLAB);
			var textureMap = new TextureMap().put(TextureKey.SIDE, textureIdSide).put(TextureKey.TOP, textureIdTopBottom).put(TextureKey.END, textureIdTopBottom);
			var textureMapOn = new TextureMap().put(TextureKey.SIDE, textureIdSide.withSuffixedPath("_on")).put(TextureKey.TOP, textureIdTopBottom).put(TextureKey.END, textureIdTopBottom);
			Identifier bottomId = Models.SLAB.upload(slab, textureMap, generator.modelCollector);
			Identifier bottomIdOn = Models.SLAB.upload(slab, "_on", textureMapOn, generator.modelCollector);
			Identifier topId = Models.SLAB_TOP.upload(slab, "", textureMap, generator.modelCollector);
			Identifier topIdOn = Models.SLAB_TOP.upload(slab, "_on", textureMapOn, generator.modelCollector);
			Identifier doubleId = Models.CUBE_COLUMN.upload(slab, "_double", textureMap, generator.modelCollector);
			Identifier doubleIdOn = Models.CUBE_COLUMN.upload(slab, "_double_on", textureMapOn, generator.modelCollector);

			var blockState = VariantsBlockModelDefinitionCreator.of(slab, createWeightedVariant(bottomId)).
			                                                    apply(BlockStateVariantMap.operations(Properties.AXIS)
			                                                                              .register(Direction.Axis.Y, ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R0))
			                                                                              .register(Direction.Axis.Z, ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270))
			                                                                              .register(Direction.Axis.X, ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90).then(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
			                                                    ).apply(BlockStateVariantMap.operations(Properties.SLAB_TYPE, Properties.LIT)
			                                                                                .register(SlabType.BOTTOM, false, ModelVariantOperator.MODEL.withValue(bottomId))
			                                                                                .register(SlabType.BOTTOM, true, ModelVariantOperator.MODEL.withValue(bottomIdOn))
			                                                                                .register(SlabType.DOUBLE, false, ModelVariantOperator.MODEL.withValue(doubleId))
			                                                                                .register(SlabType.DOUBLE, true, ModelVariantOperator.MODEL.withValue(doubleIdOn))
			                                                                                .register(SlabType.TOP, false, ModelVariantOperator.MODEL.withValue(topId))
			                                                                                .register(SlabType.TOP, true, ModelVariantOperator.MODEL.withValue(topIdOn))
					);
			generator.blockStateCollector.accept(blockState);
		}

		private static Model blockModel(String parent, TextureKey... requiredTextureKeys)
		{
			return new Model(Optional.of(Galaxies.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
		}

		private static void registerLightingPanel(Block block, BlockStateModelGenerator generator)
		{
			Identifier identifier = lightingPanelFactory().upload(block, generator.modelCollector);
			Identifier identifier2 = generator.createSubModel(block, "_on", Models.CUBE_COLUMN, ModelGenerator::createLightingPanelTextureMap);

			generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, createWeightedVariant(identifier2), createWeightedVariant(identifier))));
		}

		public static TexturedModel.Factory lightingPanelFactory()
		{
			return TexturedModel.makeFactory(ModelGenerator::createLightingPanelTextureMap, Models.CUBE_COLUMN);
		}

		public static TextureMap createLightingPanelTextureMap(Block block)
		{
			return createLightingPanelTextureMap(TextureMap.getId(block));
		}

		public static TextureMap createLightingPanelTextureMap(Identifier identifier)
		{
			return new TextureMap().put(TextureKey.SIDE, identifier).put(TextureKey.END, TextureMap.getId(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_3));
		}

		public static final void registerCorrugatedCrate(BlockStateModelGenerator generator, Block block)
		{
			var crateKey = getCorrugatedCrateKey(block).withPrefixedPath("block/model/corrugated_crate/");
			TexturedModel.makeFactory(block1 -> TextureMap.all(crateKey).put(TextureKey.PARTICLE, crateKey.withSuffixedPath("_particle")), blockModel("template_corrugated_crate", TextureKey.ALL, TextureKey.PARTICLE)).upload(block, generator.modelCollector);
			generator.registerSimpleState(block);
		}

		public static Identifier getBlockKey(Block block)
		{
			return block.getRegistryEntry().getKey().get().getValue();
		}

		public static Identifier getCorrugatedCrateKey(Block block)
		{
			String string = block.getRegistryEntry().getKey().get().getValue().toString();
			return Identifier.of(string.substring(0, string.indexOf("_corrugated_crate")));
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

		public static Identifier createItemKey(Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.textureOverride().equals(""))
				return item.getRegistryEntry().getKey().get().getValue().withPrefixedPath("item/");
			return Galaxies.id(dataGenItem.textureOverride()).withPrefixedPath("item/");
		}

		public void registerItem(ItemModelGenerator generator, Item item, DataGenItem dataGenItem)
		{
			if (dataGenItem.genModel())
			{
				if (dataGenItem.wiz())
					GalaxiesModelProvider.register(generator, item, Galaxies.id("item/wizard"), Models.GENERATED);
				else
					switch (dataGenItem.model())
					{
						case generated -> GalaxiesModelProvider.register(generator, item, createItemKey(item, dataGenItem), Models.GENERATED);
						case handheld -> GalaxiesModelProvider.register(generator, item, createItemKey(item, dataGenItem), Models.HANDHELD);
						case drink -> registerDrink(generator, item, dataGenItem);
					}
			}
		}

		public void registerDrink(ItemModelGenerator generator, Item item, DataGenItem dataGenItem)
		{
			Identifier modelId;
			Identifier overlay = (dataGenItem.overlayTextureOverride().equals("")) ? Identifier.of(createItemKey(item, dataGenItem).withSuffixedPath("_overlay").toString().replace("_filled", "")) : Galaxies.id(dataGenItem.overlayTextureOverride()).withPrefixedPath("item/");
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
