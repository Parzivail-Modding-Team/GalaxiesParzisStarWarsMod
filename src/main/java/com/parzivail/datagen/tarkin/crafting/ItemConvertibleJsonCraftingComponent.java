package com.parzivail.datagen.tarkin.crafting;

import com.google.gson.JsonObject;
import com.parzivail.datagen.tarkin.AssetGenerator;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

public class ItemConvertibleJsonCraftingComponent implements IJsonCraftingComponent
{
	private final Identifier registryName;

	public ItemConvertibleJsonCraftingComponent(ItemConvertible itemConvertible)
	{
		this.registryName = AssetGenerator.getRegistryName(itemConvertible);
	}

	@Override
	public JsonObject getIngredientObject()
	{
		JsonObject o = new JsonObject();
		o.addProperty("item", registryName.toString());

		return o;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ItemConvertibleJsonCraftingComponent that = (ItemConvertibleJsonCraftingComponent)o;

		return registryName.equals(that.registryName);
	}

	@Override
	public int hashCode()
	{
		return registryName.hashCode();
	}
}
