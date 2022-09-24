package com.parzivail.util.block.rotating;

import com.parzivail.util.ParziUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.function.BiFunction;

public class WaterloggableRotating3BlockWithGuiEntity extends WaterloggableRotatingBlockWithGuiEntity
{
	public enum Side implements StringIdentifiable
	{
		LEFT("left"),
		MIDDLE("middle"),
		RIGHT("right");

		private final String name;

		Side(String name)
		{
			this.name = name;
		}

		@Override
		public String asString()
		{
			return name;
		}
	}

	public static final EnumProperty<Side> SIDE = EnumProperty.of("side", Side.class);

	public WaterloggableRotating3BlockWithGuiEntity(Settings settings, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityBiFunction)
	{
		super(settings, blockEntityBiFunction);
		this.setDefaultState(this.getDefaultState().with(SIDE, Side.MIDDLE));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(SIDE);
	}

	@Override
	protected BlockPos getBlockEntityUsePos(BlockState state, BlockPos pos)
	{
		if (state.get(SIDE) != Side.MIDDLE)
			return getControllerPos(pos, state);
		return pos;
	}

	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		if (!neighborState.isOf(this))
		{
			if (state.get(SIDE) == Side.MIDDLE)
			{
				var peripheral = getPeripheralPos(pos, state);
				if (neighborPos.equals(peripheral.getLeft()) || neighborPos.equals(peripheral.getRight()))
					return Blocks.AIR.getDefaultState();
			}
			else
			{
				var controllerPos = getControllerPos(pos, state);
				if (neighborPos.equals(controllerPos))
					return Blocks.AIR.getDefaultState();
			}
		}

		return state;
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack)
	{
		var rotation = state.get(FACING);

		switch (rotation)
		{
			case NORTH ->
			{
				world.setBlockState(pos.west(), state.with(SIDE, Side.LEFT), Block.NOTIFY_ALL);
				world.setBlockState(pos.east(), state.with(SIDE, Side.RIGHT), Block.NOTIFY_ALL);
			}
			case SOUTH ->
			{
				world.setBlockState(pos.west(), state.with(SIDE, Side.RIGHT), Block.NOTIFY_ALL);
				world.setBlockState(pos.east(), state.with(SIDE, Side.LEFT), Block.NOTIFY_ALL);
			}
			case EAST ->
			{
				world.setBlockState(pos.north(), state.with(SIDE, Side.LEFT), Block.NOTIFY_ALL);
				world.setBlockState(pos.south(), state.with(SIDE, Side.RIGHT), Block.NOTIFY_ALL);
			}
			case WEST ->
			{
				world.setBlockState(pos.north(), state.with(SIDE, Side.RIGHT), Block.NOTIFY_ALL);
				world.setBlockState(pos.south(), state.with(SIDE, Side.LEFT), Block.NOTIFY_ALL);
			}
		}

		ParziUtil.LOG.error("Invalid rotation for 3-block: %s @ %s", rotation, pos);
	}

	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		var peripheral = getPeripheralPos(pos, state);
		return world.isAir(peripheral.getLeft()) && world.isAir(peripheral.getRight());
	}

	private Pair<BlockPos, BlockPos> getPeripheralPos(BlockPos controller, BlockState state)
	{
		switch (state.get(FACING))
		{
			case NORTH, SOUTH ->
			{
				return new Pair<>(controller.east(), controller.west());
			}
			case WEST, EAST ->
			{
				return new Pair<>(controller.north(), controller.south());
			}
		}

		ParziUtil.LOG.error("Invalid rotation for 3-block: %s @ %s", state.get(FACING), controller);
		return new Pair<>(controller, controller);
	}

	private BlockPos getControllerPos(BlockPos self, BlockState state)
	{
		var side = state.get(SIDE);
		if (side == Side.MIDDLE)
			return self;

		var rotation = state.get(FACING);

		switch (rotation)
		{
			case NORTH ->
			{
				if (side == Side.LEFT)
					return self.east();
				return self.west();
			}
			case SOUTH ->
			{
				if (side == Side.RIGHT)
					return self.east();
				return self.west();
			}
			case WEST ->
			{
				if (side == Side.LEFT)
					return self.north();
				return self.south();
			}
			case EAST ->
			{
				if (side == Side.RIGHT)
					return self.north();
				return self.south();
			}
		}

		ParziUtil.LOG.error("Invalid rotation for 3-block: %s @ %s", rotation, self);
		return self;
	}
}
