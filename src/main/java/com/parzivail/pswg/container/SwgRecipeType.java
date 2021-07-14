package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class SwgRecipeType
{
	public static final ArrayList<RecipeType<?>> RECIPE_TYPES = new ArrayList<>();

	public static final RecipeType<VaporatorRecipe> Vaporator = register(Resources.id("vaporator"));

	public static void register()
	{
	}

	private static <T extends Recipe<?>> RecipeTypeImpl<T> register(Identifier identifier)
	{
		RecipeTypeImpl<T> type = Registry.register(Registry.RECIPE_TYPE, identifier, new RecipeTypeImpl<>(identifier));
		RECIPE_TYPES.add(type);
		return type;
	}

	private record RecipeTypeImpl<T extends Recipe<?>>(Identifier identifier) implements RecipeType<T>
	{

		public String toString()
		{
			return identifier.toString();
		}
	}
}
