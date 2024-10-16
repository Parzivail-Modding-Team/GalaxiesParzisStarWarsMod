package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.configuration.BlastersConfig;
import dev.pswg.configuration.IConfigContainer;
import dev.pswg.configuration.MemoryConfigContainer;
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
	 * A logger available only to PSWG core
	 */
	static final Logger LOGGER = Galaxies.createSubLogger("blasters");

	/**
	 * The configuration file that controls the behavior of PSWG core
	 */
	public static final IConfigContainer<BlastersConfig> CONFIG = new MemoryConfigContainer<>(new BlastersConfig());

	@Override
	public void onGalaxiesReady()
	{
		// TODO: how to differentiate different modules' versions?
		LOGGER.info("Module initialized");
	}
}
