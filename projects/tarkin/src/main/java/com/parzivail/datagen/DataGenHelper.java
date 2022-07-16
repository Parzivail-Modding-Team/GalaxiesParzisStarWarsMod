package com.parzivail.datagen;

import com.parzivail.datagen.tarkin.Tarkin;
import net.fabricmc.api.ClientModInitializer;

public class DataGenHelper implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
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
