package com.parzivail.pswg.features.blasters;

import com.parzivail.pswg.features.blasters.data.BlasterPowerPack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlasterPowerPackItem extends Item
{
	private final int numShotsProvided;

	public BlasterPowerPackItem(int numShotsProvided, Settings settings)
	{
		super(settings);
		this.numShotsProvided = numShotsProvided;
	}

	public static BlasterPowerPack getPackType(ItemStack stack)
	{
		if (!(stack.getItem() instanceof BlasterPowerPackItem ppi))
			return null;

		return new BlasterPowerPack(ppi.numShotsProvided);
	}
}
