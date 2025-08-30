package dev.pswg.feature.scrapping.cutter;

import dev.pswg.container.GadgetsRecipeTypes;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;

import java.util.Objects;

public class LaserCuttingRecipeJsonBuilder
{
	private final RegistryEntryLookup<Item> registryLookup;
	private final Ingredient input;
	private final ItemStack primaryOutput;
	private final ItemStack secondaryOutput;
	private final float secondaryChance;

	public LaserCuttingRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, Ingredient input, ItemStack primaryOutput, ItemStack secondaryOutput, float secondaryChance)
	{
		this.registryLookup = registryLookup;
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.secondaryChance = secondaryChance;
	}

	public static LaserCuttingRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, Ingredient input, ItemStack primaryOutput, ItemStack secondaryOutput, float secondaryChance)
	{
		return new LaserCuttingRecipeJsonBuilder(registryLookup, input, primaryOutput, secondaryOutput, secondaryChance);
	}

	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey)
	{

		LaserCuttingRecipe laserCuttingRecipe = new LaserCuttingRecipe(input, primaryOutput, secondaryOutput, secondaryChance);
		exporter.accept(recipeKey, laserCuttingRecipe, null);
	}
}
