package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.scrapping.ScrappingTableRecipe;
import dev.pswg.registry.Registrar;
import net.minecraft.recipe.RecipeType;

public class GadgetsRecipeTypes
{
	public static final RecipeType<ScrappingTableRecipe> SCRAPPING = Registrar.recipeType(Gadgets.id("scrapping"));

	public static void register()
	{

	}
}
