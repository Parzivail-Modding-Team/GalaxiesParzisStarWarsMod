package dev.pswg.rendering.g3d;

import com.google.gson.JsonObject;
import dev.pswg.model.g3d.G3dTextureBindings;
import dev.pswg.model.g3d.G3dTextureReference;
import dev.pswg.rendering.ptex.PtexDefinition;
import dev.pswg.rendering.ptex.SourceTexture;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Shared image/Ptex/slot translation for sampled rendering and vanilla atlases.
 * Vanilla model texture maps store sprite identifiers; G3D authoring and code
 * can also supply full image paths or Ptex definition identifiers.
 */
public final class G3dTextures
{
	/**
	 * Converts a resource to its native sprite, respecting explicit Ptex definitions.
	 */
	public static Identifier spriteId(Identifier resource, Map<Identifier, PtexDefinition> definitions)
	{
		if (definitions.containsKey(resource))
			return PtexDefinition.spriteId(resource);
		var path = resource.getPath();
		if (path.startsWith("textures/") && path.endsWith(".png"))
			return resource.withPath(path.substring("textures/".length(), path.length() - ".png".length()));
		return resource;
	}

	/**
	 * Resolves resources and native sprite identifiers to the same sampled graph.
	 */
	public static PtexDefinition definition(Identifier resource, Map<Identifier, PtexDefinition> definitions)
	{
		var definition = definitions.get(resource);
		if (definition != null)
			return definition;
		var path = resource.getPath();
		if (path.startsWith("ptex/"))
		{
			definition = definitions.get(resource.withPath(path.substring("ptex/".length())));
			if (definition != null)
				return definition;
			throw new IllegalArgumentException("Missing Ptex definition for sprite " + resource);
		}
		var image = path.startsWith("textures/") && path.endsWith(".png")
				? resource
				: resource.withPath("textures/" + path + ".png");
		return new PtexDefinition(new SourceTexture(image), true);
	}

	/**
	 * Resolves a surface through vanilla's already-inherited texture slots.
	 */
	public static Material material(G3dTextureReference reference, TextureSlots slots, Map<Identifier, PtexDefinition> definitions)
	{
		if (!reference.isSlot())
			return new Material(spriteId(reference.resource(), definitions));
		var material = slots.getMaterial(reference.slot());
		if (material == null)
			throw new IllegalArgumentException("Unbound G3D texture slot " + reference.value());
		return new Material(spriteId(material.sprite(), definitions), material.forceTranslucent());
	}

	/**
	 * Converts consumer overrides to native slot data without flattening aliases.
	 */
	public static TextureSlots.Data slots(G3dTextureBindings bindings, Map<Identifier, PtexDefinition> definitions)
	{
		var result = new TextureSlots.Data.Builder();
		bindings.values().forEach((name, reference) -> {
			if (reference.isSlot())
				result.addReference(name, reference.slot());
			else
				result.addTexture(name, new Material(spriteId(reference.resource(), definitions)));
		});
		return result.build();
	}

	/**
	 * Writes the vanilla projection of source defaults or generated variant bindings.
	 * Hash references and full native material objects retain their normal semantics.
	 */
	public static JsonObject atlasTextures(JsonObject input, Map<Identifier, PtexDefinition> definitions)
	{
		var result = input.deepCopy();
		for (var entry : result.entrySet())
		{
			var value = entry.getValue();
			if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString())
			{
				var reference = new G3dTextureReference(value.getAsString());
				if (!reference.isSlot())
					entry.setValue(new com.google.gson.JsonPrimitive(spriteId(reference.resource(), definitions).toString()));
			}
			else if (value.isJsonObject() && value.getAsJsonObject().has("sprite"))
			{
				var material = value.getAsJsonObject();
				material.addProperty("sprite", spriteId(Identifier.parse(material.get("sprite").getAsString()), definitions).toString());
			}
		}
		return result;
	}

	/**
	 * Static translation boundary; no client singleton or GPU access is required.
	 */
	private G3dTextures()
	{
	}
}
