package dev.pswg.feature.brewing;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DrinkContainerSlot extends Slot
{
	public DrinkContainerSlot(Container inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return stack.is(GadgetsItems.Tags.DRINK_CONTAINER_TAG);
	}

	@Override
	public int getMaxStackSize()
	{
		return 1;
	}
}
