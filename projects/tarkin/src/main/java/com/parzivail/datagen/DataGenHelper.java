package com.parzivail.datagen;

import com.parzivail.datagen.tarkin.Tarkin;
import com.parzivail.pswg.api.PswgAddon;

public class DataGenHelper implements PswgAddon
{
	@Override
	public void onPswgReady()
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
