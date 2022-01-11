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
	public byte coolingMode;

	public boolean isAimingDownSights;
	public boolean canBypassCooling;

	public int shotsRemaining;
	public int heat;
	public int ventingHeat;

	public byte burstCounter;
	public byte shotTimer;
	public byte timeSinceLastShot;
	public byte passiveCooldownTimer;
	public byte overchargeTimer;

	public long serialNumber;

	public int attachmentBitmask;

	public BlasterTag(NbtCompound source)
	{
		super(SLUG, source);
	}

	public static BlasterTag fromRootTag(NbtCompound tag)
	{
		var parent = new NbtCompound();
		parent.put(SLUG.toString(), tag);
		return new BlasterTag(parent);
	}

	public static void mutate(ItemStack stack, Consumer<BlasterTag> action)
	{
		var nbt = stack.getOrCreateNbt();
		var t = new BlasterTag(nbt);
		action.accept(t);
		t.serializeAsSubtag(stack);
	}

	public void tick(BlasterDescriptor bd)
	{
		if (serialNumber == 0)
			serialNumber = Resources.RANDOM.nextLong();

		if (passiveCooldownTimer > 0)
			passiveCooldownTimer--;

		if (shotTimer > 0)
			shotTimer--;

		if (overchargeTimer > 0)
			overchargeTimer--;

		if (timeSinceLastShot < 20)
			timeSinceLastShot++;

		if (ventingHeat > 0)
		{
			ventingHeat -= bd.heat.overheatDrainSpeed;
			if (ventingHeat == 0)
				coolingMode = BlasterTag.COOLING_MODE_NONE;
		}

		if (heat > 0 && passiveCooldownTimer == 0)
			heat -= bd.heat.drainSpeed;
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
		return ventingHeat > 0;
	}
}
