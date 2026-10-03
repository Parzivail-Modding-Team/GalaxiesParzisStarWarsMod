package dev.pswg.model.g3d;

import java.util.List;

/**
 * A compiled visual model. It contains CPU geometry only; Minecraft owns
 * vertex formats, buffers, shaders, and uploads.
 *
 * @param rig       The common transform and socket projection.
 * @param materials The surface table.
 * @param meshes    Rigid mesh instances, each attached to one node.
 */
public record G3dModel(G3dRig rig, List<G3dSource.Material> materials, List<Mesh> meshes)
{
	/**
	 * Packed, indexed CPU geometry. Arrays belong to this mesh after construction.
	 */
	public static final class Mesh
	{
		/**
		 * Floats per vertex: position, normal, then UV.
		 */
		public static final int STRIDE = 8;

		/**
		 * The owning node index.
		 */
		private final int _node;

		/**
		 * The material index.
		 */
		private final int _material;

		/**
		 * Interleaved vertex values.
		 */
		private final float[] _vertices;

		/**
		 * Three indices per triangle.
		 */
		private final int[] _indices;

		/**
		 * Takes ownership of validated packed geometry without copying it.
		 */
		public Mesh(int node, int material, float[] vertices, int[] indices)
		{
			_node = node;
			_material = material;
			_vertices = vertices;
			_indices = indices;

			if (vertices.length % STRIDE != 0 || indices.length % 3 != 0)
				throw new IllegalArgumentException("Incomplete vertex or triangle");

			for (int index : indices)
			{
				if (index < 0 || index >= vertexCount())
					throw new IllegalArgumentException("Vertex index out of range: " + index);
			}
		}

		/**
		 * Gets the owning node index.
		 */
		public int node()
		{
			return _node;
		}

		/**
		 * Gets the surface index.
		 */
		public int material()
		{
			return _material;
		}

		/**
		 * Gets the number of indexed vertices.
		 */
		public int vertexCount()
		{
			return _vertices.length / STRIDE;
		}

		/**
		 * Gets the number of triangle indices.
		 */
		public int indexCount()
		{
			return _indices.length;
		}

		/**
		 * Reads one triangle index without exposing the backing array.
		 */
		public int index(int index)
		{
			return _indices[index];
		}

		/**
		 * Reads a vertex component: xyz, normal xyz, or uv.
		 */
		public float component(int vertex, int component)
		{
			return _vertices[vertex * STRIDE + component];
		}
	}

	/**
	 * Freezes tables and checks the references used by render adapters.
	 */
	public G3dModel
	{
		materials = List.copyOf(materials);
		meshes = List.copyOf(meshes);

		for (var mesh : meshes)
		{
			if (mesh.node() < 0 || mesh.node() >= rig.nodes().size() || mesh.material() < 0 || mesh.material() >= materials.size())
				throw new IllegalArgumentException("Mesh has a missing node or material");
		}
	}
}
