package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.Registry;

public class SwgRecipeSerializers
{
	public static final RecipeSerializer<VaporatorRecipe> Vaporator = Registry.register(Registry.RECIPE_SERIALIZER, Resources.identifier("vaporator"), new VaporatorRecipe.Serializer());

	public static void register()
	{
	}
}
