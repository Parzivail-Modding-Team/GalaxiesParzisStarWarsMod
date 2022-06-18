package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.util.sound.DopplerSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class LightsaberIdleSoundInstance extends DopplerSoundInstance
{
	private final PlayerEntity player;

	public LightsaberIdleSoundInstance(PlayerEntity player)
	{
		super(player, SwgSounds.Lightsaber.IDLE_CLASSIC, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.7F;
		this.x = (float)player.getX();
		this.y = (float)player.getY();
		this.z = (float)player.getZ();
	}

	@Override
	public boolean canPlay()
	{
		return !this.player.isSilent();
	}

	@Override
	public boolean shouldAlwaysPlay()
	{
		return true;
	}

	@Override
	public void tick()
	{
		super.tick();

		if (this.player.isRemoved())
		{
			this.setDone();
			return;
		}

		var foundSaber = false;

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
		var lt = new LightsaberTag(stack.getOrCreateNbt());

		var size = lt.getSize(0);
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

		var lt = new LightsaberTag(stack.getOrCreateNbt());
		return lt.getSize(0) > 0;
	}
}
