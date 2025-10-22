package dev.pswg.datagen;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import dev.pswg.Blasters;
import dev.pswg.BlastersClient;
import dev.pswg.Galaxies;
import dev.pswg.data.CodecDataLoader;
import dev.pswg.data.IdentifierUtil;
import dev.pswg.item.BlasterItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The blaster data generator
 */
public class BlasterDataGenerator implements DataGeneratorEntrypoint
{
	/**
	 * A resource loader for quad buffer intermediary files
	 */
	public static final CodecDataLoader<JsonElement> GQB_INTERMEDIARY_LOADER = new CodecDataLoader<>(
			Galaxies.id("gqbi"),
			"models",
			false,
			(i) -> IdentifierUtil.hasExtension(i, "gqbi"),
			Codecs.JSON_ELEMENT
	);

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		DataGenResourceHelper.loadResources(ResourceType.CLIENT_RESOURCES, GQB_INTERMEDIARY_LOADER);
		DataGenResourceHelper.loadResources(ResourceType.SERVER_DATA, Blasters.DATAPACK_LOADER);

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
	 * The GQD compiled model generator. All models should be compiled through
	 * this generator.
	 */
	private static class GqdCompiledModelGenerator implements DataProvider
	{
		private final DataOutput.PathResolver resolver;

		public GqdCompiledModelGenerator(FabricDataOutput output)
		{
			this.resolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "");
		}

		@Override
		public CompletableFuture<?> run(DataWriter writer)
		{
			var completables = new ArrayList<CompletableFuture<?>>();

			for (var entry : GQB_INTERMEDIARY_LOADER.getDefinitions().entrySet())
			{
				if (!entry.getKey().getNamespace().equals(Blasters.MODID))
					continue;

				completables.add(compile(entry));
			}

			return CompletableFuture.allOf(completables.toArray(CompletableFuture[]::new));
		}

		private CompletableFuture<?> compile(Map.Entry<Identifier, JsonElement> entry)
		{
			return CompletableFuture.runAsync(() -> {
				var nonDatagenId = entry.getKey().withPath("models/" + getNonDatagenPath(entry.getKey().getPath()));
				var outputPath = resolver.resolve(nonDatagenId, "gqb");
				// TODO: compile
			});
		}

		private String getNonDatagenPath(String filename)
		{
			String datagenPath = "/datagen/";

			var path = FilenameUtils.getPath(filename);

			if (path.endsWith(datagenPath))
				path = path.substring(0, path.length() - datagenPath.length() + 1);

			path += FilenameUtils.getBaseName(filename);

			return path;
		}

		@Override
		public String getName()
		{
			return "GQD Compiled Models";
		}

		static <T> CompletableFuture<?> writeToPath(DataWriter writer, Path path, PacketCodec<ByteBuf, T> codec, T value)
		{
			return CompletableFuture.runAsync(() -> {
				try
				{
					var byteArrayOutputStream = new ByteArrayOutputStream();
					var hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);

					var buf = Unpooled.buffer();
					codec.encode(buf, value);

					buf.readBytes(hashingOutputStream, buf.readableBytes());

					writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
				}
				catch (IOException var10)
				{
					LOGGER.error("Failed to save file to {}", path, var10);
				}
			}, Util.getMainWorkerExecutor().named("saveStable"));
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
