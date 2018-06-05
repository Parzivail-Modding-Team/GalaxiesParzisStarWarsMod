package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.swg.weapon.blastermodule.barrel.BlasterBarrel;
import com.parzivail.swg.weapon.blastermodule.grip.BlasterGrip;
import com.parzivail.swg.weapon.blastermodule.scope.BlasterScope;
import com.parzivail.util.item.ItemUtils;
import com.parzivail.util.item.NbtSerializable;
import net.minecraft.item.ItemStack;

public class BlasterData extends NbtSerializable<BlasterData>
{
	public boolean isAimingDownSights;
	public int shotsRemaining;

	// These are not actually deprecated, just a reminder to use
	// the getter instead which does safe casts. They need to be
	// public for the deserialzer.
	@Deprecated
	public BlasterAttachment scope;
	@Deprecated
	public BlasterAttachment grip;
	@Deprecated
	public BlasterAttachment barrel;

	public BlasterData(ItemStack stack)
	{
		ItemUtils.ensureNbt(stack);
		deserialize(stack.stackTagCompound);
	}

	public BlasterScope getScope()
	{
		return scope == null ? BlasterAttachments.scopeIronsights : (BlasterScope)scope;
	}

	public void setScope(BlasterScope scope)
	{
		this.scope = scope;
	}

	public BlasterGrip getGrip()
	{
		return grip == null ? BlasterAttachments.gripNone : (BlasterGrip)grip;
	}

	public void setGrip(BlasterGrip grip)
	{
		this.grip = grip;
	}

	public BlasterBarrel getBarrel()
	{
		return barrel == null ? BlasterAttachments.barrelDefault : (BlasterBarrel)barrel;
	}

	public void setBarrel(BlasterBarrel barrel)
	{
		this.barrel = barrel;
	}
}
