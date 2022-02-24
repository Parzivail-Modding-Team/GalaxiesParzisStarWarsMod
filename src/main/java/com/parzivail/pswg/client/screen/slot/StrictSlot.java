package com.parzivail.pswg.client.screen.slot;

import com.parzivail.util.Consumers;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

public class StrictSlot extends Slot
{
	private final Predicate<ItemStack> filter;
	private final Runnable onDirty;

	public StrictSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> filter)
	{
		this(inventory, index, x, y, filter, Consumers::noop);
	}

	public StrictSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> filter, Runnable onDirty)
	{
		super(inventory, index, x, y);
		this.filter = filter;
		this.onDirty = onDirty;
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return filter.test(stack);
	}

	@Override
	public void markDirty()
	{
		super.markDirty();
		onDirty.run();
	}
}
