package com.parzivail.pswg.features.blasters.client;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.util.sound.DopplerSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class BlasterBoltHissSoundInstance extends DopplerSoundInstance
{
	public BlasterBoltHissSoundInstance(BlasterBoltEntity entity)
	{
		super(entity, SwgSounds.Blaster.HISS, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0f;
	}

	@Override
	public boolean canPlay()
	{
		return !this.source.isSilent();
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

		if (this.source.isRemoved())
		{
			this.setDone();
			return;
		}

		this.volume = 1;
		this.x = (float)this.source.getX();
		this.y = (float)this.source.getY();
		this.z = (float)this.source.getZ();
	}
}
