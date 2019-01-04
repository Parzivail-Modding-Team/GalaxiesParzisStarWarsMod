package com.parzivail.util.audio;

import com.parzivail.swg.StarWarsGalaxy;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class AmbientSounds
{
	private Minecraft mc;
	private final ArrayList<AmbientSoundEntry> sounds = new ArrayList<>();
	private int ambientTickCountdown = -1;

	public AmbientSounds()
	{
		resetCountdown();
	}

	private void resetCountdown()
	{
		ambientTickCountdown = StarWarsGalaxy.random.nextInt(12000) + 6000;
	}

	public void tick(TickEvent.ClientTickEvent event)
	{
		if (mc == null)
			mc = Minecraft.getMinecraft();
		if (mc == null || mc.theWorld == null)
			return;

		ambientTickCountdown--;

		if (ambientTickCountdown <= 0)
		{
			resetCountdown();
			playAmbientSound(event);
		}
	}

	private void playAmbientSound(TickEvent.ClientTickEvent event)
	{
		if (mc == null || mc.theWorld == null)
			return;
		AmbientSoundEntry entry = sounds.get(mc.theWorld.rand.nextInt(sounds.size()));
		entry.play(mc.thePlayer);
	}

	public void add(AmbientSoundEntry ambientSoundEntry)
	{
		sounds.add(ambientSoundEntry);
	}
}
