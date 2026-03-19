package dev.pswg.interaction;

import dev.pswg.item.ILeftClickUsable;
import dev.pswg.networking.PlayerInteractItemLeftC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import java.util.Objects;

/**
 * Handles left-use item interactions on the server
 */
public final class GalaxiesEntityLeftClickManager
{
	/**
	 * Initializes this manager
	 */
	public static void initialize()
	{
		ServerPlayNetworking.registerGlobalReceiver(PlayerInteractItemLeftC2SPacket.ID, GalaxiesEntityLeftClickManager::handleInteractItemLeft);
	}

	/**
	 * Handles {@link PlayerInteractItemLeftC2SPacket} packets to left-use an item
	 */
	private static void handleInteractItemLeft(PlayerInteractItemLeftC2SPacket packet, ServerPlayNetworking.Context context)
	{
		context.server().execute(() -> {
			var player = context.player();

			ServerLevel serverWorld = player.level();
			InteractionHand hand = packet.hand();
			ItemStack itemStack = player.getItemInHand(hand);
			player.resetLastActionTime();

			if (!itemStack.isEmpty() && itemStack.isItemEnabled(serverWorld.enabledFeatures()))
			{
				float f = Mth.wrapDegrees(packet.yaw());
				float g = Mth.wrapDegrees(packet.pitch());

				if (g != player.getXRot() || f != player.getYRot())
				{
					player.absSnapRotationTo(f, g);
				}

				var result = interactItemLeft(player.gameMode, player, serverWorld, itemStack, hand, packet.repeat());
				if (result instanceof InteractionResult.Success success && success.swingSource() == InteractionResult.SwingSource.SERVER)
				{
					player.swing(hand, true);
				}
			}
		});
	}

	/**
	 * Emulates the {@link ServerPlayerGameMode#useItem} functionality for
	 * left-use items
	 */
	private static InteractionResult interactItemLeft(ServerPlayerGameMode interactionManager, ServerPlayer player, ServerLevel world, ItemStack stack, InteractionHand hand, boolean repeatEvent)
	{
		if (interactionManager.getGameModeForPlayer() == GameType.SPECTATOR)
		{
			return InteractionResult.PASS;
		}
		else
		{
			if (!(stack.getItem() instanceof ILeftClickUsable leftItem) || !(player instanceof ILeftClickingEntity leftClickingEntity))
				return InteractionResult.PASS;

			int i = stack.getCount();
			int j = stack.getDamageValue();

			InteractionResult actionResult = useLeft(world, player, hand, stack, repeatEvent);
			ItemStack itemStack;
			if (actionResult instanceof InteractionResult.Success success)
			{
				itemStack = Objects.requireNonNullElse(success.heldItemTransformedTo(), player.getItemInHand(hand));
			}
			else
			{
				itemStack = player.getItemInHand(hand);
			}

			if (itemStack == stack && itemStack.getCount() == i && leftItem.getMaxUseLeftTime(itemStack, player) <= 0 && itemStack.getDamageValue() == j)
			{
				return actionResult;
			}
			else if (actionResult instanceof InteractionResult.Fail && leftItem.getMaxUseLeftTime(itemStack, player) > 0 && !leftClickingEntity.pswg$isLeftUsingItem())
			{
				return actionResult;
			}
			else
			{
				if (stack != itemStack)
				{
					player.setItemInHand(hand, itemStack);
				}

				if (itemStack.isEmpty())
				{
					player.setItemInHand(hand, ItemStack.EMPTY);
				}

				if (!leftClickingEntity.pswg$isLeftUsingItem())
				{
					player.inventoryMenu.sendAllDataToRemote();
				}

				return actionResult;
			}
		}
	}

	/**
	 * Emulates the {@link ItemStack#use} functionality for left-use items
	 */
	static InteractionResult useLeft(Level world, Player user, InteractionHand hand, ItemStack stack, boolean repeatEvent)
	{
		if (!(stack.getItem() instanceof ILeftClickUsable leftItem))
			return InteractionResult.PASS;

		boolean isInstantUseItem = leftItem.getMaxUseLeftTime(stack, user) <= 0;
		InteractionResult actionResult = leftItem.useLeft(world, user, hand, repeatEvent);

		if (isInstantUseItem && actionResult instanceof InteractionResult.Success success)
			return success.heldItemTransformedTo(success.heldItemTransformedTo() == null ? stack : success.heldItemTransformedTo());

		return actionResult;
	}

	/**
	 * Invokes the emulated {@link Player#releaseUsingItem} functionality for left-use items
	 */
	public static void handleReleaseUseItem(ServerPlayNetworking.Context context)
	{
		context.server().execute(() -> {
			var player = context.player();
			player.resetLastActionTime();

			if (!(player instanceof ILeftClickingEntity leftClickingEntity))
				return;

			leftClickingEntity.pswg$stopLeftUsingItem();
		});
	}
}
