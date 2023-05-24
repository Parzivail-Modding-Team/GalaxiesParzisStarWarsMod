package com.parzivail.aurek.ui.view.addonbuilder;

import com.parzivail.util.generics.Consumers;

public enum FeatureType
{
	Lightsaber("Lightsaber", LightsaberAddonFeature::renderForm),
	Blaster("Blaster", Consumers::noop),
	Species("Species", Consumers::noop);

	private final String name;
	private final FeatureFormRenderer formRenderer;

	FeatureType(String name, FeatureFormRenderer formRenderer)
	{
		this.name = name;
		this.formRenderer = formRenderer;
	}

	public String getName()
	{
		return name;
	}

	public FeatureFormRenderer getFormRenderer()
	{
		return formRenderer;
	}
}
