package com.parzivail.datagen;

import com.parzivail.datagen.tarkin.Tarkin;
import com.parzivail.pswg.api.PswgClientAddon;

public class DataGenHelper implements PswgClientAddon
{
	@Override
	public void onPswgClientReady()
	{
		try
		{
			Tarkin.main();
			//			Tarkin2.main();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
}
