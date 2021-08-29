package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import com.parzivail.util.sound.DopplerSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class LightsaberThrownSoundInstance extends DopplerSoundInstance
{
	public LightsaberThrownSoundInstance(ThrownLightsaberEntity entity)
	{
		super(entity, SwgSounds.Lightsaber.IDLE_CLASSIC, SoundCategory.PLAYERS);
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0;
	}

	public boolean canPlay()
	{
		return !this.source.isSilent();
	}

	public boolean shouldAlwaysPlay()
	{
		return true;
	}

	public void tick()
	{
		super.tick();

		if (this.source.isRemoved())
		{
			this.setDone();
			return;
		}

		this.volume = (float)((Math.sin(source.age) + 1) / 2f);

		this.x = (float)this.source.getX();
		this.y = (float)this.source.getY();
		this.z = (float)this.source.getZ();
	}
}
