package com.parzivail.datagen;

import com.parzivail.datagen.tarkin.Tarkin;
import net.fabricmc.api.ModInitializer;

public class DataGenHelper implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		try
		{
			Tarkin.main();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
}
