package com.parzivail.pswg.client.render.p3d;

import net.minecraft.util.math.Matrix4f;

public class P3dObject extends P3dSocket
{
	public final byte material;
	public final P3dFace[] faces;
	public final P3dObject[] children;

	public P3dObject(String name, String parent, Matrix4f transform, byte material, P3dFace[] faces, P3dObject[] children)
	{
		super(name, parent, transform);
		this.material = material;
		this.faces = faces;
		this.children = children;
	}
}
