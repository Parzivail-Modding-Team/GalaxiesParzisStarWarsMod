package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.interaction.GalaxiesEntityLeftClickClientManager;
import dev.pswg.interaction.GalaxiesPlayerClientActionManager;
import dev.pswg.networking.GalaxiesEntitySpawnS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * The main entrypoint for PSWG client-side registration
 */
public class GalaxiesClient implements ClientModInitializer
{
	private static final MinecraftClient client = MinecraftClient.getInstance();

	/**
	 * Gets the fractional number of ticks accumulated in the frames rendered
	 * since the last game tick
	 *
	 * @return The fractional number of ticks [0,1)
	 */
	public static float getTickDelta()
	{
		return client.getRenderTickCounter().getTickProgress(false);
	}

	@Override
	public void onInitializeClient()
	{
		GalaxiesEntityLeftClickClientManager.initialize();
		GalaxiesPlayerClientActionManager.initialize();

		// Forward spawn packets to the network handler
		ClientPlayNetworking.registerGlobalReceiver(GalaxiesEntitySpawnS2CPacket.ID, (galaxiesEntitySpawnS2CPacket, context) -> {
			Optional.ofNullable(context.client())
			        .map(MinecraftClient::getNetworkHandler)
			        .ifPresent(handler -> handler.onEntitySpawn(galaxiesEntitySpawnS2CPacket));
		});

		Galaxies.LOGGER.info("Loading PSWG modules and addons via pswg-client-addon");
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesClientReady);
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesFinalizing);

		Galaxies.LOGGER.info("Galaxies client initialized");
	}
}
