package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SwgSounds
{
	public static void register()
	{
		Registry.register(Registry.SOUND_EVENT, Lightsaber.START_CLASSIC_ID, Lightsaber.START_CLASSIC);
		Registry.register(Registry.SOUND_EVENT, Lightsaber.STOP_CLASSIC_ID, Lightsaber.STOP_CLASSIC);
		Registry.register(Registry.SOUND_EVENT, Lightsaber.IDLE_CLASSIC_ID, Lightsaber.IDLE_CLASSIC);
	}

	public static class Lightsaber
	{
		public static final Identifier START_CLASSIC_ID = Resources.identifier("lightsaber.start.classic");
		public static SoundEvent START_CLASSIC = new SoundEvent(START_CLASSIC_ID);

		public static final Identifier STOP_CLASSIC_ID = Resources.identifier("lightsaber.stop.classic");
		public static SoundEvent STOP_CLASSIC = new SoundEvent(STOP_CLASSIC_ID);

		public static final Identifier IDLE_CLASSIC_ID = Resources.identifier("lightsaber.idle.classic");
		public static SoundEvent IDLE_CLASSIC = new SoundEvent(IDLE_CLASSIC_ID);
	}
}
