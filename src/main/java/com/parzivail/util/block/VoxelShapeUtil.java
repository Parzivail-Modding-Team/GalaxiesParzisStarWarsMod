package com.parzivail.util.block;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
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

	public static VoxelShape getCentered(float length, float width, float height)
	{
		length /= 32;
		width /= 32;
		height /= 16;
		return VoxelShapes.cuboid(0.5 - length, 0, 0.5 - width, 0.5 + length, height, 0.5 + width);
	}

	public static VoxelShape rotate(VoxelShape shape, int times)
	{
		return rotate(shape, Direction.Axis.Y, times, 0.5f, 0, 0.5f);
	}

	public static VoxelShape rotate(VoxelShape shape, Direction.Axis axis, int times, float cX, float cY, float cZ)
	{
		VoxelShape rotatedShape = VoxelShapes.empty();
		for (Box box : shape.getBoundingBoxes())
		{
			VoxelShape rotatedBox = rotateAABB(box, axis, times, cX, cY, cZ);
			rotatedShape = VoxelShapes.union(rotatedShape, rotatedBox);
		}
		return rotatedShape;
	}

	private static VoxelShape rotateAABB(Box box, Direction.Axis axis, int times, float cX, float cY, float cZ)
	{
		double tmp;

		double minX = box.getMin(Direction.Axis.X);
		double minY = box.getMin(Direction.Axis.Y);
		double minZ = box.getMin(Direction.Axis.Z);

		double maxX = box.getMax(Direction.Axis.X);
		double maxY = box.getMax(Direction.Axis.Y);
		double maxZ = box.getMax(Direction.Axis.Z);

		switch (axis)
		{
			case X:
				for (int i = 0; i < times; i++)
				{
					tmp = minY;
					minY = cY + minZ - cZ;
					minZ = cZ - tmp + cY;
					tmp = maxY;
					maxY = cY + maxZ - cZ;
					maxZ = cZ - tmp + cY;
				}
				break;
			case Y:
				for (int i = 0; i < times; i++)
				{
					tmp = minZ;
					minZ = cZ + minX - cX;
					minX = cX - tmp + cZ;
					tmp = maxZ;
					maxZ = cZ + maxX - cX;
					maxX = cX - tmp + cZ;
				}
				break;
			case Z:
				for (int i = 0; i < times; i++)
				{
					tmp = minX;
					minX = cX + minY - cY;
					minY = cY - tmp + cX;
					tmp = maxX;
					maxX = cX + maxY - cY;
					maxY = cY - tmp + cX;
				}
				break;
		}

		return VoxelShapes.cuboid(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
