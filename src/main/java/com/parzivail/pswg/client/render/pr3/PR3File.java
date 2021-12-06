package com.parzivail.pswg.client.render.pr3;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.data.DataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3f;

import java.io.IOException;

public record PR3File(PR3RenderedObject[] objects)
{
	private static final String MAGIC = "PR3";
	private static final int ACCEPTED_VERSION = 0x02;

	public static PR3File tryLoad(Identifier modelFile)
	{
		try
		{
			return load(modelFile);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			var crashReport = CrashReport.create(ex, String.format("Loading PR3 file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	private static PR3File load(Identifier modelFile) throws IOException
	{
		var reader = PIO.getStream("assets", modelFile);
		var objStream = new LittleEndianDataInputStream(reader);

		var identBytes = new byte[MAGIC.length()];
		var read = objStream.read(identBytes);
		var ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PR3 model");

		var version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		var numObjects = objStream.readInt();

		var objects = new PR3RenderedObject[numObjects];
		for (var i = 0; i < numObjects; i++)
		{
			var name = DataReader.readNullTerminatedString(objStream);
			var material = DataReader.readNullTerminatedString(objStream);
			var transformation = DataReader.readMatrix4f(objStream);

			var vertices = readLengthCodedVectors(objStream);
			var normals = readLengthCodedVectors(objStream);
			var uvs = readLengthCodedVectors(objStream);

			var faces = readLengthCodedFaces(objStream);

			objects[i] = new PR3RenderedObject(name, vertices, normals, uvs, faces, transformation);
		}

		return new PR3File(objects);
	}

	private static PR3FacePointer[] readLengthCodedFaces(LittleEndianDataInputStream objStream) throws IOException
	{
		var length = DataReader.read7BitEncodedInt(objStream);

		var faces = new PR3FacePointer[length];
		for (var i = 0; i < length; i++)
			faces[i] = new PR3FacePointer(DataReader.read7BitEncodedInt(objStream), DataReader.read7BitEncodedInt(objStream), DataReader.read7BitEncodedInt(objStream));

		return faces;
	}

	private static Vec3f[] readLengthCodedVectors(LittleEndianDataInputStream objStream) throws IOException
	{
		var length = DataReader.read7BitEncodedInt(objStream);

		var vectors = new Vec3f[length];
		for (var i = 0; i < length; i++)
			vectors[i] = new Vec3f(objStream.readFloat(), objStream.readFloat(), objStream.readFloat());

		return vectors;
	}
}
