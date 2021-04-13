package com.parzivail.util.client;

import com.parzivail.util.math.Point;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class ConnectedTextureHelper
{
	@FunctionalInterface
	public interface BlockConnectionFunc
	{
		boolean apply(BlockState self, BlockState other);
	}

	private static final int CONNECTED_UP = 0b0001;
	private static final int CONNECTED_DOWN = 0b0010;
	private static final int CONNECTED_LEFT = 0b0100;
	private static final int CONNECTED_RIGHT = 0b1000;

	private static final int MASK_CONNECTIONS_VERT = 0b0011;
	private static final int MASK_CONNECTIONS_HORIZ = 0b1100;

	private static Point getPointFromConnections(int connections)
	{
		int x = getOffset(connections, MASK_CONNECTIONS_HORIZ, CONNECTED_LEFT, CONNECTED_RIGHT);
		int y = getOffset(connections, MASK_CONNECTIONS_VERT, CONNECTED_UP, CONNECTED_DOWN);

		return new Point(x, y);
	}

	private static int getOffset(int connections, int directionMask, int connectionFlagNear, int connectionFlagFar)
	{
		int masked = (connections & directionMask);

		if (masked == connectionFlagNear)
			return 4;
		else if (masked == connectionFlagFar)
			return 2;
		else if (masked == (connectionFlagNear | connectionFlagFar))
			return 3;

		return 0;
	}

	public static Point getConnectedBlockTexture(BlockRenderView blockView, BlockState block, BlockPos pos, Direction facing, BlockConnectionFunc blockConnectionFunc)
	{
		if (pos == null)
			return getPointFromConnections(0);

		BlockPos up, down, left, right;

		switch (facing)
		{
			case DOWN:
			{
				up = pos.south();
				down = pos.north();
				left = pos.west();
				right = pos.east();
				break;
			}
			case UP:
			{
				up = pos.north();
				down = pos.south();
				left = pos.west();
				right = pos.east();
				break;
			}
			case NORTH:
			{
				up = pos.up();
				down = pos.down();
				left = pos.east();
				right = pos.west();
				break;
			}
			case SOUTH:
			{
				up = pos.up();
				down = pos.down();
				left = pos.west();
				right = pos.east();
				break;
			}
			case WEST:
			{
				up = pos.up();
				down = pos.down();
				left = pos.north();
				right = pos.south();
				break;
			}
			case EAST:
			{
				up = pos.up();
				down = pos.down();
				left = pos.south();
				right = pos.north();
				break;
			}
			default:
				throw new IllegalStateException("Unexpected value: " + facing);
		}

		int connections = 0;

		if (blockConnectionFunc.apply(block, blockView.getBlockState(up)))
			connections |= CONNECTED_UP;

		if (blockConnectionFunc.apply(block, blockView.getBlockState(down)))
			connections |= CONNECTED_DOWN;

		if (blockConnectionFunc.apply(block, blockView.getBlockState(left)))
			connections |= CONNECTED_LEFT;

		if (blockConnectionFunc.apply(block, blockView.getBlockState(right)))
			connections |= CONNECTED_RIGHT;

		return getPointFromConnections(connections);
	}
}
