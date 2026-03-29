package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.scrapping.cutter.LaserCuttingRecipe;
import dev.pswg.feature.scrapping.table.ScrappingTableRecipe;
import dev.pswg.registry.Registrar;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GadgetsRecipeSerializers
{
	public static final RecipeSerializer<ScrappingTableRecipe> SCRAPPING_SERIALIZER = Registrar.recipeSerializer(Gadgets.id("scrapping"), ScrappingTableRecipe.createSerializer(ScrappingTableRecipe::new));
	public static final RecipeSerializer<LaserCuttingRecipe> LASER_CUTTING_SERIALIZER = Registrar.recipeSerializer(Gadgets.id("laser_cutting"), LaserCuttingRecipe.createSerializer(LaserCuttingRecipe::new));

	public static void register()
	{
	}
}
