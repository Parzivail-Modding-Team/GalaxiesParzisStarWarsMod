package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.networking.GalaxiesPlayerActionS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Dispatches fire-and-forget handlers for S2C player actions
 */
public final class GalaxiesPlayerClientActionManager
{
	/**
	 * Initializes this manager
	 */
	public static void initialize()
	{
		ClientPlayNetworking.registerGlobalReceiver(GalaxiesPlayerActionS2CPacket.ID, GalaxiesPlayerClientActionManager::handlePlayerAction);
	}

	/**
	 * Invokes the handler within the specified subsystem based on the packet's action
	 */
	private static void handlePlayerAction(GalaxiesPlayerActionS2CPacket packet, ClientPlayNetworking.Context context)
	{
		switch (packet.action())
		{
			case INVALID -> Galaxies.LOGGER.warn("Received invalid player action packet!");
			case CONSUME_LEFT_ITEM -> GalaxiesEntityLeftClickClientManager.handleConsumeItem(context);
		}
	}
}
