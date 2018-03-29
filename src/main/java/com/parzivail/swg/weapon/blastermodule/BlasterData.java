package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.swg.weapon.blastermodule.barrel.BlasterBarrel;
import com.parzivail.swg.weapon.blastermodule.grip.BlasterGrip;
import com.parzivail.swg.weapon.blastermodule.scope.BlasterScope;
import com.parzivail.util.item.NbtSerializable;
import net.minecraft.item.ItemStack;

public class BlasterData extends NbtSerializable<BlasterData>
{
	public boolean isAimingDownSights = false;
	public int shotsRemaining = 0;
	public BlasterAttachment[] attachments = new BlasterAttachment[0];

	public BlasterData(ItemStack stack)
	{
		super(stack);
	}

	public BlasterScope getScope()
	{
		for (BlasterAttachment attachment : attachments)
			if (attachment instanceof BlasterScope)
				return (BlasterScope)attachment;
		return null;
	}

	public BlasterGrip getGrip()
	{
		for (BlasterAttachment attachment : attachments)
			if (attachment instanceof BlasterGrip)
				return (BlasterGrip)attachment;
		return null;
	}

	public BlasterBarrel getBarrel()
	{
		for (BlasterAttachment attachment : attachments)
			if (attachment instanceof BlasterBarrel)
				return (BlasterBarrel)attachment;
		return null;
	}
}
