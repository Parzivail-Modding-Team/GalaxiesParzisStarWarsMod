package com.parzivail.datagen.tarkin;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class LanguageProvider
{
	public static final String OUTPUT_LOCALE = "en_us_temp";
	public static final String TARGET_LOCALE = "en_us";

	public static LanguageProvider ofBlock(Block block)
	{
		var reg = AssetGenerator.getRegistryName(block);
		return new LanguageProvider(new Identifier(reg.getNamespace(), OUTPUT_LOCALE), "block." + reg.getNamespace() + "." + reg.getPath(), reg.toString());
	}

	public static LanguageProvider ofItem(Item item)
	{
		var reg = AssetGenerator.getRegistryName(item);
		return new LanguageProvider(new Identifier(reg.getNamespace(), OUTPUT_LOCALE), "item." + reg.getNamespace() + "." + reg.getPath(), reg.toString());
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
