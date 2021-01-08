package com.parzivail.pswg.client.pm3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PM3DFile
{
	private static final String MAGIC = "Pm3D";
	private static final int[] ACCEPTED_VERSIONS = { 0x04 };

	private final PM3DLod[] lods;

	public PM3DFile(PM3DLod[] lods)
	{
		this.lods = lods;
	}

	public PM3DLod getLevelOfDetail(int lod)
	{
		return lods[MathHelper.clamp(lod, 0, lods.length - 1)];
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

		if (!ArrayUtils.contains(ACCEPTED_VERSIONS, version))
			throw new IOException(String.format("Input file version is 0x%s, expected one of: %s", Integer.toHexString(version), getAcceptedVersionString()));

		int numLods = 1;

		if (version > 2)
		{
			numLods = objStream.readInt();
		}

		PM3DLod[] lods = new PM3DLod[numLods];

		for (int i = 0; i < numLods; i++)
		{
			int numVerts = objStream.readInt();
			int numNormals = objStream.readInt();
			int numUvs = objStream.readInt();
			int numObjects = objStream.readInt();

			Vector3f[] verts = loadVerts(numVerts, objStream);
			Vector3f[] normals = loadNormals(numNormals, objStream);
			Vector3f[] uvs = loadUvs(numUvs, objStream);
			PM3DObject[] objects = loadObjects(numObjects, objStream);

			Box bounds = getBounds(verts);

			lods[i] = new PM3DLod(modelFile, verts, normals, uvs, objects, bounds);
		}

		return new PM3DFile(lods);
	}

	private static String getAcceptedVersionString()
	{
		return Arrays.stream(ACCEPTED_VERSIONS).mapToObj(i -> "0x" + Integer.toHexString(i)).collect(Collectors.joining(", "));
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
			float x = PIO.readHalf(objStream) + 0.5f;
			float y = PIO.readHalf(objStream);
			float z = PIO.readHalf(objStream) + 0.5f;

			verts[i] = new Vector3f(x, y, z);
		}

		return verts;
	}

	private static Vector3f[] loadNormals(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		Vector3f[] verts = new Vector3f[num];

		for (int i = 0; i < num; i++)
		{
			float x = PIO.readHalf(objStream);
			float y = PIO.readHalf(objStream);
			float z = PIO.readHalf(objStream);

			verts[i] = new Vector3f(x, y, z);
		}

		return verts;
	}

	private static Vector3f[] loadUvs(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		Vector3f[] uvs = new Vector3f[num];

		for (int i = 0; i < num; i++)
		{
			float u = PIO.readHalf(objStream);
			float v = PIO.readHalf(objStream);

			uvs[i] = new Vector3f(u, v, 0);
		}

		return uvs;
	}

	private static PM3DObject[] loadObjects(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		PM3DObject[] objects = new PM3DObject[num];

		for (int i = 0; i < num; i++)
		{
			String objName = PIO.readNullTerminatedString(objStream);
			int numFaces = objStream.readInt();

			ArrayList<PM3DFace> faces = new ArrayList<>();

			for (int j = 0; j < numFaces; j++)
			{
				byte material = objStream.readByte();
				PM3DFace face = new PM3DFace(material);
				int numVerts = objStream.readInt();
				for (int k = 0; k < numVerts; k++)
				{
					int vertex = PIO.read7BitEncodedInt(objStream);
					int normal = PIO.read7BitEncodedInt(objStream);
					int texture = PIO.read7BitEncodedInt(objStream);
					face.verts.add(new PM3DVertPointer(vertex, normal, texture));
				}

				faces.add(face);
			}

			objects[i] = new PM3DObject(objName, faces);
		}

		return objects;
	}
}
