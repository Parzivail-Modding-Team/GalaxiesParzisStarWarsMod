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
	public static final byte COOLING_MODE_NONE = 0;
	public static final byte COOLING_MODE_OVERHEAT = 1;
	public static final byte COOLING_MODE_FORCED_BYPASS = 2;
	public static final byte COOLING_MODE_PENALTY_BYPASS = 3;

	public byte firingMode;

	public boolean isAimingDownSights;

	public int shotsRemaining;
	public int shotTimer;

	public int burstTimer;

	public boolean canBypassCooling;
	public int heat;
	public int coolingTimer;
	public byte coolingMode;
	public int passiveCooldownTimer;

	public long serialNumber;

	public BlasterTag(NbtCompound source)
	{
		super(SLUG, source);
		if (this.serialNumber == 0)
			this.serialNumber = Resources.RANDOM.nextLong();
	}

	public static void mutate(ItemStack stack, Consumer<BlasterTag> action)
	{
		var nbt = stack.getOrCreateNbt();
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

	public void setAimingDownSights(boolean isAimingDownSights)
	{
		this.isAimingDownSights = isAimingDownSights;
	}

	public BlasterFiringMode getFiringMode()
	{
		return BlasterFiringMode.ID_LOOKUP.get(firingMode);
	}

	public void setFiringMode(BlasterFiringMode mode)
	{
		this.firingMode = mode.getId();
	}

	public void toggleAds()
	{
		setAimingDownSights(!isAimingDownSights);
	}

	public boolean isReady()
	{
		return shotTimer == 0;
	}

	public boolean isCooling()
	{
		return coolingTimer > 0;
	}
}
