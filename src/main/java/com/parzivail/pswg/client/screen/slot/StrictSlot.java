package com.parzivail.pswg.client.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Function;

public class StrictSlot extends Slot
{
	private final Function<ItemStack, Boolean> filter;

	public StrictSlot(Inventory inventory, int index, int x, int y, Function<ItemStack, Boolean> filter)
	{
		super(inventory, index, x, y);
		this.filter = filter;
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return filter.apply(stack);
	}
}
