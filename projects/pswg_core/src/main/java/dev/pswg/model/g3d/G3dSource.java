package dev.pswg.model.g3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Optional;

/**
 * The canonical .jg3d document. Names are local to this model; resource and
 * render-layer references use Minecraft identifiers. Lists retain author order.
 *
 * @param version   The source schema version.
 * @param materials The surfaces used by the meshes.
 * @param nodes     The rigid transform hierarchy.
 * @param meshes    Indexed triangles with positions in sixteenths of a block.
 * @param sockets   Named attachment points.
 */
public record G3dSource(int version, List<Material> materials, List<Node> nodes, List<Mesh> meshes,
                        List<Socket> sockets)
{
	/**
	 * Native layer choices. Missing choices mean cutout for that target.
	 *
	 * @param block  The block layer, such as minecraft:block/cutout.
	 * @param item   The item layer, such as minecraft:item/cutout.
	 * @param entity The entity layer, such as minecraft:entity/cutout.
	 */
	public record Layers(Identifier block, Identifier item, Identifier entity)
	{
		/**
		 * Default native layers.
		 */
		public static final Layers DEFAULT = new Layers(
				Identifier.withDefaultNamespace("block/cutout"),
				Identifier.withDefaultNamespace("item/cutout"),
				Identifier.withDefaultNamespace("entity/cutout")
		);

		/**
		 * Layer identifiers are validated by Minecraft itself.
		 */
		public static final Codec<Layers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Identifier.CODEC.optionalFieldOf("block", DEFAULT.block()).forGetter(Layers::block),
				Identifier.CODEC.optionalFieldOf("item", DEFAULT.item()).forGetter(Layers::item),
				Identifier.CODEC.optionalFieldOf("entity", DEFAULT.entity()).forGetter(Layers::entity)
		).apply(instance, Layers::new));
	}

	/**
	 * A target-neutral surface. The texture names a Ptex graph or a direct
	 * assets/.../textures/... image resource.
	 *
	 * @param id            The local material name.
	 * @param texture       A Ptex definition or direct texture resource identifier.
	 * @param layers        Native render-layer references.
	 * @param tintIndex     The vanilla tint source index, or -1 for no tint.
	 * @param lightEmission Minimum light level from 0 to 15.
	 * @param doubleSided   Whether both sides should be visible.
	 */
	public record Material(String id, Identifier texture, Layers layers, int tintIndex, int lightEmission,
	                       boolean doubleSided)
	{
		/**
		 * Surface metadata shared by source and compiled files.
		 */
		public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(Material::id),
				Identifier.CODEC.fieldOf("texture").forGetter(Material::texture),
				Layers.CODEC.optionalFieldOf("layers", Layers.DEFAULT).forGetter(Material::layers),
				Codec.intRange(-1, 255).optionalFieldOf("tintIndex", -1).forGetter(Material::tintIndex),
				Codec.intRange(0, 15).optionalFieldOf("lightEmission", 0).forGetter(Material::lightEmission),
				Codec.BOOL.optionalFieldOf("doubleSided", false).forGetter(Material::doubleSided)
		).apply(instance, Material::new));
	}

	/**
	 * A rigid group. Pose overrides replace its local rest transform.
	 *
	 * @param id            The stable local node name.
	 * @param parent        The parent name, absent for a root.
	 * @param restTransform The transform relative to the parent.
	 * @param meshes        Mesh names drawn by this group.
	 */
	public record Node(String id, Optional<String> parent, G3dTransform restTransform, List<String> meshes)
	{
		/**
		 * Named hierarchy codec.
		 */
		public static final Codec<Node> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(Node::id),
				Codec.STRING.optionalFieldOf("parent").forGetter(Node::parent),
				G3dTransform.CODEC.optionalFieldOf("restTransform", G3dTransform.IDENTITY).forGetter(Node::restTransform),
				Codec.STRING.listOf().optionalFieldOf("meshes", List.of()).forGetter(Node::meshes)
		).apply(instance, Node::new));
	}

	/**
	 * An indexed vertex. UVs run from the image's top left and need not be clamped.
	 *
	 * @param position The position in source units.
	 * @param normal   The outward normal.
	 * @param uv       The texture coordinates.
	 */
	public record Vertex(Vector3fc position, Vector3fc normal, Vector2fc uv)
	{
		/**
		 * Uses vanilla JOML codecs rather than private vector formats.
		 */
		public static final Codec<Vertex> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.VECTOR3F.fieldOf("position").forGetter(Vertex::position),
				ExtraCodecs.VECTOR3F.fieldOf("normal").forGetter(Vertex::normal),
				ExtraCodecs.VECTOR2F.fieldOf("uv").forGetter(Vertex::uv)
		).apply(instance, Vertex::new));
	}

	/**
	 * One material's indexed triangles. The exporter triangulates source polygons.
	 *
	 * @param id       The local mesh name.
	 * @param material The local material name.
	 * @param vertices The indexed vertex table.
	 * @param indices  Zero-based indices, three per triangle.
	 */
	public record Mesh(String id, String material, List<Vertex> vertices, List<Integer> indices)
	{
		/**
		 * Indexed source geometry codec.
		 */
		public static final Codec<Mesh> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(Mesh::id),
				Codec.STRING.fieldOf("material").forGetter(Mesh::material),
				Vertex.CODEC.listOf().fieldOf("vertices").forGetter(Mesh::vertices),
				Codec.INT.listOf().fieldOf("indices").forGetter(Mesh::indices)
		).apply(instance, Mesh::new));
	}

	/**
	 * An attachment point that both the client and server can evaluate.
	 *
	 * @param id             The local socket name.
	 * @param node           The group carrying this socket.
	 * @param localTransform The transform relative to that group.
	 */
	public record Socket(String id, String node, G3dTransform localTransform)
	{
		/**
		 * Named source socket codec.
		 */
		public static final Codec<Socket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(Socket::id),
				Codec.STRING.fieldOf("node").forGetter(Socket::node),
				G3dTransform.CODEC.optionalFieldOf("localTransform", G3dTransform.IDENTITY).forGetter(Socket::localTransform)
		).apply(instance, Socket::new));
	}

	/**
	 * V1 accepts additional fields so exporters can keep their own metadata.
	 * Client datagen reads the optional vanilla "model" sidecar separately;
	 * display placement does not change compiled geometry or the shared rig hash.
	 */
	public static final Codec<G3dSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.intRange(1, 1).fieldOf("version").forGetter(G3dSource::version),
			Material.CODEC.listOf().fieldOf("materials").forGetter(G3dSource::materials),
			Node.CODEC.listOf().fieldOf("nodes").forGetter(G3dSource::nodes),
			Mesh.CODEC.listOf().fieldOf("meshes").forGetter(G3dSource::meshes),
			Socket.CODEC.listOf().optionalFieldOf("sockets", List.of()).forGetter(G3dSource::sockets)
	).apply(instance, G3dSource::new));
}
