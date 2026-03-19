package dev.pswg.interaction;

import dev.pswg.item.IPrimaryActionHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;

/**
 * Handles player non-primary item interactions
 */
public final class GalaxiesEntityItemActionManager
{
	/**
	 * Invokes the primary item action for the given player's held item
	 */
	public static void handlePrimaryItemAction(ServerPlayNetworking.Context context)
	{
		context.server().execute(() -> {
			var player = context.player();
			player.resetLastActionTime();

			var hand = player.getUsedItemHand();
			var activeStack = player.getItemInHand(hand);
			if (!(activeStack.getItem() instanceof IPrimaryActionHandler item))
				return;

			ItemStack resultStack = item.invokePrimaryAction(activeStack, player.level(), player);
			if (resultStack != activeStack)
			{
				player.setItemInHand(hand, resultStack);
			}
		});
	}
}
