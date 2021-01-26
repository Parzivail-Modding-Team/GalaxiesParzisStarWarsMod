package com.parzivail.datagen;

import com.parzivail.datagen.tarkin.Tarkin;

public class DataGenHelper
{
	public static void run()
	{
		String tarkinArg = System.getProperty("tarkin", "");
		if (!tarkinArg.isEmpty())
		{
			Tarkin.main(tarkinArg);
			System.exit(0);
		}
	}
}
