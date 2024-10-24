package dev.pswg.interaction;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Prescribes the functionality that entities must support in order to
 * left-use items
 */
public interface ILeftClickingEntity
{
	/**
	 * Determines if the entity is currently left-using an item
	 *
	 * @return True if the entity is currently left-using an item
	 */
	boolean pswg$isLeftUsingItem();

	/**
	 * Instructs the entity to stop left-using the current item
	 */
	void pswg$stopLeftUsingItem();

	ItemStack pswg$getLeftActiveItemStack();

	int pswg$getItemLeftUseTimeLeft();

	void pswg$tickLeftActiveItemStack();

	void pswg$clearLeftActiveItem();

	void pswg$setLeftUsingItem(boolean isLeftUsing);

	void pswg$setLeftActiveItemStack(ItemStack stack);

	void pswg$setItemLeftUseTimeLeft(int timeLeft);

	void pswg$tickItemStackLeftUsage(ItemStack stack);

	void pswg$consumeLeftItem();

	void pswg$setCurrentHandLeft(Hand hand);
}
