package dev.pswg.interaction;

import dev.pswg.item.IPrimaryActionHandler;
import dev.pswg.networking.GalaxiesPlayerActionC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Handles player non-primary item interactions on the client
 */
public final class GalaxiesEntityItemActionClientManager
{
	/**
	 * Optional code-addon routing to an item that supplies its own hand-qualified action protocol.
	 */
	@FunctionalInterface
	public interface HandSelector
	{
		/**
		 * Returns a custom-protocol source hand, or null to defer to another selector/the native used hand.
		 */
		InteractionHand select(LocalPlayer player);
	}

	/**
	 * Hand routing runs before deciding whether the source has its own custom protocol.
	 */
	public static final Event<HandSelector> HAND_SELECTION = EventFactory.createArrayBacked(HandSelector.class, listeners -> player -> {
		for (var listener : listeners)
		{
			var hand = listener.select(player);
			if (hand != null)
				return hand;
		}
		return player.getUsedItemHand();
	});

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

		var hand = HAND_SELECTION.invoker().select(player);
		if (hand != player.getUsedItemHand() && !(player.getItemInHand(hand).getItem() instanceof IPrimaryActionHandler custom && custom.usesCustomPrimaryAction()))
			hand = player.getUsedItemHand();

		if (!ItemHandPermission.EVENT.invoker().mayInteract(player, hand))
			return;

		var activeStack = player.getItemInHand(hand);
		if (!(activeStack.getItem() instanceof IPrimaryActionHandler item) || item.usesCustomPrimaryAction())
			return;

		ClientPlayNetworking.send(new GalaxiesPlayerActionC2SPacket(ClientPlayerAction.PRIMARY_ITEM_ACTION));

		ItemStack resultStack = item.invokePrimaryAction(activeStack, player.level(), player);
		if (resultStack != activeStack)
		{
			player.setItemInHand(hand, resultStack);
		}
	}
}
