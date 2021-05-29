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

	public static ItemGenerator itemNoModelLangEntry(Item block)
	{
		return itemNoModel(block)
				.lang(LanguageProvider::ofItem);
	}

	static ItemGenerator basic(Item item)
	{
		return itemNoModelLangEntry(item)
				.model(ModelFile::item);
	}

	static ItemGenerator spawn_egg(Item item)
	{
		return itemNoModelLangEntry(item)
				.model(ModelFile::spawn_egg);
	}

	static ItemGenerator empty(Item block)
	{
		return itemNoModelLangEntry(block)
				.model(ModelFile::empty);
	}

	private LanguageProvider languageProvider;

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

	public ItemGenerator lang(Function<Item, LanguageProvider> languageFunc)
	{
		languageProvider = languageFunc.apply(item);
		return this;
	}

	public void build(List<BuiltAsset> assets)
	{
		// models
		itemModels.forEach(modelFile -> assets.add(BuiltAsset.itemModel(modelFile.getId(), modelFile.build())));

		if (languageProvider != null)
			assets.add(languageProvider.build());
	}
}
