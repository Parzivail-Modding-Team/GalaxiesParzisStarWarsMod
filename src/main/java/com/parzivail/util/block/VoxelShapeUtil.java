package com.parzivail.util.block;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class VoxelShapeUtil
{
	public static VoxelShape getCenteredCube(float width, float height)
	{
		width /= 32;
		height /= 16;
		return VoxelShapes.cuboid(0.5 - width, 0, 0.5 - width, 0.5 + width, height, 0.5 + width);
	}
}
