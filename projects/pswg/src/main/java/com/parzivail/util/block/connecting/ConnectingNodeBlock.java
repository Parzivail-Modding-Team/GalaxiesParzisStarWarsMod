package com.parzivail.util.block.connecting;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.parzivail.util.block.WaterloggableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;

public abstract class ConnectingNodeBlock extends WaterloggableBlock
{
	public static final BooleanProperty NORTH;
	public static final BooleanProperty EAST;
	public static final BooleanProperty SOUTH;
	public static final BooleanProperty WEST;
	public static final BooleanProperty UP;
	public static final BooleanProperty DOWN;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES;

	static
	{
		NORTH = Properties.NORTH;
		EAST = Properties.EAST;
		SOUTH = Properties.SOUTH;
		WEST = Properties.WEST;
		UP = Properties.UP;
		DOWN = Properties.DOWN;
		FACING_PROPERTIES = Util.make(Maps.newEnumMap(Direction.class), (enumMap) -> {
			enumMap.put(Direction.NORTH, NORTH);
			enumMap.put(Direction.EAST, EAST);
			enumMap.put(Direction.SOUTH, SOUTH);
			enumMap.put(Direction.WEST, WEST);
			enumMap.put(Direction.UP, UP);
			enumMap.put(Direction.DOWN, DOWN);
		});
	}

	public ConnectingNodeBlock(Settings settings)
	{
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
	}

	@Override
	protected abstract MapCodec<? extends ConnectingNodeBlock> getCodec();

	abstract boolean canConnectTo(WorldAccess world, BlockState state, BlockState otherState, BlockPos otherPos, Direction direction);

	abstract boolean isConnectedTo(WorldAccess world, BlockState state, BlockState otherState, BlockPos otherPos, Direction direction);

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		var state = super.getPlacementState(ctx);
		var pos = ctx.getBlockPos();
		var world = ctx.getWorld();

		for (var pair : FACING_PROPERTIES.entrySet())
		{
			var neighborPos = pos.offset(pair.getKey());
			var neighborState = world.getBlockState(neighborPos);
			state = state.with(pair.getValue(), canConnectTo(world, state, neighborState, neighborPos, pair.getKey()));
		}

		return state;
	}

	public ArrayList<BlockPos> getGlobalOutlets(WorldAccess world, BlockState state, BlockPos entryPoint)
	{
		var outlets = new ArrayList<BlockPos>();

		var checked = new ArrayList<BlockPos>();
		var q = new ArrayDeque<BlockPos>();
		q.add(entryPoint);

		while (!q.isEmpty())
		{
			var node = q.poll();

			checked.add(node);

			state = world.getBlockState(node);

			outlets.addAll(getLocalOutlets(world, state, node));

			for (var pos : getLocalConnections(world, state, node))
				if (!checked.contains(pos))
					q.add(pos);
		}

		return outlets;
	}

	public ArrayList<BlockPos> getLocalOutlets(WorldAccess world, BlockState state, BlockPos pos)
	{
		var outlets = new ArrayList<BlockPos>();

		for (var directions : FACING_PROPERTIES.entrySet())
		{
			var other = pos.offset(directions.getKey());
			var otherState = world.getBlockState(other);
			if (!canConnectTo(world, state, otherState, other, directions.getKey()) && state.get(directions.getValue()))
				outlets.add(other);
		}

		return outlets;
	}

	public ArrayList<BlockPos> getLocalConnections(WorldAccess world, BlockState state, BlockPos pos)
	{
		var connections = new ArrayList<BlockPos>();

		for (var directions : FACING_PROPERTIES.entrySet())
		{
			var other = pos.offset(directions.getKey());
			var otherState = world.getBlockState(other);
			if (isConnectedTo(world, state, otherState, other, directions.getKey()))
				connections.add(other);
		}

		return connections;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState otherState, WorldAccess world, BlockPos pos, BlockPos otherPos)
	{
		if (state.get(Properties.WATERLOGGED))
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		if (!state.canPlaceAt(world, pos))
		{
			world.scheduleBlockTick(pos, this, 1);
			return super.getStateForNeighborUpdate(state, direction, otherState, world, pos, otherPos);
		}
		else
		{
			return state.with(FACING_PROPERTIES.get(direction), state.get(FACING_PROPERTIES.get(direction)) || isConnectedTo(world, state, otherState, otherPos, direction));
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!player.getAbilities().allowModifyWorld || !player.getStackInHand(hand).isEmpty())
		{
			return ActionResult.PASS;
		}
		else
		{
			if (player.isSneaking())
			{
//				if (!world.isClient)
//				{
//					ArrayList<BlockPos> outlets = getGlobalOutlets(world, state, pos);
//
//					for (BlockPos outlet : outlets)
//						DebugInfoSender.addGameTestMarker((ServerWorld)world, outlet, world.getBlockState(outlet).toString(), 0, 1000);
//				}
			}
			else
				world.setBlockState(pos, state.cycle(FACING_PROPERTIES.get(hit.getSide())), 3);
			return ActionResult.success(world.isClient);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
}
