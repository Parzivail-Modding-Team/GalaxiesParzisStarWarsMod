package dev.pswg.model.g3d;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The G3D V1 container. Big-endian header and section lengths surround vanilla
 * NBT metadata and packed CPU geometry. No compression or renderer objects are
 * stored. A rig-only reader can skip visual sections without decoding them.
 */
public final class G3dFiles
{
	/**
	 * Encodes either a complete visual model or its server-readable projection.
	 */
	public static byte[] write(G3dModel model, boolean visual) throws IOException
	{
		var sections = new ArrayList<byte[]>();
		sections.add(metadata(G3dRig.CODEC, model.rig()));

		if (visual)
		{
			sections.add(metadata(G3dSource.Material.CODEC.listOf().fieldOf("materials").codec(), model.materials()));
			sections.add(geometry(model.meshes()));
		}

		var bytes = new ByteArrayOutputStream();
		try (var output = new DataOutputStream(bytes))
		{
			output.writeInt(MAGIC);
			output.writeShort(1);
			output.writeShort(0);
			output.writeInt(sections.size());

			for (int index = 0; index < sections.size(); index++)
			{
				byte[] section = sections.get(index);
				if (section.length > MAX_SECTION_BYTES)
					throw new IOException("G3D section exceeds the V1 byte limit");

				output.writeInt(index + 1);
				output.writeInt(section.length);
				output.write(section);
			}
		}

		if (bytes.size() > MAX_FILE_BYTES)
			throw new IOException("G3D file exceeds the V1 byte limit");

		return bytes.toByteArray();
	}

	/**
	 * Loads rig data only. Visual and unknown sections are skipped, not allocated.
	 */
	public static G3dRig readRig(InputStream input) throws IOException
	{
		return decodeMetadata(G3dRig.CODEC, required(readSections(input, false), RIG));
	}

	/**
	 * Loads a complete visual file, checking counts before allocating geometry.
	 */
	public static G3dModel readModel(InputStream input) throws IOException
	{
		try
		{
			var sections = readSections(input, true);
			var rig = decodeMetadata(G3dRig.CODEC, required(sections, RIG));
			var materials = decodeMetadata(G3dSource.Material.CODEC.listOf().fieldOf("materials").codec(), required(sections, MATERIALS));
			return new G3dModel(rig, materials, readGeometry(required(sections, MESHES)));
		}
		catch (IllegalArgumentException exception)
		{
			throw new IOException("Invalid G3D references: " + exception.getMessage(), exception);
		}
	}

	/**
	 * Reads a bounded section directory. Additive minor-version sections can be skipped.
	 */
	private static Map<Integer, byte[]> readSections(InputStream input, boolean visual) throws IOException
	{
		var stream = new DataInputStream(input);
		if (stream.readInt() != MAGIC)
			throw new IOException("Not a G3D file");

		int major = stream.readUnsignedShort();
		stream.readUnsignedShort();
		if (major != 1)
			throw new IOException("Unsupported G3D major version " + major);

		int count = stream.readInt();
		if (count < 1 || count > 64)
			throw new IOException("Invalid G3D section count");

		var result = new HashMap<Integer, byte[]>();
		var seen = new java.util.HashSet<Integer>();
		long total = 12;

		for (int index = 0; index < count; index++)
		{
			int tag = stream.readInt();
			int length = stream.readInt();
			total += 8L + length;

			if (length < 0 || length > MAX_SECTION_BYTES || total > MAX_FILE_BYTES || !seen.add(tag))
				throw new IOException("Invalid G3D section length or duplicate tag");

			if ((tag == RIG || tag == MATERIALS) && length > MAX_METADATA_BYTES)
				throw new IOException("G3D metadata exceeds the V1 byte limit");

			if (tag == RIG || (visual && (tag == MATERIALS || tag == MESHES)))
			{
				var bytes = stream.readNBytes(length);
				if (bytes.length != length)
					throw new IOException("Truncated G3D section");

				result.put(tag, bytes);
			}
			else
				stream.skipNBytes(length);
		}

		if (stream.read() != -1)
			throw new IOException("Unexpected bytes after G3D sections");

		return result;
	}

	/**
	 * Reports missing mandatory sections directly.
	 */
	private static byte[] required(Map<Integer, byte[]> sections, int tag) throws IOException
	{
		var result = sections.get(tag);
		if (result == null)
			throw new IOException("Missing G3D section " + tag);

		return result;
	}

