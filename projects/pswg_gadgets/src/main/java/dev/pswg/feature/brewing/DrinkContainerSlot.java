package dev.pswg.feature.brewing;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class DrinkContainerSlot extends Slot
{
	public DrinkContainerSlot(Inventory inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return stack.isIn(GadgetsItems.Tags.DRINK_CONTAINER_TAG);
	}

	@Override
	public int getMaxItemCount()
	{
		return 1;
	}
}
