package dev.pswg.feature.brewing;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FuelSlot extends Slot
{
	public MixerScreenHandler handler;

	public FuelSlot(Container inventory, int index, int x, int y, MixerScreenHandler handler)
	{
		super(inventory, index, x, y);
		this.handler = handler;
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return handler.world.fuelValues().isFuel(stack);
	}
}
