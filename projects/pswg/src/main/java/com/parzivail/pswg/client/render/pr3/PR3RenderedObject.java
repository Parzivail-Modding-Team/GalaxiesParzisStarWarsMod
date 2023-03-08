package com.parzivail.pswg.client.render.pr3;

import com.parzivail.pswg.rig.pr3r.PR3Object;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Deprecated
public class PR3RenderedObject extends PR3Object
{
	public final Vector3f[] vertices;
	public final Vector3f[] normals;
	public final Vector3f[] uvs;
	public final PR3FacePointer[] faces;

	public PR3RenderedObject(String name, Vector3f[] vertices, Vector3f[] normals, Vector3f[] uvs, PR3FacePointer[] faces, Matrix4f transformationMatrix)
	{
		super(name, transformationMatrix);
		this.vertices = vertices;
		this.normals = normals;
		this.uvs = uvs;
		this.faces = faces;
	}
}
