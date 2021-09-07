package com.parzivail.pswg.client.render.p3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.binary.DataReader;
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
	private static final String MAGIC = "P3D";
	private static final int[] ACCEPTED_VERSIONS = { 0x01 };

	public final int version;
	public final HashMap<String, P3dSocket> sockets;
	public final HashMap<String, P3dMesh> meshes;

	public P3dModel(int version, HashMap<String, P3dSocket> sockets, HashMap<String, P3dMesh> meshes)
	{
		this.version = version;
		this.sockets = sockets;
		this.meshes = meshes;
	}

	public static P3dModel read(InputStream stream) throws IOException
	{
		var objStream = new LittleEndianDataInputStream(stream);

		var identBytes = new byte[MAGIC.length()];
		var read = objStream.read(identBytes);
		var ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not P3D model");

		var version = objStream.readInt();

		if (!ArrayUtils.contains(ACCEPTED_VERSIONS, version))
			throw new IOException(String.format("Input file version is 0x%s, expected one of: %s", Integer.toHexString(version), getAcceptedVersionString()));

		var numSockets = objStream.readInt();

		var sockets = new HashMap<String, P3dSocket>();
		for (var socketIdx = 0; socketIdx < numSockets; socketIdx++)
		{
			var name = DataReader.readNullTerminatedString(objStream);

			var hasParent = objStream.readBoolean();

			String parent = null;
			if (hasParent)
				parent = DataReader.readNullTerminatedString(objStream);

			var transform = PIO.readMatrix4f(objStream);

			sockets.put(name, new P3dSocket(name, parent, transform));
		}

		var numMeshes = objStream.readInt();

		var meshes = new HashMap<String, P3dMesh>();
		for (var mesIdx = 0; mesIdx < numMeshes; mesIdx++)
		{
			P3dMesh mesh = readMesh(objStream);

			meshes.put(mesh.name, mesh);
		}

		return new P3dModel(version, sockets, meshes);
	}

	@NotNull
	private static P3dMesh readMesh(LittleEndianDataInputStream objStream) throws IOException
	{
		var name = DataReader.readNullTerminatedString(objStream);

		var transform = PIO.readMatrix4f(objStream);

		var material = objStream.readByte();

		var numFaces = objStream.readInt();
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
		var children = new P3dMesh[numChildren];

		for (var childIdx = 0; childIdx < numChildren; childIdx++)
			children[childIdx] = readMesh(objStream);

		return new P3dMesh(name, transform, material, faces, children);
	}

	private static String getAcceptedVersionString()
	{
		return Arrays.stream(ACCEPTED_VERSIONS).mapToObj(i -> "0x" + Integer.toHexString(i)).collect(Collectors.joining(", "));
	}
}
