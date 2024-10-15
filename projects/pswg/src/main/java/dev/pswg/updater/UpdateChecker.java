package dev.pswg.updater;

import com.google.gson.GsonBuilder;
import dev.pswg.Galaxies;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Optional;

/**
 * A helper utility to check a remote repository for the most recently released version
 */
public final class UpdateChecker
{
	/**
	 * Queries the given remote repository for the most recently released version
	 *
	 * @param modid      The name of the mod to test the remote version against
	 * @param repository The repository from which to pull the version
	 *
	 * @return The version found on the remote, or empty if it is unavailable
	 */
	public static Optional<GithubReleaseEntry> getRemoteVersion(String modid, String repository)
	{
		var logger = Galaxies.createSubLogger("updater/" + modid);

		try
		{
			var container = FabricLoader.getInstance().getModContainer(modid).orElseThrow(() -> new Exception("Could not get own mod container"));

			if (FabricLoader.getInstance().isDevelopmentEnvironment() || !(container.getMetadata().getVersion() instanceof SemanticVersion ownVersion))
			{
				logger.warn("Will not perform version check in a development environment");
				return Optional.empty();
			}

			// we cannot use /releases/latest here since most released will likely
			// be pre-releases, which aren't returned by that API
			var con = new URI(String.format("https://api.github.com/repos/%s/releases", repository)).toURL().openConnection();
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			var isr = new InputStreamReader(con.getInputStream());

			var g = new GsonBuilder().create();

			var entries = g.fromJson(isr, GithubReleaseEntry[].class);

			if (entries.length == 0)
				throw new Exception("No versions present on remote");

			var mostRecentRelease = entries[0];

			if (isRemoteVersionNewer(ownVersion, SemanticVersion.parse(mostRecentRelease.tagName())))
			{
				logger.warn("A new version of {} is available: {} (vs: {})", container.getMetadata().getName(), mostRecentRelease.name(), container.getMetadata().getVersion());
				return Optional.of(mostRecentRelease);
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to check for updates:", e);
		}

		return Optional.empty();
	}

	/**
	 * Compares two semantic versions to determine if the remote version is newer
	 *
	 * @param local  The local version
	 * @param remote The remote version
	 *
	 * @return True if the remote version is newer
	 */
	private static boolean isRemoteVersionNewer(SemanticVersion local, SemanticVersion remote)
	{
		return local.compareTo((Version)remote) < 0;
	}
}
