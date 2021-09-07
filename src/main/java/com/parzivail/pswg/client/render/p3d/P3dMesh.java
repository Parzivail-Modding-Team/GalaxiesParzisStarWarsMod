package com.parzivail.pswg.client.render.p3d;

import net.minecraft.util.math.Matrix4f;

public class P3dMesh
{
	public final String name;
	public final Matrix4f transform;
	public final byte material;
	public final P3dFace[] faces;
	public final P3dMesh[] children;

	public P3dMesh(String name, Matrix4f transform, byte material, P3dFace[] faces, P3dMesh[] children)
	{
		this.name = name;
		this.transform = transform;
		this.material = material;
		this.faces = faces;
		this.children = children;
	}
}
