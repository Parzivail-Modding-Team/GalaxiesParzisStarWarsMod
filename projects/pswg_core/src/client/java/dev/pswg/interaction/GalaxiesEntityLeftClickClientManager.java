package dev.pswg.interaction;

import dev.pswg.item.ILeftClickUsable;
import dev.pswg.networking.GalaxiesPlayerActionC2SPacket;
import dev.pswg.networking.PlayerInteractItemLeftC2SPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.Objects;

/**
 * Handles left-use item interactions on the client
 */
public final class GalaxiesEntityLeftClickClientManager
{
	/**
	 * The number of ticks until a player can left-use an item again
	 */
	private static int itemUseCooldownLeft;

	/**
	 * Whether left-click has been held since the last event was fired
	 */
	private static boolean repeatEvent;

	/**
	 * Initializes this manager
	 */
	public static void initialize()
	{
		ClientTickEvents.START_CLIENT_TICK.register(GalaxiesEntityLeftClickClientManager::tick);
	}

	/**
	 * Handles per-tick left-use item actions
	 */
	private static void tick(Minecraft client)
	{
		if (itemUseCooldownLeft > 0)
		{
			itemUseCooldownLeft--;
		}
	}

	/**
	 * Emulates the {@link Minecraft#handleKeybinds()} use-item keybind
	 * handler, except using the attack key for left-click interactions
	 */
	public static void handleInputEvents(Minecraft client)
	{
		assert client.player != null;

		var handItem = client.player.getItemInHand(client.player.getUsedItemHand());
		var isHoldingLeftClickableItem = handItem.getItem() instanceof ILeftClickUsable;

		if (!isHoldingLeftClickableItem || !(client.player instanceof ILeftClickingEntity leftClickingEntity))
			return;

		if (leftClickingEntity.pswg$isLeftUsingItem())
		{
			if (!client.options.keyAttack.isDown())
				stopUsingItemLeft(client.player);
		}
		else
		{
			var startedUsing = false;

			while (client.options.keyAttack.consumeClick())
			{
				doItemUseLeft(client);
				startedUsing = true;
			}

			if (startedUsing)
				repeatEvent = true;
		}

		if (!client.options.keyAttack.isDown())
		{
			itemUseCooldownLeft = 0;
			repeatEvent = false;
		}

		if (client.options.keyAttack.isDown() && itemUseCooldownLeft == 0 && !leftClickingEntity.pswg$isLeftUsingItem())
		{
			doItemUseLeft(client);
		}
	}

	/**
	 * Emulates the {@link MultiPlayerGameMode#releaseUsingItem} functionality for
	 * left-use items
	 *
	 * @param player             The player that is interacting
	 */
	private static void stopUsingItemLeft(LocalPlayer player)
	{
		CarriedItemSyncHelper.ensureHasSentCarriedItem(player);

		ClientPlayNetworking.send(new GalaxiesPlayerActionC2SPacket(ClientPlayerAction.RELEASE_USE_LEFT_ITEM));

		if (!(player instanceof ILeftClickingEntity leftClickingEntity))
			return;

		leftClickingEntity.pswg$stopLeftUsingItem();
	}

	/**
	 * Emulates the {@link Minecraft#startUseItem} functionality for
	 * left-use items, excluding checks for riding another entity
	 */
	private static void doItemUseLeft(Minecraft client)
	{
		assert client.gameMode != null;
		assert client.player != null;
		assert client.level != null;

		if (client.gameMode.isDestroying())
			return;

		itemUseCooldownLeft = 4;

		for (var hand : InteractionHand.values())
		{
			var stack = client.player.getItemInHand(hand);

			if (!stack.isItemEnabled(client.level.enabledFeatures()))
				return;

			if (stack.isEmpty() || !(interactItemLeft(client, client.gameMode, client.player, hand) instanceof InteractionResult.Success success))
				continue;

			if (success.swingSource() == InteractionResult.SwingSource.CLIENT)
			{
				client.player.swing(hand);
			}

			return;
		}
	}

	/**
	 * Emulates the {@link MultiPlayerGameMode#useItem} functionality for
	 * left-use items
	 */
	private static InteractionResult interactItemLeft(Minecraft client, MultiPlayerGameMode interactionManager, LocalPlayer player, InteractionHand hand)
	{
		if (interactionManager.getPlayerMode() == GameType.SPECTATOR)
		{
			return InteractionResult.PASS;
		}
		else
		{
			CarriedItemSyncHelper.ensureHasSentCarriedItem(player);

			var packet = new PlayerInteractItemLeftC2SPacket(hand, player.getYRot(), player.getXRot(), repeatEvent);

			var itemStack = player.getItemInHand(hand);

			var actionResult = GalaxiesEntityLeftClickManager.useLeft(client.level, player, hand, itemStack, repeatEvent);
			ItemStack resultStack;
			if (actionResult instanceof InteractionResult.Success success)
			{
				resultStack = Objects.requireNonNullElseGet(success.heldItemTransformedTo(), () -> player.getItemInHand(hand));
			}
			else
			{
				resultStack = player.getItemInHand(hand);
			}

			if (resultStack != itemStack)
			{
				player.setItemInHand(hand, resultStack);
			}

			ClientPlayNetworking.send(packet);

			return actionResult;
		}
	}

	/**
	 * Handles packets notifying the client that the server has consumed the item
	 */
	public static void handleConsumeItem(ClientPlayNetworking.Context context)
	{
		if (!(context.player() instanceof ILeftClickingEntity lce))
			return;

		lce.pswg$consumeLeftItem();
	}
}
