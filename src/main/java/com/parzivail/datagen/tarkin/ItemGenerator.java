package com.parzivail.datagen.tarkin;

import net.minecraft.item.Item;

import java.util.List;
import java.util.function.Function;

public class ItemGenerator
{
	public static ItemGenerator itemNoModel(Item block)
	{
		return new ItemGenerator(block);
	}

	static ItemGenerator basic(Item block)
	{
		return itemNoModel(block)
				.model(ModelFile::item);
	}

	private final Item item;

	private ModelFile itemModel;

	ItemGenerator(Item item)
	{
		this.item = item;
	}

	public ItemGenerator model(Function<Item, ModelFile> modelFunc)
	{
		this.itemModel = modelFunc.apply(item);
		return this;
	}

	public void build(List<BuiltAsset> assets)
	{
		// models
		if (itemModel != null)
			assets.add(BuiltAsset.itemModel(itemModel.getId(), itemModel.build()));
	}
}
