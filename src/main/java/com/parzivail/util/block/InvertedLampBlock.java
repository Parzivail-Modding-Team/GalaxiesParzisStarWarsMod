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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class InvertedLampBlock extends Block
{
	static
	{
		LIT = RedstoneTorchBlock.LIT;
	}

	public static final BooleanProperty LIT;

	public InvertedLampBlock(AbstractBlock.Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, true));
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(LIT, !ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
	{
		if (!world.isClient)
		{
			boolean bl = state.get(LIT);
			if (bl == world.isReceivingRedstonePower(pos))
			{
				if (bl)
				{
					world.createAndScheduleBlockTick(pos, this, 4);
				}
				else
				{
					world.setBlockState(pos, state.cycle(LIT), 2);
				}
			}
		}
	}

	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (state.get(LIT) && world.isReceivingRedstonePower(pos))
		{
			world.setBlockState(pos, state.cycle(LIT), 2);
		}
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}
}
