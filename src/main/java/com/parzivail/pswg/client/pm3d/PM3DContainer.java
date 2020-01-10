package com.parzivail.pswg.client.pm3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.brotli.BrotliInputStream;
import com.parzivail.util.binary.BinaryUtil;
import com.parzivail.util.primative.Vector2f;
import com.parzivail.util.primative.Vector3f;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class PM3DContainer
{
	private static final String MAGIC = "Pm3D";
	private static final int ACCEPTED_VERSION = 0x01;

	public final Identifier identifier;
	public final String credit;
	public final EnumSet<PM3DFlags> flags;
	public final HashMap<String, String> textures;
	public final ArrayList<Vector3f> verts;
	public final ArrayList<Vector3f> normals;
	public final ArrayList<Vector2f> uvs;
	public final ArrayList<PM3DObject> objects;

	public PM3DContainer(Identifier identifier, String credit, EnumSet<PM3DFlags> flags, HashMap<String, String> textures, ArrayList<Vector3f> verts, ArrayList<Vector3f> normals, ArrayList<Vector2f> uvs, ArrayList<PM3DObject> objects)
	{
		this.identifier = identifier;
		this.credit = credit;
		this.flags = flags;
		this.textures = textures;
		this.verts = verts;
		this.normals = normals;
		this.uvs = uvs;
		this.objects = objects;
	}

	public static PM3DContainer load(InputStream reader, Identifier identifier) throws IOException
	{
		BrotliInputStream bis = new BrotliInputStream(reader);
		LittleEndianDataInputStream objStream = new LittleEndianDataInputStream(bis);

		byte[] identBytes = new byte[MAGIC.length()];
		int read = objStream.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PM3D model");

		int version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		String credit = BinaryUtil.readNullTerminatedString(objStream);

		EnumSet<PM3DFlags> flags = EnumSet.of(PM3DFlags.None);

		byte byteFlags = objStream.readByte();

		if ((byteFlags & PM3DFlags.AmbientOcclusion.getFlag()) != 0)
			flags.add(PM3DFlags.AmbientOcclusion);

		int numTextures = objStream.readInt();
		int numVerts = objStream.readInt();
		int numNormals = objStream.readInt();
		int numUvs = objStream.readInt();
		int numObjects = objStream.readInt();

		HashMap<String, String> textures = loadTextures(numTextures, objStream);

		ArrayList<Vector3f> verts = loadVerts(numVerts, objStream);
		ArrayList<Vector3f> normals = loadVerts(numNormals, objStream);
		ArrayList<Vector2f> uvs = loadUvs(numUvs, objStream);
		ArrayList<PM3DObject> objects = loadObjects(numObjects, objStream);

		return new PM3DContainer(identifier, credit, flags, textures, verts, normals, uvs, objects);
	}

	private static HashMap<String, String> loadTextures(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		HashMap<String, String> textures = new HashMap<>();

		for (int i = 0; i < num; i++)
		{
			String key = BinaryUtil.readNullTerminatedString(objStream);
			String value = BinaryUtil.readNullTerminatedString(objStream);
			textures.put(key, value);
		}

		return textures;
	}

	private static ArrayList<Vector3f> loadVerts(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<Vector3f> verts = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat();
			float y = objStream.readFloat();
			float z = objStream.readFloat();

			verts.add(new Vector3f(x, y, z));
		}

		return verts;
	}

	private static ArrayList<Vector2f> loadUvs(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<Vector2f> uvs = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float u = objStream.readFloat();
			float v = objStream.readFloat();

			uvs.add(new Vector2f(u, v));
		}

		return uvs;
	}

	private static ArrayList<PM3DObject> loadObjects(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<PM3DObject> objects = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			String objName = BinaryUtil.readNullTerminatedString(objStream);
			String matName = BinaryUtil.readNullTerminatedString(objStream);
			int numFaces = objStream.readInt();

			ArrayList<PM3DFace> faces = new ArrayList<>();

			for (int j = 0; j < numFaces; j++)
			{
				PM3DFace face = new PM3DFace();
				int numVerts = objStream.readInt();
				for (int k = 0; k < numVerts; k++)
				{
					int vertex = objStream.readInt();
					int normal = objStream.readInt();
					int texture = objStream.readInt();
					face.verts.add(new Pm3dVertPointer(vertex, normal, texture));
				}

				faces.add(face);
			}

			objects.add(new PM3DObject(objName, matName, faces));
		}

		return objects;
	}
}
