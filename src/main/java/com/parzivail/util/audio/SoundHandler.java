package com.parzivail.util.audio;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class SoundHandler
{
	public SoundHandler()
	{
	}

	public static void playSound(EntityPlayerMP source, String name, double x, double y, double z, float pitch, float volume)
	{
		source.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(name, x, y, z, volume, pitch));
	}
}
