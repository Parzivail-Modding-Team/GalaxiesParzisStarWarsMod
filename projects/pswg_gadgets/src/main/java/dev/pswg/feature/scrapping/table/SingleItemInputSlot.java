package dev.pswg.feature.scrapping.table;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SingleItemInputSlot extends Slot
{
	public SingleItemInputSlot(Container inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public int getMaxStackSize()
	{
		return 1;
	}
}
