package com.parzivail.p3d;

import org.joml.Vector3f;

public class P3dFace
{
	public final Vector3f[] positions;
	public final Vector3f normal;
	public final Vector3f[] texture;

	public P3dFace(Vector3f[] positions, Vector3f normal, Vector3f[] texture)
	{
		this.positions = positions;
		this.normal = normal;
		this.texture = texture;
	}
}
