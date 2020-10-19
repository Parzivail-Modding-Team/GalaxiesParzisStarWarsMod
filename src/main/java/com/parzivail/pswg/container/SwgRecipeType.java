package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SwgRecipeType
{
	public static final RecipeType<VaporatorRecipe> Vaporator = register(Resources.identifier("vaporator"));

	public static void register()
	{
	}

	private static <T extends Recipe<?>> RecipeTypeImpl<T> register(Identifier identifier)
	{
		return Registry.register(Registry.RECIPE_TYPE, identifier, new RecipeTypeImpl<>(identifier));
	}

	private static class RecipeTypeImpl<T extends Recipe<?>> implements RecipeType<T>
	{
		private final Identifier identifier;

		public RecipeTypeImpl(Identifier identifier)
		{
			this.identifier = identifier;
		}

		public String toString()
		{
			return identifier.toString();
		}
	}
}
