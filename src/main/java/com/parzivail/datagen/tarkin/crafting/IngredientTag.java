package com.parzivail.datagen.tarkin.crafting;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public record IngredientTag(Identifier tag) implements IJsonCraftingComponent
{

	@Override
	public JsonObject getIngredientObject()
	{
		var o = new JsonObject();
		o.addProperty("tag", tag.toString());

		return o;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		var that = (IngredientTag)o;

		return tag.equals(that.tag);
	}

	@Override
	public int hashCode()
	{
		return tag.hashCode();
	}
}
