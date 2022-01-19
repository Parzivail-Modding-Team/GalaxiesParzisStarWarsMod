package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.HashMap;

public class SwgSounds
{
	private static final HashMap<Identifier, SoundEvent> SOUND_EVENTS = new HashMap<>();

	private static SoundEvent of(Identifier identifier)
	{
		var se = new SoundEvent(identifier);
		SOUND_EVENTS.put(identifier, se);
		return se;
	}

	public static void register()
	{
		Door.register();
		Lightsaber.register();
		Blaster.register();
		Ship.register();

		for (var pair : SOUND_EVENTS.entrySet())
			Registry.register(Registry.SOUND_EVENT, pair.getKey(), pair.getValue());
	}

	public static SoundEvent getOrDefault(Identifier sound, SoundEvent fallback)
	{
		return SOUND_EVENTS.getOrDefault(sound, fallback);
	}

	public static class Lightsaber
	{
		public static final SoundEvent START_CLASSIC = of(Resources.id("lightsaber.start.classic"));
		public static final SoundEvent STOP_CLASSIC = of(Resources.id("lightsaber.stop.classic"));
		public static final SoundEvent IDLE_CLASSIC = of(Resources.id("lightsaber.idle.classic"));

		private static void register()
		{
		}
	}

	public static class Door
	{
		public static final SoundEvent PNEUMATIC = of(Resources.id("door.pneumatic"));

		private static void register()
		{
		}
	}

	public static class Blaster
	{
		public static final SoundEvent HISS = of(Resources.id("blaster.hiss"));
		public static final SoundEvent RELOAD = of(Resources.id("blaster.reload"));
		public static final SoundEvent DRYFIRE = of(Resources.id("blaster.dryfire"));
		public static final SoundEvent STUN = of(Resources.id("blaster.stun"));
		public static final SoundEvent FIRE_A280 = of(Resources.id("blaster.fire.a280"));
		public static final SoundEvent FIRE_CYCLER = of(Resources.id("blaster.fire.cycler"));
		public static final SoundEvent FIRE_ION = of(Resources.id("blaster.fire.ion"));
		public static final SoundEvent PRIMARYBYPASS = of(Resources.id("blaster.primarybypass"));
		public static final SoundEvent SECONDARYBYPASS = of(Resources.id("blaster.secondarybypass"));
		public static final SoundEvent FAILEDBYPASS = of(Resources.id("blaster.failedbypass"));
		public static final SoundEvent VENT = of(Resources.id("blaster.vent"));
		public static final SoundEvent OVERHEAT = of(Resources.id("blaster.overheat"));

		private static void register()
		{
			// register dynamic events
			Arrays.stream(new Identifier[] {
					Resources.id("blaster.fire.a280"),
					Resources.id("blaster.fire.bike"),
					Resources.id("blaster.fire.bowcaster"),
					Resources.id("blaster.fire.cycler"),
					Resources.id("blaster.fire.dc15"),
					Resources.id("blaster.fire.dc17"),
					Resources.id("blaster.fire.dh17"),
					Resources.id("blaster.fire.dl18"),
					Resources.id("blaster.fire.dl44"),
					Resources.id("blaster.fire.dlt19d"),
					Resources.id("blaster.fire.dlt19"),
					Resources.id("blaster.fire.dlt20a"),
					Resources.id("blaster.fire.e11"),
					Resources.id("blaster.fire.ee3"),
					Resources.id("blaster.fire.eweb"),
					Resources.id("blaster.fire.ion"),
					Resources.id("blaster.fire.rk3"),
					Resources.id("blaster.fire.rt97c"),
					Resources.id("blaster.fire.se14c"),
					Resources.id("blaster.fire.t21b"),
					Resources.id("blaster.fire.t21")
			}).forEach(SwgSounds::of);
		}
	}

	public static class Ship
	{
		public static final SoundEvent XWINGT65B_FIRE = of(Resources.id("ship.blaster.fire.xwingt65b"));
		public static final SoundEvent XWINGT65B_EXTERIOR = of(Resources.id("ship.exterior.xwingt65b"));

		private static void register()
		{
		}
	}
}
