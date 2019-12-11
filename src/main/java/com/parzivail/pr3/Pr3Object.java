package com.parzivail.pr3;

import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector2f;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.gltk.VertexBuffer;

import java.util.ArrayList;

public class Pr3Object
{
	public final String name;
	public final Matrix4f transformationMatrix;
	public final String materialName;
	public ArrayList<Vector3f> vertices;
	public ArrayList<Pr3FacePointer> faces;
	public ArrayList<Vector3f> normals;
	public ArrayList<Vector3f> uvs;

	public VertexBuffer vertexBuffer;

	public Pr3Object(String name, ArrayList<Vector3f> vertices, ArrayList<Pr3FacePointer> faces, ArrayList<Vector3f> normals, ArrayList<Vector3f> uvs, Matrix4f transformationMatrix, String materialName)
	{
		this.name = name;
		this.transformationMatrix = transformationMatrix;
		this.materialName = materialName;

		this.vertices = vertices;
		this.faces = faces;
		this.normals = normals;
		this.uvs = uvs;

		genVertexBuffer();
	}

	private void genVertexBuffer()
	{
		vertexBuffer = new VertexBuffer();

		Vector2f[] bUvs = new Vector2f[uvs.size()];
		Vector3f[] bNormals = new Vector3f[normals.size()];
		Vector3f[] bVertices = new Vector3f[vertices.size()];
		int[] bElements = new int[faces.size() * 3];

		for (int i = 0; i < bUvs.length; i++)
			bUvs[i] = new Vector2f(uvs.get(i).x, 1 - uvs.get(i).y);

		for (int i = 0; i < bNormals.length; i++)
			bNormals[i] = normals.get(i);

		for (int i = 0; i < bVertices.length; i++)
			bVertices[i] = vertices.get(i);

		int i = 0;
		for (Pr3FacePointer ptr : faces)
		{
			bElements[i] = ptr.a;
			bElements[i + 1] = ptr.b;
			bElements[i + 2] = ptr.c;

			i += 3;
		}

		vertices = null;
		faces = null;
		normals = null;
		uvs = null;

		vertexBuffer.initialize(bVertices, bUvs, bNormals, bElements);
	}
}
