package com.parzivail.util.sound;

import com.parzivail.util.math.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class DopplerSoundInstance extends MovingSoundInstance
{
	protected final Entity source;

	protected DopplerSoundInstance(Entity source, SoundEvent soundEvent, SoundCategory soundCategory, Random random)
	{
		super(soundEvent, soundCategory, random);
		this.source = source;
	}

	@Override
	public void tick()
	{
		var mc = MinecraftClient.getInstance();
		if (mc.getCameraEntity() != null)
			this.pitch = 1 + MathUtil.calculateDopplerShift(source, mc.getCameraEntity());
	}
}
