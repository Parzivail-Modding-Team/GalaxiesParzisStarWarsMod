package dev.pswg.rendering.g3d;

import dev.pswg.Galaxies;
import dev.pswg.model.g3d.G3dResources;
import dev.pswg.model.g3d.G3dTextureBindings;
import dev.pswg.model.g3d.G3dTransform;
import dev.pswg.rendering.ptex.PtexDefinition;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Module-facing registration and reload-safe lookup of native G3D renderers.
 */
public final class G3dClientModels
{
	/**
	 * CPU-side resources and native sidecars needed by slot-bearing templates.
	 */
	private record Prepared(
			Map<Identifier, G3dGeometry> geometries,
			Map<Identifier, PtexDefinition> textures,
			Set<Identifier> sidecars
	)
	{
	}

	/**
	 * Supplies gameplay-visible local poses during item render-state extraction.
	 */
	@FunctionalInterface
	public interface ItemPoseProvider
	{
		/**
		 * Returns overrides in blocks. Values are copied before the submit phase.
		 */
		Map<String, G3dTransform> extract(ItemStack item, ItemDisplayContext context, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed);
	}

	/**
	 * Registers the public Fabric prepare/load/bake pipeline once during client startup.
	 */
	public static void register()
	{
		PreparableModelLoadingPlugin.register((state, executor) -> CompletableFuture.supplyAsync(() -> {
			try
			{
				var textures = PtexDefinition.load(state.resourceManager());
				var geometries = new HashMap<Identifier, G3dGeometry>();
				var sidecars = new HashSet<Identifier>();

				G3dResources.loadModels(state.resourceManager()).forEach((id, model) -> {
					try
					{
						var geometry = new G3dGeometry(model, textures);
						model.materials().stream().filter(material -> !material.texture().isSlot())
						     .forEach(material -> geometry.texture(material.texture().resource()));
						geometries.put(id, geometry);
						if (model.materials().stream().anyMatch(material -> material.texture().isSlot())
						    && state.resourceManager().getResource(id.withPath("models/" + id.getPath() + ".json")).isPresent())
							sidecars.add(id);
					}
					catch (IllegalArgumentException exception)
					{
						Galaxies.LOGGER.error("Could not resolve G3D model {}", id, exception);
					}
				});
				return new Prepared(Map.copyOf(geometries), textures, Set.copyOf(sidecars));
			}
			catch (IOException exception)
			{
				throw new IllegalStateException("Could not load G3D texture definitions", exception);
			}
		}, executor), (prepared, context) -> {
			context.modifyModelOnLoad().register(ModelModifier.WRAP_PHASE, (sidecar, load) -> {
				var geometry = prepared.geometries().get(load.id());
				return geometry == null ? sidecar : new G3dUnbakedModel(sidecar, geometry);
			});

			context.addModel(RENDERERS, new UnbakedExtraModel<>()
			{
				@Override
				public void resolveDependencies(Resolver resolver)
				{
					prepared.sidecars().forEach(resolver::markDependency);
				}

				@Override
				public G3dModelViews bake(ModelBaker baker)
				{
					var renderers = new HashMap<Identifier, G3dRenderer>();
					var defaults = new HashMap<Identifier, List<TextureSlots.Data>>();
					for (var id : prepared.sidecars())
					{
						var maps = new ArrayList<TextureSlots.Data>();
						for (var model = baker.getModel(id); model != null; model = model.parent())
							maps.add(model.wrapped().textureSlots());
						defaults.put(id, List.copyOf(maps));
					}
					var views = new G3dModelViews(prepared.geometries(), defaults, prepared.textures(), Map.of());
					prepared.geometries().forEach((id, geometry) -> {
						var slots = views.slots(id, G3dTextureBindings.EMPTY);
						if (geometry.model().materials().stream().anyMatch(material -> material.texture().isSlot()
						        && slots.getMaterial(material.texture().slot()) == null))
							return;
						renderers.put(id, new G3dRenderer(geometry, SAMPLED_MODELS.contains(id) ? null : baker, slots));
					});
					return new G3dModelViews(prepared.geometries(), defaults, prepared.textures(), renderers);
				}
			});
		});
	}

	/**
	 * Selects standalone/dynamic textures for an entity-only model.
	 */
	public static void registerSampled(Identifier id)
	{
		SAMPLED_MODELS.add(id);
	}

	/**
	 * Gets a renderer from the current model manager, without retaining a stale reload cache.
	 */
	public static Optional<G3dRenderer> get(Identifier id)
	{
		var renderers = Minecraft.getInstance().getModelManager().getModel(RENDERERS);
		return renderers == null ? Optional.empty() : renderers.get(id);
	}

	/**
	 * Requests a standalone/dynamic appearance using model defaults plus immutable
	 * consumer overrides. Geometry is shared, views are cached, and reload discards
	 * old bindings. Safe for extraction-time per-entity or per-item appearance choices.
	 */
	public static Optional<G3dRenderer> getSampled(Identifier id, G3dTextureBindings bindings)
	{
		var views = Minecraft.getInstance().getModelManager().getModel(RENDERERS);
		return views == null ? Optional.empty() : views.sampled(id, bindings);
	}

	/**
	 * Modules can enable the posed item path for a model without changing item JSON.
	 */
	public static void registerItemPose(Identifier id, ItemPoseProvider provider)
	{
		ITEM_POSES.put(id, provider);
	}

	/**
	 * Gets a startup-registered pose provider, or null for a static item.
	 */
	public static @Nullable ItemPoseProvider itemPose(Identifier id)
	{
		return ITEM_POSES.get(id);
	}

	/**
	 * Installed renderers are published by vanilla's model manager on reload apply.
	 */
	private static final ExtraModelKey<G3dModelViews> RENDERERS = ExtraModelKey.create(() -> "G3D renderers and texture views");

	/**
	 * Module registrations survive resource reload; asset instances do not.
	 */
	private static final Map<Identifier, ItemPoseProvider> ITEM_POSES = new HashMap<>();

	/**
	 * The models whose default renderer uses standalone sampled textures.
	 */
	private static final Set<Identifier> SAMPLED_MODELS = new HashSet<>();

	/**
	 * Prevents construction of this registration utility.
	 */
	private G3dClientModels()
	{
	}
}
