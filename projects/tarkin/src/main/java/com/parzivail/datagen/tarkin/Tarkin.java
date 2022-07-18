package com.parzivail.datagen.tarkin;

import com.parzivail.datagen.tarkin.config.PswgTarkin;
import com.parzivail.datagen.tarkin.config.TcwTarkin;
import com.parzivail.util.Lumberjack;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * T.A.R.K.I.N. - Text Asset Record-Keeping, Integration, and Normalization
 */
public class Tarkin
{
	public static final Lumberjack LOG = new Lumberjack("TARKIN");

	public static void main() throws Exception
	{
		AssetGenerator.setOutputRoot(Path.of(System.getenv("TARKIN_OUT_DIR")));

		List<BuiltAsset> assets = new ArrayList<>();

		var tarkinModid = System.getProperty("tarkin", "");

		switch (tarkinModid)
		{
			case "pswg":
				PswgTarkin.build(assets);
				break;
			case "pswg_addon_clonewars":
				TcwTarkin.build(assets);
				break;
		}

		BuiltAsset.nukeRecipeDir();
		BuiltAsset.nukeBlockstateDir();
		BuiltAsset.nukeBlockModelJsons();
		BuiltAsset.nukeItemModelJsons();
		BuiltAsset.nukeBlockLootTables();
		BuiltAsset.nukeTags();

		for (var asset : assets)
		{
			LOG.log("Wrote %s", asset.getFilename());
			asset.write();
		}

		LOG.log("Wrote %s assets", assets.size());

		// Synchronize the keys of the en_us locale
		BuiltAsset.mergeLanguageKeys(new Identifier(tarkinModid, LanguageProvider.OUTPUT_LOCALE), new Identifier(tarkinModid, LanguageProvider.TARGET_LOCALE));
		LOG.log("Merged language keys");

		LOG.log("Done");
	}
}
