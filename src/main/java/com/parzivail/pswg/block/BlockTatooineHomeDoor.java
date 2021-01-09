package com.parzivail.pswg.block;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
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
