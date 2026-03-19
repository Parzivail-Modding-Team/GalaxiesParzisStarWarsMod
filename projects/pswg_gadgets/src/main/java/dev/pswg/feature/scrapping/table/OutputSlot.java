package dev.pswg.feature.scrapping.table;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OutputSlot extends Slot
{
	public OutputSlot(Container inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return false;
	}
}
