package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.scrapping.ScrappingTableRecipe;
import dev.pswg.registry.Registrar;
import net.minecraft.recipe.RecipeSerializer;

public class GadgetsRecipeSerializers
{
	public static final RecipeSerializer<ScrappingTableRecipe> SCRAPPING_SERIALIZER = Registrar.recipeSerializer(Gadgets.id("scrapping"), new ScrappingTableRecipe.Serializer<>(ScrappingTableRecipe::new));

	public static void register()
	{
	}
}
