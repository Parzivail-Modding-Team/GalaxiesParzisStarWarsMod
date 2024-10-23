package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.networking.GalaxiesPlayerActionC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Dispatches fire-and-forget handlers for C2S player actions
 */
public final class GalaxiesPlayerActionManager
{
	/**
	 * Initializes this manager
	 */
	public static void initialize()
	{
		ServerPlayNetworking.registerGlobalReceiver(GalaxiesPlayerActionC2SPacket.ID, GalaxiesPlayerActionManager::handlePlayerAction);
	}

	/**
	 * Invokes the handler within the specified subsystem based on the packet's action
	 */
	private static void handlePlayerAction(GalaxiesPlayerActionC2SPacket packet, ServerPlayNetworking.Context context)
	{
		switch (packet.action())
		{
			case INVALID -> Galaxies.LOGGER.warn("Received invalid player action packet!");
			case RELEASE_USE_LEFT_ITEM -> GalaxiesEntityLeftClickManager.handleReleaseUseItem(context);
		}
	}
}
