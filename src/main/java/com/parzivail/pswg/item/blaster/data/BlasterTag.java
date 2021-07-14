package com.parzivail.pswg.item.blaster.data;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class BlasterTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("blaster_data");

	public boolean isAimingDownSights;
	public boolean canBypassOverheat;
	public int shotsRemaining;
	public int heat;
	public int overheatTimer;
	public int passiveCooldownTimer;
	public int shotTimer;

	public BlasterTag(NbtCompound source)
	{
		super(SLUG, source);
	}

	public static void mutate(ItemStack stack, Consumer<BlasterTag> action)
	{
		var nbt = stack.getOrCreateTag();
		var t = new BlasterTag(nbt);
		action.accept(t);
		t.serializeAsSubtag(stack);
	}

	public void tick()
	{
		if (passiveCooldownTimer > 0)
			passiveCooldownTimer--;

		if (shotTimer > 0)
			shotTimer--;
	}

	public boolean isReady()
	{
		return shotTimer == 0;
	}

	public boolean isOverheatCooling()
	{
		return overheatTimer > 0;
	}
}
