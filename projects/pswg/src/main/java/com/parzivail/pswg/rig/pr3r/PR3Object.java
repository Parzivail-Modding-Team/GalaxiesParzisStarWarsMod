package com.parzivail.pswg.rig.pr3r;

import org.joml.Matrix4f;

@Deprecated
public class PR3Object
{
	public final String name;
	public final Matrix4f transformationMatrix;

	public PR3Object(String name, Matrix4f transformationMatrix)
	{
		this.name = name;
		this.transformationMatrix = transformationMatrix;
	}
}
