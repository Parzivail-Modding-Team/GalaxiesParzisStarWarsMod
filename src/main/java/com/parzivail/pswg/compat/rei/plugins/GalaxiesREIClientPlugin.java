package com.parzivail.pswg.compat.rei.plugins;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.compat.rei.categories.MoistureVaporatorCategory;
import com.parzivail.pswg.compat.rei.displays.MoistureVaporatorDisplay;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeType;
import com.parzivail.pswg.recipe.VaporatorRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.util.Identifier;

public class GalaxiesREIClientPlugin implements REIClientPlugin
{
	public static final Identifier DISPLAY_TEXTURE = Resources.id("textures/gui/rei/display.png");
	public static final Identifier DISPLAY_TEXTURE_DARK = Resources.id("textures/gui/rei/display_dark.png");
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

	@Override
	public void registerScreens(ScreenRegistry registry)
	{
		// TODO: register click area
	}
}
