package dev.pswg.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Provides an interface for items that can be left-clicked to
 * perform a separate action from their right-click use action
 */
public interface ILeftClickUsable
{
	/**
	 * Called when the user uses (or starts using) the item.
	 * The use action, by default, is bound to the right mouse button.
	 *
	 * <p>If the item {@linkplain #getMaxUseLeftTime can be used for multiple ticks}, then
	 * this will only be called when the user starts using it. After that,
	 * {@link #usageTickLeft} is called every tick until the user {@linkplain #finishUsingLeft
	 * finishes using the item}.
	 *
	 * <p>This method is called on both the logical client and logical server, so take caution when overriding this method.
	 * The logical side can be checked using {@link net.minecraft.world.World#isClient() world.isClient()}.
	 *
	 * @param world  The world the item was used in
	 * @param user The user who used the item
	 * @param hand   The hand used
	 *
	 * @return An action result that specifies whether using the item was successful.
	 */
	default ActionResult useLeft(World world, LivingEntity user, Hand hand)
	{
		return ActionResult.PASS;
	}

	/**
	 * Called when an entity finishes using the item.
	 *
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 *
	 * @param stack  The item stack to query
	 * @param user The user that is holding the stack
	 * @param world  The world that the user is in
	 *
	 * @return the new item stack after using the item
	 */
	default ItemStack finishUsingLeft(ItemStack stack, World world, LivingEntity user)
	{
		return stack;
	}

	/**
	 * Called on both the client and the server when an entity stops using an item
	 * before reaching the {@linkplain #getMaxUseLeftTime maximum use time}. If the time was
	 * reached, {@link #finishUsingLeft} is called instead.
	 *
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 *
	 * @param stack             The item stack to query
	 * @param user            The user that is holding the stack
	 * @param world             The world that the user is in
	 * @param remainingUseTicks The remaining number of ticks until the {@link #getMaxUseLeftTime maximum use time}
	 */
	default void onStoppedUsingLeft(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
	}

	/**
	 * Called on both the server and the client every tick while an entity uses
	 * the item. Currently used by {@link CrossbowItem} to charge the crossbow.
	 * If this is overridden, {@link #getMaxUseLeftTime} should also be overridden to
	 * return a positive value.
	 *
	 * @param stack             The item stack to query
	 * @param user            The user that is holding the stack
	 * @param world             The world that the user is in
	 * @param remainingUseTicks How long it's left until the entity finishes using the item, in ticks
	 */
	default void usageTickLeft(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
	}

	/**
	 * Determines the maximum use (left-click) time of this item, in ticks
	 * Once a user has used an item for said number of ticks, they stop using
	 * it, and {@link #finishUsingLeft} is called.
	 *
	 * @param stack  The item stack to query
	 * @param user The user that is holding the stack
	 *
	 * @return The time, in ticks
	 */
	default int getMaxUseLeftTime(ItemStack stack, LivingEntity user)
	{
		return 0;
	}

	default boolean isUsedOnLeftRelease(ItemStack stack)
	{
		return false;
	}
}
