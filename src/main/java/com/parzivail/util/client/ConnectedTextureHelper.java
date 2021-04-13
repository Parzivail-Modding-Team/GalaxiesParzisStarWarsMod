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
			return 3;
		else if (masked == connectionFlagFar)
			return 1;
		else if (masked == (connectionFlagNear | connectionFlagFar))
			return 2;

		return 0;
	}

	public static Point getConnectedBlockTexture(BlockRenderView blockView, BlockState block, BlockPos pos, Direction facing, boolean horizontalConnect, boolean verticalConnect, boolean lateralConnect, BlockConnectionFunc blockConnectionFunc)
	{
		if (pos == null)
			return getPointFromConnections(0);

		BlockPos up, down, left, right;

		switch (facing)
		{
			case DOWN:
			{
				up = lateralConnect ? pos.south() : null;
				down = lateralConnect ? pos.north() : null;
				left = lateralConnect ? pos.west() : null;
				right = lateralConnect ? pos.east() : null;
				break;
			}
			case UP:
			{
				up = lateralConnect ? pos.north() : null;
				down = lateralConnect ? pos.south() : null;
				left = lateralConnect ? pos.west() : null;
				right = lateralConnect ? pos.east() : null;
				break;
			}
			case NORTH:
			{
				up = verticalConnect ? pos.up() : null;
				down = verticalConnect ? pos.down() : null;
				left = horizontalConnect ? pos.east() : null;
				right = horizontalConnect ? pos.west() : null;
				break;
			}
			case SOUTH:
			{
				up = verticalConnect ? pos.up() : null;
				down = verticalConnect ? pos.down() : null;
				left = horizontalConnect ? pos.west() : null;
				right = horizontalConnect ? pos.east() : null;
				break;
			}
			case WEST:
			{
				up = verticalConnect ? pos.up() : null;
				down = verticalConnect ? pos.down() : null;
				left = horizontalConnect ? pos.north() : null;
				right = horizontalConnect ? pos.south() : null;
				break;
			}
			case EAST:
			{
				up = verticalConnect ? pos.up() : null;
				down = verticalConnect ? pos.down() : null;
				left = horizontalConnect ? pos.south() : null;
				right = horizontalConnect ? pos.north() : null;
				break;
			}
			default:
				throw new IllegalStateException("Unexpected value: " + facing);
		}

		int connections = 0;

		if (up != null && blockConnectionFunc.apply(block, blockView.getBlockState(up)))
			connections |= CONNECTED_UP;

		if (down != null && blockConnectionFunc.apply(block, blockView.getBlockState(down)))
			connections |= CONNECTED_DOWN;

		if (left != null && blockConnectionFunc.apply(block, blockView.getBlockState(left)))
			connections |= CONNECTED_LEFT;

		if (right != null && blockConnectionFunc.apply(block, blockView.getBlockState(right)))
			connections |= CONNECTED_RIGHT;

		return getPointFromConnections(connections);
	}
}
