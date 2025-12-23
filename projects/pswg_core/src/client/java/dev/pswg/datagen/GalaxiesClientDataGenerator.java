package dev.pswg.datagen;

import dev.pswg.GalaxiesClient;
import dev.pswg.input.GalaxiesKeybinds;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * The base data generator
 */
public class GalaxiesClientDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		pack.addProvider(LangGenerator::new);
	}

	/**
	 * The base language file generator. All language entries should be
	 * added through this generator.
	 */
	private static class LangGenerator extends FabricLanguageProvider
	{
		protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup)
		{
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder)
		{
			// Hints
			translationBuilder.add(GalaxiesClient.I18N_KEYBIND_HINT_KEY, "§9[§f%s§9]§r %s");

			// Keybinds
			ClientLangGenHelper.keybindCategory(translationBuilder, GalaxiesKeybinds.CATEGORY, "Galaxies: Parzi's Star Wars Mod");

			ClientLangGenHelper.keybind(translationBuilder, GalaxiesKeybinds.getPrimaryAction(), "Primary Item Action");
		}
	}
}
