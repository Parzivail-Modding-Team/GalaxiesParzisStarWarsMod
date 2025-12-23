package dev.pswg.sound;

import dev.pswg.Blasters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

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
		Identifier identifier = Identifier.of(Blasters.MODID, id);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}

	public static void register()
	{
	}
}
