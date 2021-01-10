package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
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
	private static final VoxelShape SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0, 1 - 0.0625, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0.0625, 0, 0.375, 1 - 0.0625, 1 - 0.0625, 0.625));
	private static final VoxelShape SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0, 1 - 0.0625, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(1 - 1.5 * 0.0625, 0, 0.375, 1 - 0.0625, 1 - 0.0625, 0.625));

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
		BlockPos controllerPos = getController(world, pos);
		TatooineHomeDoorBlockEntity e = (TatooineHomeDoorBlockEntity)world.getBlockEntity(controllerPos);

		if (e == null || !e.isOpening())
			return SHAPE_OPEN;

		return SHAPE_CLOSED;
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

			if (!e.isMoving())
				e.startMoving();
		}
		return super.onUse(state, world, pos, player, hand, hit);
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
		if (!world.isClient && player.isCreative())
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
