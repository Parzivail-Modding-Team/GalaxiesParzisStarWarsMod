package dev.pswg.datagen;

import dev.pswg.Blasters;
import dev.pswg.BlastersClient;
import dev.pswg.Galaxies;
import dev.pswg.data.CodecDataLoader;
import dev.pswg.data.IdentifierUtil;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.HasAttachmentProperty;
import dev.pswg.rendering.models.GalaxiesModelBakery;
import dev.pswg.rendering.models.GqbIntermediary;
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
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static dev.pswg.rendering.models.GqbIntermediary.GQB_INTERMEDIARY_LOADER;

/**
 * The blaster data generator
 */
public class BlasterDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		DataGenResourceHelper.loadResources(PackType.CLIENT_RESOURCES, GQB_INTERMEDIARY_LOADER);
		DataGenResourceHelper.loadResources(PackType.SERVER_DATA, Blasters.DATAPACK_LOADER);

		pack.addProvider(LangGenerator::new);
		pack.addProvider(TagGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(GqdCompiledModelGenerator::new);
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
	 * The GQD compiled model generator. All models should be compiled through
	 * this generator.
	 */
	private static class GqdCompiledModelGenerator implements DataProvider
	{
		private final PackOutput.PathProvider resolver;

		public GqdCompiledModelGenerator(FabricPackOutput output)
		{
			this.resolver = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
		}

		@Override
		public CompletableFuture<?> run(CachedOutput writer)
		{
			var completables = new ArrayList<CompletableFuture<?>>();

			for (var entry : GQB_INTERMEDIARY_LOADER.getDefinitions().entrySet())
			{
				if (!entry.getKey().getNamespace().equals(Blasters.MODID))
					continue;

				completables.add(compile(writer, entry));
			}

			return CompletableFuture.allOf(completables.toArray(CompletableFuture[]::new));
		}

		/**
		 * Compile the given GQB intermediary model
		 *
		 * @param writer The writer to add the generated data to
		 * @param entry  The entry to compile
		 *
		 * @return A future that completes when the data is written
		 */
		private CompletableFuture<?> compile(CachedOutput writer, Map.Entry<Identifier, GqbIntermediary> entry)
		{
			var completables = new ArrayList<CompletableFuture<?>>();

			if (entry.getValue().files().isPresent())
			{
				// Split the geometry and model into multiple files
				for (var fileEntry : entry.getValue().files().get().entrySet())
				{
					var nonDatagenId = entry.getKey().withPath(GalaxiesDataProvider.getNonDatagenPath(entry.getKey().getPath(), Optional.of(fileEntry.getKey())));
					var quadsOutputPath = resolver.file(nonDatagenId, "gqb");
					var jsonOutputPath = resolver.file(nonDatagenId, "json");

					completables.add(DataProvider.saveStable(writer, entry.getValue().createModelDef(), jsonOutputPath));
					completables.add(GalaxiesDataProvider.writeToPath(
							writer,
							quadsOutputPath,
							GalaxiesModelBakery.GQuadGeometry.PACKET_CODEC,
							entry.getValue().createGeometry(Optional.of(new HashSet<>(fileEntry.getValue())))
					));
				}
			}
			else
			{
				var nonDatagenId = entry.getKey().withPath(GalaxiesDataProvider.getNonDatagenPath(entry.getKey().getPath(), Optional.empty()));
				var quadsOutputPath = resolver.file(nonDatagenId, "gqb");
				var jsonOutputPath = resolver.file(nonDatagenId, "json");

				completables.add(DataProvider.saveStable(writer, entry.getValue().createModelDef(), jsonOutputPath));
				completables.add(GalaxiesDataProvider.writeToPath(
						writer,
						quadsOutputPath,
						GalaxiesModelBakery.GQuadGeometry.PACKET_CODEC,
						entry.getValue().createGeometry(Optional.empty())
				));
			}

			return CompletableFuture.allOf(completables.toArray(CompletableFuture[]::new));
		}

		@Override
		public String getName()
		{
			return "GQD Compiled Models";
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
