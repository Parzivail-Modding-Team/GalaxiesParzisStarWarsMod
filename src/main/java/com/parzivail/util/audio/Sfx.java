package com.parzivail.util.audio;

import net.minecraft.entity.Entity;

public class Sfx
{
	public static void play(Entity source, String name, float pitch, float volume)
	{
		source.worldObj.playSoundAtEntity(source, name, pitch, volume);
	}
}
