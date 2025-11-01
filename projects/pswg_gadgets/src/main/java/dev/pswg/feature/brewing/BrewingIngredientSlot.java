package dev.pswg.feature.brewing;

import dev.pswg.container.GadgetsItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BrewingIngredientSlot extends Slot
{
	public BrewingIngredientSlot(Inventory inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return stack.contains(GadgetsItems.Components.BREWING_PATH) || stack.getItem() instanceof DyeItem;
	}
}
