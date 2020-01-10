package com.parzivail.swg.item;

import com.parzivail.swg.register.ItemRegister;
import net.minecraft.item.ItemStack;

public class BlasterPowerPack
{
	public static BlasterPowerPack getPackType(ItemStack s)
	{
		if (s.getItem() == ItemRegister.gasCanisterSmall)
			return new BlasterPowerPack();

		return null;
	}

	public int getNumShots()
	{
		return 10;
	}
}
