package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.dimension.tatooine.ChunkProviderTatooine;
import com.parzivail.util.audio.AmbientSoundEntry;
import com.parzivail.util.audio.AmbientSounds;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;

public class SoundHandler
{
	private static final AmbientSounds sounds = new AmbientSounds();

	static
	{
		sounds.add(new AmbientSoundEntry(StarWarsGalaxy.config.getDimIdTatooine(), "swg.amb.bantha"));
		sounds.add(new AmbientSoundEntry(StarWarsGalaxy.config.getDimIdTatooine(), "swg.amb.warble", SoundHandler::isPlayerInCanyon));
		sounds.add(new AmbientSoundEntry(StarWarsGalaxy.config.getDimIdTatooine(), "swg.amb.rock", SoundHandler::isPlayerInCanyon));
		sounds.add(new AmbientSoundEntry(StarWarsGalaxy.config.getDimIdTatooine(), "swg.amb.wind"));
	}

	private static boolean isPlayerInCanyon(EntityPlayer entityPlayer)
	{
		if (entityPlayer.dimension == StarWarsGalaxy.config.getDimIdTatooine())
		{
			double[] weights = ChunkProviderTatooine.terrain.getBiomeWeightsAt((int)entityPlayer.posX, (int)entityPlayer.posZ);

			return weights[0] > 0.5;
		}

		return false;
	}

	public static void tick(TickEvent.ClientTickEvent event)
	{
		sounds.tick(event);
	}
}
