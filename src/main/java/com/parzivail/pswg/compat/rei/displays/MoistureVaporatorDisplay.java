package com.parzivail.pswg.compat.rei.displays;

import com.parzivail.pswg.compat.rei.plugins.GalaxiesREICategories;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MoistureVaporatorDisplay extends BasicDisplay
{
	private final int duration;

	public MoistureVaporatorDisplay(@NotNull VaporatorRecipe recipe) {
		this(EntryIngredients.ofIngredients(recipe.getIngredients()), Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
		     Optional.ofNullable(recipe.getId()), recipe.getDuration());
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
	public List<EntryIngredient> getRequiredEntries()
	{
		return super.getRequiredEntries();
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier()
	{
		return GalaxiesREICategories.MOISTURE_VAPORATOR; // XXX: replace with the actual category identifier
	}

	public static BasicDisplay.Serializer<MoistureVaporatorDisplay> serializer() {
		return BasicDisplay.Serializer.of(MoistureVaporatorDisplay::new, (display, tag) -> {
			tag.putDouble("duration", display.duration);
		});
	}
}
