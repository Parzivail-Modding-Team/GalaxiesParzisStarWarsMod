package com.parzivail.datagen.tarkin;

import net.minecraft.block.Block;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.item.Item;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
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

	public static BlockGenerator block(Block block)
	{
		return blockNoModelLangEntry(block)
				.state(BlockStateGenerator::basic)
				.model(ModelFile::cube)
				.itemModel(ModelFile::ofBlock);
	}

	public static BlockGenerator particleOnly(Block block, Identifier particle)
	{
		return blockNoModelLangEntry(block)
				.state(BlockStateGenerator::basic)
				.model(block1 -> ModelFile.particle(block1, particle));
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
		return basic(block).state(BlockStateGenerator::randomRotation);
	}

	static BlockGenerator leaves(Block block)
	{
		return basic(block).model(ModelFile::leaves);
	}

	static BlockGenerator column(Block block, Identifier topTexture)
	{
		return basic(block)
				.state(BlockStateGenerator::column)
				.model(b -> ModelFile.column(b, topTexture, AssetGenerator.getTextureName(block)));
	}

	static BlockGenerator column(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return basic(block)
				.state(BlockStateGenerator::column)
				.model(b -> ModelFile.column(b, topTexture, sideTexture));
	}

	static BlockGenerator staticColumn(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return basic(block)
				.state(BlockStateGenerator::basic)
				.model(b -> ModelFile.column(b, topTexture, sideTexture));
	}

	static BlockGenerator stairs(Block block, Identifier topTexture, Identifier sideTexture)
	{
		var id = AssetGenerator.getTextureName(block);
		var inner = IdentifierUtil.concat(id, "_inner");
		var outer = IdentifierUtil.concat(id, "_outer");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.stairs(b, inner, AssetGenerator.getTextureName(block), outer))
				.models(b -> ModelFile.stairs(b, topTexture, sideTexture));
	}

	static BlockGenerator basicRandomMirror(Block block)
	{
		var id = AssetGenerator.getTextureName(block);
		var mirrored = IdentifierUtil.concat(id, "_mirrored");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.randomMirror(b, id, mirrored))
				.models(ModelFile::randomMirror);
	}

	static BlockGenerator stairs(Block block, Identifier texture)
	{
		return stairs(block, texture, texture);
	}

	static BlockGenerator slab(Block block, Identifier fullSlabModel, Identifier topTexture, Identifier sideTexture)
	{
		var top = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_top");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.slab(block, AssetGenerator.getTextureName(block), top, fullSlabModel))
				.models(b -> ModelFile.slab(b, topTexture, sideTexture));
	}

	static BlockGenerator slabUniqueDouble(Block block, Identifier fullSlabModel, Identifier topTexture, Identifier sideTexture)
	{
		var top = IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_top");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.slab(block, AssetGenerator.getTextureName(block), top, fullSlabModel))
				.models(b -> ModelFile.slabUniqueDouble(b, topTexture, sideTexture));
	}

	static BlockGenerator slab(Block block, Identifier model)
	{
		return slab(block, model, model, model);
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

	@FunctionalInterface
	public interface BlockStateSupplierFunc
	{
		BlockStateSupplier apply(Block block, Identifier modelId);
	}

	private final Block block;

	private BlockStateSupplier stateSupplier;
	private ModelFile itemModel;
	private LanguageProvider languageProvider;

	private final Collection<ModelFile> blockModels;
	private final Collection<LootTableFile> lootTables;

	BlockGenerator(Block block)
	{
		this.block = block;

		this.blockModels = new ArrayList<>();
		this.lootTables = new ArrayList<>();
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
		// blockstate
		if (stateSupplier != null)
			assets.add(BuiltAsset.blockstate(getRegistryName(), stateSupplier.get()));

		if (languageProvider != null)
			assets.add(languageProvider.build());

		// models
		blockModels.forEach(modelFile -> assets.add(BuiltAsset.blockModel(modelFile.getId(), modelFile.build())));

		if (itemModel != null)
			assets.add(BuiltAsset.itemModel(itemModel.getId(), itemModel.build()));

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
}
