package com.parzivail.pr3;

import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector3f;

import java.util.ArrayList;

public class Pr3Object
{
	public final String name;
	public final ArrayList<Vector3f> vertices;
	public final ArrayList<Pr3FacePointer> faces;
	public final ArrayList<Vector3f> normals;
	public final ArrayList<Vector3f> uvs;
	public final Matrix4f transformationMatrix;
	public final String materialName;

	public Pr3Object(String name, ArrayList<Vector3f> vertices, ArrayList<Pr3FacePointer> faces, ArrayList<Vector3f> normals, ArrayList<Vector3f> uvs, Matrix4f transformationMatrix, String materialName)
	{
		this.name = name;
		this.vertices = vertices;
		this.faces = faces;
		this.normals = normals;
		this.uvs = uvs;
		this.transformationMatrix = transformationMatrix;
		this.materialName = materialName;
	}
}
