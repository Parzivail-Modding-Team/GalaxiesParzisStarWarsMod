package dev.pswg;

import dev.pswg.configuration.GalaxiesConfig;
import dev.pswg.configuration.IConfigContainer;
import dev.pswg.configuration.MemoryConfigContainer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main entrypoint for PSWG common-side registration
 */
public class Galaxies implements ModInitializer
{
	/**
	 * The mod ID assigned to PSWG
	 */
	public static final String MODID = "pswg";

	/**
	 * The configuration file that controls the behavior of PSWG core
	 */
	public static final IConfigContainer<GalaxiesConfig> CONFIG = new MemoryConfigContainer<>(new GalaxiesConfig());

	/**
	 * The newer version of PSWG core that exists on the remote, if any
	 */
	public static final String REMOTE_VERSION = null;

	/**
	 * A logger available only to PSWG core
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	/**
	 * Create a derived logger for a subsystem within PSWG
	 *
	 * @param key The sub-header to log underneath the modid
	 *
	 * @return The nested logger
	 */
	public static Logger createSubLogger(String key)
	{
		return LoggerFactory.getLogger(MODID + "/" + key);
	}

	@Override
	public void onInitialize()
	{
	}
}
