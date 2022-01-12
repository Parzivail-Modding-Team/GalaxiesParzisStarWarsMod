package com.parzivail.util.math;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class MatrixStackUtil
{
	public static void scalePos(MatrixStack stack, float x, float y, float z)
	{
		var entry = stack.peek();
		entry.getPositionMatrix().multiply(Matrix4f.scale(x, y, z));
	}
}
