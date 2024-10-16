package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;

/**
 * The main entrypoint for PSWG client-side blaster features
 */
public class BlastersClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{
		Blasters.LOGGER.info("Client module initialized");
	}
}
