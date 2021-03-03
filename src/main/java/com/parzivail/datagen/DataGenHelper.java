package com.parzivail.datagen;

import com.parzivail.datagen.tarkin.Tarkin;

public class DataGenHelper
{
	public static void run()
	{
		String tarkinArg = System.getProperty("tarkin", "");
		if (!tarkinArg.isEmpty())
		{
			try
			{
				Tarkin.main(tarkinArg);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
}
