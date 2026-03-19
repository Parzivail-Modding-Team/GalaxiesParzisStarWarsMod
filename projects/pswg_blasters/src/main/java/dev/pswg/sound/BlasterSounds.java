package dev.pswg.sound;

import dev.pswg.Blasters;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * The sound events for PSWG blaster features
 */
public final class BlasterSounds
{
	public static final SoundEvent BYPASS_PRIMARY = registerSound("blaster.bypass.primary");
	public static final SoundEvent BYPASS_SECONDARY = registerSound("blaster.bypass.secondary");
	public static final SoundEvent BYPASS_SECONDARY_END = registerSound("blaster.bypass.secondary_end");
	public static final SoundEvent BYPASS_FAILED = registerSound("blaster.bypass.failed");

	public static final SoundEvent DRYFIRE = registerSound("blaster.dryfire");
	public static final SoundEvent OVERHEAT = registerSound("blaster.overheat");
	public static final SoundEvent RELOAD = registerSound("blaster.reload");
	public static final SoundEvent VENT = registerSound("blaster.vent");

	/**
	 * Registers a sound event
	 *
	 * @param id The ID of the sound event
	 *
	 * @return The registered sound event
	 */
	private static SoundEvent registerSound(String id)
	{
		Identifier identifier = Identifier.fromNamespaceAndPath(Blasters.MODID, id);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
	}

	public static void register()
	{
	}
}
