package dev.pswg.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

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
	public static void keybindCategory(FabricLanguageProvider.TranslationBuilder translationBuilder, KeyBinding.Category category, String value)
	{
		translatableText(translationBuilder, category.getLabel(), value);
	}

	/**
	 * Creates a translation for a keybind
	 *
	 * @param translationBuilder The translation builder to add the translation to
	 * @param keyBinding         The keybind
	 * @param value              The translated string
	 */
	public static void keybind(FabricLanguageProvider.TranslationBuilder translationBuilder, KeyBinding keyBinding, String value)
	{
		translationBuilder.add(keyBinding.getId(), value);
	}

	/**
	 * Creates a translation for a translatable text instance
	 *
	 * @param translationBuilder The translation builder to add the translation to
	 * @param text               The keybind
	 * @param value              The translated string
	 */
	public static void translatableText(FabricLanguageProvider.TranslationBuilder translationBuilder, Text text, String value)
	{
		if (!(text.getContent() instanceof TranslatableTextContent ttc))
			throw new RuntimeException("Text is not translatable");

		translationBuilder.add(ttc.getKey(), value);
	}
}
