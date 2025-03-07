package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;

/**
 * The main entrypoint for PSWG client-side entity features
 */
public class EntitiesClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{

		Gadgets.LOGGER.info("Entities client module initialized");
	}
}
