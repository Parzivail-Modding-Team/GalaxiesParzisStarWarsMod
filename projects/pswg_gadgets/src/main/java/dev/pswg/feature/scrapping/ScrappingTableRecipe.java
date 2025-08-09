package dev.pswg.feature.scrapping;

import dev.pswg.container.GadgetsRecipeTypes;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.world.World;

import java.util.Optional;

public interface ScrappingTableRecipe extends Recipe<ScrappingTableRecipeInput>
{
	@Override
	default RecipeType<ScrappingTableRecipe> getType()
	{
		return GadgetsRecipeTypes.SCRAPPING;
	}

	@Override
	RecipeSerializer<? extends ScrappingTableRecipe> getSerializer();

	@Override
	default boolean matches(ScrappingTableRecipeInput input, World world)
	{
		return Ingredient.matches(this.scrapItem(), input.scrapItem)
		       && (this.spanner().isEmpty() || this.spanner().get().test(input.spanner))
		       && (this.cutter().isEmpty() || this.cutter().get().test(input.cutter))
		       && (this.calibrator().isEmpty() || this.calibrator().get().test(input.calibrator))
				;
	}

	Optional<Ingredient> scrapItem();

	Optional<Ingredient> spanner();

	Optional<Ingredient> cutter();

	Optional<Ingredient> calibrator();

	@Override
	default RecipeBookCategory getRecipeBookCategory()
	{
		return null;
	}

	;
}
