package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.lightsaber.LightsaberTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class LightsaberIdleSoundInstance extends MovingSoundInstance
{
	private final PlayerEntity player;

	public LightsaberIdleSoundInstance(PlayerEntity player)
	{
		super(SwgSounds.Lightsaber.IDLE_CLASSIC, SoundCategory.PLAYERS);
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.7F;
		this.x = (float)player.getX();
		this.y = (float)player.getY();
		this.z = (float)player.getZ();
	}

	public boolean canPlay()
	{
		return !this.player.isSilent();
	}

	public boolean shouldAlwaysPlay()
	{
		return true;
	}

	public void tick()
	{
		if (this.player.removed)
		{
			this.setDone();
			return;
		}

		boolean foundSaber = false;

		if (player.getMainHandStack().getItem() instanceof LightsaberItem)
			foundSaber = tryUseStack(player.getMainHandStack());

		if (!foundSaber && player.getOffHandStack().getItem() instanceof LightsaberItem)
			foundSaber = tryUseStack(player.getOffHandStack());

		if (!foundSaber)
		{
			this.setDone();
			return;
		}

		this.x = (float)this.player.getX();
		this.y = (float)this.player.getY();
		this.z = (float)this.player.getZ();
	}

	private boolean tryUseStack(ItemStack stack)
	{
		LightsaberTag lt = new LightsaberTag(stack.getOrCreateTag());

		float size = lt.getSize(0);
		if (size > 0)
		{
			volume = size;
			return true;
		}

		return false;
	}

	public static boolean areConditionsMet(PlayerEntity player)
	{
		return isActiveLightsaber(player.getMainHandStack()) || isActiveLightsaber(player.getOffHandStack());
	}

	private static boolean isActiveLightsaber(ItemStack stack)
	{
		if (!(stack.getItem() instanceof LightsaberItem))
			return false;

		LightsaberTag lt = new LightsaberTag(stack.getOrCreateTag());
		return lt.getSize(0) > 0;
	}
}
