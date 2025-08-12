package dev.pswg.feature.scrapping;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SingleItemInputSlot extends Slot
{
	public SingleItemInputSlot(Inventory inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public int getMaxItemCount()
	{
		return 1;
	}
}
