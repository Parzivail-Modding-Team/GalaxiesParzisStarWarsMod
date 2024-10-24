package dev.pswg.interaction;

import dev.pswg.item.ILeftClickUsable;
import dev.pswg.networking.PlayerInteractItemLeftC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * Handles left-use item interactions on the server
 */
public class GalaxiesEntityLeftClickManager
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

			ServerWorld serverWorld = player.getServerWorld();
			Hand hand = packet.hand();
			ItemStack itemStack = player.getStackInHand(hand);
			player.updateLastActionTime();

			if (!itemStack.isEmpty() && itemStack.isItemEnabled(serverWorld.getEnabledFeatures()))
			{
				float f = MathHelper.wrapDegrees(packet.yaw());
				float g = MathHelper.wrapDegrees(packet.pitch());

				if (g != player.getPitch() || f != player.getYaw())
				{
					player.setAngles(f, g);
				}

				var result = interactItemLeft(player.interactionManager, player, serverWorld, itemStack, hand);
				if (result instanceof ActionResult.Success success && success.swingSource() == ActionResult.SwingSource.SERVER)
				{
					player.swingHand(hand, true);
				}
			}
		});
	}

	/**
	 * Emulates the {@link ServerPlayerInteractionManager#interactItem} functionality for
	 * left-use items
	 */
	private static ActionResult interactItemLeft(ServerPlayerInteractionManager interactionManager, ServerPlayerEntity player, ServerWorld world, ItemStack stack, Hand hand)
	{
		if (interactionManager.getGameMode() == GameMode.SPECTATOR)
		{
			return ActionResult.PASS;
		}
		else
		{
			if (!(stack.getItem() instanceof ILeftClickUsable leftItem) || !(player instanceof ILeftClickingEntity leftClickingEntity))
				return ActionResult.PASS;

			int i = stack.getCount();
			int j = stack.getDamage();

			ActionResult actionResult = useLeft(world, player, hand, stack);
			ItemStack itemStack;
			if (actionResult instanceof ActionResult.Success success)
			{
				itemStack = Objects.requireNonNullElse(success.getNewHandStack(), player.getStackInHand(hand));
			}
			else
			{
				itemStack = player.getStackInHand(hand);
			}

			if (itemStack == stack && itemStack.getCount() == i && leftItem.getMaxUseLeftTime(itemStack, player) <= 0 && itemStack.getDamage() == j)
			{
				return actionResult;
			}
			else if (actionResult instanceof ActionResult.Fail && leftItem.getMaxUseLeftTime(itemStack, player) > 0 && !leftClickingEntity.pswg$isLeftUsingItem())
			{
				return actionResult;
			}
			else
			{
				if (stack != itemStack)
				{
					player.setStackInHand(hand, itemStack);
				}

				if (itemStack.isEmpty())
				{
					player.setStackInHand(hand, ItemStack.EMPTY);
				}

				if (!leftClickingEntity.pswg$isLeftUsingItem())
				{
					player.playerScreenHandler.syncState();
				}

				return actionResult;
			}
		}
	}

	/**
	 * Emulates the {@link ItemStack#use} functionality for left-use items
	 */
	static ActionResult useLeft(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (!(stack.getItem() instanceof ILeftClickUsable leftItem))
			return ActionResult.PASS;

		boolean isInstantUseItem = leftItem.getMaxUseLeftTime(stack, user) <= 0;
		ActionResult actionResult = leftItem.useLeft(world, user, hand);

		if (isInstantUseItem && actionResult instanceof ActionResult.Success success)
			return success.withNewHandStack(success.getNewHandStack() == null ? stack : success.getNewHandStack());

		return actionResult;
	}

	/**
	 * Invokes the emulated {@link PlayerEntity#stopUsingItem} functionality for left-use items
	 */
	public static void handleReleaseUseItem(ServerPlayNetworking.Context context)
	{
		context.server().execute(() -> {
			var player = context.player();
			player.updateLastActionTime();

			if (!(player instanceof ILeftClickingEntity leftClickingEntity))
				return;

			leftClickingEntity.pswg$stopLeftUsingItem();
		});
	}
}
