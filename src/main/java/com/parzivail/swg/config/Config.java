package com.parzivail.swg.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config extends Configuration
{
	private static final String CATEGORY_DIMENSIONS = "dimensions";

	private Property hasSeenIntroCrawlProp;
	private Property dimIdTatooine;
	private Property dimIdEndor;

	public Config(File file)
	{
		super(file);
		load();
	}

	public void load()
	{
		super.load();

		// TODO: change to false when it's ready to be shown in a release
		hasSeenIntroCrawlProp = get(Configuration.CATEGORY_GENERAL, "hasSeenIntroCrawl", true, "True if the user has seen the intro crawl before");

		dimIdTatooine = get(CATEGORY_DIMENSIONS, "dimIdTatooine", 2, "Tatooine's dimension identifier");
		dimIdEndor = get(CATEGORY_DIMENSIONS, "dimIdEndor", 3, "Endor's dimension identifier");

		if (hasChanged())
			save();
	}

	public boolean getHasSeenIntroCrawl()
	{
		return hasSeenIntroCrawlProp.getBoolean();
	}

	public void setHasSeenIntroCrawl(boolean hasSeenIntroCrawl)
	{
		hasSeenIntroCrawlProp.set(hasSeenIntroCrawl);
		if (hasChanged())
			save();
	}

	public int getDimIdTatooine()
	{
		return dimIdTatooine.getInt();
	}

	public int getDimIdEndor()
	{
		return dimIdEndor.getInt();
	}
}
