package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import com.parzivail.pswg.entity.ship.ShipEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvent;

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

	public static void playShipExteriorSound(ShipEntity entity, SoundEvent sound)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new ShipExteriorSoundInstance(entity, sound, SoundInstance.createRandom()));
	}
}
