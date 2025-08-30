package dev.pswg.feature.scrapping.table;

import dev.pswg.feature.scrapping.cutter.LaserCuttingRecipe;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;

public class ScrappingRecipeJsonBuilder
{
	private final RegistryEntryLookup<Item> registryLookup;
	private final Ingredient tool;
	private final Ingredient input;
	private final ItemStack primaryOutput;
	private final ItemStack secondaryOutput;
	private final float secondaryChance;

	public ScrappingRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, Ingredient tool, Ingredient input, ItemStack primaryOutput, ItemStack secondaryOutput, float secondaryChance)
	{
		this.registryLookup = registryLookup;
		this.tool = tool;
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.secondaryChance = secondaryChance;
	}

	public static ScrappingRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, Ingredient tool, Ingredient input, ItemStack primaryOutput, ItemStack secondaryOutput, float secondaryChance)
	{
		return new ScrappingRecipeJsonBuilder(registryLookup, tool, input, primaryOutput, secondaryOutput, secondaryChance);
	}

	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey)
	{
		ScrappingTableRecipe scrappingTableRecipe = new ScrappingTableRecipe(tool, input, primaryOutput, secondaryOutput, secondaryChance);
		exporter.accept(recipeKey, scrappingTableRecipe, null);
	}
}
