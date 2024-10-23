package dev.pswg.interaction;

import net.minecraft.item.ItemStack;

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
	boolean pswg$isUsingItemLeft();

	/**
	 * Instructs the entity to stop left-using the current item
	 */
	void pswg$stopUsingItemLeft();

	ItemStack pswg$getLeftActiveItemStack();

	int pswg$getItemLeftUseTimeLeft();

	void pswg$tickLeftActiveItemStack();

	void pswg$clearLeftActiveItem();

	void pswg$setLeftUsingItemFlag(boolean isLeftUsing);

	void pswg$setLeftActiveItemStack(ItemStack stack);

	void pswg$setItemLeftUseTimeLeft(int timeLeft);
}
