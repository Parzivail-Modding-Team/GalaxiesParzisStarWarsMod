package com.parzivail.pswg.client.render.p3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.binary.DataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class P3dModel
{
	private static final String MODEL_MAGIC = "P3D";
	private static final String RIG_MAGIC = "P3DR";
	private static final int[] ACCEPTED_VERSIONS = { 0x01 };

	public final int version;
	public final HashMap<String, P3dSocket> sockets;
	public final P3dObject[] rootMeshes;
	public final HashMap<String, P3dObject> meshMap;

	public P3dModel(int version, HashMap<String, P3dSocket> sockets, P3dObject[] rootMeshes, HashMap<String, P3dObject> meshMap)
	{
		this.version = version;
		this.sockets = sockets;
		this.rootMeshes = rootMeshes;
		this.meshMap = meshMap;
	}

	public static P3dModel tryLoad(Identifier modelFile, boolean hasVertexData)
	{
		try
		{
			var reader = PIO.getStream("data", modelFile);
			return read(reader, hasVertexData);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			var crashReport = CrashReport.create(ex, String.format("Loading PR3R file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	public static P3dModel read(InputStream stream, boolean hasVertexData) throws IOException
	{
		var objStream = new LittleEndianDataInputStream(stream);

		var magic = hasVertexData ? MODEL_MAGIC : RIG_MAGIC;

		// read header
		var identBytes = new byte[magic.length()];
		var read = objStream.read(identBytes);
		var ident = new String(identBytes);
		if (!ident.equals(magic) || read != identBytes.length)
			throw new IOException(String.format("Input file not %s model", magic));

		var version = objStream.readInt();

		if (!ArrayUtils.contains(ACCEPTED_VERSIONS, version))
			throw new IOException(String.format("Input file version is 0x%s, expected one of: %s", Integer.toHexString(version), getAcceptedVersionString()));

		// read sockets
		var numSockets = objStream.readInt();
		var sockets = new HashMap<String, P3dSocket>();

		for (var socketIdx = 0; socketIdx < numSockets; socketIdx++)
		{
			var name = DataReader.readNullTerminatedString(objStream);

			var hasParent = objStream.readBoolean();

			String parent = null;
			if (hasParent)
				parent = DataReader.readNullTerminatedString(objStream);

			var transform = DataReader.readMatrix4f(objStream);

			sockets.put(name, new P3dSocket(name, parent, transform));
		}

		// read meshes
		var numMeshes = objStream.readInt();
		var meshes = new P3dObject[numMeshes];
		var meshMap = new HashMap<String, P3dObject>();

		for (var meshIdx = 0; meshIdx < numMeshes; meshIdx++)
		{
			var mesh = readMesh(meshMap, null, objStream, hasVertexData);
			meshMap.put(mesh.name, mesh);

			meshes[meshIdx] = mesh;
		}

		// build ancestry trees for sockets which enables directly calculating transformation
		for (var socket : sockets.values())
		{
			var parent = socket.parent;
			while (parent != null)
			{
				var parentMesh = meshMap.get(parent);
				socket.ancestry.add(0, parentMesh);
				parent = parentMesh.parent;
			}
		}

		return new P3dModel(version, sockets, meshes, meshMap);
	}

	@NotNull
	private static P3dObject readMesh(HashMap<String, P3dObject> meshes, String parent, LittleEndianDataInputStream objStream, boolean hasVertexData) throws IOException
	{
		var name = DataReader.readNullTerminatedString(objStream);

		var transform = DataReader.readMatrix4f(objStream);

		var material = hasVertexData ? objStream.readByte() : (byte)0;

		var numFaces = hasVertexData ? objStream.readInt() : 0;
		var faces = new P3dFace[numFaces];

		for (var faceIdx = 0; faceIdx < numFaces; faceIdx++)
		{
			var positions = new Vec3f[4];
			var normal = new Vec3f[4];
			var texture = new Vec3f[4];

			for (var vertIdx = 0; vertIdx < 4; vertIdx++)
			{
				positions[vertIdx] = new Vec3f(objStream.readFloat(), objStream.readFloat(), objStream.readFloat());
				normal[vertIdx] = new Vec3f(objStream.readFloat(), objStream.readFloat(), objStream.readFloat());
				texture[vertIdx] = new Vec3f(objStream.readFloat(), objStream.readFloat(), 0);
			}

			faces[faceIdx] = new P3dFace(positions, normal, texture);
		}

		var numChildren = objStream.readInt();
		var children = new P3dObject[numChildren];

		for (var childIdx = 0; childIdx < numChildren; childIdx++)
		{
			var m = readMesh(meshes, name, objStream, hasVertexData);
			meshes.put(m.name, m);
			children[childIdx] = m;
		}

		return new P3dObject(name, parent, transform, material, faces, children);
	}

	private static String getAcceptedVersionString()
	{
		return Arrays.stream(ACCEPTED_VERSIONS).mapToObj(i -> "0x" + Integer.toHexString(i)).collect(Collectors.joining(", "));
	}
}
