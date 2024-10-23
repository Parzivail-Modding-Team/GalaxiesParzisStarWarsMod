package dev.pswg.interaction;

import dev.pswg.item.ILeftClickUsable;
import dev.pswg.mixin.client.ClientPlayerInteractionManagerAccessor;
import dev.pswg.networking.GalaxiesPlayerActionC2SPacket;
import dev.pswg.networking.PlayerInteractItemLeftC2SPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;

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
	 * Initializes this manager
	 */
	public static void initialize()
	{
		ClientTickEvents.START_CLIENT_TICK.register(GalaxiesEntityLeftClickClientManager::tick);
	}

	/**
	 * Handles per-tick left-use item actions
	 */
	private static void tick(MinecraftClient client)
	{
		if (itemUseCooldownLeft > 0)
		{
			itemUseCooldownLeft--;
		}
	}

	/**
	 * Emulates the {@link MinecraftClient#handleInputEvents()} use-item keybind
	 * handler, except using the attack key for left-click interactions
	 */
	public static void handleInputEvents(MinecraftClient client)
	{
		assert client.player != null;

		var handItem = client.player.getStackInHand(client.player.getActiveHand());
		var isHoldingLeftClickableItem = handItem.getItem() instanceof ILeftClickUsable;

		if (!isHoldingLeftClickableItem || !(client.player instanceof ILeftClickingEntity leftClickingEntity))
			return;

		if (leftClickingEntity.pswg$isUsingItemLeft())
		{
			if (!client.options.attackKey.isPressed())
			{
				stopUsingItemLeft(client.interactionManager, client.player);
			}
		}
		else
		{
			while (client.options.attackKey.wasPressed())
			{
				doItemUseLeft(client);
			}
		}

		if (client.options.attackKey.isPressed() && itemUseCooldownLeft == 0 && !client.player.isUsingItem())
		{
			doItemUseLeft(client);
		}
	}

	/**
	 * Emulates the {@link ClientPlayerInteractionManager#stopUsingItem} functionality for
	 * left-use items
	 *
	 * @param interactionManager The interaction manager to wrap
	 * @param player             The player that is interacting
	 */
	private static void stopUsingItemLeft(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player)
	{
		((ClientPlayerInteractionManagerAccessor)interactionManager).invokeSyncSelectedSlot();

		ClientPlayNetworking.send(new GalaxiesPlayerActionC2SPacket(PlayerAction.RELEASE_USE_LEFT_ITEM));

		if (!(player instanceof ILeftClickingEntity leftClickingEntity))
			return;

		leftClickingEntity.pswg$stopUsingItemLeft();
	}

	/**
	 * Emulates the {@link MinecraftClient#doItemUse} functionality for
	 * left-use items, excluding checks for riding another entity
	 */
	private static void doItemUseLeft(MinecraftClient client)
	{
		assert client.interactionManager != null;
		assert client.player != null;
		assert client.world != null;

		if (client.interactionManager.isBreakingBlock())
			return;

		itemUseCooldownLeft = 4;

		for (var hand : Hand.values())
		{
			var stack = client.player.getStackInHand(hand);

			if (!stack.isItemEnabled(client.world.getEnabledFeatures()))
				return;

			if (stack.isEmpty() || !(interactItemLeft(client, client.interactionManager, client.player, hand) instanceof ActionResult.Success success))
				continue;

			if (success.swingSource() == ActionResult.SwingSource.CLIENT)
			{
				client.player.swingHand(hand);
			}

			// TODO: wanted?
			client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
			return;
		}
	}

	/**
	 * Emulates the {@link ClientPlayerInteractionManager#interactItem} functionality for
	 * left-use items
	 */
	private static ActionResult interactItemLeft(MinecraftClient client, ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, Hand hand)
	{
		if (interactionManager.getCurrentGameMode() == GameMode.SPECTATOR)
		{
			return ActionResult.PASS;
		}
		else
		{
			((ClientPlayerInteractionManagerAccessor)interactionManager).invokeSyncSelectedSlot();

			var packet = new PlayerInteractItemLeftC2SPacket(hand, player.getYaw(), player.getPitch());

			var itemStack = player.getStackInHand(hand);

			var actionResult = GalaxiesEntityLeftClickManager.useLeft(client.world, player, hand, itemStack);
			ItemStack resultStack;
			if (actionResult instanceof ActionResult.Success success)
			{
				resultStack = Objects.requireNonNullElseGet(success.getNewHandStack(), () -> player.getStackInHand(hand));
			}
			else
			{
				resultStack = player.getStackInHand(hand);
			}

			if (resultStack != itemStack)
			{
				player.setStackInHand(hand, resultStack);
			}

			ClientPlayNetworking.send(packet);

			return actionResult;
		}
	}
}
