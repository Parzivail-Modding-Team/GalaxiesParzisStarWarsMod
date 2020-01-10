package com.parzivail.pm3d;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class Pm3dVert
{
	public final float x;
	public final float y;
	public final float z;

	public Pm3dVert(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Pm3dVert scale(float factor)
	{
		return new Pm3dVert(x * factor, y * factor, z * factor);
	}

	public Pm3dVert translate(float x, float y, float z)
	{
		return new Pm3dVert(this.x + x, this.y + y, this.z + z);
	}

	public Pm3dVert apply(Matrix4f m)
	{
		Vector4f out = new Vector4f();
		m.transform(new Vector4f(x, y, z, 0), out);
		return new Pm3dVert(out.x, out.y, out.z);
	}
}
