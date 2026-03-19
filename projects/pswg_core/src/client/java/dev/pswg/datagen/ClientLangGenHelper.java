package dev.pswg.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

/**
 * A set of utilities for working with language generation
 */
public final class ClientLangGenHelper
{
	/**
	 * Creates a translation for a keybind category
	 *
	 * @param translationBuilder The translation builder to add the translation to
	 * @param category           The keybind category
	 * @param value              The translated string
	 */
	public static void keybindCategory(FabricLanguageProvider.TranslationBuilder translationBuilder, KeyMapping.Category category, String value)
	{
		translatableText(translationBuilder, category.label(), value);
	}

	/**
	 * Creates a translation for a keybind
	 *
	 * @param translationBuilder The translation builder to add the translation to
	 * @param keyBinding         The keybind
	 * @param value              The translated string
	 */
	public static void keybind(FabricLanguageProvider.TranslationBuilder translationBuilder, KeyMapping keyBinding, String value)
	{
		translationBuilder.add(keyBinding.getName(), value);
	}

	/**
	 * Creates a translation for a translatable text instance
	 *
	 * @param translationBuilder The translation builder to add the translation to
	 * @param text               The keybind
	 * @param value              The translated string
	 */
	public static void translatableText(FabricLanguageProvider.TranslationBuilder translationBuilder, Component text, String value)
	{
		if (!(text.getContents() instanceof TranslatableContents ttc))
			throw new RuntimeException("Text is not translatable");

		translationBuilder.add(ttc.getKey(), value);
	}
}
