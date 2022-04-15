package com.parzivail.datagen.tarkin;

import com.parzivail.pswg.container.registry.RegistryHelper;
import com.parzivail.util.block.IPicklingBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.item.Item;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockGenerator
{
	public static BlockGenerator basic(Block block)
	{
		return block(block)
				.lootTable(LootTableFile::singleSelf);
	}

	public static BlockGenerator basic(Block block, Identifier texture)
	{
		return block(block, texture)
				.lootTable(LootTableFile::singleSelf);
	}

	public static BlockGenerator blockNoModelLangEntry(Block block)
	{
		return new BlockGenerator(block)
				.lang(LanguageProvider::ofBlock);
	}

	public static BlockGenerator blockNoModel(Block block)
	{
		return new BlockGenerator(block);
	}

	public static BlockGenerator blockNoModelDefaultDrops(Block block)
	{
		return blockNoModelLangEntry(block)
				.lootTable(LootTableFile::singleSelf);
	}

	public static <T extends Block & IPicklingBlock> BlockGenerator blockNoModelPicklingDrops(T block)
	{
		return blockNoModelLangEntry(block)
				.lootTable(block1 -> LootTableFile.pickling(block));
	}

	public static BlockGenerator block(Block block)
	{
		return blockNoModelLangEntry(block)
				.state(BlockStateModelGenerator::createSingletonBlockState)
				.model(ModelFile::cube)
				.itemModel(ModelFile::ofBlock);
	}

	public static BlockGenerator block(Block block, Identifier texture)
	{
		return blockNoModelLangEntry(block)
				.state(BlockStateModelGenerator::createSingletonBlockState)
				.model(block1 -> ModelFile.cube(block1, texture))
				.itemModel(ModelFile::ofBlock);
	}

	public static BlockGenerator particleOnly(Block block, Identifier particle)
	{
		return blockNoModelLangEntry(block)
				.state(BlockStateModelGenerator::createSingletonBlockState)
				.model(block1 -> ModelFile.particle(block1, particle));
	}

	static BlockGenerator accumulatingLayers(Block block)
	{
		return blockNoModelLangEntry(block)
				.state(BlockStateGenerator::accumulatingLayers)
				.models(ModelFile::accumulatingLayers)
				.itemModel(ModelFile::ofAccumulatingBlock);
	}

	static BlockGenerator cropStages(Block block, Supplier<IntProperty> ageProp, Identifier itemTexture)
	{
		return blockNoModelLangEntry(block)
				.state((block1, modelId) -> BlockStateGenerator.stages(block1, modelId, ageProp.get()))
				.models(block1 -> ModelFile.cropStages(block1, ageProp.get()))
				.itemModel(block2 -> ModelFile.item(block2, itemTexture));
	}

	static BlockGenerator bushStages(Block block, Supplier<IntProperty> ageProp, Identifier itemTexture)
	{
		return blockNoModelLangEntry(block)
				.state((block1, modelId) -> BlockStateGenerator.stages(block1, modelId, ageProp.get()))
				.models(block1 -> ModelFile.bushStages(block1, ageProp.get()))
				.itemModel(block2 -> ModelFile.item(block2, itemTexture));
	}

	static BlockGenerator bloomingBushStages(Block block, Supplier<IntProperty> ageProp, Supplier<BooleanProperty> bloomingProp, Identifier itemTexture)
	{
		return blockNoModelLangEntry(block)
				.state((block1, modelId) -> BlockStateGenerator.bloomingStages(block1, modelId, ageProp.get(), bloomingProp.get()))
				.models(block1 -> ModelFile.bloomingBushStages(block1, ageProp.get()))
				.itemModel(block2 -> ModelFile.item(block2, itemTexture));
	}

	static BlockGenerator basicRandomRotation(Block block)
	{
		return basic(block).state(BlockStateModelGenerator::createBlockStateWithRandomHorizontalRotations);
	}

	static BlockGenerator leaves(Block block)
	{
		return basic(block).model(ModelFile::leaves);
	}

	static BlockGenerator trapdoor(Block block, Identifier texture)
	{
		return basic(block)
				.state(BlockStateGenerator::trapdoor)
				.models(block1 -> ModelFile.trapdoor(block, texture))
				.itemModel(block1 -> ModelFile.ofBlockDifferentParent(block, IdentifierUtil.concat(AssetGenerator.getTextureName(block1), "_bottom")))
				.blockTag(BlockTags.TRAPDOORS);
	}

	static BlockGenerator trapdoor(Block block)
	{
		return trapdoor(block, AssetGenerator.getTextureName(block));
	}

	static BlockGenerator door(Block block, Identifier itemTexture)
	{
		return block(block)
				.lootTable(LootTableFile::door)
				.state(BlockStateGenerator::door)
				.models(block1 -> ModelFile.door(block, AssetGenerator.getTextureName(block)))
				.itemModel(block1 -> ModelFile.item(block, itemTexture))
				.blockTag(BlockTags.DOORS);
	}

	static BlockGenerator column(Block block, Identifier topTexture)
	{
		return basic(block)
				.state(BlockStateModelGenerator::createAxisRotatedBlockState)
				.model(b -> ModelFile.column(b, topTexture, AssetGenerator.getTextureName(block)));
	}

	static BlockGenerator column(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return basic(block)
				.state(BlockStateModelGenerator::createAxisRotatedBlockState)
				.model(b -> ModelFile.column(b, topTexture, sideTexture));
	}

	static BlockGenerator staticColumn(Block block, Identifier topTexture)
	{
		return basic(block)
				.state(BlockStateModelGenerator::createSingletonBlockState)
				.model(b -> ModelFile.column(b, topTexture, AssetGenerator.getTextureName(block)));
	}

	static BlockGenerator staticColumn(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return basic(block)
				.state(BlockStateModelGenerator::createSingletonBlockState)
				.model(b -> ModelFile.column(b, topTexture, sideTexture));
	}

	static BlockGenerator staticColumnTopBottom(Block block)
	{
		return basic(block)
				.state(BlockStateModelGenerator::createSingletonBlockState)
				.model(ModelFile::columnTopBottom);
	}

	static BlockGenerator stairs(Block block, Identifier topTexture, Identifier sideTexture)
	{
		var id = AssetGenerator.getTextureName(block);
		var inner = IdentifierUtil.concat(id, "_inner");
		var outer = IdentifierUtil.concat(id, "_outer");
		return basic(block)
				.state((b, modelId) -> BlockStateModelGenerator.createStairsBlockState(b, inner, AssetGenerator.getTextureName(block), outer))
				.models(b -> ModelFile.stairs(b, topTexture, sideTexture));
	}

	static BlockGenerator basicRandomMirror(Block block)
	{
		var id = AssetGenerator.getTextureName(block);
		var mirrored = IdentifierUtil.concat(id, "_mirrored");
		return basic(block)
				.state((b, modelId) -> BlockStateModelGenerator.createBlockStateWithTwoModelAndRandomInversion(b, id, mirrored))
				.models(ModelFile::randomMirror);
	}

	static BlockGenerator stairs(Block block, Identifier texture)
	{
		return stairs(block, texture, texture);
	}

	static BlockGenerator verticalSlab(Block block, Identifier fullSlabModel, Identifier topTexture, Identifier sideTexture)
	{
		var top = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_top");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.createVerticalSlabBlockState(block, AssetGenerator.getTextureName(block), top, fullSlabModel))
				.models(b -> ModelFile.verticalSlab(b, topTexture, sideTexture));
	}

	static BlockGenerator wall(Block block, Identifier texture)
	{
		var id = AssetGenerator.getTextureName(block);
		return blockNoModelDefaultDrops(block)
				.state((b, modelId) -> BlockStateModelGenerator.createWallBlockState(block, IdentifierUtil.concat(id, "_post"), IdentifierUtil.concat(id, "_side"), IdentifierUtil.concat(id, "_side_tall")))
				.itemModel(b -> ModelFile.wallInventory(b, texture))
				.models(b -> ModelFile.wall(b, texture))
				.blockTag(BlockTags.WALLS);
	}

	static BlockGenerator slabUniqueDouble(Block block, Identifier fullSlabModel, Identifier topTexture, Identifier sideTexture)
	{
		var top = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_top");
		return basic(block)
				.state((b, modelId) -> BlockStateModelGenerator.createSlabBlockState(block, AssetGenerator.getTextureName(block), top, fullSlabModel))
				.models(b -> ModelFile.slabUniqueDouble(b, topTexture, sideTexture));
	}

	static BlockGenerator verticalSlab(Block block, Identifier model)
	{
		return verticalSlab(block, model, model, model);
	}

	static BlockGenerator fence(Block block, Identifier texture)
	{
		var post = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_post");
		var side = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_side");
		return basic(block)
				.state((b, modelId) -> BlockStateModelGenerator.createFenceBlockState(block, post, side))
				.models(b -> ModelFile.fence(b, texture));
	}

	static BlockGenerator fenceGate(Block block, Identifier texture)
	{
		var open = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_open");
		var wallClosed = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_wall");
		var wallOpen = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_wall_open");
		return basic(block)
				.state((b, modelId) -> BlockStateModelGenerator.createFenceGateBlockState(block, open, AssetGenerator.getTextureName(block), wallOpen, wallClosed))
				.models(b -> ModelFile.fenceGate(b, texture));
	}

	public static BlockGenerator tangentFan(Block block)
	{
		return basic(block)
				.state(BlockStateGenerator::tangentRotating)
				.model(ModelFile::fan)
				.itemModel(ModelFile::item);
	}

	public static BlockGenerator tangentFans(Block block)
	{
		return basic(block)
				.state((block1, model) -> BlockStateGenerator.tangentRotating(block1, IdentifierUtil.concat(model, "_wall"), model))
				.models(ModelFile::fans)
				.itemModel(ModelFile::item);
	}

	static BlockGenerator cross(Block block)
	{
		return basic(block)
				.model(ModelFile::cross)
				.itemModel(ModelFile::item);
	}

	static BlockGenerator tangentCross(Block block)
	{
		return basic(block)
				.state(BlockStateGenerator::tangentRotating)
				.model(ModelFile::cross)
				.itemModel(ModelFile::item);
	}

	static BlockGenerator tintedCross(Block block)
	{
		return basic(block)
				.model(ModelFile::tintedCross)
				.itemModel(ModelFile::item);
	}

	public static BlockGenerator basicDropMany(Block block, Item item, int min, int max)
	{
		return BlockGenerator.basic(block)
		                     .lootTable(block1 -> LootTableFile.many(block1, item, new LootTableFile.Pool.CountFunction.Range(min, max, new Identifier("uniform"))));
	}

	public static BlockGenerator basicDropFortuneBonus(Block block, Item item)
	{
		return BlockGenerator.basic(block).lootTable(block1 -> LootTableFile.singleFortuneBonus(block1, item));
	}

	public static void basicStoneProducts(RegistryHelper.StoneProducts variants, TagKey<Block> miningTag, List<BuiltAsset> assets)
	{
		stoneProducts(variants, BlockGenerator::basic, miningTag, assets);
	}

	public static void stoneProducts(RegistryHelper.StoneProducts variants, Function<Block, BlockGenerator> generatorFunction, TagKey<Block> miningTag, List<BuiltAsset> assets)
	{
		var id = AssetGenerator.getTextureName(variants.block);
		generatorFunction.apply(variants.block)
		                 .blockTag(miningTag)
		                 .build(assets);
		BlockGenerator.verticalSlab(variants.slab, id)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.SLABS)
		              .build(assets);
		BlockGenerator.stairs(variants.stairs, id)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.STAIRS)
		              .build(assets);
		BlockGenerator.wall(variants.wall, id)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.WALLS)
		              .build(assets);
	}

	public static void basicWoodProducts(RegistryHelper.WoodProducts variants, TagKey<Block> miningTag, List<BuiltAsset> assets)
	{
		woodProducts(variants, BlockGenerator::basic, miningTag, assets);
	}

	public static void woodProducts(RegistryHelper.WoodProducts variants, Function<Block, BlockGenerator> generatorFunction, TagKey<Block> miningTag, List<BuiltAsset> assets)
	{
		var id = AssetGenerator.getTextureName(variants.plank);
		generatorFunction.apply(variants.plank)
		                 .blockTag(miningTag)
		                 .build(assets);
		BlockGenerator.verticalSlab(variants.slab, id)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.SLABS)
		              .build(assets);
		BlockGenerator.stairs(variants.stairs, id)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.STAIRS)
		              .build(assets);
		BlockGenerator.fence(variants.fence, id)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.FENCES)
		              .build(assets);
		BlockGenerator.fenceGate(variants.gate, id)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.FENCE_GATES)
		              .build(assets);
		BlockGenerator.trapdoor(variants.trapdoor)
		              .blockTag(miningTag)
		              .blockTag(BlockTags.TRAPDOORS)
		              .build(assets);
		BlockGenerator.door(variants.door, AssetGenerator.getTextureName(variants.door.asItem()))
		              .blockTag(miningTag)
		              .blockTag(BlockTags.DOORS)
		              .build(assets);
	}

	@FunctionalInterface
	public interface BlockStateSupplierFunc
	{
		BlockStateSupplier apply(Block block, Identifier modelId);
	}

	private final Block block;

	private BlockStateSupplier stateSupplier;
	private ModelFile itemModel;
	private LanguageProvider languageProvider;
	private final List<TagKey<Block>> blockTags;
	private final List<TagKey<?>> itemTags;

	private final Collection<ModelFile> blockModels;
	private final Collection<LootTableFile> lootTables;

	BlockGenerator(Block block)
	{
		this.block = block;

		this.blockModels = new ArrayList<>();
		this.lootTables = new ArrayList<>();

		this.blockTags = new ArrayList<>();
		this.itemTags = new ArrayList<>();
	}

	public Block getBlock()
	{
		return block;
	}

	private Identifier getRegistryName()
	{
		return Registry.BLOCK.getId(block);
	}

	public void build(List<BuiltAsset> assets)
	{
		var regName = getRegistryName();

		// blockstate
		if (stateSupplier != null)
			assets.add(BuiltAsset.blockstate(regName, stateSupplier.get()));

		if (languageProvider != null)
			assets.add(languageProvider.build());

		// models
		blockModels.forEach(modelFile -> assets.add(BuiltAsset.blockModel(modelFile.getId(), modelFile.build())));

		if (itemModel != null)
			assets.add(BuiltAsset.itemModel(itemModel.getId(), itemModel.build()));

		for (TagKey<Block> tag : blockTags)
			TagGenerator.forObject("tags/blocks", tag, regName).build(assets);

		for (var tag : itemTags)
			TagGenerator.forObject("tags/items", tag, regName).build(assets);

		// loot tables
		lootTables.forEach(lootTableFile -> assets.add(BuiltAsset.lootTable(lootTableFile.filename, lootTableFile.build())));
	}

	public BlockGenerator lang(Function<Block, LanguageProvider> languageFunc)
	{
		languageProvider = languageFunc.apply(block);
		return this;
	}

	public BlockGenerator state(BlockStateSupplierFunc stateSupplierFunc, Identifier modelId)
	{
		this.stateSupplier = stateSupplierFunc.apply(block, modelId);
		return this;
	}

	public BlockGenerator state(BlockStateSupplierFunc stateSupplierFunc)
	{
		return state(stateSupplierFunc, AssetGenerator.getTextureName(block));
	}

	public BlockGenerator models(Function<Block, Collection<ModelFile>> modelFunc)
	{
		this.blockModels.addAll(modelFunc.apply(block));
		return this;
	}

	public BlockGenerator model(Function<Block, ModelFile> modelFunc)
	{
		this.blockModels.add(modelFunc.apply(block));
		return this;
	}

	public BlockGenerator itemModel(Function<Block, ModelFile> modelFunc)
	{
		this.itemModel = modelFunc.apply(block);
		return this;
	}

	public BlockGenerator lootTable(Function<Block, LootTableFile> lootTableFunc)
	{
		this.lootTables.add(lootTableFunc.apply(block));
		return this;
	}

	public BlockGenerator blockTag(TagKey<Block> tag)
	{
		this.blockTags.add(tag);
		return this;
	}

	public BlockGenerator itemTag(TagKey<Item> tag)
	{
		this.itemTags.add(tag);
		return this;
	}
}
