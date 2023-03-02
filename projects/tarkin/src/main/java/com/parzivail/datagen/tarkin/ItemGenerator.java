package com.parzivail.datagen.tarkin;

import com.parzivail.util.registry.ArmorItems;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ItemGenerator
{
	public static void armor(ArmorItems armorItems, List<BuiltAsset> assets)
	{
		basic(armorItems.helmet).build(assets);
		basic(armorItems.chestplate).build(assets);
		basic(armorItems.leggings).build(assets);
		basic(armorItems.boots).build(assets);
	}

	public static ItemGenerator itemNoModel(Item block)
	{
		return new ItemGenerator(block);
	}

	public static ItemGenerator itemNoModelLangEntry(Item block)
	{
		return itemNoModel(block)
				.lang(LanguageProvider::item);
	}

	public static ItemGenerator basic(Item item)
	{
		return itemNoModelLangEntry(item)
				.model(ModelFile::item);
	}

	public static ItemGenerator tool(Item item)
	{
		return itemNoModelLangEntry(item)
				.model(ModelFile::handheld_item);
	}

	public static ItemGenerator spawn_egg(Item item)
	{
		return itemNoModelLangEntry(item)
				.model(ModelFile::spawn_egg);
	}

	public static ItemGenerator empty(Item block)
	{
		return itemNoModelLangEntry(block)
				.model(ModelFile::empty);
	}

	private LanguageProvider languageProvider;
	private final List<TagKey<?>> tags;

	private final Item item;
	private final Collection<ModelFile> itemModels;

	ItemGenerator(Item item)
	{
		this.item = item;

		itemModels = new ArrayList<>();
		tags = new ArrayList<>();
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

	public ItemGenerator tag(TagKey<?> tag)
	{
		this.tags.add(tag);
		return this;
	}

	public void build(List<BuiltAsset> assets)
	{
		// models
		itemModels.forEach(modelFile -> assets.add(BuiltAsset.itemModel(modelFile.getId(), modelFile.build())));

		if (languageProvider != null)
			assets.add(languageProvider.build());

		for (var tag : tags)
			TagGenerator.forObject("tags/items", tag, Registries.ITEM.getId(item)).build(assets);
	}
}
