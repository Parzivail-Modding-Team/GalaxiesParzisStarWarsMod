package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.RotatingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockTatooineHomeDoor extends RotatingBlock
{
	static
	{
		PART = IntProperty.of("part", 0, 1);
	}

	public static class Item extends BlockItem
	{
		private final BlockTatooineHomeDoor block;

		public Item(BlockTatooineHomeDoor block, net.minecraft.item.Item.Settings settings)
		{
			super(block, settings);
			this.block = block;
		}

		@Override
		protected boolean place(ItemPlacementContext context, BlockState state)
		{
			return block.canPlace(context.getWorld(), context.getBlockPos(), state, context.getPlayer()) && super.place(context, state);
		}
	}

	private static final int SIZE = 2;
	private static final VoxelShape INTERACTION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0, 1 - 0.0625, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0.0625, 0, 0.375, 1 - 0.0625, 1 - 0.0625, 0.625));
	private static final VoxelShape INTERACTION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0, 1 - 0.0625, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(1 - 1.5 * 0.0625, 0, 0.375, 1 - 0.0625, 1 - 0.0625, 0.625));
	private static final VoxelShape COLLISION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0.0625, 0, 0.375, 1 - 0.0625, 1 - 0.0625, 0.625));
	private static final VoxelShape COLLISION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(1 - 1.5 * 0.0625, 0, 0.375, 1 - 0.0625, 1 - 0.0625, 0.625));

	private static final VoxelShape[] INTERACTION_SHAPES_CLOSED = new VoxelShape[4];
	private static final VoxelShape[] INTERACTION_SHAPES_OPEN = new VoxelShape[4];
	private static final VoxelShape[] COLLISION_SHAPES_CLOSED = new VoxelShape[4];
	private static final VoxelShape[] COLLISION_SHAPES_OPEN = new VoxelShape[4];

	static
	{
		for (int i = 0; i < 4; i++)
		{
			INTERACTION_SHAPES_CLOSED[i] = VoxelShapeUtil.rotate(INTERACTION_SHAPE_CLOSED, i);
			INTERACTION_SHAPES_OPEN[i] = VoxelShapeUtil.rotate(INTERACTION_SHAPE_OPEN, i);
			COLLISION_SHAPES_CLOSED[i] = VoxelShapeUtil.rotate(COLLISION_SHAPE_CLOSED, i);
			COLLISION_SHAPES_OPEN[i] = VoxelShapeUtil.rotate(COLLISION_SHAPE_OPEN, i);
		}
	}

	public static final IntProperty PART;

	public BlockTatooineHomeDoor(Settings settings)
	{
		super(settings);

		this.setDefaultState(this.stateManager.getDefaultState().with(PART, 0));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(PART);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getShape(state, world, pos, INTERACTION_SHAPES_OPEN, INTERACTION_SHAPES_CLOSED);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getShape(state, world, pos, COLLISION_SHAPES_OPEN, COLLISION_SHAPES_CLOSED);
	}

	protected VoxelShape getShape(BlockState state, BlockView world, BlockPos pos, VoxelShape[] openShapes, VoxelShape[] closedShapes)
	{
		BlockPos controllerPos = getController(world, pos);
		TatooineHomeDoorBlockEntity e = (TatooineHomeDoorBlockEntity)world.getBlockEntity(controllerPos);

		int rotation = (state.get(ROTATION) + 3) % 4;

		if (e == null || !e.isOpening() || e.isMoving())
			return openShapes[rotation];

		return closedShapes[rotation];
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SwgBlocks.Door.TatooineHomeController);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(ROTATION, (MathHelper.floor((double)((ctx.getPlayerYaw() - 90) * divisions / 360.0F) + 0.5D) + 2) & (divisions - 1));
	}

	protected BlockPos getController(BlockView world, BlockPos self)
	{
		return self.down();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (world.isClient)
			return ActionResult.SUCCESS;
		else
		{
			BlockPos controllerPos = getController(world, pos);
			TatooineHomeDoorBlockEntity e = (TatooineHomeDoorBlockEntity)world.getBlockEntity(controllerPos);

			assert e != null;

			if (!e.isMoving())
			{
				e.setPowered(false);
				e.startMoving();
				return ActionResult.SUCCESS;
			}

			return ActionResult.CONSUME;
		}
	}

	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
	{
		BlockPos controllerPos = getController(world, pos);
		TatooineHomeDoorBlockEntity e = (TatooineHomeDoorBlockEntity)world.getBlockEntity(controllerPos);

		if (e == null)
			return;

		boolean wasPowered = e.isPowered();
		boolean isPowered = world.isReceivingRedstonePower(pos);

		if (!world.isClient && block != this)
		{
			e.setPowered(isPowered);
			if (!e.isMoving())
			{
				if (wasPowered && !isPowered && !e.isOpening())
				{
					e.setDirection(false);
					e.startMoving();
				}
				else if (isPowered && !wasPowered && e.isOpening())
				{
					e.setDirection(true);
					e.startMoving();
				}
			}
		}
	}

	public boolean canPlace(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer)
	{
		for (int i = 1; i < SIZE; i++)
		{
			pos = pos.up();
			if (!world.isAir(pos))
				return false;
		}

		return true;
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient)
		{
			for (int i = 1; i < SIZE; i++)
			{
				pos = pos.up();
				world.setBlockState(pos, SwgBlocks.Door.TatooineHomeFiller.getDefaultState().with(ROTATION, state.get(ROTATION)).with(PART, i), 3);
			}

			world.updateNeighbors(pos, Blocks.AIR);
			state.updateNeighbors(world, pos, 3);
		}
	}

	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (!world.isClient)
		{
			int part = state.get(PART);

			for (int i = 0; i < part; i++)
				destroyPart(world, pos.down(i + 1), player);

			for (int i = 0; i < SIZE - part - 1; i++)
				destroyPart(world, pos.up(i + 1), player);
		}

		super.onBreak(world, pos, state, player);
	}

	private void destroyPart(World world, BlockPos blockPos, PlayerEntity player)
	{
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof BlockTatooineHomeDoor)
		{
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
			world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
		}
	}
}
