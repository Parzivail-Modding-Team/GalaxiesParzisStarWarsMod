package com.parzivail.pswg.compat.rei.plugins;

import com.parzivail.pswg.compat.rei.displays.MoistureVaporatorDisplay;
import com.parzivail.pswg.screen.MoistureVaporatorScreenHandler;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.simple.RecipeBookGridMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider;

public class GalaxiesREIServerPlugin implements REIServerPlugin
{
	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry)
	{
		registry.register(GalaxiesREICategories.MOISTURE_VAPORATOR, MoistureVaporatorDisplay.serializer());
	}

	@Override
	public void registerMenuInfo(MenuInfoRegistry registry)
	{
		registry.register(GalaxiesREICategories.MOISTURE_VAPORATOR, MoistureVaporatorScreenHandler.class, SimpleMenuInfoProvider.of(RecipeBookGridMenuInfo::new));
	}
}
