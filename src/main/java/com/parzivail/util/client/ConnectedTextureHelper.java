package com.parzivail.util.client;

import com.parzivail.util.math.Point;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class ConnectedTextureHelper
{
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

	public static Point getConnectedBlockTexture(BlockRenderView blockView, BlockState block, BlockPos pos, Direction facing, boolean horizontalConnect, boolean verticalConnect, boolean lateralConnect)
	{
		if (pos == null)
			return getPointFromConnections(0);

		BooleanProperty up, down, left, right;

		switch (facing)
		{
			case DOWN:
			{
				up = lateralConnect ? ConnectingBlock.SOUTH : null;
				down = lateralConnect ? ConnectingBlock.NORTH : null;
				left = lateralConnect ? ConnectingBlock.WEST : null;
				right = lateralConnect ? ConnectingBlock.EAST : null;
				break;
			}
			case UP:
			{
				up = lateralConnect ? ConnectingBlock.NORTH : null;
				down = lateralConnect ? ConnectingBlock.SOUTH : null;
				left = lateralConnect ? ConnectingBlock.WEST : null;
				right = lateralConnect ? ConnectingBlock.EAST : null;
				break;
			}
			case NORTH:
			{
				up = verticalConnect ? ConnectingBlock.UP : null;
				down = verticalConnect ? ConnectingBlock.DOWN : null;
				left = horizontalConnect ? ConnectingBlock.EAST : null;
				right = horizontalConnect ? ConnectingBlock.WEST : null;
				break;
			}
			case SOUTH:
			{
				up = verticalConnect ? ConnectingBlock.UP : null;
				down = verticalConnect ? ConnectingBlock.DOWN : null;
				left = horizontalConnect ? ConnectingBlock.WEST : null;
				right = horizontalConnect ? ConnectingBlock.EAST : null;
				break;
			}
			case WEST:
			{
				up = verticalConnect ? ConnectingBlock.UP : null;
				down = verticalConnect ? ConnectingBlock.DOWN : null;
				left = horizontalConnect ? ConnectingBlock.NORTH : null;
				right = horizontalConnect ? ConnectingBlock.SOUTH : null;
				break;
			}
			case EAST:
			{
				up = verticalConnect ? ConnectingBlock.UP : null;
				down = verticalConnect ? ConnectingBlock.DOWN : null;
				left = horizontalConnect ? ConnectingBlock.SOUTH : null;
				right = horizontalConnect ? ConnectingBlock.NORTH : null;
				break;
			}
			default:
				throw new IllegalStateException("Unexpected value: " + facing);
		}

		int connections = 0;

		if (up != null && block.get(up))
			connections |= CONNECTED_UP;

		if (down != null && block.get(down))
			connections |= CONNECTED_DOWN;

		if (left != null && block.get(left))
			connections |= CONNECTED_LEFT;

		if (right != null && block.get(right))
			connections |= CONNECTED_RIGHT;

		return getPointFromConnections(connections);
	}
}
