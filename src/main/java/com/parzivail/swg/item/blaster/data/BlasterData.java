package com.parzivail.swg.item.blaster.data;

import com.parzivail.swg.item.blaster.data.barrel.BlasterBarrel;
import com.parzivail.swg.item.blaster.data.grip.BlasterGrip;
import com.parzivail.swg.item.blaster.data.scope.BlasterScope;
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

	@NbtSave
	protected BlasterAttachment scope;
	@NbtSave
	protected BlasterAttachment grip;
	@NbtSave
	protected BlasterAttachment barrel;

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

	public boolean isCoolingDown()
	{
		return cooldownTimer > 0;
	}

	public boolean isReady()
	{
		return shotTimer <= 0;
	}
}
