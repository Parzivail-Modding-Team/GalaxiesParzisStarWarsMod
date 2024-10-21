package dev.pswg.datagen;

import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * The base data generator
 */
public class GalaxiesDataGenerator implements DataGeneratorEntrypoint
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
			translationBuilder.add(GalaxiesEntityAttributes.I18N_ATTR_MULTIPLIER, "%sx %s");

			translationBuilder.add(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, "Zoom");
		}
	}
}
