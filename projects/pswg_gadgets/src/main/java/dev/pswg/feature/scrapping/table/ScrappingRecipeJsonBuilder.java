package dev.pswg.feature.scrapping.table;

import dev.pswg.feature.scrapping.cutter.LaserCuttingRecipe;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class ScrappingRecipeJsonBuilder
{
	private final HolderGetter<Item> registryLookup;
	private final Ingredient tool;
	private final Ingredient input;
	private final ItemStackTemplate primaryOutput;
	private final ItemStackTemplate secondaryOutput;
	private final float secondaryChance;

	public ScrappingRecipeJsonBuilder(
		HolderGetter<Item> registryLookup,
		Ingredient tool,
		Ingredient input,
		ItemStackTemplate primaryOutput,
		ItemStackTemplate secondaryOutput,
		float secondaryChance
	)
	{
		this.registryLookup = registryLookup;
		this.tool = tool;
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.secondaryChance = secondaryChance;
	}

	public static ScrappingRecipeJsonBuilder create(
		HolderGetter<Item> registryLookup,
		Ingredient tool,
		Ingredient input,
		ItemStackTemplate primaryOutput,
		ItemStackTemplate secondaryOutput,
		float secondaryChance
	)
	{
		return new ScrappingRecipeJsonBuilder(registryLookup, tool, input, primaryOutput, secondaryOutput, secondaryChance);
	}

	public void offerTo(RecipeOutput exporter, ResourceKey<Recipe<?>> recipeKey)
	{
		ScrappingTableRecipe scrappingTableRecipe = new ScrappingTableRecipe(tool, input, primaryOutput, secondaryOutput, secondaryChance);
		exporter.accept(recipeKey, scrappingTableRecipe, null);
	}
}