	/**
	 * Writes metadata through the same Minecraft codec used by source JSON.
	 */
	private static <T> byte[] metadata(Codec<T> codec, T value) throws IOException
	{
		var tag = codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(IOException::new);
		if (!(tag instanceof CompoundTag compound))
			throw new IOException("G3D metadata must be a compound");

		var bytes = new ByteArrayOutputStream();
		NbtIo.write(compound, new DataOutputStream(bytes));
		if (bytes.size() > MAX_METADATA_BYTES)
			throw new IOException("G3D metadata exceeds the V1 byte limit");

		return bytes.toByteArray();
	}

	/**
	 * Uses vanilla's NBT allocation budget to bound nested or oversized metadata.
	 */
	private static <T> T decodeMetadata(Codec<T> codec, byte[] bytes) throws IOException
	{
		if (bytes.length > MAX_METADATA_BYTES)
			throw new IOException("G3D metadata exceeds the V1 byte limit");

		var input = new DataInputStream(new ByteArrayInputStream(bytes));
		var tag = NbtIo.read(input, NbtAccounter.create(MAX_METADATA_BYTES));
		if (input.available() != 0)
			throw new IOException("Unexpected bytes after G3D metadata");

		return codec.parse(NbtOps.INSTANCE, tag).getOrThrow(IOException::new);
	}

	/**
	 * Stores float values and indices without per-vertex object or NBT overhead.
	 */
	private static byte[] geometry(List<G3dModel.Mesh> meshes) throws IOException
	{
		var bytes = new ByteArrayOutputStream();
		var output = new DataOutputStream(bytes);
		output.writeInt(meshes.size());

		for (var mesh : meshes)
		{
			output.writeInt(mesh.node());
			output.writeInt(mesh.material());
			output.writeInt(mesh.vertexCount());
			output.writeInt(mesh.indexCount());

			for (int vertex = 0; vertex < mesh.vertexCount(); vertex++)
			{
				for (int component = 0; component < G3dModel.Mesh.STRIDE; component++)
					output.writeFloat(mesh.component(vertex, component));
			}

			for (int index = 0; index < mesh.indexCount(); index++)
				output.writeInt(mesh.index(index));
		}

		return bytes.toByteArray();
	}

	/**
	 * Counts must fit inside the remaining section before any array is allocated.
	 */
	private static List<G3dModel.Mesh> readGeometry(byte[] bytes) throws IOException
	{
		var input = new DataInputStream(new ByteArrayInputStream(bytes));
		int count = input.readInt();
		if (count < 0 || count > input.available() / 16)
			throw new IOException("Invalid G3D mesh count");

		var meshes = new ArrayList<G3dModel.Mesh>(count);
		for (int mesh = 0; mesh < count; mesh++)
		{
			int node = input.readInt();
			int material = input.readInt();
			int vertices = input.readInt();
			int indices = input.readInt();
			long size = (long)vertices * G3dModel.Mesh.STRIDE * 4 + (long)indices * 4;

			if (vertices < 0 || indices < 0 || indices % 3 != 0 || size > input.available())
				throw new IOException("Invalid G3D geometry count");

			var values = new float[vertices * G3dModel.Mesh.STRIDE];
			var triangles = new int[indices];

			for (int index = 0; index < values.length; index++)
			{
				values[index] = input.readFloat();
				if (!Float.isFinite(values[index]))
					throw new IOException("Non-finite G3D vertex");
			}

			for (int index = 0; index < triangles.length; index++)
				triangles[index] = input.readInt();

			meshes.add(new G3dModel.Mesh(node, material, values, triangles));
		}

		if (input.available() != 0)
			throw new IOException("Unexpected bytes after G3D geometry");

		return meshes;
	}

	/**
	 * ASCII G3D followed by a zero byte.
	 */
	private static final int MAGIC = 0x47334400;
	/**
	 * Bounds a file before allocations, including unknown future sections.
	 */
	private static final int MAX_FILE_BYTES = 128 * 1024 * 1024;
	/**
	 * Bounds any individual section.
	 */
	public static final int MAX_SECTION_BYTES = 64 * 1024 * 1024;
	/**
	 * NBT has its own allocation accounting in addition to the byte limit.
	 */
	private static final int MAX_METADATA_BYTES = 16 * 1024 * 1024;
	/**
	 * Rig metadata section tag.
	 */
	private static final int RIG = 1;
	/**
	 * Material metadata section tag.
	 */
	private static final int MATERIALS = 2;
	/**
	 * Packed geometry section tag.
	 */
	private static final int MESHES = 3;

	/**
	 * Prevents construction of this utility class.
	 */
	private G3dFiles()
	{
	}
}
