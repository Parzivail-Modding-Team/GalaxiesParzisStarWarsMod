package com.parzivail.util.audio;

import com.parzivail.swg.audio.MovingSoundLightsaberIdle;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class ClientSoundHandler
{
	private static final HashMap<EntityPlayer, MovingSoundLightsaberIdle> PLAYER_SOUND_MAP = new HashMap<>();

	public static void playLightsaberSound(EntityPlayer player)
	{
		if (!PLAYER_SOUND_MAP.containsKey(player))
		{
			MovingSoundLightsaberIdle sound = new MovingSoundLightsaberIdle(player);
			SoundHandler sh = FMLClientHandler.instance().getClient().getSoundHandler();
			sh.playSound(sound);
			PLAYER_SOUND_MAP.put(player, sound);
		}
	}

	public static void stopLightsaberSound(EntityPlayer player)
	{
		if (PLAYER_SOUND_MAP.containsKey(player))
		{
			MovingSoundLightsaberIdle sound = PLAYER_SOUND_MAP.get(player);
			SoundHandler sh = FMLClientHandler.instance().getClient().getSoundHandler();
			sh.stopSound(sound);
			PLAYER_SOUND_MAP.remove(player);
		}
	}
}
