package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.item.IPrimaryActionHandler;
import dev.pswg.networking.GalaxiesPlayerActionC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

/**
 * Handles player non-primary item interactions on the client
 */
public final class GalaxiesEntityItemActionClientManager
{
	/**
	 * Invokes the primary item action for the given player's held item
	 */
	public static void handlePrimaryItemAction()
	{
		var client = Minecraft.getInstance();
		var interactionManager = client.gameMode;
		var player = client.player;
		if (interactionManager == null || player == null)
			return;

		CarriedItemSyncHelper.ensureHasSentCarriedItem(player);
		var hand = player.getUsedItemHand();
		var activeStack = player.getItemInHand(hand);
		if (!(activeStack.getItem() instanceof IPrimaryActionHandler item))
			return;

		ClientPlayNetworking.send(new GalaxiesPlayerActionC2SPacket(ClientPlayerAction.PRIMARY_ITEM_ACTION));

		ItemStack resultStack = item.invokePrimaryAction(activeStack, player.level(), player);
		if (resultStack != activeStack)
		{
			player.setItemInHand(hand, resultStack);
		}
	}
}
