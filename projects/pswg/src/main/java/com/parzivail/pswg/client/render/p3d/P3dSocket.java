package com.parzivail.pswg.client.render.p3d;

import org.joml.Matrix4f;

import java.util.ArrayList;

public class P3dSocket
{
	public ArrayList<P3dSocket> ancestry;

	public final String name;
	public final String parent;
	public final Matrix4f transform;

	public P3dSocket(String name, String parent, Matrix4f transform)
	{
		this.name = name;
		this.parent = parent;
		this.transform = transform;

		ancestry = new ArrayList<>();
	}
}
