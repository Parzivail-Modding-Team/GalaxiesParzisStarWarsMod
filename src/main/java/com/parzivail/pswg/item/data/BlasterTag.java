package com.parzivail.pswg.item.data;

import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class BlasterTag extends TagSerializer
{
	public boolean isAimingDownSights;
	public int shotsRemaining;
	public int heat;
	public int cooldownTimer;
	public int shotTimer;

	public BlasterTag(CompoundTag source)
	{
		super(source);
	}

	public static void mutate(ItemStack stack, Consumer<BlasterTag> action)
	{
		CompoundTag nbt = stack.getOrCreateTag();
		BlasterTag t = new BlasterTag(nbt);
		action.accept(t);
		stack.setTag(t.serialize());
	}

	public void tick()
	{
		if (cooldownTimer > 0)
			cooldownTimer--;

		if (shotTimer > 0)
			shotTimer--;

		if (heat > 0)
			heat--;
	}

	public boolean isReady()
	{
		return shotTimer == 0;
	}

	public boolean isCoolingDown()
	{
		return cooldownTimer > 0;
	}
}
