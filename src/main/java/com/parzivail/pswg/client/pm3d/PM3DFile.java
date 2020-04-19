package com.parzivail.pswg.client.pm3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.brotli.BrotliInputStream;
import com.parzivail.util.binary.BinaryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PM3DFile
{
	private static final String MAGIC = "Pm3D";
	private static final int ACCEPTED_VERSION = 0x02;

	public final Identifier identifier;
	public final ArrayList<Vec3d> verts;
	public final ArrayList<Vec3d> normals;
	public final ArrayList<Vec2f> uvs;
	public final ArrayList<PM3DObject> objects;

	public PM3DFile(Identifier identifier, ArrayList<Vec3d> verts, ArrayList<Vec3d> normals, ArrayList<Vec2f> uvs, ArrayList<PM3DObject> objects)
	{
		this.identifier = identifier;
		this.verts = verts;
		this.normals = normals;
		this.uvs = uvs;
		this.objects = objects;
	}

	public static PM3DFile tryLoad(Identifier modelFile)
	{
		try
		{
			return load(modelFile);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			CrashReport crashReport = CrashReport.create(ex, String.format("Loading PM3D file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	private static PM3DFile load(Identifier modelFile) throws IOException
	{
		InputStream reader = MinecraftClient.getInstance().getResourceManager().getResource(modelFile).getInputStream();
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

		int numVerts = objStream.readInt();
		int numNormals = objStream.readInt();
		int numUvs = objStream.readInt();
		int numObjects = objStream.readInt();

		ArrayList<Vec3d> verts = loadVerts(numVerts, objStream);
		ArrayList<Vec3d> normals = loadVerts(numNormals, objStream);
		ArrayList<Vec2f> uvs = loadUvs(numUvs, objStream);
		ArrayList<PM3DObject> objects = loadObjects(numObjects, objStream);

		return new PM3DFile(modelFile, verts, normals, uvs, objects);
	}

	private static ArrayList<Vec3d> loadVerts(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<Vec3d> verts = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat();
			float y = objStream.readFloat();
			float z = objStream.readFloat();

			verts.add(new Vec3d(x, y, z));
		}

		return verts;
	}

	private static ArrayList<Vec2f> loadUvs(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<Vec2f> uvs = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float u = objStream.readFloat();
			float v = objStream.readFloat();

			uvs.add(new Vec2f(u, v));
		}

		return uvs;
	}

	private static ArrayList<PM3DObject> loadObjects(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<PM3DObject> objects = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			String objName = BinaryUtil.readNullTerminatedString(objStream);
			int numFaces = objStream.readInt();

			ArrayList<PM3DFace> faces = new ArrayList<>();

			for (int j = 0; j < numFaces; j++)
			{
				byte material = objStream.readByte();
				PM3DFace face = new PM3DFace(material);
				int numVerts = objStream.readInt();
				for (int k = 0; k < numVerts; k++)
				{
					int vertex = objStream.readInt();
					int normal = objStream.readInt();
					int texture = objStream.readInt();
					face.verts.add(new PM3DVertPointer(vertex, normal, texture));
				}

				faces.add(face);
			}

			objects.add(new PM3DObject(objName, faces));
		}

		return objects;
	}
}
