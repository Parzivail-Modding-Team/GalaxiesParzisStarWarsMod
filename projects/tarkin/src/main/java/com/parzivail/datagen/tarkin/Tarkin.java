package com.parzivail.datagen.tarkin;

import com.parzivail.datagen.tarkin.config.PswgTarkin;
import com.parzivail.datagen.tarkin.config.TcwTarkin;
import com.parzivail.pswg.Config;
import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.data.SwgSpeciesManager;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.util.Lumberjack;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static void generateSpeciesLang(List<BuiltAsset> assets, LanguageBuilder lang, String namespace)
	{
		var speciesManager = SwgSpeciesManager.INSTANCE;
		ResourceManagerUtil.forceReload(speciesManager, ResourceType.SERVER_DATA);
		var speciesLangBase = lang.cloneWithRoot("species").modid();

		speciesLangBase.dot(SpeciesVariable.NONE).build(assets);

		for (var species : SwgSpeciesRegistry.getSpecies())
		{
			if (!species.getSlug().getNamespace().equals(namespace))
				continue;
			var speciesLang = speciesLangBase.dot(species.getSlug().getPath());
			speciesLang.build(assets);

			for (var variable : species.getVariables())
			{
				lang.cloneWithRoot(variable.getTranslationKey()).build(assets);

				for (var value : variable.getPossibleValues())
					lang.cloneWithRoot(variable.getTranslationFor(value)).build(assets);
			}
		}
	}

	public static void generateBlasterLang(List<BuiltAsset> assets, LanguageBuilder lang, String namespace)
	{
		var blasterManager = SwgBlasterManager.INSTANCE;
		ResourceManagerUtil.forceReload(blasterManager, ResourceType.SERVER_DATA);
		var blasterData = blasterManager.getData();

		for (var blasterEntry : blasterData.entrySet())
		{
			var blasterId = blasterEntry.getKey();
			if (!blasterId.getNamespace().equals(namespace))
				continue;

			var blasterDescriptor = blasterEntry.getValue();

			lang.cloneWithRoot(BlasterItem.getTranslationKeyForModel(blasterId)).build(assets);

			for (var attachment : blasterDescriptor.attachmentMap.values())
				lang.cloneWithRoot(BlasterItem.getAttachmentTranslation(blasterId, attachment).getKey()).build(assets);
		}
	}

	public static void generateConfigLang(List<BuiltAsset> assets, LanguageBuilder lang, Class<Config> config)
	{
		var autoconfig = lang.cloneWithRoot("text").dot("autoconfig").modid();
		autoconfig.dot("title").build(assets);
		var autoconfigOption = autoconfig.dot("option");
		generateLangFromConfigAnnotations(autoconfigOption, assets, config);
	}

	private static void generateLangFromConfigAnnotations(LanguageBuilder autoconfigOption, List<BuiltAsset> assets, Class<?> config)
	{
		var subclasses = Arrays.asList(config.getDeclaredClasses());

		for (var field : config.getDeclaredFields())
		{
			var fieldLang = autoconfigOption.dot(field.getName());
			fieldLang.build(assets);

			if (field.isAnnotationPresent(ConfigEntry.Gui.Tooltip.class))
			{
				String defaultValue = null;

				var commentAnnotation = field.getAnnotation(Comment.class);
				if (commentAnnotation != null)
					defaultValue = commentAnnotation.value();

				fieldLang.dot("@Tooltip").build(assets, defaultValue);
			}

			if (subclasses.contains(field.getType()))
			{
				var subclassLang = autoconfigOption.dot(field.getName());
				generateLangFromConfigAnnotations(subclassLang, assets, field.getType());
			}
		}
	}
}
