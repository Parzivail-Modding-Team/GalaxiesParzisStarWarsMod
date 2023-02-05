package com.parzivail.datagen.tarkin.config;

import com.parzivail.datagen.tarkin.BuiltAsset;
import com.parzivail.datagen.tarkin.ItemGenerator;
import com.parzivail.datagen.tarkin.LanguageBuilder;
import com.parzivail.datagen.tarkin.LanguageProvider;
import com.parzivail.pswg.Config;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtcw.PswgTcwAddon;
import com.parzivail.pswgtcw.TcwItems;

import java.util.List;

public class TcwTarkin
{
	public static void build(List<BuiltAsset> assets)
	{
		ItemGenerator.armor(TcwItems.Armor.Phase1Clone, assets);
		ItemGenerator.armor(TcwItems.Armor.Phase2Clone, assets);

		var lang = new LanguageBuilder(PswgTcwAddon.id(LanguageProvider.OUTPUT_LOCALE));

		// Species
		PswgTarkin.generateSpeciesLang(assets, lang, Resources.MODID);

		// Blaster attachments
		PswgTarkin.generateBlasterLang(assets, lang, Resources.MODID);

		// Autoconfig
		PswgTarkin.generateConfigLang(assets, lang, Config.class);
	}
}
