package dev.pswg.model.g3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A renderer-free rig. Nodes are stored parent-first for linear evaluation.
 */
public final class G3dRig
{
	/**
	 * A compiled rigid group with bounds in its local coordinates.
	 *
	 * @param id            The stable node name.
	 * @param parent        The earlier parent index, or -1 for a root.
	 * @param restTransform The local rest transform in blocks.
	 * @param bounds        Local rest-pose mesh bounds.
	 */
	public record Node(String id, int parent, G3dTransform restTransform, G3dBounds bounds)
	{
		/**
		 * Rig node metadata codec.
		 */
		public static final Codec<Node> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(Node::id),
				Codec.INT.fieldOf("parent").forGetter(Node::parent),
				G3dTransform.CODEC.fieldOf("restTransform").forGetter(Node::restTransform),
				G3dBounds.CODEC.fieldOf("bounds").forGetter(Node::bounds)
		).apply(instance, Node::new));
	}

	/**
	 * A compiled attachment point.
	 *
	 * @param id             The stable socket name.
	 * @param node           The owning node index.
	 * @param localTransform The local transform in blocks.
	 */
	public record Socket(String id, int node, G3dTransform localTransform)
	{
		/**
		 * Rig socket metadata codec.
		 */
		public static final Codec<Socket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(Socket::id),
				Codec.INT.fieldOf("node").forGetter(Socket::node),
				G3dTransform.CODEC.fieldOf("localTransform").forGetter(Socket::localTransform)
		).apply(instance, Socket::new));
	}

	/**
	 * Shared by the client visual file and the server's rig-only file.
	 */
	public static final Codec<G3dRig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("id").forGetter(G3dRig::id),
			Codec.STRING.fieldOf("sourceHash").forGetter(G3dRig::sourceHash),
			Node.CODEC.listOf().fieldOf("nodes").forGetter(G3dRig::nodes),
			Socket.CODEC.listOf().fieldOf("sockets").forGetter(G3dRig::sockets),
			G3dBounds.CODEC.fieldOf("bounds").forGetter(G3dRig::bounds)
	).apply(instance, G3dRig::new));

	/**
	 * The resource identifier, shared by both projections.
	 */
	private final Identifier _id;

	/**
	 * Hash of the canonical source document.
	 */
	private final String _sourceHash;

	/**
	 * Parent-first transform table.
	 */
	private final List<Node> _nodes;

	/**
	 * Stable socket table.
	 */
	private final List<Socket> _sockets;

	/**
	 * Model-space rest bounds.
	 */
	private final G3dBounds _bounds;

	/**
	 * Node lookup built once at load time.
	 */
	private final Map<String, Integer> _nodeIndices = new HashMap<>();

	/**
	 * Socket lookup built once at load time.
	 */
	private final Map<String, Socket> _socketNames = new HashMap<>();

	/**
	 * Builds the lookup tables and checks references needed for safe evaluation.
	 */
	public G3dRig(Identifier id, String sourceHash, List<Node> nodes, List<Socket> sockets, G3dBounds bounds)
	{
		_id = id;
		_sourceHash = sourceHash;
		_nodes = List.copyOf(nodes);
		_sockets = List.copyOf(sockets);
		_bounds = bounds;

		for (int index = 0; index < nodes.size(); index++)
		{
			var node = nodes.get(index);
			if (node.parent() < -1 || node.parent() >= index)
				throw new IllegalArgumentException("Node parent must precede " + node.id());

			if (_nodeIndices.put(node.id(), index) != null)
				throw new IllegalArgumentException("Duplicate node " + node.id());
		}

		for (var socket : sockets)
		{
			if (socket.node() < 0 || socket.node() >= nodes.size())
				throw new IllegalArgumentException("Missing node for socket " + socket.id());

			if (_socketNames.put(socket.id(), socket) != null)
				throw new IllegalArgumentException("Duplicate socket " + socket.id());
		}
	}

	/**
	 * Gets the model resource identifier.
	 */
	public Identifier id()
	{
		return _id;
	}

	/**
	 * Gets the source hash used to compare client and server projections.
	 */
	public String sourceHash()
	{
		return _sourceHash;
	}

	/**
	 * Gets the read-only, parent-first node table.
	 */
	public List<Node> nodes()
	{
		return _nodes;
	}

	/**
	 * Gets the read-only socket table.
	 */
	public List<Socket> sockets()
	{
		return _sockets;
	}

	/**
	 * Gets the model-space rest bounds.
	 */
	public G3dBounds bounds()
	{
		return _bounds;
	}

	/**
	 * Finds a node index, or -1 when the name is absent.
	 */
	public int nodeIndex(String name)
	{
		return _nodeIndices.getOrDefault(name, -1);
	}

	/**
	 * Finds a socket, reporting a missing gameplay attachment clearly.
	 */
	public Socket socket(String name)
	{
		var socket = _socketNames.get(name);
		if (socket == null)
			throw new IllegalArgumentException("Missing socket " + name + " in " + _id);

		return socket;
	}
}
