package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.pswg.model.g3d.G3dModel;
import dev.pswg.model.g3d.G3dPose;
import dev.pswg.rendering.ptex.PtexTextureSpec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The native submission adapter for entities, projectiles, block entities,
 * and posed or sampled items.
 */
public final class G3dRenderer
{
	/**
	 * Native consumer whose layer and glint conventions apply to a submission.
	 */
	private enum Target
	{
		ENTITY,
		ITEM,
		ARMOR
	}

	/**
	 * Renderer-free geometry shared across instances.
	 */
	private final G3dModel _model;

	/**
	 * Ptex graphs resolved once per reload.
	 */
	private final PtexTextureSpec[] _textures;

	/**
	 * Native atlas regions from this reload. Null entries use sampled-only graphs.
	 */
	private final TextureAtlasSprite[] _sprites;

	/**
	 * Shared read-only rest transforms.
	 */
	private final Matrix4fc[] _rest;

	/**
	 * Only nodes with geometry contribute to visible item bounds.
	 */
	private final int[] _geometryNodes;

	/**
	 * Creates a sampler-only renderer when no model baker is available. Use the
	 * baker overload for atlas animation, or obtain a renderer from G3dClientModels.
	 */
	public G3dRenderer(G3dGeometry geometry)
	{
		this(geometry, null);
	}

	/**
	 * Captures atlas sprites during baking so vanilla supplies the current frame.
	 * Animated source PNGs contain a frame strip; their model UVs address one frame,
	 * not that complete image. Sampled-only graphs keep their existing texture path.
	 */
	public G3dRenderer(G3dGeometry geometry, @Nullable ModelBaker baker)
	{
		this(geometry, baker, TextureSlots.EMPTY);
	}

	/**
	 * Binds one immutable consumer texture view while retaining the shared geometry.
	 */
	public G3dRenderer(G3dGeometry geometry, @Nullable ModelBaker baker, TextureSlots slots)
	{
		_model = geometry.model();

		_textures = new PtexTextureSpec[_model.materials().size()];
		_sprites = new TextureAtlasSprite[_textures.length];
		for (int index = 0; index < _textures.length; index++)
		{
			var reference = _model.materials().get(index).texture();
			var material = geometry.material(reference, slots);
			var definition = geometry.texture(reference, slots);
			_textures[index] = definition.graph();
			if (baker != null && definition.atlas())
				_sprites[index] = baker.materials().get(
						material,
						() -> _model.rig().id().toString()
				).sprite();
		}

		_rest = new G3dPose(_model.rig()).snapshot();
		_geometryNodes = _model.meshes().stream().mapToInt(G3dModel.Mesh::node).distinct().toArray();
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
		for (int index : _geometryNodes)
		{
			if (G3dPose.isCollapsed(matrices[index]))
				continue;
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
		submit(matrices, stack, collector, light, overlay, color, tints, item, foil, outline, true);
	}

	/**
	 * Queues visible surfaces and optional outlines. Invisible entities can keep
	 * their glowing outline without submitting the normal body.
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
			int outline,
			boolean renderBody
	)
	{
		submit(matrices, stack, collector, light, overlay, color, tints, item ? Target.ITEM : Target.ENTITY, foil, outline, renderBody);
	}

	/**
	 * Submits worn geometry with native armor cutout/glint layers and no hurt
	 * overlay.
	 */
	public void submitArmor(
			Matrix4fc[] matrices,
			PoseStack stack,
			SubmitNodeCollector collector,
			int light,
			int[] tints,
			boolean foil,
			int outline
	)
	{
		submit(matrices, stack, collector, light, OverlayTexture.NO_OVERLAY, -1, tints, Target.ARMOR, foil, outline, true);
	}

	/**
	 * Shared vertex submission for native entity, item, and armor consumers.
	 */
	private void submit(
			Matrix4fc[] matrices,
			PoseStack stack,
			SubmitNodeCollector collector,
			int light,
			int overlay,
			int color,
			int[] tints,
			Target target,
			boolean foil,
			int outline,
			boolean renderBody
	)
	{
		for (var mesh : _model.meshes())
		{
			if (G3dPose.isCollapsed(matrices[mesh.node()]))
				continue;
			var material = _model.materials().get(mesh.material());
			var sprite = _sprites[mesh.material()];
			var texture = sprite == null
					? _textures[mesh.material()].getOrElse(MissingTextureAtlasSprite.getLocation())
					: sprite.atlasLocation();
			var layerId = target == Target.ITEM ? material.layers().item() : material.layers().entity();
			var type = target == Target.ARMOR
					? G3dLayers.armor(layerId, texture, material.doubleSided(), foil)
					: G3dLayers.sampled(layerId, texture, material.doubleSided(), foil);
			boolean backFaces = G3dLayers.needsBackFaces(layerId, material.doubleSided());
			int tint = material.tintIndex() >= 0 && material.tintIndex() < tints.length ? tints[material.tintIndex()] : color;
			int lit = LightCoordsUtil.lightCoordsWithEmission(light, material.lightEmission());

			stack.pushPose();
			try
			{
				stack.mulPose(matrices[mesh.node()]);
				if (renderBody)
					collector.submitCustomGeometry(stack, type, (pose, buffer) -> emit(mesh, pose, buffer, tint, lit, overlay, backFaces, sprite));
				if (outline != 0 && type.outline().isPresent())
					collector.submitCustomGeometry(stack, type.outline().get(), (pose, buffer) -> emit(mesh, pose, buffer, outline, lit, overlay, backFaces, sprite));
			}
			finally
			{
				stack.popPose();
			}
		}
	}

	/**
	 * Native entity render types consume quads, so the last triangle vertex is repeated.
	 */
	private static void emit(
			G3dModel.Mesh mesh,
			PoseStack.Pose pose,
			VertexConsumer buffer,
			int color,
			int light,
			int overlay,
			boolean doubleSided,
			@Nullable TextureAtlasSprite sprite
	)
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
					float u = mesh.component(vertex, 6);
					float v = mesh.component(vertex, 7);
					buffer.addVertex(pose, mesh.component(vertex, 0), mesh.component(vertex, 1), mesh.component(vertex, 2))
					      .setColor(color)
					      .setUv(sprite == null ? u : sprite.getU(u), sprite == null ? v : sprite.getV(v))
					      .setOverlay(overlay)
					      .setLight(light)
					      .setNormal(pose, mesh.component(vertex, 3) * normalSign, mesh.component(vertex, 4) * normalSign, mesh.component(vertex, 5) * normalSign);
				}
			}
		}
	}
}
