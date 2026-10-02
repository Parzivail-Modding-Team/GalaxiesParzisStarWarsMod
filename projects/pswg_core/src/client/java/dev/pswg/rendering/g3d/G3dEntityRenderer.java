package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.model.g3d.G3dPose;
import dev.pswg.model.g3d.G3dTransform;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * A module-friendly entity/projectile renderer with an extraction/submit boundary.
 * Subclasses supply gameplay pose inputs and a model-to-entity transform.
 *
 * @param <T> The registered entity type.
 */
public class G3dEntityRenderer<T extends Entity> extends EntityRenderer<T, G3dEntityRenderer.State>
{
	/**
	 * Only captured model data and matrices cross into the submit phase.
	 */
	public static class State extends EntityRenderState
	{
		/**
		 * The renderer selected from the current resource reload.
		 */
		public @Nullable G3dRenderer model;

		/**
		 * Read-only model-space node matrices.
		 */
		public Matrix4fc[] matrices = new Matrix4fc[0];

		/**
		 * The model-to-entity transform, copied during extraction.
		 */
		public final Matrix4f transform = new Matrix4f();

		/**
		 * Overall ARGB color for untinted surfaces.
		 */
		public int color = -1;
	}

	/**
	 * Shared empty tint input for entity submissions.
	 */
	private static final int[] NO_TINTS = new int[0];

	/**
	 * The reload-independent model resource identifier.
	 */
	private final Identifier _modelId;

	/**
	 * Creates a renderer for a registered module entity and a G3D model id.
	 */
	public G3dEntityRenderer(EntityRendererProvider.Context context, Identifier modelId)
	{
		super(context);
		_modelId = modelId;
	}

	/**
	 * Creates Minecraft's reusable render state.
	 */
	@Override
	public State createRenderState()
	{
		return new State();
	}

	/**
	 * Resolves reloaded assets and copies gameplay-derived poses before submission.
	 */
	@Override
	public void extractRenderState(T entity, State state, float tickDelta)
	{
		super.extractRenderState(entity, state, tickDelta);
		state.model = G3dClientModels.get(_modelId).orElse(null);
		state.transform.identity();
		state.color = -1;
		extractTransform(entity, state.transform, tickDelta);

		if (state.model == null)
		{
			state.matrices = new Matrix4fc[0];
			return;
		}

		var overrides = extractPose(entity, tickDelta);
		if (overrides.isEmpty())
			state.matrices = state.model.restPose();
		else
		{
			var pose = new G3dPose(state.model.model().rig());
			pose.evaluate(overrides);
			state.matrices = pose.snapshot();
		}
	}

	/**
	 * Uses captured values only; vanilla still submits names, leashes, and shadows.
	 */
	@Override
	public void submit(State state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera)
	{
		if (state.model != null)
		{
			stack.pushPose();
			stack.mulPose(state.transform);
			state.model.submit(
					state.matrices,
					stack,
					collector,
					state.lightCoords,
					OverlayTexture.NO_OVERLAY,
					state.color,
					NO_TINTS,
					false,
					false,
					state.outlineColor
			);
			stack.popPose();
		}
		super.submit(state, stack, collector, camera);
	}

	/**
	 * Override to return gameplay-visible local transforms, in blocks.
	 */
	protected Map<String, G3dTransform> extractPose(T entity, float tickDelta)
	{
		return Map.of();
	}

	/**
	 * Override to copy orientation, translation, or size into the render state.
	 */
	protected void extractTransform(T entity, Matrix4f output, float tickDelta)
	{
	}
}
