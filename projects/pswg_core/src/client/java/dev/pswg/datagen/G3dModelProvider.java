package dev.pswg.datagen;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.pswg.model.g3d.G3dCompiler;
import dev.pswg.model.g3d.G3dFiles;
import dev.pswg.model.g3d.G3dResources;
import dev.pswg.model.g3d.G3dSource;
import dev.pswg.model.g3d.G3dTextureBindings;
import dev.pswg.rendering.g3d.G3dTextures;
import dev.pswg.rendering.ptex.SourceTexture;
import dev.pswg.rendering.ptex.PtexDefinition;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Compiles each module's .jg3d sources into visual, rig, and model resources.
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
		 * Vanilla model metadata supplied by the authoring source, when present.
		 */
		private Map<Identifier, JsonObject> _sidecars = Map.of();

		/**
		 * Direct image resources available in the same input snapshot.
		 */
		private Set<Identifier> _images = Set.of();

		/**
		 * Loads only authoring resources, with a generous bounded document size.
		 */
		@Override
		public void onResourceManagerReload(ResourceManager manager)
		{
			var models = new HashMap<Identifier, G3dSource>();
			var sidecars = new HashMap<Identifier, JsonObject>();
			var images = new HashSet<Identifier>();
			try
			{
				var textures = PtexDefinition.load(manager);
				for (var entry : G3dResources.SOURCE_FILES.listMatchingResources(manager).entrySet())
				{
					try (var input = entry.getValue().open())
					{
						var bytes = input.readNBytes(64 * 1024 * 1024 + 1);
						if (bytes.length > 64 * 1024 * 1024)
							throw new IOException("G3D source is too large: " + entry.getKey());

						var document = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8));
						var model = G3dSource.CODEC.parse(JsonOps.INSTANCE, document)
						                           .getOrThrow(message -> new IOException(entry.getKey() + ": " + message));
						var id = G3dResources.SOURCE_FILES.fileToId(entry.getKey());
						models.put(id, model);
						if (document.getAsJsonObject().has("model"))
						{
							try
							{
								var metadata = document.getAsJsonObject().getAsJsonObject("model");
								sidecars.put(id, createSidecar(model, metadata, textures));
							}
							catch (RuntimeException exception)
							{
								throw new IOException(entry.getKey() + ": invalid model metadata", exception);
							}
						}
						for (var material : model.materials())
						{
							if (!material.texture().isSlot())
							{
								var imageId = material.texture().resource();
								var definition = G3dTextures.definition(imageId, textures);
								if (definition.graph() instanceof SourceTexture source && manager.getResource(source.identifier()).isPresent())
									images.add(imageId);
							}
						}
					}
				}
				_models = Map.copyOf(models);
				_textures = textures;
				_sidecars = Map.copyOf(sidecars);
				_images = Set.copyOf(images);
			}
			catch (IOException exception)
			{
				throw new IllegalStateException("Could not load G3D authoring resources", exception);
			}
		}
	}

	/**
	 * Builds a native model sidecar without changing the supplied authoring data.
	 * Display transforms stay in vanilla units and use vanilla's own parser.
	 * An authored particle or parent keeps its normal texture selection; a new
	 * standalone model gets a particle from its first atlas-capable surface.
	 */
	public static JsonObject createSidecar(
			G3dSource source,
			JsonObject metadata,
			Map<Identifier, PtexDefinition> textures
	)
	{
		var result = metadata.deepCopy();
		// Indexed geometry lives in the .g3d file, never in a second cube list.
		result.remove("elements");
		if (!result.has("parent"))
		{
			var slots = result.has("textures") ? result.getAsJsonObject("textures") : new JsonObject();
			if (!slots.has("particle"))
				slots.addProperty("particle", particleSprite(source, textures).toString());
			result.add("textures", slots);
		}
		if (result.has("textures"))
			result.add("textures", G3dTextures.atlasTextures(result.getAsJsonObject("textures"), textures));
		if (result.has("display"))
		{
			for (var view : result.getAsJsonObject("display").entrySet())
			{
				var transform = view.getValue().getAsJsonObject();
				for (var field : new String[]{"rotation", "translation", "scale"})
				{
					if (!transform.has(field))
						continue;
					var values = transform.getAsJsonArray(field);
					if (values.size() != 3)
						throw new IllegalArgumentException(view.getKey() + " " + field + " needs three numbers");
					for (var value : values)
					{
						if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber() || !Float.isFinite(value.getAsFloat()))
							throw new IllegalArgumentException(view.getKey() + " " + field + " needs finite numbers");
					}
				}
			}
		}
		CuboidModel.fromStream(new StringReader(result.toString()));
		return result;
	}

	/**
	 * Recognizes the direct-image fallback also used by G3D rendering.
	 */
	private static boolean isDirectImage(Identifier texture)
	{
		return texture.getPath().startsWith("textures/") && texture.getPath().endsWith(".png");
	}

	/**
	 * Chooses a visible particle without changing a model's surface textures.
	 */
	private static String particleSprite(G3dSource source, Map<Identifier, PtexDefinition> textures)
	{
		for (var material : source.materials())
		{
			var texture = material.texture();
			if (texture.isSlot())
				return texture.value();
			var resource = texture.resource();
			var definition = textures.get(resource);
			if (definition != null)
			{
				if (definition.atlas())
					return PtexDefinition.spriteId(resource).toString();
			}
			else if (isDirectImage(resource))
			{
				return G3dTextures.spriteId(resource, textures).toString();
			}
		}
		return "minecraft:missingno";
	}

	/**
	 * Generates a lightweight vanilla child model for a shared G3D template. Values
	 * use the same resource/Ptex bindings as sampled consumers; only the emitted
	 * vanilla projection converts them to atlas sprite identifiers.
	 */
	public static JsonObject createVariant(Identifier parent, G3dTextureBindings bindings, Map<Identifier, PtexDefinition> textures)
	{
		var result = new JsonObject();
		result.addProperty("parent", parent.toString());
		var values = G3dTextureBindings.CODEC.encodeStart(JsonOps.INSTANCE, bindings).getOrThrow().getAsJsonObject();
		result.add("textures", G3dTextures.atlasTextures(values, textures));
		return result;
	}

	/**
	 * Uses the active datagen resource snapshot when generating module variants.
	 */
	public static JsonObject createVariant(Identifier parent, G3dTextureBindings bindings)
	{
		return createVariant(parent, bindings, SOURCES._textures);
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
			var pending = new ArrayList<CompletableFuture<?>>();
			for (var entry : SOURCES._models.entrySet())
			{
				var id = entry.getKey();
				if (!id.getNamespace().equals(_namespace))
					continue;

				for (var material : entry.getValue().materials())
				{
					if (!material.texture().isSlot() && !SOURCES._textures.containsKey(material.texture().resource()) && !SOURCES._images.contains(material.texture().resource()))
						throw new IOException(id + ": missing Ptex definition or image " + material.texture());
				}

				var model = G3dCompiler.compile(id, entry.getValue());
				write(output, _models.file(id, "g3d"), G3dFiles.write(model, true));
				write(output, _rigs.file(id, "g3d"), G3dFiles.write(model, false));
				var sidecar = SOURCES._sidecars.get(id);
				if (sidecar != null)
					pending.add(DataProvider.saveStable(output, sidecar, _models.json(id)));
			}

			return CompletableFuture.allOf(pending.toArray(CompletableFuture[]::new));
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
		return _namespace + " G3D V1 models, rigs, and sidecars";
	}
}
