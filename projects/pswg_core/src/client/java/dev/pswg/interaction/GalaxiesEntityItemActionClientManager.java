package dev.pswg.interaction;

import dev.pswg.item.IPrimaryActionHandler;
import dev.pswg.mixin.client.accessors.ClientPlayerInteractionManagerAccessor;
import dev.pswg.networking.GalaxiesPlayerActionC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

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
		var client = MinecraftClient.getInstance();
		var interactionManager = client.interactionManager;

		((ClientPlayerInteractionManagerAccessor)interactionManager).invokeSyncSelectedSlot();

		var player = client.player;
		var hand = player.getActiveHand();
		var activeStack = player.getStackInHand(hand);
		if (!(activeStack.getItem() instanceof IPrimaryActionHandler item))
			return;

		ClientPlayNetworking.send(new GalaxiesPlayerActionC2SPacket(ClientPlayerAction.PRIMARY_ITEM_ACTION));

		ItemStack resultStack = item.invokePrimaryAction(activeStack, player.getEntityWorld(), player);
		if (resultStack != activeStack)
		{
			player.setStackInHand(hand, resultStack);
		}
	}
}
