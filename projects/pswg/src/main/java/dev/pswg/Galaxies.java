package dev.pswg;

import dev.pswg.configuration.GalaxiesConfig;
import dev.pswg.configuration.IConfigContainer;
import dev.pswg.configuration.MemoryConfigContainer;
import dev.pswg.updater.GithubReleaseEntry;
import dev.pswg.updater.UpdateChecker;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The main entrypoint for PSWG common-side registration
 */
public final class Galaxies implements ModInitializer
{
	/**
	 * The mod ID assigned to PSWG
	 */
	public static final String MODID = "pswg";

	/**
	 * The newer version of PSWG core that exists on the remote, if any
	 */
	private static GithubReleaseEntry REMOTE_VERSION = null;

	/**
	 * A logger available only to PSWG core
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	/**
	 * The configuration file that controls the behavior of PSWG core
	 */
	public static final IConfigContainer<GalaxiesConfig> CONFIG = new MemoryConfigContainer<>(new GalaxiesConfig());

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

	/**
	 * Determines if the user has requested that PSWG core and addons
	 * should not use the internet to determine if newer versions are
	 * available
	 *
	 * @return True if update checking is disabled
	 */
	public static boolean isUpdateCheckDisabled()
	{
		var config = CONFIG.get();
		return config.isUpdateCheckingDisabled;
	}

	/**
	 * Gets the newer version of PSWG core present on the remote, if any
	 *
	 * @return The version if a newer one exists, or empty otherwise
	 */
	public static Optional<GithubReleaseEntry> getRemoteVersion()
	{
		return Optional.ofNullable(REMOTE_VERSION);
	}

	@Override
	public void onInitialize()
	{
		if (!isUpdateCheckDisabled())
		{
			REMOTE_VERSION = UpdateChecker.getRemoteVersion(MODID, "Parzivail-Modding-Team/GalaxiesParzisStarWarsMod").orElse(null);
		}
	}
}
