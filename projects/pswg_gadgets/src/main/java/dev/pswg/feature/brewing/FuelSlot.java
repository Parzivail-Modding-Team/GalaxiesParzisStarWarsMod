package dev.pswg.feature.brewing;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FuelSlot extends Slot
{
	public MixerScreenHandler handler;

	public FuelSlot(Inventory inventory, int index, int x, int y, MixerScreenHandler handler)
	{
		super(inventory, index, x, y);
		this.handler = handler;
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return handler.world.getFuelRegistry().isFuel(stack);
	}
}
