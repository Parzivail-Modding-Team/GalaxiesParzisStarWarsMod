package dev.pswg.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtil
{
	public static VoxelShape getCenteredCube(float width, float height)
	{
		width /= 32;
		height /= 16;
		return Shapes.box(0.5 - width, 0, 0.5 - width, 0.5 + width, height, 0.5 + width);
	}

	public static VoxelShape getCenteredCube(float width, float height, float dX, float dZ)
	{
		width /= 32;
		height /= 16;
		dX /= 16;
		dZ /= 16;
		return Shapes.box(0.5 - width + dX, 0, 0.5 - width + dZ, 0.5 + width + dX, height, 0.5 + width + dZ);
	}

	public static VoxelShape getCentered(float length, float width, float height)
	{
		length /= 32;
		width /= 32;
		height /= 16;
		return Shapes.box(0.5 - length, 0, 0.5 - width, 0.5 + length, height, 0.5 + width);
	}

	public static VoxelShape rotateToFace(VoxelShape shape, Direction direction)
	{
		if (direction == Direction.UP)
			return VoxelShapeUtil.rotate(shape, Direction.Axis.Z, 3, 0.5f, 0.5f, 0.5f);
		if (direction == Direction.DOWN)
			return VoxelShapeUtil.rotate(shape, Direction.Axis.Z, 1, 0.5f, 0.5f, 0.5f);
		// East isn't zero, but everything defaults to facing east
		return VoxelShapeUtil.rotate(shape, (direction.get2DDataValue() + 1) % 4);
	}

	public static VoxelShape rotate(VoxelShape shape, int times)
	{
		return rotate(shape, Direction.Axis.Y, times, 0.5f, 0, 0.5f);
	}

	public static VoxelShape rotate(VoxelShape shape, Direction.Axis axis, int times, float cX, float cY, float cZ)
	{
		var rotatedShape = Shapes.empty();
		for (var box : shape.toAabbs())
		{
			var rotatedBox = rotateAABB(box, axis, times, cX, cY, cZ);
			rotatedShape = Shapes.or(rotatedShape, rotatedBox);
		}
		return rotatedShape;
	}

	private static VoxelShape rotateAABB(AABB box, Direction.Axis axis, int times, float cX, float cY, float cZ)
	{
		double tmp;

		var minX = box.min(Direction.Axis.X);
		var minY = box.min(Direction.Axis.Y);
		var minZ = box.min(Direction.Axis.Z);

		var maxX = box.max(Direction.Axis.X);
		var maxY = box.max(Direction.Axis.Y);
		var maxZ = box.max(Direction.Axis.Z);

		switch (axis)
		{
			case X:
				for (var i = 0; i < times; i++)
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
				for (var i = 0; i < times; i++)
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
				for (var i = 0; i < times; i++)
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

		if (minX > maxX)
		{
			var temp = minX;
			minX = maxX;
			maxX = temp;
		}

		if (minY > maxY)
		{
			var temp = minY;
			minY = maxY;
			maxY = temp;
		}

		if (minZ > maxZ)
		{
			var temp = minZ;
			minZ = maxZ;
			maxZ = temp;
		}

		return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static Vec3 getCenter(VoxelShape shape)
	{
		return new Vec3((shape.min(Direction.Axis.X) + shape.max(Direction.Axis.X)) / 2,
		                 (shape.min(Direction.Axis.Y) + shape.max(Direction.Axis.Y)) / 2,
		                 (shape.min(Direction.Axis.Z) + shape.max(Direction.Axis.Z)) / 2);
	}

	public static VoxelShape union(VoxelShape... shapes){
		VoxelShape finalShape = Shapes.empty();
		for(VoxelShape shape : shapes){
			finalShape = Shapes.join(finalShape, shape, BooleanOp.OR);
		}
		return finalShape;
	}
}