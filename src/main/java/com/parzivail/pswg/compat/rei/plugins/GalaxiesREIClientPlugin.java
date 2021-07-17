package com.parzivail.pswg.compat.rei.plugins;

import com.parzivail.pswg.compat.rei.categories.MoistureVaporatorCategory;
import com.parzivail.pswg.compat.rei.displays.MoistureVaporatorDisplay;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeType;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class GalaxiesREIClientPlugin implements REIClientPlugin
{
	@Override
	public void registerCategories(CategoryRegistry registry)
	{
		registry.add(new MoistureVaporatorCategory());
		registry.addWorkstations(GalaxiesREICategories.MOISTURE_VAPORATOR, EntryStacks.of(SwgBlocks.MoistureVaporator.Gx8));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry)
	{
		registry.registerRecipeFiller(VaporatorRecipe.class, SwgRecipeType.Vaporator, MoistureVaporatorDisplay::new);
	}
}
