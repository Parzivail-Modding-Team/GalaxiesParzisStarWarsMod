package com.parzivail.pswg.client.pr3r;

import com.parzivail.pswg.client.pr3.PR3FacePointer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class PR3Object
{
	public final String name;
	public final Identifier material;
	public final Vector3f[] vertices;
	public final Vector3f[] normals;
	public final Vector3f[] uvs;
	public final PR3FacePointer[] faces;
	public final Matrix4f transformationMatrix;

	public PR3Object(String name, Identifier material, Vector3f[] vertices, Vector3f[] normals, Vector3f[] uvs, PR3FacePointer[] faces, Matrix4f transformationMatrix)
	{
		this.name = name;
		this.material = material;
		this.vertices = vertices;
		this.normals = normals;
		this.uvs = uvs;
		this.faces = faces;
		this.transformationMatrix = transformationMatrix;
	}
}
