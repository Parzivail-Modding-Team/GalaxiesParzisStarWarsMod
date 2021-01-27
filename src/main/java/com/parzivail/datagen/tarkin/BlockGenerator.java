package com.parzivail.datagen.tarkin;

import net.minecraft.block.Block;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BlockGenerator
{
	static BlockGenerator basic(Block block)
	{
		return AssetGenerator.blockDefaultDrops(block);
	}

	static BlockGenerator basicRandomRotation(Block block)
	{
		return basic(block).state(BlockStateGenerator::randomRotation);
	}

	static BlockGenerator leaves(Block block)
	{
		return basic(block).model(ModelFile::leaves);
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
		Identifier id = AssetGenerator.getRegistryName(block);
		Identifier inner = IdentifierUtil.concat("block/", id, "_inner");
		Identifier outer = IdentifierUtil.concat("block/", id, "_outer");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.stairs(b, inner, AssetGenerator.getTextureName(block), outer))
				.models(b -> ModelFile.stairs(b, topTexture, sideTexture));
	}

	static BlockGenerator stairs(Block block, Identifier texture)
	{
		return stairs(block, texture, texture);
	}

	static BlockGenerator slab(Block block, Identifier fullSlabModel, Identifier topTexture, Identifier sideTexture)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		Identifier top = IdentifierUtil.concat("block/", id, "_top");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.slab(block, AssetGenerator.getTextureName(block), top, fullSlabModel))
				.models(b -> ModelFile.slab(b, topTexture, sideTexture));
	}

	static BlockGenerator slab(Block block, Identifier model)
	{
		return slab(block, model, model, model);
	}

	private static BlockGenerator slabUniqueDouble(Block block, Identifier topTexture, Identifier sideTexture)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		Identifier top = IdentifierUtil.concat("block/", id, "_top");
		Identifier full = IdentifierUtil.concat("block/", id, "_double");
		return basic(block)
				.state((b, modelId) -> BlockStateGenerator.slab(block, AssetGenerator.getTextureName(block), top, full))
				.models(b -> ModelFile.slabUniqueDouble(b, topTexture, sideTexture));
	}

	static BlockGenerator cross(Block block)
	{
		return basic(block).model(ModelFile::tintedCross);
	}

	@FunctionalInterface
	public interface BlockStateSupplierFunc
	{
		BlockStateSupplier apply(Block block, Identifier modelId);
	}

	private final Block block;

	private BlockStateSupplier stateSupplier;
	private Collection<ModelFile> blockModel;
	private ModelFile itemModel;
	private final Collection<LootTableFile> lootTables;

	BlockGenerator(Block block)
	{
		this.block = block;

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
		assets.add(BuiltAsset.blockstate(getRegistryName(), stateSupplier.get()));

		// models
		blockModel.forEach(modelFile -> assets.add(BuiltAsset.blockModel(modelFile.getId(), modelFile.build())));
		assets.add(BuiltAsset.itemModel(itemModel.getId(), itemModel.build()));

		// loot tables
		if (!lootTables.isEmpty())
			lootTables.forEach(lootTableFile -> assets.add(BuiltAsset.lootTable(lootTableFile.filename, lootTableFile.build())));
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
		this.blockModel = modelFunc.apply(block);
		return this;
	}

	public BlockGenerator model(Function<Block, ModelFile> modelFunc)
	{
		this.blockModel = Collections.singletonList(modelFunc.apply(block));
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
