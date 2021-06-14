package com.parzivail.pswg.client.pr3;

import com.parzivail.pswg.rig.pr3r.PR3Object;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class PR3RenderedObject extends PR3Object
{
	public final Identifier material;
	public final Vec3f[] vertices;
	public final Vec3f[] normals;
	public final Vec3f[] uvs;
	public final PR3FacePointer[] faces;

	public PR3RenderedObject(String name, Identifier material, Vec3f[] vertices, Vec3f[] normals, Vec3f[] uvs, PR3FacePointer[] faces, Matrix4f transformationMatrix)
	{
		super(name, transformationMatrix);
		this.material = material;
		this.vertices = vertices;
		this.normals = normals;
		this.uvs = uvs;
		this.faces = faces;
	}
}
