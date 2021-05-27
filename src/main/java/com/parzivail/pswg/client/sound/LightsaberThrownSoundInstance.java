package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class LightsaberThrownSoundInstance extends MovingSoundInstance
{
	private final ThrownLightsaberEntity entity;

	public LightsaberThrownSoundInstance(ThrownLightsaberEntity entity)
	{
		super(SwgSounds.Lightsaber.IDLE_CLASSIC, SoundCategory.PLAYERS);
		this.entity = entity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.7F;
		this.x = (float)entity.getX();
		this.y = (float)entity.getY();
		this.z = (float)entity.getZ();
	}

	public boolean canPlay()
	{
		return !this.entity.isSilent();
	}

	public boolean shouldAlwaysPlay()
	{
		return true;
	}

	public void tick()
	{
		if (this.entity.isRemoved())
		{
			this.setDone();
			return;
		}

		this.volume = (float)((Math.sin(entity.age) + 1) / 2f);

		this.x = (float)this.entity.getX();
		this.y = (float)this.entity.getY();
		this.z = (float)this.entity.getZ();
	}
}
