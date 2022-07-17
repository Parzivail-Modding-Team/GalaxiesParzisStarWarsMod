package com.example.addon;

import com.parzivail.pswg.api.PswgClientAddon;

public class TestAddonClient implements PswgClientAddon
{
	@Override
	public void onPswgClientReady()
	{
		TestAddon.LOG.log("Hello, Client World!");
	}
}
