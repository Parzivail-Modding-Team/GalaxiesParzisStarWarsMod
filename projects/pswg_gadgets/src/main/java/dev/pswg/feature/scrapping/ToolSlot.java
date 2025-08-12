package dev.pswg.feature.scrapping;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ToolSlot extends Slot
{
	Item tool;

	public ToolSlot(Inventory inventory, int index, int x, int y, Item tool)
	{
		super(inventory, index, x, y);
		this.tool = tool;
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return stack.isOf(tool);
	}
}
