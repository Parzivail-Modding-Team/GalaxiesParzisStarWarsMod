package dev.pswg.datagen;

import dev.pswg.Blasters;
import dev.pswg.BlastersClient;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.HasAttachmentProperty;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.EmptyModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The blaster data generator
 */
public class BlasterDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		DataGenResourceHelper.loadResources(PackType.CLIENT_RESOURCES, G3dModelProvider.SOURCES);

		pack.addProvider(LangGenerator::new);
		pack.addProvider(TagGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider((fabricPackOutput, completableFuture) -> new G3dModelProvider(fabricPackOutput, Blasters.MODID));
	}

	/**
	 * The blaster model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricPackOutput output)
		{
			super(output, Blasters.MODID);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator)
		{
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerator)
		{
			//			register(itemModelGenerator, Blasters.BLASTER_ITEM, ItemModels.basic(Blasters.id("item/blaster")));

			register(itemModelGenerator, Blasters.BLASTER_ITEM, ItemModelUtils.composite(
					ItemModelUtils.plainModel(Blasters.id("item/e11d")),
					ItemModelUtils.conditional(
							new HasAttachmentProperty(
									Blasters.id("barrel_slot"),
									Blasters.id("e11/bipod")
							),
							ItemModelUtils.plainModel(Blasters.id("item/e11d_flashlight")),
							new EmptyModel.Unbaked()
					)
			));
		}
	}

	/**
	 * The blaster language file generator. All language entries should be
	 * added through this generator.
	 */
	private static class LangGenerator extends FabricLanguageProvider
	{
		/**
		 * Static translation values for one blaster and its attachment IDs.
		 */
		private record BlasterLang(String name, Map<Identifier, String> attachmentLangs)
		{
		}

		/**
		 * Static translations for the test blaster.
		 */
		private final Map<Identifier, BlasterLang> _blasterLang = Map.ofEntries(
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

		protected LangGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup)
		{
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder)
		{
			// Tag that contains the blasters
			translationBuilder.add(Blasters.BLASTERS_TAG, "Blasters");

			// Item name
			translationBuilder.add(Blasters.BLASTER_ITEM, "Blaster");

			// Model number of each blaster
			translationBuilder.add(BlasterItem.MISSING_ID, "[unknown model]");

			for (var entry : _blasterLang.entrySet())
			{
				// Add the name of the blaster
				translationBuilder.add(entry.getKey().toLanguageKey(), entry.getValue().name());

				// Add all the attachments
				for (var attachmentEntry : entry.getValue().attachmentLangs().entrySet())
				{
					translationBuilder.add("attachment." + attachmentEntry.getKey().toLanguageKey().replace('/', '.'), attachmentEntry.getValue());
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
			translationBuilder.add("tooltip.pswg_blasters.unavailable", "Definition unavailable");
			translationBuilder.add("tooltip.pswg_blasters.mode", "Mode: %s");
			translationBuilder.add("tooltip.pswg_blasters.stats", "Damage: %s\nRange: %s blocks\nInterval: %s t\nEffective Range: %s blocks");
			translationBuilder.add("tooltip.pswg_blasters.handling", "Zoom: %sx\nHip recoil %s degrees\nHip spread %s degrees");
			translationBuilder.add("tooltip.pswg_blasters.cooling", "Cooling: %s /t\nVent %s /t");
			translationBuilder.add("tooltip.pswg_blasters.ammo", "Loaded: %s / %s");
			translationBuilder.add("tooltip.pswg_blasters.conversion", "Conversion: %s");
			translationBuilder.add("tooltip.pswg_blasters.deployed", "Deployed");
			translationBuilder.add("tooltip.pswg_blasters.folded", "Folded");
		}
	}

	/**
	 * The blaster item tag generator. All item tags should be added
	 * through this generator.
	 */
	private static class TagGenerator extends FabricTagsProvider.ItemTagsProvider
	{
		public TagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
		{
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup)
		{
			getOrCreateRawBuilder(Blasters.BLASTERS_TAG)
					.addElement(Blasters.BLASTER_ITEM_ID);
		}
	}
}
