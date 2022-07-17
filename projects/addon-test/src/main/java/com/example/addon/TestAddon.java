package com.example.addon;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.util.Lumberjack;

public class TestAddon implements PswgAddon
{
	public static final String MODID = "pswg-addon-test";
	public static final Lumberjack LOG = new Lumberjack(MODID);

	@Override
	public void onPswgReady()
	{
		LOG.log("Hello, World!");
	}
}
