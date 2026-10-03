package dev.pswg.rendering.g3d;

import dev.pswg.Galaxies;
import dev.pswg.model.g3d.G3dResources;
import dev.pswg.model.g3d.G3dTransform;
import dev.pswg.rendering.ptex.PtexDefinition;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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

				G3dResources.loadModels(state.resourceManager()).forEach((id, model) -> {
					try
					{
						var geometry = new G3dGeometry(model, textures);
						model.materials().forEach(material -> geometry.texture(material.texture()));
						geometries.put(id, geometry);
					}
					catch (IllegalArgumentException exception)
					{
						Galaxies.LOGGER.error("Could not resolve G3D model {}", id, exception);
					}
				});
				return Map.copyOf(geometries);
			}
			catch (IOException exception)
			{
				throw new IllegalStateException("Could not load G3D texture definitions", exception);
			}
		}, executor), (geometries, context) -> {
			context.modifyModelOnLoad().register(ModelModifier.WRAP_PHASE, (sidecar, load) -> {
				var geometry = geometries.get(load.id());
				return geometry == null ? sidecar : new G3dUnbakedModel(sidecar, geometry);
			});

			context.addModel(RENDERERS, new UnbakedExtraModel<>()
			{
				@Override
				public void resolveDependencies(Resolver resolver)
				{
					// Entity-only models need no vanilla sidecar or texture-slot dependency.
				}

				@Override
				public Map<Identifier, G3dRenderer> bake(ModelBaker baker)
				{
					var renderers = new HashMap<Identifier, G3dRenderer>();
					geometries.forEach((id, geometry) -> renderers.put(id, new G3dRenderer(
							geometry,
							SAMPLED_MODELS.contains(id) ? null : baker
					)));
					return Map.copyOf(renderers);
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
		return renderers == null ? Optional.empty() : Optional.ofNullable(renderers.get(id));
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
	private static final ExtraModelKey<Map<Identifier, G3dRenderer>> RENDERERS = ExtraModelKey.create(() -> "G3D renderers");

	/**
	 * Module registrations survive resource reload; asset instances do not.
	 */
	private static final Map<Identifier, ItemPoseProvider> ITEM_POSES = new HashMap<>();

	/**
	 * The models that use samples textures.
	 */
	private static final Set<Identifier> SAMPLED_MODELS = new HashSet<>();

	/**
	 * Prevents construction of this registration utility.
	 */
	private G3dClientModels()
	{
	}
}
