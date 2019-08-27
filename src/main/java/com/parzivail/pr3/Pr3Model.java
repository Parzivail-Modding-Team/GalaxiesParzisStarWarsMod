package com.parzivail.pr3;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.brotli.BrotliInputStream;
import com.parzivail.pm3d.*;
import com.parzivail.util.binary.BinaryUtil;
import net.minecraft.client.resources.IResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class Pr3Model
{
	private static final String MAGIC = "Pm3D";
	private static int ACCEPTED_VERSION = 0x01;

	private final LittleEndianDataInputStream objStream;
	private final IResource from;

	public Pr3Model(IResource from) throws IOException
	{
		this.from = from;
		BrotliInputStream bis = new BrotliInputStream(from.getInputStream());
		this.objStream = new LittleEndianDataInputStream(bis);
	}

	public Pm3dModel load() throws IOException
	{
		byte[] identBytes = new byte[MAGIC.length()];
		int read = objStream.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not Pm3D model");

		int version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		String credit = BinaryUtil.readNullTerminatedString(objStream);

		EnumSet<Pm3dFlags> flags = EnumSet.of(Pm3dFlags.None);

		byte byteFlags = objStream.readByte();

		if ((byteFlags & Pm3dFlags.AmbientOcclusion.getFlag()) != 0)
			flags.add(Pm3dFlags.AmbientOcclusion);

		int numTextures = objStream.readInt();
		int numVerts = objStream.readInt();
		int numNormals = objStream.readInt();
		int numUvs = objStream.readInt();
		int numObjects = objStream.readInt();

		HashMap<String, String> textures = loadTextures(numTextures, objStream);

		ArrayList<Pm3dVert> verts = loadVerts(numVerts, objStream);
		ArrayList<Pm3dVert> normals = loadVerts(numNormals, objStream);
		ArrayList<Pm3dUv> uvs = loadUvs(numUvs, objStream);
		HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> objects = loadObjects(numObjects, objStream);

		return new Pm3dModel(from.getResourceLocation(), credit, flags, textures, verts, normals, uvs, objects);
	}

	private HashMap<String, String> loadTextures(int num, LittleEndianDataInputStream objStream) throws IOException
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

	private ArrayList<Pm3dVert> loadVerts(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<Pm3dVert> verts = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat();
			float y = objStream.readFloat();
			float z = objStream.readFloat();

			verts.add(new Pm3dVert(x, y, z));
		}

		return verts;
	}

	private ArrayList<Pm3dUv> loadUvs(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<Pm3dUv> uvs = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float u = objStream.readFloat();
			float v = objStream.readFloat();

			uvs.add(new Pm3dUv(u, v));
		}

		return uvs;
	}

	private HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> loadObjects(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> objects = new HashMap<>();

		for (int i = 0; i < num; i++)
		{
			String objName = BinaryUtil.readNullTerminatedString(objStream);
			String matName = BinaryUtil.readNullTerminatedString(objStream);
			int numFaces = objStream.readInt();

			ArrayList<Pm3dFace> faces = new ArrayList<>();

			for (int j = 0; j < numFaces; j++)
			{
				Pm3dFace face = new Pm3dFace();
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

			objects.put(new Pm3dModelObjectInfo(objName, matName), faces);
		}

		return objects;
	}
}
