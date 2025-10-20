package dev.pswg.datagen;

import dev.pswg.Blasters;
import dev.pswg.BlastersClient;
import dev.pswg.Galaxies;
import dev.pswg.item.BlasterItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Models;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The blaster data generator
 */
public class BlasterDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		DataGenResourceHelper.loadResources(ResourceType.SERVER_DATA, Blasters.DATAPACK_LOADER);

		pack.addProvider(LangGenerator::new);
		pack.addProvider(TagGenerator::new);
		pack.addProvider(ModelGenerator::new);
	}

	/**
	 * The blaster model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricDataOutput output)
		{
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
		{
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator)
		{
//			register(itemModelGenerator, Blasters.BLASTER_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);

			register(itemModelGenerator, Blasters.BLASTER_ITEM, ItemModels.basic(Blasters.id("item/blaster")));
		}
	}

	/**
	 * The blaster language file generator. All language entries should be
	 * added through this generator.
	 */
	private static class LangGenerator extends FabricLanguageProvider
	{
		private record BlasterLang(String name, Map<Identifier, String> attachmentLangs)
		{
		}

		private final Map<Identifier, BlasterLang> blasterLang = Map.ofEntries(
				Map.entry(Blasters.id("test_blaster"), new BlasterLang(
						"Test Blaster",
						Map.ofEntries(
								Map.entry(Blasters.id("e11/scope_d"), "1.5x Scope"),
								Map.entry(Blasters.id("e11/scope_x"), "2.5x Scope"),
								Map.entry(Blasters.id("e11/bipod"), "Bipod"),
								Map.entry(Blasters.id("e11/barrel_d"), "Extended barrel"),
								Map.entry(Blasters.id("e11/cooling"), "Heat Spreader"),
								Map.entry(Blasters.id("e11/rapidfire"), "Advanced Regeneration")
						)
				))
		);

		protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup)
		{
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder)
		{
			// Tag that contains the blasters
			translationBuilder.add(Blasters.BLASTERS_TAG, "Blasters");

			// Item name
			translationBuilder.add(Blasters.BLASTER_ITEM, "Blaster");

			// Model number of each blaster
			translationBuilder.add(BlasterItem.MISSING_ID, "[unknown model]");

			for (var entry : Blasters.DATAPACK_LOADER.getDefinitions().entrySet())
			{
				var blasterEntry = blasterLang.getOrDefault(entry.getKey(), null);
				if (blasterEntry == null)
					throw new RuntimeException("Missing blaster lang entry for " + entry.getKey());

				// Add the name of the blaster
				translationBuilder.add(entry.getKey(), blasterEntry.name());

				// Add all the attachments
				for (var attachmentEntry : entry.getValue().attachments().options().entrySet())
				{
					var blasterAttachmentEntry = blasterEntry.attachmentLangs().getOrDefault(attachmentEntry.getKey(), null);
					if (blasterAttachmentEntry == null)
						throw new RuntimeException("Missing blaster attachment lang entry for " + attachmentEntry.getKey());

					translationBuilder.add(attachmentEntry.getValue().translationKey(), blasterAttachmentEntry);
				}
			}

			// Sound subtitles
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.fire"), "Blaster fires");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.reload"), "Blaster reloads");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.dryfire"), "Blaster dry-fires");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.bypass.primary"), "Blaster cools");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.bypass.secondary"), "Blaster overcharges");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.bypass.failed"), "Blaster cooling fails");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.vent"), "Blaster vents");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.overheat"), "Blaster overheats");

			// Tooltips
			translationBuilder.add(BlastersClient.I18N_VENT_BLASTER, "Vent Heat");
		}
	}

	/**
	 * The blaster item tag generator. All item tags should be added
	 * through this generator.
	 */
	private static class TagGenerator extends FabricTagProvider.ItemTagProvider
	{
		public TagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture)
		{
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
		{
			getTagBuilder(Blasters.BLASTERS_TAG)
					.add(Blasters.BLASTER_ITEM_ID);
		}
	}
}
