package com.parzivail.pswg.client.render.p3d;

import net.minecraft.util.math.Vec3f;

public class P3dFace
{
	public final Vec3f[] positions;
	public final Vec3f[] normal;
	public final Vec3f[] texture;

	public P3dFace(Vec3f[] positions, Vec3f[] normal, Vec3f[] texture)
	{
		this.positions = positions;
		this.normal = normal;
		this.texture = texture;
	}
}
