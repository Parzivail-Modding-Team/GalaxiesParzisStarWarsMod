package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.pswg.model.g3d.G3dModel;
import dev.pswg.model.g3d.G3dPose;
import dev.pswg.rendering.ptex.PtexTextureSpec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * The native submission adapter for entities, projectiles, block entities,
 * and posed or sampled items.
 */
public final class G3dRenderer
{
	/**
	 * Renderer-free geometry shared across instances.
	 */
	private final G3dModel _model;

	/**
	 * Ptex graphs resolved once per reload.
	 */
	private final PtexTextureSpec[] _textures;

	/**
	 * Shared read-only rest transforms.
	 */
	private final Matrix4fc[] _rest;

	/**
	 * Creates a renderer from a reload-bound geometry adapter. No GPU work occurs.
	 */
	public G3dRenderer(G3dGeometry geometry)
	{
		_model = geometry.model();

		_textures = new PtexTextureSpec[_model.materials().size()];
		for (int index = 0; index < _textures.length; index++)
			_textures[index] = geometry.texture(_model.materials().get(index).texture()).graph();

		_rest = new G3dPose(_model.rig()).snapshot();
	}

	/**
	 * Gets common model data for render-state pose extraction.
	 */
	public G3dModel model()
	{
		return _model;
	}

	/**
	 * Gets the shared read-only rest matrices. Do not edit them.
	 */
	public Matrix4fc[] restPose()
	{
		return _rest;
	}

	/**
	 * Emits posed node-bound corners for native item bounds and inventory framing.
	 */
	public void extents(Matrix4fc[] matrices, Consumer<Vector3fc> output)
	{
		for (int index = 0; index < matrices.length; index++)
		{
			var bounds = _model.rig().nodes().get(index).bounds();
			for (int corner = 0; corner < 8; corner++)
			{
				var position = new Vector3f(
						(corner & 1) == 0 ? bounds.min().x() : bounds.max().x(),
						(corner & 2) == 0 ? bounds.min().y() : bounds.max().y(),
						(corner & 4) == 0 ? bounds.min().z() : bounds.max().z()
				);
				output.accept(matrices[index].transformPosition(position));
			}
		}
	}

	/**
	 * Queues geometry using captured pose matrices. Pass a G3dPose snapshot during
	 * extraction; it must not be edited after submission. The item flag selects
	 * the material's item layers, while other consumers select entity layers.
	 */
	public void submit(
			Matrix4fc[] matrices,
			PoseStack stack,
			SubmitNodeCollector collector,
			int light,
			int overlay,
			int color,
			int[] tints,
			boolean item,
			boolean foil,
			int outline
	)
	{
		for (var mesh : _model.meshes())
		{
			var material = _model.materials().get(mesh.material());
			var texture = _textures[mesh.material()].getOrElse(MissingTextureAtlasSprite.getLocation());
			var layerId = item ? material.layers().item() : material.layers().entity();
			var type = G3dLayers.sampled(layerId, texture, material.doubleSided(), foil);
			boolean backFaces = G3dLayers.needsBackFaces(layerId, material.doubleSided());
			int tint = material.tintIndex() >= 0 && material.tintIndex() < tints.length ? tints[material.tintIndex()] : color;
			int lit = LightCoordsUtil.lightCoordsWithEmission(light, material.lightEmission());

			stack.pushPose();
			stack.mulPose(matrices[mesh.node()]);

			collector.submitCustomGeometry(stack, type, (pose, buffer) -> emit(mesh, pose, buffer, tint, lit, overlay, backFaces));

			if (outline != 0 && type.outline().isPresent())
				collector.submitCustomGeometry(stack, type.outline().get(), (pose, buffer) -> emit(mesh, pose, buffer, outline, lit, overlay, backFaces));

			stack.popPose();
		}
	}

	/**
	 * Native entity render types consume quads, so the last triangle vertex is repeated.
	 */
	private static void emit(G3dModel.Mesh mesh, PoseStack.Pose pose, VertexConsumer buffer, int color, int light, int overlay, boolean doubleSided)
	{
		for (int triangle = 0; triangle < mesh.indexCount(); triangle += 3)
		{
			for (int side = 0; side < (doubleSided ? 2 : 1); side++)
			{
				for (int corner = 0; corner < 4; corner++)
				{
					int source = side == 0 ? Math.min(corner, 2) : 2 - Math.min(corner, 2);
					int vertex = mesh.index(triangle + source);
					float normalSign = side == 0 ? 1 : -1;
					buffer.addVertex(pose, mesh.component(vertex, 0), mesh.component(vertex, 1), mesh.component(vertex, 2))
					      .setColor(color)
					      .setUv(mesh.component(vertex, 6), mesh.component(vertex, 7))
					      .setOverlay(overlay)
					      .setLight(light)
					      .setNormal(pose, mesh.component(vertex, 3) * normalSign, mesh.component(vertex, 4) * normalSign, mesh.component(vertex, 5) * normalSign);
				}
			}
		}
	}
}
