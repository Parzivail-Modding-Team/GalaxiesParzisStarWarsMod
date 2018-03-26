package com.parzivail.swg.item.data;

import com.parzivail.util.item.NbtSerializable;
import net.minecraft.item.ItemStack;

public class BlasterData extends NbtSerializable<BlasterData>
{
	public boolean isAimingDownSights;
	public int shotsRemaining;

	public BlasterData(ItemStack stack)
	{
		super(stack);
	}
}
