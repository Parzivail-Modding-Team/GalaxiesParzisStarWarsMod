package com.parzivail.aurek.ui.view.addonbuilder;

@FunctionalInterface
public interface FeatureFormRenderer
{
	void render(AddonBuilder context, IAddonFeature feature);
}
