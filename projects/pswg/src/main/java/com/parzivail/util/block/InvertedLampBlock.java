package com.parzivail.util.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InvertedLampBlock extends Block
{
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public InvertedLampBlock(AbstractBlock.Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, true));
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(LIT, !ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
	{
		if (!world.isClient)
		{
			boolean bl = state.get(LIT);
			if (bl == world.isReceivingRedstonePower(pos))
			{
				if (bl)
				{
					world.scheduleBlockTick(pos, this, 4);
				}
				else
				{
					world.setBlockState(pos, state.cycle(LIT), 2);
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (state.get(LIT) && world.isReceivingRedstonePower(pos))
		{
			world.setBlockState(pos, state.cycle(LIT), 2);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}
}
