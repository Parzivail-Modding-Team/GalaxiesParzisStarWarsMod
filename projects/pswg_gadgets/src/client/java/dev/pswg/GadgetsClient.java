package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;

/**
 * The main entrypoint for PSWG client-side gadget features
 */
public class GadgetsClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{
		Gadgets.LOGGER.info("Client module initialized");
	}
}
