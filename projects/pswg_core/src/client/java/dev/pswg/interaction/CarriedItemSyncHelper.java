package dev.pswg.interaction;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;

/**
 * Synchronizes the local player's selected hotbar slot before custom interaction packets are sent
 */
final class CarriedItemSyncHelper
{
	/**
	 * The last selected hotbar slot synchronized by this helper
	 */
	private static int _lastSyncedSelectedSlot = -1;

	/**
	 * Synchronizes the currently selected hotbar slot with the server when it has changed
	 *
	 * @param player The local player whose selected slot should be synchronized
	 */
	static void ensureHasSentCarriedItem(LocalPlayer player)
	{
		var selectedSlot = player.getInventory().getSelectedSlot();
		if (_lastSyncedSelectedSlot == selectedSlot)
			return;

		player.connection.send(new ServerboundSetCarriedItemPacket(selectedSlot));
		_lastSyncedSelectedSlot = selectedSlot;
	}
}
