package com.parzivail.datagen.tarkin;

import com.google.gson.JsonObject;
import com.parzivail.datagen.AssetUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LanguageProvider
{
	public static final String OUTPUT_LOCALE = "en_us_temp";
	public static final String TARGET_LOCALE = "en_us";

	private static String generateDefaultLang(Identifier reg)
	{
		var path = reg.getPath();
		return "### " + Arrays.stream(path.split("_"))
		                         .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
		                         .collect(Collectors.joining(" "));
	}

	public static LanguageProvider block(Block block)
	{
		var reg = AssetUtils.getRegistryName(block);
		return new LanguageProvider(reg.withPath(OUTPUT_LOCALE), reg.toTranslationKey("block"), generateDefaultLang(reg));
	}

	public static LanguageProvider item(Item item)
	{
		var reg = AssetUtils.getRegistryName(item);
		return new LanguageProvider(reg.withPath(OUTPUT_LOCALE), reg.toTranslationKey("item"), generateDefaultLang(reg));
	}

	public static LanguageProvider empty(Item item)
	{
		return null;
	}

	private final Identifier locale;
	private final String key;
	private final String defaultValue;

	public LanguageProvider(Identifier locale, String key, String defaultValue)
	{
		this.locale = locale;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public BuiltAsset build()
	{
		var contents = new JsonObject();
		contents.addProperty(key, defaultValue);
		return BuiltAsset.lang(locale, contents);
	}
}
