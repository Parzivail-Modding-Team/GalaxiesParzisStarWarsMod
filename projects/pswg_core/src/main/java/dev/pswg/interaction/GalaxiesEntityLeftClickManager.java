package dev.pswg.interaction;

import dev.pswg.item.ILeftClickUsable;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Objects;

public class GalaxiesEntityLeftClickManager
{
	public ActionResult interactItemLeft(ServerPlayerInteractionManager manager, ServerPlayerEntity player, World world, ItemStack stack, Hand hand)
	{
		if (!(stack.getItem() instanceof ILeftClickUsable))
			return ActionResult.PASS;
		else if (manager.getGameMode() == GameMode.SPECTATOR)
			return ActionResult.PASS;
		else if (player.getItemCooldownManager().isCoolingDown(stack))
			return ActionResult.PASS;
		else
		{
			var i = stack.getCount();
			var j = stack.getDamage();
			var actionResult = useLeft(stack, world, player, hand);
			ItemStack itemStack;
			if (actionResult instanceof ActionResult.Success success)
			{
				itemStack = Objects.requireNonNullElse(success.getNewHandStack(), player.getStackInHand(hand));
			}
			else
			{
				itemStack = player.getStackInHand(hand);
			}

			if (itemStack == stack && itemStack.getCount() == i && getMaxUseLeftTime(itemStack, player) <= 0 && itemStack.getDamage() == j)
			{
				return actionResult;
			}
			else if (actionResult instanceof ActionResult.Fail && getMaxUseLeftTime(itemStack, player) > 0 && !isUsingItemLeft(player))
			{
				return actionResult;
			}
			else
			{
				// TODO: should the left-hand usage ever be allowed to modify the itemstack?
				//				if (stack != itemStack)
				//				{
				//					player.setStackInHand(hand, itemStack);
				//				}
				//
				//				if (itemStack.isEmpty())
				//				{
				//					player.setStackInHand(hand, ItemStack.EMPTY);
				//				}

				if (!isUsingItemLeft(player))
				{
					player.playerScreenHandler.syncState();
				}

				return actionResult;
			}
		}
	}

	public static boolean isUsingItemLeft(ServerPlayerEntity player)
	{
		return (player.getDataTracker().get(LEFT_USING_FLAGS) & FLAG_LEFT_USING) > 0;
	}

	public static ActionResult useLeft(ItemStack stack, World world, ServerPlayerEntity user, Hand hand)
	{
		var itemStack = stack.copy();
		var isImmediateUseItem = getMaxUseLeftTime(stack, user) <= 0;
		var actionResult = stack.getItem().use(world, user, hand);

		if (isImmediateUseItem && actionResult instanceof ActionResult.Success success)
			return success.withNewHandStack(itemStack);
		else
			return actionResult;
	}

	public static int getMaxUseLeftTime(ItemStack stack, ServerPlayerEntity user)
	{
		return ((ILeftClickUsable)stack.getItem()).getMaxUseLeftTime(stack, user);
	}
}
