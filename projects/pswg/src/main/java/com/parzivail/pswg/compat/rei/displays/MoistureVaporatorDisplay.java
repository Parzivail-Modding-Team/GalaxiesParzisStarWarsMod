package com.parzivail.pswg.compat.rei.displays;

import com.parzivail.pswg.compat.rei.plugins.GalaxiesREICategories;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MoistureVaporatorDisplay extends BasicDisplay implements SimpleGridMenuDisplay
{
	private final int duration;

	public MoistureVaporatorDisplay(RecipeEntry<VaporatorRecipe> recipeEntry) {
		this(EntryIngredients.ofIngredients(recipeEntry.value().getIngredients()), Collections.singletonList(EntryIngredients.of(recipeEntry.value().getResult(BasicDisplay.registryAccess()))),
		     Optional.ofNullable(recipeEntry.id()), recipeEntry.value().duration());
	}

	public MoistureVaporatorDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location, NbtCompound tag) {
		this(inputs, outputs, location, tag.getInt("duration"));
	}

	public MoistureVaporatorDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location, int duration) {
		super(inputs, outputs, location);
		this.duration = duration;
	}

	public int getDuration()
	{
		return duration;
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier()
	{
		return GalaxiesREICategories.MOISTURE_VAPORATOR;
	}

	public static BasicDisplay.Serializer<MoistureVaporatorDisplay> serializer() {
		return BasicDisplay.Serializer.of(MoistureVaporatorDisplay::new, (display, tag) -> {
			tag.putDouble("duration", display.duration);
		});
	}

	@Override
	public int getWidth()
	{
		return 1;
	}

	@Override
	public int getHeight()
	{
		return 1;
	}
}
