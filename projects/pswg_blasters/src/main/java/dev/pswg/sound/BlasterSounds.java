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
