package com.parzivail.datagen.tarkin;

import net.minecraft.block.Block;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BlockGenerator
{
	static BlockGenerator basic(Block block)
	{
		return AssetGenerator.block(block);
	}

	static BlockGenerator basicRandomRotation(Block block)
	{
		return AssetGenerator
				.block(block)
				.state(BlockStateGenerator::randomRotation);
	}

	static BlockGenerator leaves(Block block)
	{
		return AssetGenerator
				.block(block)
				.model(ModelGenerator::leaves);
	}

	static BlockGenerator column(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return AssetGenerator
				.block(block)
				.state(BlockStateGenerator::column)
				.model(b -> ModelGenerator.column(b, topTexture, sideTexture));
	}

	static BlockGenerator staticColumn(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return AssetGenerator
				.block(block)
				.state(BlockStateGenerator::basic)
				.model(b -> ModelGenerator.column(b, topTexture, sideTexture));
	}

	static BlockGenerator stairs(Block block, Identifier topTexture, Identifier sideTexture)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		Identifier inner = IdentifierUtil.concat("block/", id, "_inner");
		Identifier outer = IdentifierUtil.concat("block/", id, "_outer");
		return AssetGenerator
				.block(block)
				.state((b, modelId) -> BlockStateGenerator.stairs(b, inner, AssetGenerator.getTextureName(block), outer))
				.models(b -> ModelGenerator.stairs(b, topTexture, sideTexture));
	}

	static BlockGenerator stairs(Block block, Identifier texture)
	{
		return stairs(block, texture, texture);
	}

	static BlockGenerator slab(Block block, Identifier fullSlabModel, Identifier topTexture, Identifier sideTexture)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		Identifier top = IdentifierUtil.concat("block/", id, "_top");
		return AssetGenerator
				.block(block)
				.state((b, modelId) -> BlockStateGenerator.slab(block, AssetGenerator.getTextureName(block), top, fullSlabModel))
				.models(b -> ModelGenerator.slab(b, topTexture, sideTexture));
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
		return AssetGenerator
				.block(block)
				.state((b, modelId) -> BlockStateGenerator.slab(block, AssetGenerator.getTextureName(block), top, full))
				.models(b -> ModelGenerator.slabUniqueDouble(b, topTexture, sideTexture));
	}

	static BlockGenerator cross(Block block)
	{
		return AssetGenerator
				.block(block)
				.model(ModelGenerator::tintedCross);
	}

	@FunctionalInterface
	public interface BlockStateSupplierFunc
	{
		BlockStateSupplier apply(Block block, Identifier modelId);
	}

	private final Block block;

	private BlockStateSupplier stateSupplier;
	private Collection<ModelGenerator> blockModel;
	private ModelGenerator itemModel;

	BlockGenerator(Block block)
	{
		this.block = block;
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
		assets.add(BuiltAsset.blockstate(getRegistryName(), stateSupplier.get()));

		blockModel.forEach(modelGenerator -> assets.add(BuiltAsset.blockModel(modelGenerator.getId(), modelGenerator.build())));
		assets.add(BuiltAsset.itemModel(itemModel.getId(), itemModel.build()));
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

	public BlockGenerator models(Function<Block, Collection<ModelGenerator>> modelFunc)
	{
		this.blockModel = modelFunc.apply(block);
		return this;
	}

	public BlockGenerator model(Function<Block, ModelGenerator> modelFunc)
	{
		this.blockModel = Collections.singletonList(modelFunc.apply(block));
		return this;
	}

	public BlockGenerator itemModel(Function<Block, ModelGenerator> modelFunc)
	{
		this.itemModel = modelFunc.apply(block);
		return this;
	}
}
