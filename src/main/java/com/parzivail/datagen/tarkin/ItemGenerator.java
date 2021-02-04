package com.parzivail.datagen.tarkin;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ItemGenerator
{
	public static ItemGenerator itemNoModel(Item block)
	{
		return new ItemGenerator(block);
	}

	static ItemGenerator basic(Item item)
	{
		return itemNoModel(item)
				.model(ModelFile::item);
	}

	static ItemGenerator empty(Item block)
	{
		return itemNoModel(block)
				.model(ModelFile::empty);
	}

	private final Item item;
	private final Collection<ModelFile> itemModels;

	ItemGenerator(Item item)
	{
		this.item = item;

		itemModels = new ArrayList<>();
	}

	public ItemGenerator model(Function<Item, ModelFile> modelFunc)
	{
		this.itemModels.add(modelFunc.apply(item));
		return this;
	}

	public void build(List<BuiltAsset> assets)
	{
		// models
		itemModels.forEach(modelFile -> assets.add(BuiltAsset.itemModel(modelFile.getId(), modelFile.build())));
	}
}
