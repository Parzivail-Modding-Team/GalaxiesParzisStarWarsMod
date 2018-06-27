package com.parzivail.util.ui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class SpecificSlot extends Slot
{
	private final Function<ItemStack, Boolean> funcIsItemValid;

	public SpecificSlot(IInventory inventory, int id, int x, int y, Function<ItemStack, Boolean> isItemValid)
	{
		super(inventory, id, x, y);
		funcIsItemValid = isItemValid;
	}

	/**
	 * Check if the stack is a valid item for this blasterSlot. Always true beside for the armor slots.
	 */
	public boolean isItemValid(ItemStack stack)
	{
		return stack != null && funcIsItemValid.apply(stack);
	}

	/**
	 * Returns the maximum stack size for a given blasterSlot (usually the same as getInventoryStackLimit(), but 1 in the
	 * case of armor slots)
	 */
	public int getSlotStackLimit()
	{
		return 1;
	}
}
