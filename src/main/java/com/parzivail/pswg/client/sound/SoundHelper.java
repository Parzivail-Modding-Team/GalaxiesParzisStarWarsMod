package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import net.minecraft.client.MinecraftClient;

public class SoundHelper
{
	public static void playThrownLightsaberSound(ThrownLightsaberEntity entity)
	{
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new LightsaberThrownSoundInstance(entity));
	}
}
