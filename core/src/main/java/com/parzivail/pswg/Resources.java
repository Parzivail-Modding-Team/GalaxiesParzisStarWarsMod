package com.parzivail.pswg;

import com.google.gson.GsonBuilder;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.noise.OpenSimplex2F;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.net.URL;

public class Resources
{
	public static final String I18N_SCREEN_APPLY = "screen.pswg.apply";

	public static class GithubReleaseEntry
	{
		public String tag_name;
		public String name;
	}

	public static final String MODID = "pswg";
	public static final String NAME = "Galaxies: Parzi's Star Wars Mod";
	public static final boolean IS_DEBUG = System.getenv("PSWG_DEBUG") != null && Boolean.parseBoolean(System.getenv("PSWG_DEBUG"));

	public static final OpenSimplex2F SIMPLEX_0 = new OpenSimplex2F(0);
	public static final Random RANDOM = Random.createThreadSafe();
	public static ConfigHolder<Config> CONFIG;

	public static GithubReleaseEntry REMOTE_VERSION = null;

	public static Identifier id(@NotNull String path)
	{
		return new Identifier(MODID, path);
	}

	public static String dotModId(String category, String str)
	{
		return category + "." + MODID + "." + str;
	}

	public static String screen(String str)
	{
		return dotModId("screen", str);
	}

	public static String container(String str)
	{
		return dotModId("container", str);
	}

	public static String command(String str)
	{
		return dotModId("command", str);
	}

	public static String keyBinding(String str)
	{
		return dotModId("key", str);
	}

	public static String msg(String str)
	{
		return dotModId("msg", str);
	}

	public static String info(String str)
	{
		return dotModId("info", str);
	}

	public static void checkVersion()
	{
		try
		{
			var container = FabricLoader.getInstance().getModContainer(Resources.MODID).orElseThrow(() -> new Exception("Could not get own mod container"));
			var ownVersion = container.getMetadata().getVersion().getFriendlyString();

			if (FabricLoader.getInstance().isDevelopmentEnvironment() || ownVersion.equals("${version}"))
			{
				Lumberjack.log("Will not perform version check in a development environment");
				return;
			}

			var con = new URL("https://api.github.com/repos/Parzivail-Modding-Team/GalaxiesParzisStarWarsMod/releases").openConnection();
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			var isr = new InputStreamReader(con.getInputStream());

			var g = new GsonBuilder().create();

			var entries = g.fromJson(isr, GithubReleaseEntry[].class);

			if (entries.length == 0)
				throw new Exception("No versions present on remote");

			var mostRecentRelease = entries[0];

			if (isRemoteVersionNewer(ownVersion, mostRecentRelease.tag_name))
			{
				REMOTE_VERSION = mostRecentRelease;

				Lumberjack.warn("A new version is available at https://www.curseforge.com/minecraft/mc-mods/pswg: %s (vs: %s)", REMOTE_VERSION.name, ownVersion);
			}
		}
		catch (Exception e)
		{
			Lumberjack.error("Failed to check for updates: %s", e.getMessage());
		}
	}

	private static boolean isRemoteVersionNewer(String local, String remote)
	{
		// TODO: actual version comparison
		return !local.equals(remote);
	}
}
