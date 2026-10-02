package dev.pswg.rendering.g3d;

import dev.pswg.model.g3d.G3dModel;
import dev.pswg.model.g3d.G3dPose;
import dev.pswg.rendering.ptex.PtexDefinition;
import dev.pswg.rendering.ptex.SourceTexture;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.joml.*;

import java.lang.Math;
import java.util.Map;

/**
 * Bakes rigid rest geometry into vanilla quads, leaving batching to Minecraft.
 */
public final class G3dGeometry implements UnbakedGeometry
{
	/**
	 * Bakes one triangle, applying vanilla's blockstate rotation and UV-lock matrix.
	 */
	private static void addTriangle(
			QuadCollection.Builder result,
			G3dModel.Mesh mesh,
			int triangle,
			boolean reverse,
			Matrix4f transform,
			Matrix3fc restNormal,
			ModelState state,
			BakedQuad.MaterialInfo info,
			ModelBaker baker
	)
	{
		var positions = new Vector3fc[4];
		var uvs = new long[4];
		int first = mesh.index(triangle);
		var normal = restNormal.transform(
				mesh.component(first, 3),
				mesh.component(first, 4),
				mesh.component(first, 5),
				new Vector3f()
		);

		// UV lock starts from the face after the node's rest rotation, but before
		// the blockstate variant rotates the whole model around the block center.
		var originalFace = normal.isFinite() ? Direction.getApproximateNearest(normal.x, normal.y, normal.z) : Direction.UP;
		var uvTransform = state.inverseFaceTransformation(originalFace);
		for (int corner = 0; corner < 4; corner++)
		{
			int sourceCorner = Math.min(corner, 2);
			if (reverse)
				sourceCorner = 2 - sourceCorner;

			int vertex = mesh.index(triangle + sourceCorner);
			var position = new Vector3f(mesh.component(vertex, 0), mesh.component(vertex, 1), mesh.component(vertex, 2));
			transform.transformPosition(position);
			positions[corner] = baker.interner().vector(position);

			var uv = uvTransform.transformPosition(new Vector3f(mesh.component(vertex, 6) - 0.5f, mesh.component(vertex, 7) - 0.5f, 0));
			uvs[corner] = UVPair.pack(info.sprite().getU(uv.x + 0.5f), info.sprite().getV(uv.y + 0.5f));
		}

		GeometryUtils.normal(positions[0], positions[1], positions[2], normal);
		var direction = normal.isFinite() ? Direction.getApproximateNearest(normal.x, normal.y, normal.z) : Direction.UP;
		result.addUnculledFace(new BakedQuad(
				positions[0],
				positions[1],
				positions[2],
				positions[3],
				uvs[0],
				uvs[1],
				uvs[2],
				uvs[3],
				direction,
				info
		));
	}

	/**
	 * Common, renderer-independent model data.
	 */
	private final G3dModel _model;

	/**
	 * Texture definitions from this same resource-reload snapshot.
	 */
	private final Map<Identifier, PtexDefinition> _textures;

	/**
	 * Creates a geometry adapter bound to one reload's model and texture definitions.
	 */
	public G3dGeometry(G3dModel model, Map<Identifier, PtexDefinition> textures)
	{
		_model = model;
		_textures = textures;
	}

	/**
	 * Gets the common model for poses or custom submissions.
	 */
	public G3dModel model()
	{
		return _model;
	}

	/**
	 * Gets one Ptex graph; missing definitions are reported before baking.
	 */
	public PtexDefinition texture(Identifier id)
	{
		var result = _textures.get(id);
		if (result != null)
			return result;

		// Plain Minecraft texture resources need no wrapper document. Keep the
		// explicit Ptex definition lookup first so authored graphs take precedence.
		if (isDirectTexture(id))
			return new PtexDefinition(new SourceTexture(id), true);

		throw new IllegalArgumentException("Missing Ptex definition or direct texture resource " + id + " for " + _model.rig().id());
	}

	/**
	 * Gets the atlas sprite for a material, respecting explicit Ptex graphs first.
	 */
	private Identifier spriteId(Identifier id)
	{
		if (_textures.containsKey(id))
			return PtexDefinition.spriteId(id);

		if (!isDirectTexture(id))
			throw new IllegalArgumentException("Not a direct texture resource: " + id);

		var path = id.getPath();
		return Identifier.fromNamespaceAndPath(id.getNamespace(), path.substring("textures/".length(), path.length() - ".png".length()));
	}

	/**
	 * Tests whether an identifier points directly to an image in a resource pack.
	 */
	private static boolean isDirectTexture(Identifier id)
	{
		var path = id.getPath();
		return path.startsWith("textures/") && path.endsWith(".png") && path.length() > "textures/.png".length();
	}

	/**
	 * Whether every surface can use vanilla's static atlas path.
	 */
	public boolean atlasCapable()
	{
		return _model.materials().stream().allMatch(material -> texture(material.texture()).atlas());
	}

	/**
	 * Converts each triangle to a four-vertex face by repeating the last vertex.
	 * Faces are unculled: a named mesh does not imply a voxel boundary face.
	 */
	@Override
	public QuadCollection bake(TextureSlots slots, ModelBaker baker, ModelState state, ModelDebugName debugName)
	{
		var result = new QuadCollection.Builder();
		var pose = new G3dPose(_model.rig());
		var modelTransform = new Matrix4f().translation(0.5f, 0.5f, 0.5f)
		                                   .mul(state.transformation().getMatrix()).translate(-0.5f, -0.5f, -0.5f);

		for (var mesh : _model.meshes())
		{
			var surface = _model.materials().get(mesh.material());
			if (!texture(surface.texture()).atlas())
				throw new IllegalArgumentException("Sampled-only Ptex surface cannot be baked into a block: " + surface.texture());

			var nativeMaterial = baker.materials().get(new Material(spriteId(surface.texture())), debugName);

			var blockInfo = BakedQuad.MaterialInfo.of(
					nativeMaterial,
					G3dLayers.transparency(surface.layers().block()),
					surface.tintIndex(),
					null,
					surface.lightEmission()
			);

			var itemInfo = BakedQuad.MaterialInfo.of(
					nativeMaterial,
					G3dLayers.transparency(surface.layers().item()),
					surface.tintIndex(),
					null,
					surface.lightEmission()
			);

			var info = baker.interner().materialInfo(new BakedQuad.MaterialInfo(
					blockInfo.sprite(),
					blockInfo.layer(),
					itemInfo.itemRenderType(),
					itemInfo.itemGlintRenderType(),
					itemInfo.itemGlintSpecialRenderType(),
					surface.tintIndex(),
					null,
					surface.lightEmission()
			));

			var transform = modelTransform.mul(pose.nodeMatrix(mesh.node()), new Matrix4f());
			var restNormal = pose.nodeMatrix(mesh.node()).normal(new Matrix3f());

			for (int triangle = 0; triangle < mesh.indexCount(); triangle += 3)
			{
				addTriangle(result, mesh, triangle, false, transform, restNormal, state, info, baker);
				if (surface.doubleSided())
					addTriangle(result, mesh, triangle, true, transform, restNormal, state, info, baker);
			}
		}

		return result.build();
	}
}
