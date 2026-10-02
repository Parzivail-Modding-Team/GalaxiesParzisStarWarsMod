package dev.pswg.rendering.ptex;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A named Ptex graph stored at assets/namespace/ptex/path.json.
 *
 * @param graph The registered texture-service graph.
 * @param atlas Whether this graph can be generated during atlas reload.
 */
public record PtexDefinition(PtexTextureSpec graph, boolean atlas)
{
	/**
	 * Gets the stable atlas sprite name without needing a client or a GPU.
	 */
	public static Identifier spriteId(Identifier definition)
	{
		return definition.withPrefix("ptex/");
	}

	/**
	 * Loads one bounded document. Missing graphs are reported with their resource id.
	 */
	public static PtexDefinition read(ResourceManager manager, Identifier id) throws IOException
	{
		var resource = manager.getResource(FILES.idToFile(id)).orElseThrow(() -> new IOException("Missing Ptex definition " + id));
		try (var input = resource.open())
		{
			var bytes = input.readNBytes(1024 * 1024 + 1);
			if (bytes.length > 1024 * 1024)
				throw new IOException("Ptex definition is too large: " + id);
			return CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)))
			            .getOrThrow(IOException::new);
		}
	}

	/**
	 * Loads a pack snapshot. Failures are diagnosed by the caller's reload path.
	 */
	public static Map<Identifier, PtexDefinition> load(ResourceManager manager) throws IOException
	{
		var result = new HashMap<Identifier, PtexDefinition>();
		for (var file : FILES.listMatchingResources(manager).keySet())
		{
			var id = FILES.fileToId(file);
			result.put(id, read(manager, id));
		}
		return Map.copyOf(result);
	}

	/**
	 * Discovers definitions through the vanilla resource pipeline.
	 */
	public static final FileToIdConverter FILES = new FileToIdConverter("ptex", ".json");
	/**
	 * Named graph codec; sampled output is available for every graph.
	 */
	public static final Codec<PtexDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PtexCodecs.CODEC.fieldOf("graph").forGetter(PtexDefinition::graph),
			Codec.BOOL.optionalFieldOf("atlas", true).forGetter(PtexDefinition::atlas)
	).apply(instance, PtexDefinition::new));
}
