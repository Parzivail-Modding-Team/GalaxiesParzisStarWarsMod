package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SwgRecipeSerializers
{
	public static final RecipeSerializer<VaporatorRecipe> Vaporator = Registry.register(Registries.RECIPE_SERIALIZER, Resources.id("vaporator"), new VaporatorRecipe.Serializer());

	public static void register()
	{
	}
}
