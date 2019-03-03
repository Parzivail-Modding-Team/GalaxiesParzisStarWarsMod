package com.parzivail.swg.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config extends Configuration
{
	private static final String CATEGORY_DIMENSIONS = "dimensions";
	private static final String CATEGORY_BIOMES = "biomes";

	private Property hasSeenIntroCrawlProp;

	private Property dimIdTatooine;
	private Property dimIdEndor;
	private Property dimIdHyperspace;

	private Property biomeIdTatooineDunes;
	private Property biomeIdEndor;
	private Property biomeIdHyperspace;

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
		dimIdHyperspace = get(CATEGORY_DIMENSIONS, "dimIdHyperspace", 4, "Hyperspace's dimension identifier");

		biomeIdTatooineDunes = get(CATEGORY_BIOMES, "biomeIdTatooineDunes", 100, "Tatooine Dunes' biome identifier");
		biomeIdEndor = get(CATEGORY_BIOMES, "biomeIdEndor", 101, "Endor's biome identifier");
		biomeIdHyperspace = get(CATEGORY_BIOMES, "biomeIdHyperspace", 102, "Hyperspace's biome identifier");

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

	public int getDimIdHyperspace()
	{
		return dimIdHyperspace.getInt();
	}

	public int getBiomeIdTatooineDunes()
	{
		return biomeIdTatooineDunes.getInt();
	}

	public int getBiomeIdEndor()
	{
		return biomeIdEndor.getInt();
	}

	public int getBiomeIdHyperspace()
	{
		return biomeIdHyperspace.getInt();
	}
}
