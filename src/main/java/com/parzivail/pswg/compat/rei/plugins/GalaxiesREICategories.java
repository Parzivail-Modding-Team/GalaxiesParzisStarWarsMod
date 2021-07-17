package com.parzivail.pswg.compat.rei.plugins;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.compat.rei.displays.MoistureVaporatorDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class GalaxiesREICategories
{
	private GalaxiesREICategories() {}

	public static final CategoryIdentifier<MoistureVaporatorDisplay> MOISTURE_VAPORATOR = CategoryIdentifier.of(Resources.id("moisture_vaporator"));
}
