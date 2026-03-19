package dev.pswg.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * A set of utilities for working with language generation
 */
public final class LangGenHelper
{
	/**
	 * Create a translation for a sound subtitle
	 *
	 * @param translationBuilder The translation builder to add the translation to
	 * @param identifier         The sound identifier
	 * @param value              The translated string
	 */
	public static void soundSubtitle(FabricLanguageProvider.TranslationBuilder translationBuilder, ResourceLocation identifier, String value)
	{
		translationBuilder.add("subtitle.%s.%s".formatted(identifier.getNamespace(), identifier.getPath()), value);
	}
}
