package com.parzivail.pswg.client.pm3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.binary.DataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public record PM3DFile(PM3DLod[] lods)
{
	private static final String MAGIC = "Pm3D";
	private static final int[] ACCEPTED_VERSIONS = { 0x04 };

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
		catch (NullPointerException | IOException ex)
		{
			ex.printStackTrace();
			var crashReport = CrashReport.create(ex, String.format("Loading PM3D file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	public static PM3DFile loadOrNull(Identifier modelFile)
	{
		try
		{
			return load(modelFile);
		}
		catch (NullPointerException | IOException ex)
		{
			return null;
		}
	}

	private static PM3DFile load(Identifier modelFile) throws IOException
	{
		// TODO: convert this to a KeyedReloadableLoader
		var reader = PIO.getStream("assets", modelFile);
		var objStream = new LittleEndianDataInputStream(reader);

		var identBytes = new byte[MAGIC.length()];
		var read = objStream.read(identBytes);
		var ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PM3D model");

		var version = objStream.readInt();

		if (!ArrayUtils.contains(ACCEPTED_VERSIONS, version))
			throw new IOException(String.format("Input file version is 0x%s, expected one of: %s", Integer.toHexString(version), getAcceptedVersionString()));

		var numLods = 1;

		if (version > 2)
		{
			numLods = objStream.readInt();
		}

		var lods = new PM3DLod[numLods];

		for (var i = 0; i < numLods; i++)
		{
			var numVerts = objStream.readInt();
			var numNormals = objStream.readInt();
			var numUvs = objStream.readInt();
			var numObjects = objStream.readInt();

			var verts = loadVerts(numVerts, objStream);
			var normals = loadNormals(numNormals, objStream);
			var uvs = loadUvs(numUvs, objStream);
			var objects = loadObjects(numObjects, objStream);

			var bounds = getBounds(verts);

			lods[i] = new PM3DLod(modelFile, verts, normals, uvs, objects, bounds);
		}

		return new PM3DFile(lods);
	}

	private static String getAcceptedVersionString()
	{
		return Arrays.stream(ACCEPTED_VERSIONS).mapToObj(i -> "0x" + Integer.toHexString(i)).collect(Collectors.joining(", "));
	}

	private static Box getBounds(Vec3f[] verts)
	{
		var min = new Vec3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		var max = new Vec3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

		for (var v : verts)
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

	private static Vec3f[] loadVerts(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		var verts = new Vec3f[num];

		for (var i = 0; i < num; i++)
		{
			var x = DataReader.readHalf(objStream) + 0.5f;
			var y = DataReader.readHalf(objStream);
			var z = DataReader.readHalf(objStream) + 0.5f;

			verts[i] = new Vec3f(x, y, z);
		}

		return verts;
	}

	private static Vec3f[] loadNormals(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		var verts = new Vec3f[num];

		for (var i = 0; i < num; i++)
		{
			var x = DataReader.readHalf(objStream);
			var y = DataReader.readHalf(objStream);
			var z = DataReader.readHalf(objStream);

			verts[i] = new Vec3f(x, y, z);
		}

		return verts;
	}

	private static Vec3f[] loadUvs(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		var uvs = new Vec3f[num];

		for (var i = 0; i < num; i++)
		{
			var u = DataReader.readHalf(objStream);
			var v = DataReader.readHalf(objStream);

			uvs[i] = new Vec3f(u, v, 0);
		}

		return uvs;
	}

	private static PM3DObject[] loadObjects(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		var objects = new PM3DObject[num];

		for (var i = 0; i < num; i++)
		{
			var objName = DataReader.readNullTerminatedString(objStream);
			var numFaces = objStream.readInt();

			var faces = new ArrayList<PM3DFace>();

			for (var j = 0; j < numFaces; j++)
			{
				var material = objStream.readByte();
				var face = new PM3DFace(material);
				var numVerts = objStream.readInt();
				for (var k = 0; k < numVerts; k++)
				{
					var vertex = DataReader.read7BitEncodedInt(objStream);
					var normal = DataReader.read7BitEncodedInt(objStream);
					var texture = DataReader.read7BitEncodedInt(objStream);
					face.verts.add(new PM3DVertPointer(vertex, normal, texture));
				}

				faces.add(face);
			}

			objects[i] = new PM3DObject(objName, faces);
		}

		return objects;
	}
}
