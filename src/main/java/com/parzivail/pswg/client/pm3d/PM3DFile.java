package com.parzivail.pswg.client.pm3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.binary.BinaryUtil;
import com.parzivail.util.client.VertexConsumerBuffer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PM3DFile
{
	private static final String MAGIC = "Pm3D";
	private static final int ACCEPTED_VERSION = 0x02;

	public final Identifier identifier;
	public final Vector3f[] verts;
	public final Vector3f[] normals;
	public final Vector3f[] uvs;
	public final PM3DObject[] objects;
	public final Box bounds;

	public PM3DFile(Identifier identifier, Vector3f[] verts, Vector3f[] normals, Vector3f[] uvs, PM3DObject[] objects, Box bounds)
	{
		this.identifier = identifier;
		this.verts = verts;
		this.normals = normals;
		this.uvs = uvs;
		this.objects = objects;
		this.bounds = bounds;
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
		InputStream reader = PIO.getStream("assets", modelFile);
		LittleEndianDataInputStream objStream = new LittleEndianDataInputStream(reader);

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

		Vector3f[] verts = loadVerts(numVerts, objStream);
		Vector3f[] normals = loadNormals(numNormals, objStream);
		Vector3f[] uvs = loadUvs(numUvs, objStream);
		PM3DObject[] objects = loadObjects(numObjects, objStream);

		Box bounds = getBounds(verts);

		return new PM3DFile(modelFile, verts, normals, uvs, objects, bounds);
	}

	private static Box getBounds(Vector3f[] verts)
	{
		Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

		for (Vector3f v : verts)
		{
			if (v.getX() < min.getX())
				min.set(v.getX(), min.getY(), min.getZ());
			if (v.getY() < min.getY())
				min.set(min.getX(), v.getY(), min.getZ());
			if (v.getZ() < min.getZ())
				min.set(min.getX(), min.getY(), v.getZ());

			if (v.getX() > max.getX())
				max.set(v.getX(), max.getY(), max.getZ());
			if (v.getY() > max.getY())
				max.set(max.getX(), v.getY(), max.getZ());
			if (v.getZ() > max.getZ())
				max.set(max.getX(), max.getY(), v.getZ());
		}

		return new Box(new Vec3d(min), new Vec3d(max));
	}

	private static Vector3f[] loadVerts(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		Vector3f[] verts = new Vector3f[num];

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat() + 0.5f;
			float y = objStream.readFloat();
			float z = objStream.readFloat() + 0.5f;

			verts[i] = new Vector3f(x, y, z);
		}

		return verts;
	}

	private static Vector3f[] loadNormals(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		Vector3f[] verts = new Vector3f[num];

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat();
			float y = objStream.readFloat();
			float z = objStream.readFloat();

			verts[i] = new Vector3f(x, y, z);
		}

		return verts;
	}

	private static Vector3f[] loadUvs(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		Vector3f[] uvs = new Vector3f[num];

		for (int i = 0; i < num; i++)
		{
			float u = objStream.readFloat();
			float v = objStream.readFloat();

			uvs[i] = new Vector3f(u, v, 0);
		}

		return uvs;
	}

	private static PM3DObject[] loadObjects(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		PM3DObject[] objects = new PM3DObject[num];

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

			objects[i] = new PM3DObject(objName, faces);
		}

		return objects;
	}

	public void render(VertexConsumerBuffer vcb)
	{
		for (PM3DObject o : objects)
			for (PM3DFace face : o.faces)
				emitFace(vcb, face);
	}

	private void emitFace(VertexConsumerBuffer vcb, PM3DFace face)
	{
		PM3DVertPointer a = face.verts.get(0);
		PM3DVertPointer b = face.verts.get(1);
		PM3DVertPointer c = face.verts.get(2);
		PM3DVertPointer d = face.verts.size() == 4 ? face.verts.get(3) : c;

		Vector3f tA = uvs[a.texture];
		Vector3f tB = uvs[b.texture];
		Vector3f tC = uvs[c.texture];
		Vector3f tD = uvs[d.texture];

		vcb.vertex(verts[a.vertex], normals[a.normal], tA.getX(), 1 - tA.getY());
		vcb.vertex(verts[b.vertex], normals[b.normal], tB.getX(), 1 - tB.getY());
		vcb.vertex(verts[c.vertex], normals[c.normal], tC.getX(), 1 - tC.getY());
		vcb.vertex(verts[d.vertex], normals[d.normal], tD.getX(), 1 - tD.getY());
	}
}
