package com.parzivail.util.math;

import net.minecraft.client.util.math.MatrixStack;

public class MatrixStackUtil
{
	public static void scalePos(MatrixStack stack, float x, float y, float z)
	{
		var entry = stack.peek();
		entry.getPositionMatrix().scale(x, y, z);
	}
}
