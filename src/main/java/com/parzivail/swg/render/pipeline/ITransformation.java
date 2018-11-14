package com.parzivail.swg.render.pipeline;

import javax.vecmath.Matrix4f;

public interface ITransformation
{
	Matrix4f getMatrix();

	EnumFacing rotate(EnumFacing facing);

	int rotate(EnumFacing facing, int vertexIndex);
}
