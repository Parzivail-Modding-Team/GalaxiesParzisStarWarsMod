package dev.pswg.item;

import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * Provides an interface for items that can skip the hand animation when swapping
 */
public interface IHandAnimationAware
{
	/**
	 * Determines whether the hand animation should be skipped when the item stack updates
	 *
	 * @param from The starting item stack
	 * @param to   The ending item stack
	 *
	 * @return True if the hand animation should be skipped, false otherwise, and empty if the vanilla behavior is preferred
	 */
	Optional<Boolean> shouldSkipHandAnimationOnSwap(ItemStack from, ItemStack to);
}
