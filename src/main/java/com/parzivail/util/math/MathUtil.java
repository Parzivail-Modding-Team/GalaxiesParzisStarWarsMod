package com.parzivail.util.math;

import com.parzivail.util.primative.Matrix4f;
import com.parzivail.util.primative.Vector3f;
import com.parzivail.util.primative.Vector4f;

public class MathUtil
{
	public Vector3f transform(Matrix4f m, Vector3f v)
	{
		Vector4f out = new Vector4f();
		Matrix4f.transform(m, new Vector4f(v.x, v.y, v.z, 0), out);
		return new Vector3f(out.x, out.y, out.z);
	}
}
