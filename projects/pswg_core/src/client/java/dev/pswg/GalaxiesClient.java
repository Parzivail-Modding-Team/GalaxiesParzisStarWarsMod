package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

/**
 * The main entrypoint for PSWG client-side registration
 */
public class GalaxiesClient implements ClientModInitializer
{
	private static final MinecraftClient client = MinecraftClient.getInstance();

	/**
	 * Gets the fractional amount of ticks accumulated in the frames rendered
	 * since the last game tick
	 *
	 * @return The fractional amount of ticks [0,1)
	 */
	public static float getTickDelta()
	{
		return client.getRenderTickCounter().getTickDelta(false);
	}

	@Override
	public void onInitializeClient()
	{
		Galaxies.LOGGER.info("Loading PSWG modules and addons via pswg-client-addon");
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesClientReady);

		Galaxies.LOGGER.info("Galaxies client initialized");
	}
}
