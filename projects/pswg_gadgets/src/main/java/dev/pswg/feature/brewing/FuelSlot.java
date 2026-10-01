package dev.pswg.feature.brewing;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Accepts only items carrying Minecraft's cooking-fuel component.
 */
public class FuelSlot extends Slot
{
	/**
	 * Creates the mixer fuel slot.
	 *
	 * @param inventory the mixer inventory
	 * @param index the inventory slot index
	 * @param x the screen x coordinate
	 * @param y the screen y coordinate
	 */
	public FuelSlot(Container inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return stack.has(DataComponents.COOKING_FUEL);
	}
}
