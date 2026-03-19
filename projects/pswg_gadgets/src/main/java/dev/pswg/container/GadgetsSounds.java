package dev.pswg.container;

import dev.pswg.Gadgets;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class GadgetsSounds
{

	public static final SoundEvent ARM = registerSound("shared.arm");
	public static final SoundEvent DISARM = registerSound("shared.disarm");
	public static final SoundEvent THROW = registerSound("shared.throw");
	public static final SoundEvent THERMAL_DETONATOR_BEEP = registerSound("thermaldetonator.beep");
	public static final SoundEvent THERMAL_DETONATOR_EXPLOSION = registerSound("thermaldetonator.explode");
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION1 = registerSound("fragmentationgrenade.explode1");
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION2 = registerSound("fragmentationgrenade.explode2");
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION3 = registerSound("fragmentationgrenade.explode3");
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION4 = registerSound("fragmentationgrenade.explode4");
	public static final SoundEvent FRAGMENTATION_GRENADE_BEEP = registerSound("fragmentationgrenade.beep");

	public static void register()
	{

	}

	private static SoundEvent registerSound(String string)
	{
		return Registry.register(BuiltInRegistries.SOUND_EVENT, Gadgets.id(string), SoundEvent.createVariableRangeEvent(Gadgets.id(string)));
	}
}
