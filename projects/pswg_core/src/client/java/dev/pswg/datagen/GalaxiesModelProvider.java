package dev.pswg.datagen;

import com.mojang.math.Quadrant;
import dev.pswg.block.collection.*;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.item.SwgDrinkTintSource;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
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
	protected static void register(ItemModelGenerators itemModelGenerator, Item item, ResourceLocation texture, ModelTemplate model)
	{
		register(itemModelGenerator, item, ItemModelUtils.plainModel(registerDisjointModel(itemModelGenerator, item, texture, model)));
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
	protected static ResourceLocation registerDisjointModel(ItemModelGenerators itemModelGenerator, Item item, ResourceLocation texture, ModelTemplate model)
	{
		return model.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(texture), itemModelGenerator.modelOutput);
	}

	/**
	 * Registers an item model with the provided item model generator.
	 *
	 * @param itemModelGenerator The generator into which the item will be uploaded
	 * @param item               The item to generate a model for
	 * @param model              The unbaked model that will be used for the item
	 */
	protected static void register(ItemModelGenerators itemModelGenerator, Item item, ItemModel.Unbaked model)
	{
		itemModelGenerator.itemModelOutput.accept(item, model);
	}

	public static MultiVariant createWeightedVariant(ResourceLocation id)
	{
		return new MultiVariant(WeightedList.of(new Variant(id)));
	}

	protected static void registerReducedDryingRuinedStoneProducts(ReducedDryingRuiningStoneProducts dryingRuinedStoneProducts, BlockModelGenerators generator)
	{
		generator.family(dryingRuinedStoneProducts.block)
		         .stairs(dryingRuinedStoneProducts.stairs);
		registerVerticalSlabAllTextures(dryingRuinedStoneProducts.block, dryingRuinedStoneProducts.slab, generator);
	}

	protected static void registerReducedDryingStoneProducts(ReducedDryingStoneProducts dryingStoneProducts, BlockModelGenerators generator)
	{
		generator.family(dryingStoneProducts.block)
		         .stairs(dryingStoneProducts.stairs);
		registerVerticalSlabAllTextures(dryingStoneProducts.block, dryingStoneProducts.slab, generator);
	}

	protected static void registerReducedStoneProducts(ReducedStoneProducts stoneProducts, BlockModelGenerators generator)
	{
		generator.family(stoneProducts.block)
		         .stairs(stoneProducts.stairs);
		registerVerticalSlabAllTextures(stoneProducts.block, stoneProducts.slab, generator);
	}

	protected static void registerStoneProducts(StoneProducts stoneProducts, BlockModelGenerators generator)
	{
		generator.family(stoneProducts.block)
		         .wall(stoneProducts.wall)
		         .stairs(stoneProducts.stairs);
		registerVerticalSlabAllTextures(stoneProducts.block, stoneProducts.slab, generator);
	}

	protected static void registerWoodProducts(WoodProducts woodProducts, BlockModelGenerators generator)
	{
		generator.family(woodProducts.plank)
		         .fence(woodProducts.fence)
		         .fenceGate(woodProducts.gate)
		         .stairs(woodProducts.stairs);
		generator.createDoor(woodProducts.door);
		generator.createTrapdoor(woodProducts.trapdoor);

		registerVerticalSlabAllTextures(woodProducts.plank, woodProducts.slab, generator);
	}

	protected static void registerAccumulatingBlock(Block block, BlockModelGenerators generator)
	{
		var id = TextureMapping.getBlockTexture(block);

		generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(BlockStateProperties.LAYERS).generate(
				height -> {
					ResourceLocation modelId = TexturedModel.createDefault(block1 -> TextureMapping.cube(id).put(TextureSlot.PARTICLE, id), blockModel("template_accumulating_height" + height * 2, TextureSlot.ALL, TextureSlot.PARTICLE)).createWithSuffix(block, "_height" + height * 2, generator.modelOutput);
					return createWeightedVariant(modelId);
				}))
		);
		generator.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block, "_height2"));
	}

	protected static void registerCrossAge3(Block block, BlockModelGenerators generator)
	{
		generator.registerSimpleFlatItemModel(block, "_stage3");
		generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
		                                                                        .with(PropertyDispatch.initial(BlockStateProperties.AGE_3)
		                                                                                                  .generate(stage -> createWeightedVariant(generator.createSuffixedVariant(block, "_stage" + stage, ModelTemplates.CROSS, TextureMapping::cross)))));
	}

	protected static void registerCrossAge3Blooming(Block block, BlockModelGenerators generator)
	{
		generator.registerSimpleFlatItemModel(block, "_stage3_blooming");
		generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
		                                                                        .with(PropertyDispatch.initial(BlockStateProperties.AGE_3, BlockStateProperties.BLOOM)
		                                                                                                  .generate((stage, blooming) -> {
			                                                                                                  String suffix = blooming ? ("_stage" + stage + "_blooming") : ("_stage" + stage);
			                                                                                                  return createWeightedVariant(generator.createSuffixedVariant(block, suffix, ModelTemplates.CROSS, TextureMapping::cross));
		                                                                                                  }))
		);
	}

	protected static void registerJaporLeaves(Block block, BlockModelGenerators generator)
	{
		ModelTemplate model = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(namespace, ("block/template_japor_leaves"))), Optional.empty(), TextureSlot.TEXTURE);
		TexturedModel texturedModel = TexturedModel.createDefault(TextureMapping::defaultTexture, model).get(block);
		MultiVariant weightedVariant = createWeightedVariant(texturedModel.create(block, generator.modelOutput));
		generator.blockStateOutput.accept(
				MultiVariantGenerator.dispatch(block, weightedVariant).with(PropertyDispatch.modify(BlockStateProperties.FACING)
				                                                                                         .select(Direction.DOWN, BlockModelGenerators.X_ROT_180)
				                                                                                         .select(Direction.UP, BlockModelGenerators.NOP)
				                                                                                         .select(Direction.EAST, BlockModelGenerators.Y_ROT_90.then(BlockModelGenerators.X_ROT_90))
				                                                                                         .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180.then(BlockModelGenerators.X_ROT_90))
				                                                                                         .select(Direction.WEST, BlockModelGenerators.Y_ROT_270.then(BlockModelGenerators.X_ROT_90))
				                                                                                         .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)));
		generator.registerSimpleFlatItemModel(block);
	}

	protected static void registerLog(Block block, DataGenBlock dataGenBlock, BlockModelGenerators generator)
	{
		String logKey = getBlockKey(block).toString();
		BlockModelGenerators.WoodProvider texturePool = generator.woodProvider(block).logWithHorizontal(block);
		if (dataGenBlock.model() == DataGenBlockModel.LOG_WITH_WOOD)
		{
			String woodKey = logKey.substring(0, logKey.indexOf("_log")) + "_wood";
			Block woodBlock = BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(woodKey));
			texturePool.wood(woodBlock);
		}
	}

	protected static void registerCubeWithRotation(Block block, DataGenBlock dataGenBlock, TexturedModel.Provider modelFactory, BlockModelGenerators generator)
	{
		switch (dataGenBlock.rotation())
		{
			case DEFAULT -> generator.createTrivialBlock(block, modelFactory);
			case RANDOM_ROTATION_X ->
			{
				ResourceLocation id = modelFactory.create(block, generator.modelOutput);
				var blockStateSupplier = MultiPartGenerator.multiPart(block).with(new MultiVariant(WeightedList.of(
						new Weighted<>(new Variant(id).withXRot(Quadrant.R0), 1),
						new Weighted<>(new Variant(id).withXRot(Quadrant.R90), 1),
						new Weighted<>(new Variant(id).withXRot(Quadrant.R180), 1),
						new Weighted<>(new Variant(id).withXRot(Quadrant.R270), 1)
				)));
				generator.blockStateOutput.accept(blockStateSupplier);
			}
			case AXIS_ROTATED ->
			{
				ResourceLocation id = modelFactory.create(block, generator.modelOutput);
				var blockStateSupplier = MultiVariantGenerator.dispatch(block, createWeightedVariant(id)).with(PropertyDispatch.modify(BlockStateProperties.AXIS)
				                                                                                                                            .select(Direction.Axis.Y, VariantMutator.MODEL.withValue(id))
				                                                                                                                            .select(Direction.Axis.Z, VariantMutator.MODEL.withValue(id)
				                                                                                                                                                                                  .then(VariantMutator.X_ROT.withValue(Quadrant.R90)))
				                                                                                                                            .select(Direction.Axis.X, VariantMutator.MODEL.withValue(id)
				                                                                                                                                                                                  .then(VariantMutator.X_ROT.withValue(Quadrant.R90))
				                                                                                                                                                                                  .then(VariantMutator.Y_ROT.withValue(Quadrant.R90))
				                                                                                                                            )
				);
				generator.blockStateOutput.accept(blockStateSupplier);
			}
		}
	}

	protected static void registerStairs(Block stairs, BlockModelGenerators generator)
	{
		ResourceLocation stairsId = ModelTemplates.STAIRS_STRAIGHT.create(stairs, TextureMapping.cube(stairs), generator.modelOutput);
		ResourceLocation stairsOuterId = ModelTemplates.STAIRS_OUTER.create(stairs, TextureMapping.cube(stairs), generator.modelOutput);
		ResourceLocation stairsInnerId = ModelTemplates.STAIRS_INNER.create(stairs, TextureMapping.cube(stairs), generator.modelOutput);

		generator.blockStateOutput.accept(BlockModelGenerators.createStairs(stairs, createWeightedVariant(stairsInnerId), createWeightedVariant(stairsId), createWeightedVariant(stairsOuterId)));
		generator.registerSimpleFlatItemModel(stairs);
	}

	protected static void registerVerticalSlab(Block slab, BlockModelGenerators generator)
	{
		registerVerticalSlabAllTextures(slab, slab, generator);
	}

	protected static void registerVerticalSlabAllTextures(Block textureBase, Block slab, BlockModelGenerators generator)
	{
		var textureId = TextureMapping.getBlockTexture(textureBase);
		var textureMap = new TextureMapping().put(TextureSlot.SIDE, textureId).put(TextureSlot.TOP, textureId).put(TextureSlot.END, textureId);
		ResourceLocation bottomId = ModelTemplates.SLAB_BOTTOM.create(slab, textureMap, generator.modelOutput);
		ResourceLocation topId = ModelTemplates.SLAB_TOP.createWithSuffix(slab, "_top", textureMap, generator.modelOutput);
		ResourceLocation doubleId = ModelTemplates.CUBE_COLUMN.createWithSuffix(slab, "_double", textureMap, generator.modelOutput);

		var blockState = MultiVariantGenerator.dispatch(slab, createWeightedVariant(bottomId)).
		                                                    with(PropertyDispatch.modify(BlockStateProperties.AXIS)
		                                                                              .select(Direction.Axis.Y, VariantMutator.X_ROT.withValue(Quadrant.R0))
		                                                                              .select(Direction.Axis.Z, VariantMutator.X_ROT.withValue(Quadrant.R270).then(VariantMutator.UV_LOCK.withValue(true)))
		                                                                              .select(Direction.Axis.X, VariantMutator.X_ROT.withValue(Quadrant.R90).then(VariantMutator.Y_ROT.withValue(Quadrant.R90)).then(VariantMutator.UV_LOCK.withValue(true)))
		                                                    ).with(PropertyDispatch.modify(BlockStateProperties.SLAB_TYPE)
		                                                                                .select(SlabType.BOTTOM, VariantMutator.MODEL.withValue(bottomId))
		                                                                                .select(SlabType.DOUBLE, VariantMutator.MODEL.withValue(doubleId))
		                                                                                .select(SlabType.TOP, VariantMutator.MODEL.withValue(topId))
				);
		generator.blockStateOutput.accept(blockState);
	}

	protected static void registerVerticalLightingSlab(BlockModelGenerators generator)
	{

		var textureIdTopBottom = TextureMapping.getBlockTexture(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_3);
		var slab = GalaxiesBlocks.GRAY_IMPERIAL_LIGHTING_SLAB;
		var textureIdSide = TextureMapping.getBlockTexture(GalaxiesBlocks.GRAY_IMPERIAL_LIGHTING_SLAB);
		var textureMap = new TextureMapping().put(TextureSlot.SIDE, textureIdSide).put(TextureSlot.TOP, textureIdTopBottom).put(TextureSlot.END, textureIdTopBottom);
		var textureMapOn = new TextureMapping().put(TextureSlot.SIDE, textureIdSide.withSuffix("_on")).put(TextureSlot.TOP, textureIdTopBottom).put(TextureSlot.END, textureIdTopBottom);
		ResourceLocation bottomId = ModelTemplates.SLAB_BOTTOM.create(slab, textureMap, generator.modelOutput);
		ResourceLocation bottomIdOn = ModelTemplates.SLAB_BOTTOM.createWithSuffix(slab, "_on", textureMapOn, generator.modelOutput);
		ResourceLocation topId = ModelTemplates.SLAB_TOP.createWithSuffix(slab, "", textureMap, generator.modelOutput);
		ResourceLocation topIdOn = ModelTemplates.SLAB_TOP.createWithSuffix(slab, "_on", textureMapOn, generator.modelOutput);
		ResourceLocation doubleId = ModelTemplates.CUBE_COLUMN.createWithSuffix(slab, "_double", textureMap, generator.modelOutput);
		ResourceLocation doubleIdOn = ModelTemplates.CUBE_COLUMN.createWithSuffix(slab, "_double_on", textureMapOn, generator.modelOutput);

		var blockState = MultiVariantGenerator.dispatch(slab, createWeightedVariant(bottomId)).
		                                                    with(PropertyDispatch.modify(BlockStateProperties.AXIS)
		                                                                              .select(Direction.Axis.Y, VariantMutator.X_ROT.withValue(Quadrant.R0))
		                                                                              .select(Direction.Axis.Z, VariantMutator.X_ROT.withValue(Quadrant.R270))
		                                                                              .select(Direction.Axis.X, VariantMutator.X_ROT.withValue(Quadrant.R90).then(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
		                                                    ).with(PropertyDispatch.modify(BlockStateProperties.SLAB_TYPE, BlockStateProperties.LIT)
		                                                                                .select(SlabType.BOTTOM, false, VariantMutator.MODEL.withValue(bottomId))
		                                                                                .select(SlabType.BOTTOM, true, VariantMutator.MODEL.withValue(bottomIdOn))
		                                                                                .select(SlabType.DOUBLE, false, VariantMutator.MODEL.withValue(doubleId))
		                                                                                .select(SlabType.DOUBLE, true, VariantMutator.MODEL.withValue(doubleIdOn))
		                                                                                .select(SlabType.TOP, false, VariantMutator.MODEL.withValue(topId))
		                                                                                .select(SlabType.TOP, true, VariantMutator.MODEL.withValue(topIdOn))
				);
		generator.blockStateOutput.accept(blockState);
	}

	protected static void registerLightingPanel(Block block, BlockModelGenerators generator)
	{
		ResourceLocation identifier = lightingPanelFactory().create(block, generator.modelOutput);
		ResourceLocation identifier2 = generator.createSuffixedVariant(block, "_on", ModelTemplates.CUBE_COLUMN, GalaxiesModelProvider::createLightingPanelTextureMap);

		generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, createWeightedVariant(identifier2), createWeightedVariant(identifier))));
	}

	public static TexturedModel.Provider lightingPanelFactory()
	{
		return TexturedModel.createDefault(GalaxiesModelProvider::createLightingPanelTextureMap, ModelTemplates.CUBE_COLUMN);
	}

	public static TextureMapping createLightingPanelTextureMap(Block block)
	{
		return createLightingPanelTextureMap(TextureMapping.getBlockTexture(block));
	}

	public static TextureMapping createLightingPanelTextureMap(ResourceLocation identifier)
	{
		return new TextureMapping().put(TextureSlot.SIDE, identifier).put(TextureSlot.END, TextureMapping.getBlockTexture(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_3));
	}

	public static final void registerCorrugatedCrate(BlockModelGenerators generator, Block block)
	{
		var crateKey = getCorrugatedCrateKey(block).withPrefix("block/model/corrugated_crate/");
		TexturedModel.createDefault(block1 -> TextureMapping.cube(crateKey).put(TextureSlot.PARTICLE, crateKey.withSuffix("_particle")), blockModel("template_corrugated_crate", TextureSlot.ALL, TextureSlot.PARTICLE)).create(block, generator.modelOutput);
		generator.createNonTemplateModelBlock(block);
	}

	protected static ModelTemplate blockModel(String parent, TextureSlot... requiredTextureKeys)
	{
		return new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(namespace, ("block/" + parent))), Optional.empty(), requiredTextureKeys);
	}

	public static ResourceLocation getBlockKey(Block block)
	{
		return block.builtInRegistryHolder().unwrapKey().get().location();
	}

	public static ResourceLocation getCorrugatedCrateKey(Block block)
	{
		String string = block.builtInRegistryHolder().unwrapKey().get().location().toString();
		return ResourceLocation.parse(string.substring(0, string.indexOf("_corrugated_crate")));
	}

	public void registerDrink(ItemModelGenerators generator, Item item, DataGenItem dataGenItem)
	{
		ResourceLocation modelId;
		ResourceLocation overlay = (dataGenItem.overlayTextureOverride().equals("")) ? ResourceLocation.parse(createItemKey(item, dataGenItem).withSuffix("_overlay").toString().replace("_filled", "")) : ResourceLocation.fromNamespaceAndPath(namespace, dataGenItem.overlayTextureOverride()).withPrefix("item/");
		ResourceLocation base = ResourceLocation.parse(ModelLocationUtils.getModelLocation(item).toString().replace("_filled", ""));

		if (dataGenItem.invertLayer())
		{
			modelId = generator.generateLayeredItem(item, base, overlay);
			generator.itemModelOutput.accept(item, ItemModelUtils.tintedModel(modelId, new Constant(16777215), new SwgDrinkTintSource()));
		}
		else
		{
			modelId = generator.generateLayeredItem(item, overlay, base);
			generator.itemModelOutput.accept(item, ItemModelUtils.tintedModel(modelId, new SwgDrinkTintSource()));
		}
	}

	public static ResourceLocation createItemKey(Item item, DataGenItem dataGenItem)
	{
		if (dataGenItem.textureOverride().equals(""))
			return item.builtInRegistryHolder().unwrapKey().get().location().withPrefix("item/");
		return ResourceLocation.fromNamespaceAndPath(namespace, dataGenItem.textureOverride()).withPrefix("item/");
	}
}
