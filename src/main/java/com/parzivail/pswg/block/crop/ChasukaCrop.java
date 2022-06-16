package com.parzivail.pswg.block.crop;

import com.parzivail.pswg.container.SwgItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class ChasukaCrop extends CropBlock
{
	static
	{
		AGE = Properties.AGE_2;
		AGE_TO_SHAPE = new VoxelShape[] { Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D) };
	}

	public static final IntProperty AGE;
	private static final VoxelShape[] AGE_TO_SHAPE;

	public ChasukaCrop(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	@Override
	public IntProperty getAgeProperty()
	{
		return AGE;
	}

	@Override
	public int getMaxAge()
	{
		return 2;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected ItemConvertible getSeedsItem()
	{
		return SwgItems.Seeds.ChasukaSeeds;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (random.nextInt(3) != 0)
		{
			super.randomTick(state, world, pos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world)
	{
		return super.getGrowthAmount(world) / 3;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return AGE_TO_SHAPE[state.get(this.getAgeProperty())];
	}
}
