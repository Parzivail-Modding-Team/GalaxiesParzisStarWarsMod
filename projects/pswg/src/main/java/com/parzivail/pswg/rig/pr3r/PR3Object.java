package com.parzivail.pswg.rig.pr3r;

import net.minecraft.util.math.Matrix4f;

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
