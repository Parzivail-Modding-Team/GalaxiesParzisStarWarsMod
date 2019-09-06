package com.parzivail.pr3;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.brotli.BrotliInputStream;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.binary.BinaryUtil;
import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;

public class Pr3Model
{
	private static final String MAGIC = "PR3";
	private static int ACCEPTED_VERSION = 0x01;

	private final ArrayList<Pr3Object> objects;

	public Pr3Model(ArrayList<Pr3Object> objects)
	{
		this.objects = objects;
	}

	public static Pr3Model load(ResourceLocation location) throws IOException
	{
		IResource from = Client.mc.getResourceManager().getResource(location);
		BrotliInputStream bis = new BrotliInputStream(from.getInputStream());
		LittleEndianDataInputStream objStream = new LittleEndianDataInputStream(bis);

		byte[] identBytes = new byte[MAGIC.length()];
		int read = objStream.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PR3 model");

		int version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		int numObjects = objStream.readInt();

		ArrayList<Pr3Object> objects = new ArrayList<Pr3Object>();

		for (int i = 0; i < numObjects; i++)
		{
			String name = BinaryUtil.readNullTerminatedString(objStream);
			String materialName = BinaryUtil.readNullTerminatedString(objStream);

			Matrix4f transformationMatrix = BinaryUtil.readMatrix4(objStream);

			ArrayList<Vector3f> vertices = readLengthCodedVectors(objStream);
			ArrayList<Vector3f> normals = readLengthCodedVectors(objStream);
			ArrayList<Vector3f> uvs = readLengthCodedVectors(objStream);

			ArrayList<Pr3FacePointer> faces = readLengthCodedFaces(objStream);

			objects.add(new Pr3Object(name, vertices, faces, normals, uvs, transformationMatrix, materialName));
		}

		return new Pr3Model(objects);
	}

	private static ArrayList<Vector3f> readLengthCodedVectors(LittleEndianDataInputStream objStream) throws IOException
	{
		int num = objStream.readInt();

		ArrayList<Vector3f> vectors = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat();
			float y = objStream.readFloat();
			float z = objStream.readFloat();

			vectors.add(new Vector3f(x, y, z));
		}

		return vectors;
	}

	private static ArrayList<Pr3FacePointer> readLengthCodedFaces(LittleEndianDataInputStream objStream) throws IOException
	{
		int num = objStream.readInt();

		ArrayList<Pr3FacePointer> faces = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			int a = objStream.readInt();
			int b = objStream.readInt();
			int c = objStream.readInt();

			faces.add(new Pr3FacePointer(a, b, c));
		}

		return faces;
	}
}
