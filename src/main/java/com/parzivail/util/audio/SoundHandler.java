package com.parzivail.util.audio;

import net.minecraft.entity.Entity;

public class SoundHandler
{
	public SoundHandler()
	{
	}

	public static void playSound(Entity source, String name, float pitch, float volume)
	{
		source.worldObj.playSoundAtEntity(source, name, pitch, volume);
	}
}
