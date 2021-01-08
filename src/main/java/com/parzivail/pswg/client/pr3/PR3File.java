package com.parzivail.pswg.client.pr3;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Matrix4f;

import java.io.IOException;
import java.io.InputStream;

public class PR3File
{
	private static final String MAGIC = "PR3";
	private static final int ACCEPTED_VERSION = 0x02;

	public final PR3Object[] objects;

	public PR3File(PR3Object[] objects)
	{

		this.objects = objects;
	}

	public static PR3File tryLoad(Identifier modelFile)
	{
		try
		{
			return load(modelFile);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			CrashReport crashReport = CrashReport.create(ex, String.format("Loading PR3 file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	private static PR3File load(Identifier modelFile) throws IOException
	{
		InputStream reader = PIO.getStream("assets", modelFile);
		LittleEndianDataInputStream objStream = new LittleEndianDataInputStream(reader);

		byte[] identBytes = new byte[MAGIC.length()];
		int read = objStream.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PR3 model");

		int version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		int numObjects = objStream.readInt();

		PR3Object[] objects = new PR3Object[numObjects];
		for (int i = 0; i < numObjects; i++)
		{
			String name = PIO.readNullTerminatedString(objStream);
			String material = PIO.readNullTerminatedString(objStream);
			Matrix4f transformation = PIO.readMatrix4f(objStream);

			Vector3f[] vertices = readLengthCodedVectors(objStream);
			Vector3f[] normals = readLengthCodedVectors(objStream);
			Vector3f[] uvs = readLengthCodedVectors(objStream);

			PR3FacePointer[] faces = readLengthCodedFaces(objStream);

			objects[i] = new PR3Object(name, new Identifier(material), vertices, normals, uvs, faces, transformation);
		}

		return new PR3File(objects);
	}

	private static PR3FacePointer[] readLengthCodedFaces(LittleEndianDataInputStream objStream) throws IOException
	{
		int length = PIO.read7BitEncodedInt(objStream);

		PR3FacePointer[] faces = new PR3FacePointer[length];
		for (int i = 0; i < length; i++)
			faces[i] = new PR3FacePointer(PIO.read7BitEncodedInt(objStream), PIO.read7BitEncodedInt(objStream), PIO.read7BitEncodedInt(objStream));

		return faces;
	}

	private static Vector3f[] readLengthCodedVectors(LittleEndianDataInputStream objStream) throws IOException
	{
		int length = PIO.read7BitEncodedInt(objStream);

		Vector3f[] vectors = new Vector3f[length];
		for (int i = 0; i < length; i++)
			vectors[i] = new Vector3f(PIO.readHalf(objStream), PIO.readHalf(objStream), PIO.readHalf(objStream));

		return vectors;
	}
}
