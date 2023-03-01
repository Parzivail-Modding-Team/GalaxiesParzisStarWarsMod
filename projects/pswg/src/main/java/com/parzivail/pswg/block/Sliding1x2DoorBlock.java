package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.SlidingDoorBlockEntity;
import com.parzivail.pswg.item.DoorInsertItem;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithEntity;
import com.parzivail.util.world.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class Sliding1x2DoorBlock extends WaterloggableRotatingBlockWithEntity
{
	private enum ShapeKeyType
	{
		OUTLINE,
		COLLISION
	}

	private record ShapeKey(DoubleBlockHalf half, ShapeKeyType type, boolean open, int rotation)
	{
	}

	private static final VoxelShape TOP_INTERACTION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 1 - 0.0625, 0, 0.75, 1, 1),
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625)
	);
	private static final VoxelShape TOP_INTERACTION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 1 - 0.0625, 0, 0.75, 1, 1),
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 1 - 1.5 * 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625)
	);
	private static final VoxelShape TOP_COLLISION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625)
	);
	private static final VoxelShape TOP_COLLISION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 1 - 1.5 * 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625)
	);
	private static final VoxelShape BOTTOM_INTERACTION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0.0625, 0.0625, 0.625, 1, 1 - 0.0625),
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.0625, 1)
	);
	private static final VoxelShape BOTTOM_INTERACTION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0.0625, 1 - 1.5 * 0.0625, 0.625, 1, 1 - 0.0625),
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.0625, 1)
	);
	private static final VoxelShape BOTTOM_COLLISION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0.0625, 0.0625, 0.625, 1, 1 - 0.0625)
	);
	private static final VoxelShape BOTTOM_COLLISION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0.0625, 1 - 1.5 * 0.0625, 0.625, 1, 1 - 0.0625)
	);

	private static final HashMap<ShapeKey, VoxelShape> SHAPES = new HashMap<>();

	static
	{
		for (var i = 0; i < 4; i++)
		{
			SHAPES.put(new ShapeKey(DoubleBlockHalf.UPPER, ShapeKeyType.OUTLINE, false, i), VoxelShapeUtil.rotate(TOP_INTERACTION_SHAPE_CLOSED, i));
			SHAPES.put(new ShapeKey(DoubleBlockHalf.UPPER, ShapeKeyType.OUTLINE, true, i), VoxelShapeUtil.rotate(TOP_INTERACTION_SHAPE_OPEN, i));
			SHAPES.put(new ShapeKey(DoubleBlockHalf.UPPER, ShapeKeyType.COLLISION, false, i), VoxelShapeUtil.rotate(TOP_COLLISION_SHAPE_CLOSED, i));
			SHAPES.put(new ShapeKey(DoubleBlockHalf.UPPER, ShapeKeyType.COLLISION, true, i), VoxelShapeUtil.rotate(TOP_COLLISION_SHAPE_OPEN, i));
			SHAPES.put(new ShapeKey(DoubleBlockHalf.LOWER, ShapeKeyType.OUTLINE, false, i), VoxelShapeUtil.rotate(BOTTOM_INTERACTION_SHAPE_CLOSED, i));
			SHAPES.put(new ShapeKey(DoubleBlockHalf.LOWER, ShapeKeyType.OUTLINE, true, i), VoxelShapeUtil.rotate(BOTTOM_INTERACTION_SHAPE_OPEN, i));
			SHAPES.put(new ShapeKey(DoubleBlockHalf.LOWER, ShapeKeyType.COLLISION, false, i), VoxelShapeUtil.rotate(BOTTOM_COLLISION_SHAPE_CLOSED, i));
			SHAPES.put(new ShapeKey(DoubleBlockHalf.LOWER, ShapeKeyType.COLLISION, true, i), VoxelShapeUtil.rotate(BOTTOM_COLLISION_SHAPE_OPEN, i));
		}
	}

	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final BooleanProperty MOVING = BooleanProperty.of("moving");
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final IntProperty DOOR_COLOR = IntProperty.of("door_color", 0, 17);

	public Sliding1x2DoorBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(MOVING, false).with(OPEN, true).with(POWERED, false).with(DOOR_COLOR, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(HALF);
		builder.add(OPEN);
		builder.add(MOVING);
		builder.add(POWERED);
		builder.add(DOOR_COLOR);
	}

	private static int getRotationKey(Direction direction)
	{
		return (direction.getHorizontal() + 1) % 4;
	}

	public static Optional<DyeColor> getDoorColor(BlockState state)
	{
		var color = state.get(DOOR_COLOR);

		if (color == 0)
			return Optional.empty();

		return Optional.of(DyeColor.byId(color - 1));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPES.get(new ShapeKey(state.get(HALF), ShapeKeyType.OUTLINE, state.get(OPEN), getRotationKey(state.get(FACING))));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPES.get(new ShapeKey(state.get(HALF), ShapeKeyType.COLLISION, state.get(OPEN), getRotationKey(state.get(FACING))));
	}

	private void playOpenCloseSound(World world, BlockPos pos, boolean open)
	{
		//		world.syncWorldEvent((PlayerEntity)null, open ? this.getOpenSoundEventId() : this.getCloseSoundEventId(), pos, 0);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (!world.isClient && player.isCreative())
			WorldUtil.destroyDoubleBlockFromBottom(world, pos, state, player);

		super.onBreak(world, pos, state, player);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		return switch (type)
				{
					case LAND, AIR -> state.get(OPEN);
					default -> false;
				};
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (getDoorColor(state).isEmpty() && player.getStackInHand(hand).getItem() instanceof DoorInsertItem dii)
		{
			// TODO: allow door removing
			// TODO: drop door insert when broken
			state = state.with(DOOR_COLOR, dii.getColor().getId() + 1).with(OPEN, state.get(POWERED));

			if (!player.getAbilities().creativeMode)
				player.getStackInHand(hand).decrement(1);
		}
		else if (getDoorColor(state).isPresent())
		{
			// Do not allow door to be cycled (i.e. closed from its
			// default-open state) if there is no door insert
			state = state.cycle(OPEN);

			var be = (SlidingDoorBlockEntity)world.getBlockEntity((state.get(HALF) == DoubleBlockHalf.LOWER) ? pos : pos.down());
			be.start(state.get(OPEN));
			state = state.with(MOVING, true);

			playOpenCloseSound(world, pos, state.get(OPEN));
			world.emitGameEvent(player, state.get(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		}

		world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
		return ActionResult.success(world.isClient);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
	{
		boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (!this.getDefaultState().isOf(sourceBlock) && bl != state.get(POWERED))
		{
			if (bl != state.get(OPEN))
			{
				this.playOpenCloseSound(world, pos, bl);
				world.emitGameEvent(null, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
			}

			world.setBlockState(pos, state.with(POWERED, bl).with(OPEN, bl), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP))
			return neighborState.isOf(this) && neighborState.get(HALF) != doubleBlockHalf ? state.with(FACING, neighborState.get(FACING)).with(MOVING, neighborState.get(MOVING)).with(OPEN, neighborState.get(OPEN)).with(DOOR_COLOR, neighborState.get(DOOR_COLOR)).with(POWERED, neighborState.get(POWERED)) : Blocks.AIR.getDefaultState();
		else
			return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		BlockPos blockPos = ctx.getBlockPos();
		World world = ctx.getWorld();

		if (blockPos.getY() >= world.getTopY() - 1 || !world.getBlockState(blockPos.up()).canReplace(ctx))
			return null;

		boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(HALF, DoubleBlockHalf.LOWER).with(POWERED, bl);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack)
	{
		world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation)
	{
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return mirror == BlockMirror.NONE ? state : state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos)
	{
		return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		if (state.get(HALF) != DoubleBlockHalf.LOWER)
			return null;
		return new SlidingDoorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (state.get(HALF) != DoubleBlockHalf.LOWER)
			return null;
		return SlidingDoorBlockEntity::tick;
	}
}
