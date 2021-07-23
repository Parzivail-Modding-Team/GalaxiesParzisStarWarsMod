package com.parzivail.pswg.block.crop;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.util.world.WorldUtil;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class MoloShrubBlock extends PlantBlock implements Fertilizable
{
	static
	{
		AGE = Properties.AGE_3;
		BLOOMING = BooleanProperty.of("blooming");
		SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
		LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	}

	public static final IntProperty AGE;
	public static final BooleanProperty BLOOMING;
	private static final VoxelShape SMALL_SHAPE;
	private static final VoxelShape LARGE_SHAPE;

	public MoloShrubBlock(Settings settings)
	{
		super(settings.ticksRandomly());
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(BLOOMING, false));
	}

	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SwgItems.Natural.MoloFlower);
	}

	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos)
	{
		var block = floor.getBlock();
		return BlockTags.SAND.contains(block) ||
		       block == SwgBlocks.Dirt.DesertLoam ||
		       block == SwgBlocks.Salt.Caked ||
		       block == Blocks.GRASS_BLOCK ||
		       block == Blocks.DIRT ||
		       block == Blocks.COARSE_DIRT ||
		       block == Blocks.PODZOL;
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if (state.get(AGE) == 0)
			return SMALL_SHAPE;
		else
			return state.get(AGE) < 3 ? LARGE_SHAPE : super.getOutlineShape(state, world, pos, context);
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		int i = state.get(AGE);
		var finalState = state;

		if (random.nextInt(5) == 0 && i < 3)
			finalState = finalState.with(AGE, i + 1);

		finalState = finalState.with(BLOOMING, WorldUtil.isNightTime(world));

		world.setBlockState(pos, finalState, Block.NOTIFY_LISTENERS);
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AGE, BLOOMING);
	}

	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient)
	{
		return state.get(AGE) < 3;
	}

	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state)
	{
		return true;
	}

	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state)
	{
		var i = Math.min(3, state.get(AGE) + 1);
		world.setBlockState(pos, state.with(AGE, i), Block.NOTIFY_LISTENERS);
	}
}
