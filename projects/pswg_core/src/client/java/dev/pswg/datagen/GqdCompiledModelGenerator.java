package dev.pswg.datagen;

import dev.pswg.rendering.models.GalaxiesModelBakery;
import dev.pswg.rendering.models.GqbIntermediary;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static dev.pswg.rendering.models.GqbIntermediary.GQB_INTERMEDIARY_LOADER;

/**
 * The GQD compiled model generator. All models should be compiled through
 * this generator.
 */
public class GqdCompiledModelGenerator implements DataProvider
{
	private final PackOutput.PathProvider resolver;
	String modId;

	public GqdCompiledModelGenerator(FabricPackOutput output, String modId)
	{
		this.resolver = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
		this.modId = modId;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput writer)
	{
		var completables = new ArrayList<CompletableFuture<?>>();

		for (var entry : GQB_INTERMEDIARY_LOADER.getDefinitions().entrySet())
		{
			if (!entry.getKey().getNamespace().equals(modId))
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
			return "Galaxies GQD Compiled Models";
		}
}