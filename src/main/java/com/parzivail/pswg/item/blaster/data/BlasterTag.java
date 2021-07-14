package com.parzivail.pswg.item.blaster.data;

import com.parzivail.pswg.Resources;
import com.parzivail.util.math.Ease;
import com.parzivail.util.nbt.TagSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class BlasterTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("blaster_data");
	public static final int ADS_TIMER_LENGTH = 5;

	public boolean isAimingDownSights;
	public int aimingDownSightsTimer;
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

		if (aimingDownSightsTimer > 0)
			aimingDownSightsTimer--;
	}

	public void setAimingDownSights(boolean isAimingDownSights)
	{
		if (isAimingDownSights != this.isAimingDownSights)
			aimingDownSightsTimer = ADS_TIMER_LENGTH;
		this.isAimingDownSights = isAimingDownSights;
	}

	@Environment(EnvType.CLIENT)
	public float getAdsLerp()
	{
		if (aimingDownSightsTimer == 0)
			return isAimingDownSights ? 1 : 0;

		var delta = MinecraftClient.getInstance().getTickDelta();

		var timer = (aimingDownSightsTimer - delta) / (float)ADS_TIMER_LENGTH;

		if (isAimingDownSights)
			return Ease.outCubic(1 - timer);
		return Ease.inCubic(timer);
	}

	public void toggleAds()
	{
		setAimingDownSights(!isAimingDownSights);
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
