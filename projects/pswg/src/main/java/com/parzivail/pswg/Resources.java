package com.parzivail.pswg;

import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.updater.GithubReleaseEntry;
import com.parzivail.updater.UpdateChecker;
import com.parzivail.util.noise.OpenSimplex2F;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

public class Resources
{
	@TarkinLang
	public static final String I18N_SCREEN_APPLY = "screen.pswg.apply";
	@TarkinLang
	public static final String I18N_SCREEN_RANDOM = "screen.pswg.random";
	@TarkinLang
	public static final String I18N_SCREEN_GENDER_MALE = "screen.pswg.male";
	@TarkinLang
	public static final String I18N_SCREEN_GENDER_FEMALE = "screen.pswg.female";
	@TarkinLang
	public static final String I18N_SCREEN_SAVE_PRESET = "screen.pswg.save_preset";
	@TarkinLang
	public static final String I18N_SCREEN_EXPORT_PRESET = "screen.pswg.export_preset";

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

	public static String tooltip(String str)
	{
		return dotModId("tooltip", str);
	}

	public static boolean isUpdateCheckDisabled()
	{
		var config = CONFIG.get();
		return config.disableUpdateCheck;
	}

	public static void checkVersion()
	{
		if (isUpdateCheckDisabled())
			return;

		REMOTE_VERSION = UpdateChecker.getRemoteVersion(MODID, "Parzivail-Modding-Team/GalaxiesParzisStarWarsMod");
	}
}
