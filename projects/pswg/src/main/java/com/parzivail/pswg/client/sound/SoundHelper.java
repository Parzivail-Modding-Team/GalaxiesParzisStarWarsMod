package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.features.lightsabers.client.ThrownLightsaberEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;

public class SoundHelper
{
	public static void playThrownLightsaberSound(ThrownLightsaberEntity entity)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new LightsaberThrownSoundInstance(entity));
	}

	public static void playShipExteriorSound(ShipEntity entity, SoundEvent sound)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new ShipExteriorSoundInstance(entity, sound, SoundInstance.createRandom()));
	}

	public static void playDetonatorItemSound(PlayerEntity player)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new ThermalDetonatorItemSoundInstance(player));
	}

	public static void playFragmentationGrenadeItemSound(PlayerEntity player)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new FragmentationGrenadeItemSoundInstance(player));
	}

	public static void playDetonatorEntitySound(ThermalDetonatorEntity entity)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getSoundManager().play(new ThermalDetonatorEntitySoundInstance(entity));
	}
}
