package dev.pswg.block.plant;

import dev.pswg.container.GalaxiesItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChasukaCrop extends CropBlock
{
	static
	{
		AGE = BlockStateProperties.AGE_2;
		AGE_TO_SHAPE = new VoxelShape[] { Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D) };
	}

	public static final IntegerProperty AGE;
	private static final VoxelShape[] AGE_TO_SHAPE;

	public ChasukaCrop(BlockBehaviour.Properties settings)
	{
		super(settings);
	}

	@Override
	public IntegerProperty getAgeProperty()
	{
		return AGE;
	}

	@Override
	public int getMaxAge()
	{
		return 2;
	}

	@Override
	protected ItemLike getBaseSeedId()
	{
		return GalaxiesItems.CHASUKA_SEEDS;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		if (random.nextInt(3) != 0)
		{
			super.randomTick(state, world, pos, random);
		}
	}

	@Override
	protected int getBonemealAgeIncrease(Level world)
	{
		return super.getBonemealAgeIncrease(world) / 3;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return AGE_TO_SHAPE[state.getValue(this.getAgeProperty())];
	}
}
