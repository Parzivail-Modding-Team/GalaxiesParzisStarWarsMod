package com.parzivail.util.client;

import com.parzivail.util.Lumberjack;
import com.parzivail.util.math.Point;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class ConnectedTextureHelper
{
	public static record Sides(Point TopLeft, Point TopRight, Point BottomLeft, Point BottomRight)
	{
	}

	private static final int CONNECTED_UP = 0b00000001;
	private static final int CONNECTED_DOWN = 0b00000010;
	private static final int CONNECTED_LEFT = 0b00000100;
	private static final int CONNECTED_RIGHT = 0b00001000;

	private static final int CONNECTED_UPLEFT = (CONNECTED_UP | CONNECTED_LEFT);
	private static final int CONNECTED_UPRIGHT = (CONNECTED_UP | CONNECTED_RIGHT);
	private static final int CONNECTED_DOWNLEFT = (CONNECTED_DOWN | CONNECTED_LEFT);
	private static final int CONNECTED_DOWNRIGHT = (CONNECTED_DOWN | CONNECTED_RIGHT);

	private static final int CONNECTED_DIAG_UPLEFT = 0b00010000;
	private static final int CONNECTED_DIAG_UPRIGHT = 0b00100000;
	private static final int CONNECTED_DIAG_DOWNLEFT = 0b01000000;
	private static final int CONNECTED_DIAG_DOWNRIGHT = 0b10000000;

	private static final int MASK_CONNECTIONS_VERT = 0b0011;
	private static final int MASK_CONNECTIONS_HORIZ = 0b1100;

	private static Sides getPointFromConnections(int connections)
	{
		if (connections == 0b11111111)
			return null;

		return new Sides(
				getCornerConnection(connections, CONNECTED_UP, CONNECTED_LEFT, CONNECTED_DIAG_UPLEFT, 0, 0),
				getCornerConnection(connections, CONNECTED_UP, CONNECTED_RIGHT, CONNECTED_DIAG_UPRIGHT, 1, 0),
				getCornerConnection(connections, CONNECTED_DOWN, CONNECTED_LEFT, CONNECTED_DIAG_DOWNLEFT, 0, 1),
				getCornerConnection(connections, CONNECTED_DOWN, CONNECTED_RIGHT, CONNECTED_DIAG_DOWNRIGHT, 1, 1)
		);
	}

	private static Point getCornerConnection(int connections, int vertEdge, int horizEdge, int diagBit, int x, int y)
	{
		var edgeMask = vertEdge | horizEdge;

		if ((connections & edgeMask) == 0)
			return new Point(x, y); // interior corner
		else if ((connections & edgeMask) == edgeMask)
		{
			if ((connections & diagBit) == 0)
				return new Point(x + 2, y + 2); // exterior corner
			else
				return new Point(-x, -y); // no corner
		}
		else if ((connections & horizEdge) == horizEdge)
			return new Point(x + 2, y); // horizontal connection
		else if ((connections & vertEdge) == vertEdge)
			return new Point(x, y + 2); // vertical connection
		else
		{
			Lumberjack.debug("Impossible connection case?");
			return null;
		}
	}

	private static int getOffset(int connections, int directionMask, int connectionFlagNear, int connectionFlagFar)
	{
		var masked = (connections & directionMask);

		if (masked == connectionFlagNear)
			return 3;
		else if (masked == connectionFlagFar)
			return 1;
		else if (masked == (connectionFlagNear | connectionFlagFar))
			return 2;

		return 0;
	}

	public static Sides getConnectedBlockTexture(BlockRenderView blockView, BlockState block, BlockPos pos, Direction facing, boolean horizontalConnect, boolean verticalConnect, boolean lateralConnect)
	{
		if (pos == null)
			return getPointFromConnections(0);

		Direction up, down, left, right;

		switch (facing)
		{
			case DOWN -> {
				up = lateralConnect ? Direction.SOUTH : null;
				down = lateralConnect ? Direction.NORTH : null;
				left = lateralConnect ? Direction.WEST : null;
				right = lateralConnect ? Direction.EAST : null;
			}
			case UP -> {
				up = lateralConnect ? Direction.NORTH : null;
				down = lateralConnect ? Direction.SOUTH : null;
				left = lateralConnect ? Direction.WEST : null;
				right = lateralConnect ? Direction.EAST : null;
			}
			case NORTH -> {
				up = verticalConnect ? Direction.UP : null;
				down = verticalConnect ? Direction.DOWN : null;
				left = horizontalConnect ? Direction.EAST : null;
				right = horizontalConnect ? Direction.WEST : null;
			}
			case SOUTH -> {
				up = verticalConnect ? Direction.UP : null;
				down = verticalConnect ? Direction.DOWN : null;
				left = horizontalConnect ? Direction.WEST : null;
				right = horizontalConnect ? Direction.EAST : null;
			}
			case WEST -> {
				up = verticalConnect ? Direction.UP : null;
				down = verticalConnect ? Direction.DOWN : null;
				left = horizontalConnect ? Direction.NORTH : null;
				right = horizontalConnect ? Direction.SOUTH : null;
			}
			case EAST -> {
				up = verticalConnect ? Direction.UP : null;
				down = verticalConnect ? Direction.DOWN : null;
				left = horizontalConnect ? Direction.SOUTH : null;
				right = horizontalConnect ? Direction.NORTH : null;
			}
			default -> throw new IllegalStateException("Unexpected value: " + facing);
		}

		var connections = 0;

		if (up != null && shouldConnect(blockView, pos, pos.offset(up)))
			connections |= CONNECTED_UP;

		if (down != null && shouldConnect(blockView, pos, pos.offset(down)))
			connections |= CONNECTED_DOWN;

		if (left != null && shouldConnect(blockView, pos, pos.offset(left)))
			connections |= CONNECTED_LEFT;

		if (right != null && shouldConnect(blockView, pos, pos.offset(right)))
			connections |= CONNECTED_RIGHT;

		if (up != null && left != null && shouldConnect(blockView, pos, pos.offset(up).offset(left)))
			connections |= CONNECTED_DIAG_UPLEFT;

		if (up != null && right != null && shouldConnect(blockView, pos, pos.offset(up).offset(right)))
			connections |= CONNECTED_DIAG_UPRIGHT;

		if (down != null && left != null && shouldConnect(blockView, pos, pos.offset(down).offset(left)))
			connections |= CONNECTED_DIAG_DOWNLEFT;

		if (down != null && right != null && shouldConnect(blockView, pos, pos.offset(down).offset(right)))
			connections |= CONNECTED_DIAG_DOWNRIGHT;

		return getPointFromConnections(connections);
	}

	private static boolean shouldConnect(BlockRenderView world, BlockPos a, BlockPos b)
	{
		return shouldConnect(world.getBlockState(a), world.getBlockState(b));
	}

	private static boolean shouldConnect(BlockState a, BlockState b)
	{
		return a.isOf(b.getBlock());
	}
}
