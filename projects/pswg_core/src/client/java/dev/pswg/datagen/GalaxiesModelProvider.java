package dev.pswg.datagen;

import dev.pswg.Galaxies;
import dev.pswg.block.collection.*;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.item.SwgDrinkTintSource;
import dev.pswg.mixin.client.accessors.ItemModelGeneratorAccessor;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Optional;

/**
 * Provides extra utilities to register block and item models
 */
public abstract class GalaxiesModelProvider extends FabricModelProvider
{
	static String namespace;

	public GalaxiesModelProvider(FabricDataOutput output, String namespace)
	{
		super(output);
		GalaxiesModelProvider.namespace = namespace;
	}

	/**
	 * Generate a model for an item that points to a texture that does not derive from
	 * the item's identifier
	 *
	 * @param itemModelGenerator The generator into which the item will be uploaded
	 * @param item               The item to generate a model for
	 * @param texture            The texture to assign to the item
	 * @param model              The model that will be uploaded with the given texture
	 */
	protected static void register(ItemModelGenerator itemModelGenerator, Item item, Identifier texture, Model model)
	{
		register(itemModelGenerator, item, ItemModels.basic(registerDisjointModel(itemModelGenerator, item, texture, model)));
	}

	/**
	 * Registers a model for an item that points to a texture that does not derive from
	 * the item's identifier
	 *
	 * @param itemModelGenerator The generator into which the item model will be uploaded
	 * @param item               The item to generate a model for
	 * @param texture            The texture to assign to the item
	 * @param model              The model that will be uploaded with the given texture
	 *
	 * @return The identifier of the registered item model
	 */
	protected static Identifier registerDisjointModel(ItemModelGenerator itemModelGenerator, Item item, Identifier texture, Model model)
	{
		return model.upload(ModelIds.getItemModelId(item), TextureMap.layer0(texture), ((ItemModelGeneratorAccessor)itemModelGenerator).getModelCollector());
	}

	/**
	 * Registers an item model with the provided item model generator.
	 *
	 * @param itemModelGenerator The generator into which the item will be uploaded
	 * @param item               The item to generate a model for
	 * @param model              The unbaked model that will be used for the item
	 */
	protected static void register(ItemModelGenerator itemModelGenerator, Item item, ItemModel.Unbaked model)
	{
		((ItemModelGeneratorAccessor)itemModelGenerator).getOutput().accept(item, model);
	}

	public static WeightedVariant createWeightedVariant(Identifier id)
	{
		return new WeightedVariant(Pool.of(new ModelVariant(id)));
	}

	protected static void registerReducedDryingRuinedStoneProducts(ReducedDryingRuiningStoneProducts dryingRuinedStoneProducts, BlockStateModelGenerator generator)
	{
		generator.registerCubeAllModelTexturePool(dryingRuinedStoneProducts.block)
		         .stairs(dryingRuinedStoneProducts.stairs);
		registerVerticalSlabAllTextures(dryingRuinedStoneProducts.block, dryingRuinedStoneProducts.slab, generator);
	}

	protected static void registerReducedDryingStoneProducts(ReducedDryingStoneProducts dryingStoneProducts, BlockStateModelGenerator generator)
	{
		generator.registerCubeAllModelTexturePool(dryingStoneProducts.block)
		         .stairs(dryingStoneProducts.stairs);
		registerVerticalSlabAllTextures(dryingStoneProducts.block, dryingStoneProducts.slab, generator);
	}

	protected static void registerReducedStoneProducts(ReducedStoneProducts stoneProducts, BlockStateModelGenerator generator)
	{
		generator.registerCubeAllModelTexturePool(stoneProducts.block)
		         .stairs(stoneProducts.stairs);
		registerVerticalSlabAllTextures(stoneProducts.block, stoneProducts.slab, generator);
	}

	protected static void registerStoneProducts(StoneProducts stoneProducts, BlockStateModelGenerator generator)
	{
		generator.registerCubeAllModelTexturePool(stoneProducts.block)
		         .wall(stoneProducts.wall)
		         .stairs(stoneProducts.stairs);
		registerVerticalSlabAllTextures(stoneProducts.block, stoneProducts.slab, generator);
	}

	protected static void registerWoodProducts(WoodProducts woodProducts, BlockStateModelGenerator generator)
	{
		generator.registerCubeAllModelTexturePool(woodProducts.plank)
		         .fence(woodProducts.fence)
		         .fenceGate(woodProducts.gate)
		         .stairs(woodProducts.stairs);
		generator.registerDoor(woodProducts.door);
		generator.registerTrapdoor(woodProducts.trapdoor);

		registerVerticalSlabAllTextures(woodProducts.plank, woodProducts.slab, generator);
	}

	protected static void registerAccumulatingBlock(Block block, BlockStateModelGenerator generator)
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

