package dev.pswg.rendering.g3d;

import dev.pswg.Galaxies;
import dev.pswg.model.g3d.G3dTextureBindings;
import dev.pswg.rendering.ptex.PtexDefinition;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * One reload's shared geometry and immutable, independently cached sampled views.
 * A view changes material bindings, never meshes, rig transforms, or other views.
 */
public final class G3dModelViews
{
	/**
	 * Complete cache identity for a consumer's sampled appearance.
	 */
	private record Key(Identifier model, G3dTextureBindings bindings)
	{
	}

	/**
	 * Geometry shared by vanilla baking and every sampled appearance.
	 */
	private final Map<Identifier, G3dGeometry> _geometries;

	/**
	 * Ordered native texture maps, with the child's defaults before its parents.
	 */
	private final Map<Identifier, List<TextureSlots.Data>> _defaults;

	/**
	 * Ptex definitions captured from this same reload.
	 */
	private final Map<Identifier, PtexDefinition> _textures;

	/**
	 * Default atlas-aware or explicitly sampled renderers from model baking.
	 */
	private final Map<Identifier, G3dRenderer> _renderers;

	/**
	 * Lazy sampled requests need no retained baker or per-frame texture resolution.
	 */
	private final Map<Key, Optional<G3dRenderer>> _sampled = new ConcurrentHashMap<>();

	/**
	 * Takes a completed native reload snapshot. Unbound templates remain available
	 * for child block models and runtime consumers that supply their missing slots.
	 */
	public G3dModelViews(
			Map<Identifier, G3dGeometry> geometries,
			Map<Identifier, List<TextureSlots.Data>> defaults,
			Map<Identifier, PtexDefinition> textures,
			Map<Identifier, G3dRenderer> renderers
	)
	{
		_geometries = Map.copyOf(geometries);
		var maps = new HashMap<Identifier, List<TextureSlots.Data>>();
		defaults.forEach((id, entries) -> {
			var copies = new ArrayList<TextureSlots.Data>();
			for (var data : entries)
				copies.add(new TextureSlots.Data(Map.copyOf(data.values())));
			maps.put(id, List.copyOf(copies));
		});
		_defaults = Map.copyOf(maps);
		_textures = Map.copyOf(textures);
		_renderers = Map.copyOf(renderers);
	}

	/**
	 * Gets the default appearance without retaining an earlier reload's renderer.
	 */
	public Optional<G3dRenderer> get(Identifier id)
	{
		return Optional.ofNullable(_renderers.get(id));
	}

	/**
	 * Resolves and caches a consumer's sampled appearance within this reload only.
	 */
	public Optional<G3dRenderer> sampled(Identifier id, G3dTextureBindings bindings)
	{
		return _sampled.computeIfAbsent(new Key(id, bindings), key -> {
			var geometry = _geometries.get(key.model());
			if (geometry == null)
				return Optional.empty();
			try
			{
				return Optional.of(new G3dRenderer(geometry, null, slots(key.model(), key.bindings())));
			}
			catch (IllegalArgumentException exception)
			{
				Galaxies.LOGGER.error("Could not bind sampled G3D model {} with {}", id, bindings, exception);
				return Optional.empty();
			}
		});
	}

	/**
	 * Applies consumer overrides before native inheritance/alias resolution, so
	 * aliases to an overridden slot also change. No aliases are prematurely flattened.
	 */
	public TextureSlots slots(Identifier id, G3dTextureBindings bindings)
	{
		var resolver = new TextureSlots.Resolver().addLast(G3dTextures.slots(bindings, _textures));
		for (var defaults : _defaults.getOrDefault(id, List.of()))
			resolver.addLast(defaults);
		return resolver.resolve(() -> id.toString());
	}
}
