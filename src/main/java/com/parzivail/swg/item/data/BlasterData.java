package com.parzivail.swg.item.data;

import com.parzivail.util.item.NbtSerializable;
import net.minecraft.nbt.NBTTagCompound;

public class BlasterData extends NbtSerializable<BlasterData>
{
	public boolean isAimingDownSights;
	public int shotsRemaining;

	public BlasterData(NBTTagCompound compound)
	{
		super(compound);
	}
}
