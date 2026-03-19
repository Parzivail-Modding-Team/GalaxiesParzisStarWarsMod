package dev.pswg.feature.scrapping.table;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ToolSlot extends Slot
{
	Item tool;

	public ToolSlot(Container inventory, int index, int x, int y, Item tool)
	{
		super(inventory, index, x, y);
		this.tool = tool;
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return stack.is(tool);
	}
}
