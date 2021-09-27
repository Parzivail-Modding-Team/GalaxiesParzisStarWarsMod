package com.parzivail.pswg.entity.collision;

import com.parzivail.util.math.Matrix4fUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class TransformedBox extends Box
{
	private Matrix4f transformation;

	public TransformedBox(BlockPos pos)
	{
		super(pos);
	}

	public void setTransformation(Matrix4f t)
	{
		transformation = t;
	}

	public Matrix4f getTransformation()
	{
		return transformation;
	}

	@Override
	public Optional<Vec3d> raycast(Vec3d min, Vec3d max)
	{
		var tMin = Matrix4fUtil.transform(min, transformation);
		var tMax = Matrix4fUtil.transform(max, transformation);
		return super.raycast(tMin, tMax);
	}
}
