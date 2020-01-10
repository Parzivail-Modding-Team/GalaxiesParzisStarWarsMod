package com.parzivail.swg.register;

import com.parzivail.swg.Resources;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;

public class SoundRegister
{
	private static boolean REGISTRY_MODE = false;
	private static HashMap<String, SoundEvent> CACHE = new HashMap<>();

	public static SoundEvent BLASTER_DRYFIRE;
	public static SoundEvent BLASTER_RELOAD;

	public static SoundEvent LIGHTSABER_BLOCK;
	public static SoundEvent LIGHTSABER_SPARK;
	public static SoundEvent LIGHTSABER_DEFLECT;

	@SubscribeEvent
	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event)
	{
		IForgeRegistry<SoundEvent> r = event.getRegistry();
		REGISTRY_MODE = true;

		r.register(BLASTER_DRYFIRE = getSound("blaster.dryfire"));
		r.register(BLASTER_RELOAD = getSound("blaster.reload"));

		r.register(getSound("blaster.fire.a280"));

		r.register(LIGHTSABER_BLOCK = getSound("lightsaber.block"));
		r.register(LIGHTSABER_SPARK = getSound("lightsaber.spark"));
		r.register(LIGHTSABER_DEFLECT = getSound("lightsaber.deflect"));

		r.register(getSound("lightsaber.idle.default"));
		r.register(getSound("lightsaber.start.default"));
		r.register(getSound("lightsaber.stop.default"));
		r.register(getSound("lightsaber.swing.default"));

		REGISTRY_MODE = false;
	}

	private static SoundEvent createSound(String soundId)
	{
		return new SoundEvent(Resources.location(soundId)).setRegistryName(soundId);
	}

	public static SoundEvent getSound(String soundId)
	{
		if (CACHE.containsKey(soundId))
			return CACHE.get(soundId);

		if (!REGISTRY_MODE)
		{
			Lumberjack.warn("Unable to locate sound event: '%s'", soundId);
			return null;
		}

		SoundEvent e = createSound(soundId);
		CACHE.put(soundId, e);
		return e;
	}
}
