package com.parzivail.datagen.tarkin.crafting;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class IngredientTag implements IJsonCraftingComponent
{
	private final Identifier tag;

	public IngredientTag(Identifier tag)
	{
		this.tag = tag;
	}

	@Override
	public JsonObject getIngredientObject()
	{
		JsonObject o = new JsonObject();
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

		IngredientTag that = (IngredientTag)o;

		return tag.equals(that.tag);
	}

	@Override
	public int hashCode()
	{
		return tag.hashCode();
	}
}
