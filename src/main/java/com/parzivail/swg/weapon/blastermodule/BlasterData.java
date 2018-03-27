package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.util.item.NbtSerializable;
import net.minecraft.item.ItemStack;

public class BlasterData extends NbtSerializable<BlasterData>
{
	public boolean isAimingDownSights;
	public int shotsRemaining;
	public int scope;

	public BlasterData(ItemStack stack)
	{
		super(stack);
	}
}
