package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.container.SwgSpeciesRegistry;

public class BaseContentClient implements PswgClientAddon
{
	@Override
	public void onPswgClientReady()
	{
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_CHISS, SwgSpeciesRenderer.humanoidBase(Resources.id("species/human")), null);
	}
}
