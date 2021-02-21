package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class SwgSounds
{
	private static final HashMap<Identifier, SoundEvent> SOUND_EVENTS = new HashMap<>();

	private static SoundEvent of(Identifier identifier)
	{
		SoundEvent se = new SoundEvent(identifier);
		SOUND_EVENTS.put(identifier, se);
		return se;
	}

	public static void register()
	{
		Door.register();
		Lightsaber.register();
		Blaster.register();

		for (Map.Entry<Identifier, SoundEvent> pair : SOUND_EVENTS.entrySet())
			Registry.register(Registry.SOUND_EVENT, pair.getKey(), pair.getValue());
	}

	public static SoundEvent getOrDefault(Identifier sound, SoundEvent fallback)
	{
		return SOUND_EVENTS.getOrDefault(sound, fallback);
	}

	public static class Lightsaber
	{
		public static final SoundEvent START_CLASSIC = of(Resources.identifier("lightsaber.start.classic"));
		public static final SoundEvent STOP_CLASSIC = of(Resources.identifier("lightsaber.stop.classic"));
		public static final SoundEvent IDLE_CLASSIC = of(Resources.identifier("lightsaber.idle.classic"));

		private static void register()
		{
		}
	}

	public static class Door
	{
		public static final SoundEvent PNEUMATIC = of(Resources.identifier("door.pneumatic"));

		private static void register()
		{
		}
	}

	public static class Blaster
	{
		public static final SoundEvent RELOAD = of(Resources.identifier("blaster.reload"));
		public static final SoundEvent DRYFIRE = of(Resources.identifier("blaster.dryfire"));
		public static final SoundEvent FIRE_A280 = of(Resources.identifier("blaster.fire.a280"));
		public static final SoundEvent FIRE_BIKE = of(Resources.identifier("blaster.fire.bike"));
		public static final SoundEvent FIRE_BOWCASTER = of(Resources.identifier("blaster.fire.bowcaster"));
		public static final SoundEvent FIRE_CYCLER = of(Resources.identifier("blaster.fire.cycler"));
		public static final SoundEvent FIRE_DH17 = of(Resources.identifier("blaster.fire.dh17"));
		public static final SoundEvent FIRE_DL44 = of(Resources.identifier("blaster.fire.dl44"));
		public static final SoundEvent FIRE_DLT19 = of(Resources.identifier("blaster.fire.dlt19"));
		public static final SoundEvent FIRE_E11 = of(Resources.identifier("blaster.fire.e11"));
		public static final SoundEvent FIRE_EWEB = of(Resources.identifier("blaster.fire.eweb"));
		public static final SoundEvent FIRE_ION = of(Resources.identifier("blaster.fire.ion"));
		public static final SoundEvent FIRE_RT97C = of(Resources.identifier("blaster.fire.rt97c"));
		public static final SoundEvent FIRE_T21 = of(Resources.identifier("blaster.fire.t21"));
		public static final SoundEvent FIRE_SE14C = of(Resources.identifier("blaster.fire.se14c"));
		public static final SoundEvent FIRE_DL18 = of(Resources.identifier("blaster.fire.dl18"));
		public static final SoundEvent FIRE_SCOUT = of(Resources.identifier("blaster.fire.dl44"));
		public static final SoundEvent FIRE_DEFENDER = of(Resources.identifier("blaster.fire.dl44"));
		public static final SoundEvent FIRE_DL21 = of(Resources.identifier("blaster.fire.dl44"));

		private static void register()
		{
		}
	}
}
