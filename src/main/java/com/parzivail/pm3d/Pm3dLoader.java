package com.parzivail.pm3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.brotli.BrotliInputStream;
import com.parzivail.util.binary.BinaryUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class Pm3dLoader
{
	private static final String MAGIC = "Pm3D";
	private static int ACCEPTED_VERSION = 0x01;

	private final LittleEndianDataInputStream objStream;
	private final IResourceManager manager;

	public Pm3dLoader(IResource from, IResourceManager manager) throws IOException
	{
		this.manager = manager;
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

		int numVerts = objStream.readInt();
		int numNormals = objStream.readInt();
		int numUvs = objStream.readInt();
		int numObjects = objStream.readInt();

		ArrayList<Pm3dVert> verts = loadVerts(numVerts, objStream);
		ArrayList<Pm3dNormal> normals = loadNormals(numNormals, objStream);
		ArrayList<Pm3dUv> uvs = loadUvs(numUvs, objStream);
		HashMap<Pm3dModelObjectInfo, ArrayList<Pm3dFace>> objects = loadObjects(numObjects, objStream);

		return new Pm3dModel(verts, normals, uvs, objects);
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

	private ArrayList<Pm3dNormal> loadNormals(int num, LittleEndianDataInputStream objStream) throws IOException
	{
		ArrayList<Pm3dNormal> normals = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat();
			float y = objStream.readFloat();
			float z = objStream.readFloat();

			normals.add(new Pm3dNormal(x, y, z));
		}

		return normals;
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

		int numObjects = objStream.readInt();

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
