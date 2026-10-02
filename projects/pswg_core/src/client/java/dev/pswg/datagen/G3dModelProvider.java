package dev.pswg.datagen;

import com.google.common.hash.Hashing;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.pswg.model.g3d.G3dCompiler;
import dev.pswg.model.g3d.G3dFiles;
import dev.pswg.model.g3d.G3dResources;
import dev.pswg.model.g3d.G3dSource;
import dev.pswg.rendering.ptex.PtexDefinition;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Compiles each module's .jg3d sources into visual and rig resources.
 */
public final class G3dModelProvider implements DataProvider
{
	/**
	 * Captures sources before Fabric runs the providers. Loading failures stop
	 * datagen instead of silently leaving an older generated model in place.
	 */
	public static final class Sources implements ResourceManagerReloadListener
	{
		/**
		 * Sources keyed by their final model identifiers.
		 */
		private Map<Identifier, G3dSource> _models = Map.of();

		/**
		 * Definitions from the same resource snapshot.
		 */
		private Map<Identifier, PtexDefinition> _textures = Map.of();

		/**
		 * Loads only authoring resources, with a generous bounded document size.
		 */
		@Override
		public void onResourceManagerReload(ResourceManager manager)
		{
			var models = new HashMap<Identifier, G3dSource>();
			try
			{
				for (var entry : G3dResources.SOURCE_FILES.listMatchingResources(manager).entrySet())
				{
					try (var input = entry.getValue().open())
					{
						var bytes = input.readNBytes(64 * 1024 * 1024 + 1);
						if (bytes.length > 64 * 1024 * 1024)
							throw new IOException("G3D source is too large: " + entry.getKey());

						var model = G3dSource.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)))
						                           .getOrThrow(message -> new IOException(entry.getKey() + ": " + message));
						models.put(G3dResources.SOURCE_FILES.fileToId(entry.getKey()), model);
					}
				}
				_models = Map.copyOf(models);
				_textures = PtexDefinition.load(manager);
			}
			catch (IOException exception)
			{
				throw new IllegalStateException("Could not load G3D authoring resources", exception);
			}
		}
	}

	/**
	 * Saves raw binary bytes with the same cache hashing used by other providers.
	 */
	private static void write(CachedOutput output, Path path, byte[] bytes) throws IOException
	{
		output.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
	}

	/**
	 * Shared input loader used by each configured module's datagen entrypoint.
	 */
	public static final Sources SOURCES = new Sources();

	/**
	 * Client visual output path provider.
	 */
	private final PackOutput.PathProvider _models;

	/**
	 * Common server rig output path provider.
	 */
	private final PackOutput.PathProvider _rigs;

	/**
	 * The module namespace this provider owns.
	 */
	private final String _namespace;

	/**
	 * Creates a provider for one official or third-party module.
	 */
	public G3dModelProvider(FabricPackOutput output, String namespace)
	{
		_models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
		_rigs = output.createPathProvider(PackOutput.Target.DATA_PACK, "g3d/rigs");
		_namespace = namespace;
	}

	/**
	 * Compiles and writes deterministic bytes through vanilla's cached output.
	 */
	@Override
	public CompletableFuture<?> run(CachedOutput output)
	{
		try
		{
			for (var entry : SOURCES._models.entrySet())
			{
				var id = entry.getKey();
				if (!id.getNamespace().equals(_namespace))
					continue;

				for (var material : entry.getValue().materials())
				{
					if (!SOURCES._textures.containsKey(material.texture()))
						throw new IOException(id + ": missing Ptex definition " + material.texture());
				}

				var model = G3dCompiler.compile(id, entry.getValue());
				write(output, _models.file(id, "g3d"), G3dFiles.write(model, true));
				write(output, _rigs.file(id, "g3d"), G3dFiles.write(model, false));
			}

			return CompletableFuture.completedFuture(null);
		}
		catch (Exception exception)
		{
			return CompletableFuture.failedFuture(exception);
		}
	}

	/**
	 * Names this provider in Fabric's datagen diagnostics.
	 */
	@Override
	public String getName()
	{
		return _namespace + " G3D V1 models and rigs";
	}
}
