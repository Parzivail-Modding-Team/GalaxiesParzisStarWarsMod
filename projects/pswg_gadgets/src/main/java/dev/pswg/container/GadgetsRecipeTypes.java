package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.scrapping.cutter.LaserCuttingRecipe;
import dev.pswg.feature.scrapping.table.ScrappingTableRecipe;
import dev.pswg.registry.Registrar;
import net.minecraft.recipe.RecipeType;

public class GadgetsRecipeTypes
{
	public static final RecipeType<ScrappingTableRecipe> SCRAPPING = Registrar.recipeType(Gadgets.id("scrapping"));
	public static final RecipeType<LaserCuttingRecipe> CUTTING = Registrar.recipeType(Gadgets.id("laser_cutting"));

	public static void register()
	{

	}
}
