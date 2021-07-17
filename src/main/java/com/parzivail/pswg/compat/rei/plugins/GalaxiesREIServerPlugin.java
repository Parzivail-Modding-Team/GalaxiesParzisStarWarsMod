package com.parzivail.pswg.compat.rei.plugins;

import com.parzivail.pswg.compat.rei.displays.MoistureVaporatorDisplay;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class GalaxiesREIServerPlugin implements REIServerPlugin
{
	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry)
	{
		registry.register(GalaxiesREICategories.MOISTURE_VAPORATOR, MoistureVaporatorDisplay.serializer());
	}
}
