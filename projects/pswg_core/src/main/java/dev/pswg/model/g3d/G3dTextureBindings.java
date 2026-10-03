package dev.pswg.model.g3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable local texture bindings for model defaults, variants, or renderers.
 * Resource values use the same image/Ptex identifiers as fixed G3D materials.
 *
 * @param values Slot names mapped to resources or other #slot references.
 */
public record G3dTextureBindings(Map<String, G3dTextureReference> values)
{
	/**
	 * Creates a single binding, suitable for a one-image armor or block variant.
	 */
	public static G3dTextureBindings of(String slot, Identifier resource)
	{
		return EMPTY.with(slot, resource);
	}

	/**
	 * Reports invalid map keys as ordinary codec errors during resource loading.
	 */
	private static DataResult<G3dTextureBindings> decode(Map<String, G3dTextureReference> values)
	{
		try
		{
			return DataResult.success(new G3dTextureBindings(values));
		}
		catch (RuntimeException exception)
		{
			return DataResult.error(exception::getMessage);
		}
	}

	/**
	 * No consumer overrides; model defaults still apply.
	 */
	public static final G3dTextureBindings EMPTY = new G3dTextureBindings(Map.of());

	/**
	 * The ordinary textures-map shape, including vanilla-style aliases.
	 */
	public static final Codec<G3dTextureBindings> CODEC = Codec.unboundedMap(Codec.STRING, G3dTextureReference.CODEC)
	                                                        .comapFlatMap(G3dTextureBindings::decode, G3dTextureBindings::values);

	/**
	 * Copies settings so registrations, cached views, and queued draws cannot diverge.
	 */
	public G3dTextureBindings
	{
		values = Map.copyOf(values);
		for (var slot : values.keySet())
			new G3dTextureReference("#" + slot);
	}

	/**
	 * Returns a new binding set with one fixed resource assigned.
	 */
	public G3dTextureBindings with(String slot, Identifier resource)
	{
		return with(slot, new G3dTextureReference(resource));
	}

	/**
	 * Returns a new binding set with a resource or #slot alias assigned.
	 */
	public G3dTextureBindings with(String slot, G3dTextureReference reference)
	{
		var result = new HashMap<>(values);
		result.put(slot, reference);
		return new G3dTextureBindings(result);
	}
}
