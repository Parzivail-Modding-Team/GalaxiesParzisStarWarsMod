package com.parzivail.aurek.model.p3di;

import com.google.common.io.LittleEndianDataOutputStream;
import com.parzivail.aurek.ToolkitClient;
import com.parzivail.p3d.P3dModel;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public record P3diModel(int version, P3diSocket[] sockets, P3diMesh[] meshes)
{
	public void compile(File filename, boolean writeVertexData) throws IOException, P3diCompileException
	{
		try (var f = new FileOutputStream(filename))
		{
			compile(f, writeVertexData);
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	public void compile(OutputStream f, boolean writeVertexData) throws IOException, P3diCompileException
	{
		try (var bw = new LittleEndianDataOutputStream(f))
		{
			bw.write((writeVertexData ? "P3D" : "P3DR").getBytes(StandardCharsets.US_ASCII));
			bw.writeInt(version);

			bw.writeInt(sockets.length);
			for (var socket : sockets)
			{
				bw.write(socket.name().getBytes(StandardCharsets.US_ASCII));
				bw.writeByte(0);

				bw.writeBoolean(socket.parent() != null);
				if (socket.parent() != null)
				{
					bw.write(socket.parent().getBytes(StandardCharsets.US_ASCII));
					bw.writeByte(0);
				}

				writeSocketTransform(bw, socket.transform());
			}

			bw.writeInt(meshes.length);
			for (var mesh : meshes)
				writeMesh(bw, mesh, writeVertexData);
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	private void writeSocketTransform(LittleEndianDataOutputStream bw, float[][] t) throws IOException
	{
		var mat = new Matrix4f(
				t[0][0], t[0][1], t[0][2], t[0][3],
				t[1][0], t[1][1], t[1][2], t[1][3],
				t[2][0], t[2][1], t[2][2], t[2][3],
				t[3][0], t[3][1], t[3][2], t[3][3]
		);

		mat = mat.mul(new Matrix4f(
				1, 0, 0, 0,
				0, 0, 1, 0,
				0, -1, 0, 0,
				0, 0, 0, 1
		));
		mat = new Matrix4f().rotation(new Quaternionf().rotationX(-MathHelper.PI / 2)).mul(mat);

		// TODO: wrong?
		mat = new Matrix4f().scale(1, 1, -1).mul(mat);

		bw.writeFloat(mat.m00());
		bw.writeFloat(mat.m01());
		bw.writeFloat(mat.m02());
		bw.writeFloat(mat.m03());
		bw.writeFloat(mat.m10());
		bw.writeFloat(mat.m11());
		bw.writeFloat(mat.m12());
		bw.writeFloat(mat.m13());
		bw.writeFloat(mat.m20());
		bw.writeFloat(mat.m21());
		bw.writeFloat(mat.m22());
		bw.writeFloat(mat.m23());
		bw.writeFloat(mat.m30());
		bw.writeFloat(mat.m31());
		bw.writeFloat(mat.m32());
		bw.writeFloat(mat.m33());
	}

	@SuppressWarnings("UnstableApiUsage")
	private void writeMesh(LittleEndianDataOutputStream bw, P3diMesh mesh, boolean writeVertexData) throws IOException, P3diCompileException
	{
		bw.write(mesh.name().getBytes(StandardCharsets.US_ASCII));
		bw.writeByte(0);

		writeMeshTransform(bw, mesh.transform());

		if (writeVertexData)
		{
			bw.writeByte(getMaterial(mesh.name(), mesh.material()));

			bw.writeInt(mesh.faces().length);
			for (var face : mesh.faces())
			{
				writeVec3(bw, face.normal());

				var vertices = face.vertices();
				if (vertices.length == 3)
				{
					for (var i = 0; i < 3; i++)
						writeVertex(bw, vertices[i]);

					writeVertex(bw, vertices[2]);
				}
				else if (vertices.length == 4)
				{
					for (var i = 0; i < 4; i++)
						writeVertex(bw, vertices[i]);
				}
				else
					throw new P3diCompileException(String.format("Only triangles and quads supported, found %s-gon in object \"%s\"", vertices.length, mesh.name()));
			}
		}

		bw.writeInt(mesh.children().length);
		for (var child : mesh.children())
			writeMesh(bw, child, writeVertexData);
	}

	@SuppressWarnings("UnstableApiUsage")
	private void writeVertex(LittleEndianDataOutputStream bw, P3diVertex vertex) throws IOException
	{
		writeVec3(bw, vertex.v());

		for (var i = 0; i < 2; i++)
			// TODO: snap tex coord support?
			bw.writeFloat(vertex.t()[i]);
	}

	@SuppressWarnings("UnstableApiUsage")
	private void writeVec3(LittleEndianDataOutputStream bw, float[] v) throws IOException
	{
		bw.writeFloat(v[0]); // X

		// Convert from Z-up to Y-up
		bw.writeFloat(v[2]); // Z

		// TODO: wrong?
		bw.writeFloat(-v[1]); // Y
	}

	private int getMaterial(String objectName, String materialName)
	{
		switch (materialName)
		{
			case "MAT_DIFFUSE_OPAQUE":
				return P3dModel.MAT_ID_DIFFUSE_OPAQUE;
			case "MAT_DIFFUSE_CUTOUT":
				return P3dModel.MAT_ID_DIFFUSE_CUTOUT;
			case "MAT_DIFFUSE_TRANSLUCENT":
				return P3dModel.MAT_ID_DIFFUSE_TRANSLUCENT;
			case "MAT_EMISSIVE":
				return P3dModel.MAT_ID_EMISSIVE;
			default:
				ToolkitClient.NOTIFIER.warn("P3Di Compiler", String.format("Unsupported material \"%s\" for object \"%s\", defaulting to DIFFUSE_CUTOUT", materialName, objectName));
				return P3dModel.MAT_ID_DIFFUSE_CUTOUT;
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	private void writeMeshTransform(LittleEndianDataOutputStream bw, float[][] t) throws IOException
	{
		var mat = new Matrix4f(
				t[0][0], t[0][1], t[0][2], t[0][3],
				t[1][0], t[1][1], t[1][2], t[1][3],
				t[2][0], t[2][1], t[2][2], t[2][3],
				t[3][0], t[3][1], t[3][2], t[3][3]
		);

		// Convert from Z-up to Y-up
		var tX = mat.m03();
		var tY = mat.m13();
		var tZ = mat.m23();

		bw.writeFloat(mat.m00());
		bw.writeFloat(mat.m01());
		bw.writeFloat(mat.m02());
		bw.writeFloat(tX);
		bw.writeFloat(mat.m10());
		bw.writeFloat(mat.m11());
		bw.writeFloat(mat.m12());
		bw.writeFloat(tZ);
		bw.writeFloat(mat.m20());
		bw.writeFloat(mat.m21());
		bw.writeFloat(mat.m22());
		// TODO: wrong?
		bw.writeFloat(-tY);
		bw.writeFloat(mat.m30());
		bw.writeFloat(mat.m31());
		bw.writeFloat(mat.m32());
		bw.writeFloat(mat.m33());
	}
}
