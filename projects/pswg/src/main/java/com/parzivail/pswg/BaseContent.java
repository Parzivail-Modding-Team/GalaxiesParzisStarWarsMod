package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;

public class BaseContent implements PswgAddon, PswgClientAddon
{
	@Override
	public void onPswgReady()
	{
		PswgContent.registerLightsaberPreset(
				new LightsaberDescriptor(Resources.id("anakin"), "Anakin Skywalker", Resources.id("anakin"), 0.62f, 1, 1),
				new LightsaberDescriptor(Resources.id("ezra_padawan"), "Ezra Bridger", Resources.id("ezra_padawan"), 0.33f, 1, 1),
				new LightsaberDescriptor(Resources.id("kenobi"), "Obi-Wan Kenobi", Resources.id("kenobi"), 0.62f, 1, 1),
				new LightsaberDescriptor(Resources.id("luke_rotj"), "Luke Skywalker", Resources.id("luke_rotj"), 0.33f, 1, 1),
				new LightsaberDescriptor(Resources.id("qui_gon"), "Qui-Gon Jinn", Resources.id("qui_gon"), 0.33f, 1, 1),
				new LightsaberDescriptor(Resources.id("vader"), "Darth Vader", Resources.id("vader"), 0, 1, 1)
		);
	}

	@Override
	public void onPswgClientReady()
	{
	}
}
