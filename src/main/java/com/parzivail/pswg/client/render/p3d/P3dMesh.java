package com.parzivail.pswg.client.render.p3d;

import net.minecraft.util.math.Matrix4f;

public class P3dMesh
{
	public final String name;
	public final String parent;
	public final Matrix4f transform;
	public final byte material;
	public final P3dFace[] faces;

	public P3dMesh(String name, String parent, Matrix4f transform, byte material, P3dFace[] faces)
	{
		this.name = name;
		this.parent = parent;
		this.transform = transform;
		this.material = material;
		this.faces = faces;
	}
}
