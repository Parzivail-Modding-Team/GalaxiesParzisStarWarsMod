package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.scrapping.ScrappingTableRecipe;
import dev.pswg.registry.Registrar;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SingleStackRecipe;
import net.minecraft.recipe.StonecuttingRecipe;

public class GadgetsRecipeTypes
{
	public static final RecipeType<ScrappingTableRecipe> SCRAPPING = Registrar.recipeType(Gadgets.id("scrapping"));
	public static final RecipeSerializer<ScrappingTableRecipe> SCRAPPING_SERIALIZER = RecipeSerializer.register("scrapping", new ScrappingTableRecipe.Serializer<>(ScrappingTableRecipe::new));

	public static void register()
	{

	}
}
