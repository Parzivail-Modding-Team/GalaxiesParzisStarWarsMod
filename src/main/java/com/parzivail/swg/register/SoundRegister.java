package com.parzivail.swg.register;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.data.BlasterDescriptor;
import com.parzivail.util.item.ModularItems;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.HashMap;

public class SoundRegister
{
	private static HashMap<String, SoundEvent> BLASTER_FIRE = new HashMap<>();

	@SubscribeEvent
	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event)
	{
		IForgeRegistry<SoundEvent> r = event.getRegistry();

		ArrayList<BlasterDescriptor> blasterDescriptors = ModularItems.getBlasterDescriptors();
		for (BlasterDescriptor d : blasterDescriptors)
		{
			SoundEvent soundEvent = getSound("blaster.fire." + d.name);
			r.register(soundEvent);

			BLASTER_FIRE.put(d.name, soundEvent);
		}
	}

	private static SoundEvent getSound(String soundId)
	{
		return new SoundEvent(Resources.location(soundId)).setRegistryName(soundId);
	}

	public static SoundEvent getBlasterFire(String blasterId)
	{
		if (!BLASTER_FIRE.containsKey(blasterId))
			return null;

		return BLASTER_FIRE.get(blasterId);
	}
}
