package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.configuration.BlastersConfig;
import dev.pswg.configuration.IConfigContainer;
import dev.pswg.configuration.MemoryConfigContainer;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/**
 * The main entrypoint for PSWG common-side blaster features
 */
public final class Blasters implements GalaxiesAddon
{
	/**
	 * The mod ID assigned to PSWG
	 */
	public static final String MODID = "pswg_blasters";

	/**
	 * Creates a scoped {@link Identifier} whose domain is this
	 * mod's MODID
	 *
	 * @param path The path for the {@link Identifier}
	 *
	 * @return A scoped {@link Identifier}
	 */
	public static Identifier id(String path)
	{
		return Identifier.of(MODID, path);
	}

	/**
	 * A logger available only to PSWG core
	 */
	static final Logger LOGGER = Galaxies.createSubLogger("blasters");

	/**
	 * The configuration file that controls the behavior of PSWG core
	 */
	public static final IConfigContainer<BlastersConfig> CONFIG = new MemoryConfigContainer<>(new BlastersConfig());

	/**
	 * An item tag that contains all PSWG module and addon blasters
	 */
	public static final TagKey<Item> BLASTERS_TAG = TagKey.of(RegistryKeys.ITEM, id("blasters"));

	@Override
	public void onGalaxiesReady()
	{
		// TODO: how to differentiate different modules' versions?
		LOGGER.info("Module initialized");
	}
}
