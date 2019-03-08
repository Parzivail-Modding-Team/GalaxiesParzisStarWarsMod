package com.parzivail.util.audio;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class ClientSoundHandler
{
	private static final HashMap<EntityPlayer, SwgSounds> PLAYER_SOUND_MAP = new HashMap<>();

	public static void playSound(EntityPlayer player, SwgSound type)
	{
		SwgSounds sounds = getSounds(player);

		ISound sound = sounds.get(type);
		SoundHandler sh = FMLClientHandler.instance().getClient().getSoundHandler();
		if (!sh.isSoundPlaying(sound))
			sh.playSound(sound);

		putSounds(player, sounds);
	}

	public static void stopSound(EntityPlayer player, SwgSound type)
	{
		SwgSounds sounds = getSounds(player);

		ISound sound = sounds.get(type);
		SoundHandler sh = FMLClientHandler.instance().getClient().getSoundHandler();
		if (sh.isSoundPlaying(sound))
			sh.stopSound(sound);

		putSounds(player, sounds);
	}

	private static SwgSounds getSounds(EntityPlayer player)
	{
		if (!PLAYER_SOUND_MAP.containsKey(player))
			PLAYER_SOUND_MAP.put(player, new SwgSounds(player));
		return PLAYER_SOUND_MAP.get(player);
	}

	private static void putSounds(EntityPlayer player, SwgSounds sounds)
	{
		PLAYER_SOUND_MAP.replace(player, sounds);
	}
}
