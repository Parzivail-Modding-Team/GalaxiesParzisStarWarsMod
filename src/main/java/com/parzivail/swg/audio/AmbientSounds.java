package com.parzivail.swg.audio;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.dimension.tatooine.ChunkProviderTatooine;
import com.parzivail.swg.proxy.Client;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.function.Function;

public class AmbientSounds
{
	private static final ArrayList<AmbientSoundEntry> sounds = new ArrayList<>();
	private static int ambientTickCountdown = -1;

	static
	{
		resetCountdown();

		sounds.add(new AmbientSoundEntry(Resources.dimIdTatooine, "swg.amb.bantha"));
		sounds.add(new AmbientSoundEntry(Resources.dimIdTatooine, "swg.amb.warble", AmbientSounds::isPlayerInCanyon));
		sounds.add(new AmbientSoundEntry(Resources.dimIdTatooine, "swg.amb.rock", AmbientSounds::isPlayerInCanyon));
		sounds.add(new AmbientSoundEntry(Resources.dimIdTatooine, "swg.amb.wind"));
	}

	private static void resetCountdown()
	{
		ambientTickCountdown = StarWarsGalaxy.random.nextInt(12000) + 6000;
	}

	public static void tick(TickEvent.ClientTickEvent event)
	{
		if (Client.mc == null || Client.mc.theWorld == null)
			return;

		ambientTickCountdown--;

		if (ambientTickCountdown <= 0)
		{
			resetCountdown();
			playAmbientSound(event);
		}
	}

	private static void playAmbientSound(TickEvent.ClientTickEvent event)
	{
		// try up to 5 times to find a sound that can be played here
		for (int i = 0; i < 5; i++)
		{
			AmbientSoundEntry entry = sounds.get(Client.mc.theWorld.rand.nextInt(sounds.size()));
			if (entry.play(Client.mc.thePlayer))
				return;
		}
	}

	private static boolean isPlayerInCanyon(EntityPlayer entityPlayer)
	{
		if (entityPlayer.dimension == Resources.dimIdTatooine)
		{
			double[] weights = ChunkProviderTatooine.terrain.getBiomeWeightsAt((int)entityPlayer.posX, (int)entityPlayer.posZ);

			return weights[0] > 0.5;
		}

		return false;
	}

	private static class AmbientSoundEntry
	{
		private final int dimension;
		private final String name;
		private final Function<EntityPlayer, Boolean> condition;

		public AmbientSoundEntry(int dimension, String name)
		{
			this(dimension, name, null);
		}

		public AmbientSoundEntry(int dimension, String name, Function<EntityPlayer, Boolean> condition)
		{
			this.dimension = dimension;
			this.name = name;
			this.condition = condition;
		}

		public boolean play(EntityPlayer player)
		{
			if (player == null || player.dimension != dimension)
				return false;

			if (condition != null && !condition.apply(player))
				return false;

			player.playSound("pswg:" + name, 0.7F, 0.8F + player.worldObj.rand.nextFloat() * 0.2F);
			return true;
		}
	}
}
