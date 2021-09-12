package com.parzivail.pswg.client.render.p3d;

import net.minecraft.util.math.Matrix4f;

public class P3dObject
{
	public final String name;
	public final String parent;
	public final Matrix4f transform;
	public final byte material;
	public final P3dFace[] faces;
	public final P3dObject[] children;

	public P3dObject(String name, String parent, Matrix4f transform, byte material, P3dFace[] faces, P3dObject[] children)
	{
		this.name = name;
		this.parent = parent;
		this.transform = transform;
		this.material = material;
		this.faces = faces;
		this.children = children;
	}
}
