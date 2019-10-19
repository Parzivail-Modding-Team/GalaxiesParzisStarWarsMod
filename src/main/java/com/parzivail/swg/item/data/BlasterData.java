package com.parzivail.swg.item.data;

import com.parzivail.util.item.ItemUtils;
import com.parzivail.util.item.NbtSave;
import com.parzivail.util.item.NbtSerializable;
import net.minecraft.item.ItemStack;

public class BlasterData extends NbtSerializable<BlasterData>
{
	@NbtSave
	public boolean isAimingDownSights;
	@NbtSave
	public int shotsRemaining;
	@NbtSave
	public int heat;
	@NbtSave
	public int cooldownTimer;
	@NbtSave
	public int shotTimer;

	public BlasterData(ItemStack stack)
	{
		ItemUtils.ensureNbt(stack);
		deserialize(stack);
	}

	public boolean isCoolingDown()
	{
		return cooldownTimer > 0;
	}

	public boolean isReady()
	{
		return shotTimer <= 0;
	}

	public int getHeatPerShot()
	{
		return 10;
	}
}
