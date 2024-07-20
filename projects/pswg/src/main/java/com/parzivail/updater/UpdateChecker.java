package com.parzivail.updater;

import com.google.gson.GsonBuilder;
import com.parzivail.util.Lumberjack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;

import java.io.InputStreamReader;
import java.net.URI;

public class UpdateChecker
{
	public static GithubReleaseEntry getRemoteVersion(String modid, String repository)
	{
		var logger = new Lumberjack(modid + ":updater");

		try
		{
			var container = FabricLoader.getInstance().getModContainer(modid).orElseThrow(() -> new Exception("Could not get own mod container"));

			if (FabricLoader.getInstance().isDevelopmentEnvironment() || !(container.getMetadata().getVersion() instanceof SemanticVersion ownVersion))
			{
				logger.log("Will not perform version check in a development environment");
				return null;
			}

			// TODO: use JDK HttpClient
			var con = URI.create("https://api.github.com/repos/" + repository + "/releases").toURL().openConnection();
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			var isr = new InputStreamReader(con.getInputStream());

			var g = new GsonBuilder().create();

			var entries = g.fromJson(isr, GithubReleaseEntry[].class);

			if (entries.length == 0)
				throw new Exception("No versions present on remote");

			var mostRecentRelease = entries[0];

			if (isRemoteVersionNewer(ownVersion, SemanticVersion.parse(mostRecentRelease.tag_name)))
			{
				logger.warn("A new version of %s is available: %s (vs: %s)", container.getMetadata().getName(), mostRecentRelease.name, container.getMetadata().getVersion());
				return mostRecentRelease;
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to check for updates: %s", e.getMessage());
		}

		return null;
	}

	private static boolean isRemoteVersionNewer(SemanticVersion local, SemanticVersion remote)
	{
		return local.compareTo((Version)remote) < 0;
	}
}
