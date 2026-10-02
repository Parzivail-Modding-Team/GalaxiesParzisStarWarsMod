package dev.pswg.model.g3d;

import com.google.common.hash.Hashing;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * Compiles source data once.
 */
public final class G3dCompiler
{
	/**
	 * Resolves names, orders parents before children, converts to blocks, and
	 * computes bounds.
	 */
	public static G3dModel compile(Identifier id, G3dSource source)
	{
		if (source.version() != 1)
			throw new IllegalArgumentException("Unsupported G3D source version " + source.version());

		var materials = names(source.materials(), G3dSource.Material::id);
		var meshes = names(source.meshes(), G3dSource.Mesh::id);
		var nodes = names(source.nodes(), G3dSource.Node::id);
		names(source.sockets(), G3dSource.Socket::id);
		var ordered = orderNodes(source.nodes(), nodes);
		var compiledNodes = new ArrayList<G3dRig.Node>();
		var compiledMeshes = new ArrayList<G3dModel.Mesh>();
		var compiledSockets = new ArrayList<G3dRig.Socket>();
		var indices = new HashMap<String, Integer>();
		long geometryBytes = 4;

		for (var node : ordered)
		{
			int nodeIndex = compiledNodes.size();
			indices.put(node.id(), nodeIndex);

			var min = new Vector3f(Float.POSITIVE_INFINITY);
			var max = new Vector3f(Float.NEGATIVE_INFINITY);
			boolean hasVertices = false;

			for (var meshName : node.meshes())
			{
				var mesh = source.meshes().get(reference(meshes, meshName));
				int material = reference(materials, mesh.material());

				// Reusing a mesh on many nodes must not amplify a small source into
				// unbounded allocations. The same limit is used by the file reader.
				geometryBytes += 16L + (long)mesh.vertices().size() * G3dModel.Mesh.STRIDE * 4 + (long)mesh.indices().size() * 4;
				if (geometryBytes > G3dFiles.MAX_SECTION_BYTES)
					throw new IllegalArgumentException("Compiled G3D geometry exceeds the V1 byte limit");

				var values = new float[Math.multiplyExact(mesh.vertices().size(), G3dModel.Mesh.STRIDE)];
				int offset = 0;

				for (var vertex : mesh.vertices())
				{
					if (!vertex.position().isFinite() || !vertex.normal().isFinite() || !vertex.uv().isFinite())
						throw new IllegalArgumentException("Non-finite vertex in " + mesh.id());

					var position = new Vector3f(vertex.position()).mul(1 / 16f);
					min.min(position);
					max.max(position);
					hasVertices = true;

					values[offset++] = position.x;
					values[offset++] = position.y;
					values[offset++] = position.z;
					values[offset++] = vertex.normal().x();
					values[offset++] = vertex.normal().y();
					values[offset++] = vertex.normal().z();
					values[offset++] = vertex.uv().x();
					values[offset++] = vertex.uv().y();
				}

				compiledMeshes.add(new G3dModel.Mesh(nodeIndex, material, values, mesh.indices().stream().mapToInt(Integer::intValue).toArray()));
			}

			compiledNodes.add(new G3dRig.Node(
					node.id(),
					node.parent().map(indices::get).orElse(-1),
					node.restTransform().compile(),
					hasVertices ? new G3dBounds(min, max) : G3dBounds.EMPTY
			));
		}
		for (var socket : source.sockets())
		{
			compiledSockets.add(new G3dRig.Socket(
					socket.id(),
					reference(indices, socket.node()),
					socket.localTransform().compile()
			));
		}

		// Codec output has a fixed field order and strips exporter-only fields.
		var canonical = G3dSource.CODEC.encodeStart(JsonOps.INSTANCE, source).getOrThrow().toString();
		var hash = Hashing.sha256().hashString(canonical, StandardCharsets.UTF_8).toString();
		var rig = new G3dRig(id, hash, compiledNodes, compiledSockets, G3dBounds.EMPTY);
		var pose = new G3dPose(rig);
		var min = new Vector3f(Float.POSITIVE_INFINITY);
		var max = new Vector3f(Float.NEGATIVE_INFINITY);
		var position = new Vector3f();
		boolean hasVertices = false;

		for (var mesh : compiledMeshes)
		{
			for (int vertex = 0; vertex < mesh.vertexCount(); vertex++)
			{
				position.set(mesh.component(vertex, 0), mesh.component(vertex, 1), mesh.component(vertex, 2));
				pose.nodeMatrix(mesh.node()).transformPosition(position);
				min.min(position);
				max.max(position);
				hasVertices = true;
			}
		}

		rig = new G3dRig(id, hash, compiledNodes, compiledSockets, hasVertices ? new G3dBounds(min, max) : G3dBounds.EMPTY);
		return new G3dModel(rig, source.materials(), compiledMeshes);
	}

	/**
	 * Makes a local name table once, catching ambiguous references.
	 */
	private static <T> Map<String, Integer> names(List<T> values, Function<T, String> name)
	{
		var result = new HashMap<String, Integer>();
		for (int index = 0; index < values.size(); index++)
		{
			var key = name.apply(values.get(index));
			if (result.put(key, index) != null)
				throw new IllegalArgumentException("Duplicate name " + key);
		}
		return result;
	}

	/**
	 * Resolves a reference without a later null unboxing or array error.
	 */
	private static int reference(Map<String, Integer> names, String name)
	{
		var index = names.get(name);
		if (index == null)
			throw new IllegalArgumentException("Missing reference " + name);

		return index;
	}

	/**
	 * Orders the hierarchy iteratively, so a deep source cannot overflow the stack.
	 */
	private static List<G3dSource.Node> orderNodes(List<G3dSource.Node> nodes, Map<String, Integer> names)
	{
		var children = new ArrayList<List<Integer>>();
		var ready = new ArrayDeque<Integer>();

		for (int index = 0; index < nodes.size(); index++)
			children.add(new ArrayList<>());

		for (int index = 0; index < nodes.size(); index++)
		{
			var parent = nodes.get(index).parent();
			if (parent.isEmpty())
				ready.add(index);
			else
				children.get(reference(names, parent.get())).add(index);
		}

		var ordered = new ArrayList<G3dSource.Node>();

		while (!ready.isEmpty())
		{
			int index = ready.remove();
			ordered.add(nodes.get(index));
			ready.addAll(children.get(index));
		}

		if (ordered.size() != nodes.size())
			throw new IllegalArgumentException("Node hierarchy contains a parent cycle");

		return ordered;
	}

	/**
	 * Prevents construction of this utility class.
	 */
	private G3dCompiler()
	{
	}
}
