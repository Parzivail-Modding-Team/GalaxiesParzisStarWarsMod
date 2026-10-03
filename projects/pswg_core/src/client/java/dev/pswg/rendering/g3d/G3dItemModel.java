package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.model.g3d.G3dPose;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A native special-model leaf for sampled textures or code-driven node poses.
 */
public final class G3dItemModel implements ItemModel, SpecialModelRenderer<G3dItemModel.Argument>
{
	/**
	 * Captured render inputs. Arrays belong to this extracted state and are read-only.
	 *
	 * @param matrices The evaluated model-space node transforms.
	 * @param tints    The calculated vanilla tint source colors.
	 */
	public record Argument(Matrix4fc[] matrices, int[] tints)
	{
	}

	/**
	 * Reload-bound renderer and Ptex graphs.
	 */
	private final G3dRenderer _renderer;

	/**
	 * Vanilla sidecar render properties.
	 */
	private final ModelRenderProperties _properties;

	/**
	 * Vanilla item leaf's additional transform.
	 */
	private final Matrix4fc _transform;

	/**
	 * Original tint sources from the ordinary minecraft:model leaf.
	 */
	private final List<ItemTintSource> _tints;

	/**
	 * Optional module-owned pose extraction hook.
	 */
	private final G3dClientModels.@Nullable ItemPoseProvider _poseProvider;

	/**
	 * Rest-pose bounds computed once for static sampled items.
	 */
	private final Vector3fc[] _extents;

	/**
	 * Creates a special leaf while keeping vanilla's sidecar properties.
	 */
	public G3dItemModel(
			G3dGeometry geometry,
			ModelRenderProperties properties,
			Matrix4fc transform,
			List<ItemTintSource> tints,
			G3dClientModels.@Nullable ItemPoseProvider poseProvider
	)
	{
		this(new G3dRenderer(geometry), properties, transform, tints, poseProvider);
	}

	/**
	 * Creates a special leaf with sprites captured by the current model baker.
	 * This keeps atlas animation working for posed items and mixed texture models.
	 */
	public G3dItemModel(
			G3dRenderer renderer,
			ModelRenderProperties properties,
			Matrix4fc transform,
			List<ItemTintSource> tints,
			G3dClientModels.@Nullable ItemPoseProvider poseProvider
	)
	{
		_renderer = renderer;
		_properties = properties;
		_transform = transform;
		_tints = List.copyOf(tints);
		_poseProvider = poseProvider;
		_extents = extents(_renderer.restPose());
	}

	/**
	 * Copies poses and calculated tints before Minecraft queues the submit phase.
	 */
	@Override
	public void update(
			ItemStackRenderState output,
			ItemStack item,
			ItemModelResolver resolver,
			ItemDisplayContext context,
			@Nullable ClientLevel level,
			@Nullable ItemOwner owner,
			int seed
	)
	{
		var matrices = _renderer.restPose();
		if (_poseProvider != null)
		{
			var pose = new G3dPose(_renderer.model().rig());
			pose.evaluate(_poseProvider.extract(item, context, level, owner, seed));
			matrices = pose.snapshot();
		}

		var colors = new int[_tints.size()];
		for (int index = 0; index < colors.length; index++)
			colors[index] = _tints.get(index).calculate(item, level, owner == null ? null : owner.asLivingEntity());

		var argument = new Argument(matrices, colors);
		var bounds = _poseProvider == null ? _extents : extents(matrices);
		output.appendModelIdentityElement(this);

		// Sampled images can complete asynchronously; do not freeze a GUI placeholder.
		output.setAnimated();

		var layer = output.newLayer();
		layer.setExtents(() -> bounds);
		layer.setLocalTransform(_transform);
		layer.setupSpecialModel(this, argument);

		if (item.hasFoil())
			layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);

		_properties.applyToLayer(layer, context);
	}

	/**
	 * Uses captured values only, preserving vanilla's foil and outline request.
	 */
	@Override
	public void submit(@Nullable Argument argument, PoseStack stack, SubmitNodeCollector collector, int light, int overlay, boolean foil, int outline)
	{
		if (argument != null)
			_renderer.submit(argument.matrices(), stack, collector, light, overlay, -1, argument.tints(), true, foil, outline);
	}

	/**
	 * Exposes rest bounds through the native special-renderer interface.
	 */
	@Override
	public void getExtents(Consumer<Vector3fc> output)
	{
		for (var extent : _extents)
			output.accept(extent);
	}

	/**
	 * This leaf extracts context-aware arguments in update rather than this fallback hook.
	 */
	@Override
	public Argument extractArgument(ItemStack item)
	{
		return new Argument(_renderer.restPose(), new int[0]);
	}

	/**
	 * Collects just node-bound corners, not every mesh vertex on each frame.
	 */
	private Vector3fc[] extents(Matrix4fc[] matrices)
	{
		var points = new ArrayList<Vector3fc>();
		_renderer.extents(matrices, points::add);
		return points.toArray(Vector3fc[]::new);
	}
}