	protected static void registerCrossAge3(Block block, BlockStateModelGenerator generator)
	{
		generator.registerItemModel(block, "_stage3");
		generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block)
		                                                                        .with(BlockStateVariantMap.models(Properties.AGE_3)
		                                                                                                  .generate(stage -> createWeightedVariant(generator.createSubModel(block, "_stage" + stage, Models.CROSS, TextureMap::cross)))));
	}

	protected static void registerCrossAge3Blooming(Block block, BlockStateModelGenerator generator)
	{
		generator.registerItemModel(block, "_stage3_blooming");
		generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block)
		                                                                        .with(BlockStateVariantMap.models(Properties.AGE_3, Properties.BLOOM)
		                                                                                                  .generate((stage, blooming) -> {
			                                                                                                  String suffix = blooming ? ("_stage" + stage + "_blooming") : ("_stage" + stage);
			                                                                                                  return createWeightedVariant(generator.createSubModel(block, suffix, Models.CROSS, TextureMap::cross));
		                                                                                                  }))
		);
	}

	protected static void registerJaporLeaves(Block block, BlockStateModelGenerator generator)
	{
		Model model = new Model(Optional.of(Identifier.of(namespace, ("block/template_japor_leaves"))), Optional.empty(), TextureKey.TEXTURE);
		TexturedModel texturedModel = TexturedModel.makeFactory(TextureMap::texture, model).get(block);
		WeightedVariant weightedVariant = createWeightedVariant(texturedModel.upload(block, generator.modelCollector));
		generator.blockStateCollector.accept(
				VariantsBlockModelDefinitionCreator.of(block, weightedVariant).apply(BlockStateVariantMap.operations(Properties.FACING)
				                                                                                         .register(Direction.DOWN, BlockStateModelGenerator.ROTATE_X_180)
				                                                                                         .register(Direction.UP, BlockStateModelGenerator.NO_OP)
				                                                                                         .register(Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90.then(BlockStateModelGenerator.ROTATE_X_90))
				                                                                                         .register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180.then(BlockStateModelGenerator.ROTATE_X_90))
				                                                                                         .register(Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270.then(BlockStateModelGenerator.ROTATE_X_90))
				                                                                                         .register(Direction.NORTH, BlockStateModelGenerator.ROTATE_X_90)));
		generator.registerItemModel(block);
	}

	protected static void registerLog(Block block, DataGenBlock dataGenBlock, BlockStateModelGenerator generator)
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

	protected static void registerCubeWithRotation(Block block, DataGenBlock dataGenBlock, TexturedModel.Factory modelFactory, BlockStateModelGenerator generator)
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

	protected static void registerStairs(Block stairs, BlockStateModelGenerator generator)
	{
		Identifier stairsId = Models.STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);
		Identifier stairsOuterId = Models.OUTER_STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);
		Identifier stairsInnerId = Models.INNER_STAIRS.upload(stairs, TextureMap.all(stairs), generator.modelCollector);

		generator.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(stairs, createWeightedVariant(stairsInnerId), createWeightedVariant(stairsId), createWeightedVariant(stairsOuterId)));
		generator.registerItemModel(stairs);
	}

	protected static void registerVerticalSlab(Block slab, BlockStateModelGenerator generator)
	{
		registerVerticalSlabAllTextures(slab, slab, generator);
	}

	protected static void registerVerticalSlabAllTextures(Block textureBase, Block slab, BlockStateModelGenerator generator)
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

	protected static void registerVerticalLightingSlab(BlockStateModelGenerator generator)
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

	protected static void registerLightingPanel(Block block, BlockStateModelGenerator generator)
	{
		Identifier identifier = lightingPanelFactory().upload(block, generator.modelCollector);
		Identifier identifier2 = generator.createSubModel(block, "_on", Models.CUBE_COLUMN, GalaxiesModelProvider::createLightingPanelTextureMap);

		generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, createWeightedVariant(identifier2), createWeightedVariant(identifier))));
	}

	public static TexturedModel.Factory lightingPanelFactory()
	{
		return TexturedModel.makeFactory(GalaxiesModelProvider::createLightingPanelTextureMap, Models.CUBE_COLUMN);
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

	protected static Model blockModel(String parent, TextureKey... requiredTextureKeys)
	{
		return new Model(Optional.of(Identifier.of(namespace, ("block/" + parent))), Optional.empty(), requiredTextureKeys);
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

	public void registerDrink(ItemModelGenerator generator, Item item, DataGenItem dataGenItem)
	{
		Identifier modelId;
		Identifier overlay = (dataGenItem.overlayTextureOverride().equals("")) ? Identifier.of(createItemKey(item, dataGenItem).withSuffixedPath("_overlay").toString().replace("_filled", "")) : Identifier.of(namespace, dataGenItem.overlayTextureOverride()).withPrefixedPath("item/");
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

	public static Identifier createItemKey(Item item, DataGenItem dataGenItem)
	{
		if (dataGenItem.textureOverride().equals(""))
			return item.getRegistryEntry().getKey().get().getValue().withPrefixedPath("item/");
		return Identifier.of(namespace, dataGenItem.textureOverride()).withPrefixedPath("item/");
	}
}
