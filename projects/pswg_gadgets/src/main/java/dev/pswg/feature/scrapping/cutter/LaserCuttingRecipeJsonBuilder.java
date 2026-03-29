package dev.pswg.feature.scrapping.cutter;

import dev.pswg.container.GadgetsRecipeTypes;
import java.util.Objects;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class LaserCuttingRecipeJsonBuilder
{
	private final HolderGetter<Item> registryLookup;
	private final Ingredient input;
	private final ItemStackTemplate primaryOutput;
	private final ItemStackTemplate secondaryOutput;
	private final float secondaryChance;

	public LaserCuttingRecipeJsonBuilder(
		HolderGetter<Item> registryLookup,
		Ingredient input,
		ItemStackTemplate primaryOutput,
		ItemStackTemplate secondaryOutput,
		float secondaryChance
	)
	{
		this.registryLookup = registryLookup;
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.secondaryChance = secondaryChance;
	}

	public static LaserCuttingRecipeJsonBuilder create(
		HolderGetter<Item> registryLookup,
		Ingredient input,
		ItemStackTemplate primaryOutput,
		ItemStackTemplate secondaryOutput,
		float secondaryChance
	)
	{
		return new LaserCuttingRecipeJsonBuilder(registryLookup, input, primaryOutput, secondaryOutput, secondaryChance);
	}

	public void offerTo(RecipeOutput exporter, ResourceKey<Recipe<?>> recipeKey)
	{

		LaserCuttingRecipe laserCuttingRecipe = new LaserCuttingRecipe(input, primaryOutput, secondaryOutput, secondaryChance);
		exporter.accept(recipeKey, laserCuttingRecipe, null);
	}
}
