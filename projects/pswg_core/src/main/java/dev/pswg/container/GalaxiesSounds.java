package dev.pswg.container;

import dev.pswg.Galaxies;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * The sound events for PSWG core
 */
public class GalaxiesSounds
{
	public static final SoundEvent METAL_PIPE = registerSound("metalpipe");

	public static void register()
	{
	}

	/**
	 * Registers a sound event
	 *
	 * @param id The ID of the sound event
	 *
	 * @return The registered sound event
	 */
	private static SoundEvent registerSound(String id)
	{
		Identifier identifier = Identifier.fromNamespaceAndPath(Galaxies.MODID, id);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
	}
}
