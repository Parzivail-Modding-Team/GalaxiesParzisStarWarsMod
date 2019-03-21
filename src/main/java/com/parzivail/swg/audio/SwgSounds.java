package com.parzivail.swg.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class SwgSounds
{
	private final HashMap<SwgSound, ISound> soundHashMap = new HashMap<>();

	public SwgSounds(EntityPlayer player)
	{
		soundHashMap.put(SwgSound.LightsaberIdle, new MovingSoundLightsaberIdle(player));
		soundHashMap.put(SwgSound.HyperspaceAlarm, new MovingSoundHyperspaceAlarm(player));
	}

	public ISound get(SwgSound type)
	{
		return soundHashMap.get(type);
	}
}
