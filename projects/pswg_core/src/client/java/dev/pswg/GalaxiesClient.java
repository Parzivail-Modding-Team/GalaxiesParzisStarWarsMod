package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * The main entrypoint for PSWG client-side registration
 */
public class GalaxiesClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		Galaxies.LOGGER.info("Loading PSWG modules and addons via pswg-client-addon");
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesClientReady);

		Galaxies.LOGGER.info("Galaxies client initialized");
	}
}
