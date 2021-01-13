package com.parzivail.pswg.item.blaster;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class BlasterTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.identifier("blaster");

	public boolean isAimingDownSights;
	public boolean canBypassCooling;
	public int shotsRemaining;
	public int heat;
	public int cooldownTimer;
	public int shotTimer;

	public BlasterTag(CompoundTag source)
	{
		super(SLUG, source);
	}

	public static void mutate(ItemStack stack, Consumer<BlasterTag> action)
	{
		CompoundTag nbt = stack.getOrCreateTag();
		BlasterTag t = new BlasterTag(nbt);
		action.accept(t);
		t.serializeAsSubtag(stack);
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
