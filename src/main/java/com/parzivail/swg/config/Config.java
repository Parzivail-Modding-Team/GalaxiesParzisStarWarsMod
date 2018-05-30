package com.parzivail.swg.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config extends Configuration
{
	private Property hasSeenIntroCrawlProp;

	public Config(File file)
	{
		super(file);
		load();
	}

	public void load()
	{
		super.load();

		// TODO: change to false when it's ready to be shown in a release
		hasSeenIntroCrawlProp = get(CATEGORY_GENERAL, "hasSeenIntroCrawl", true, "True if the user has seen the intro crawl before");

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
}
