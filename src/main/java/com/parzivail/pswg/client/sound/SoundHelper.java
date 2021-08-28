package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import net.minecraft.client.MinecraftClient;

public class SoundHelper
{
	public static void playThrownLightsaberSound(ThrownLightsaberEntity entity)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new LightsaberThrownSoundInstance(entity));
	}

	public static void playBlasterBoltHissSound(BlasterBoltEntity entity)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new BlasterBoltHissSoundInstance(entity));
	}
}
