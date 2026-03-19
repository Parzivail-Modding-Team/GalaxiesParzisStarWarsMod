package dev.pswg.block.plant;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.GalaxiesItems;
import dev.pswg.util.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoloShrubBlock extends VegetationBlock implements BonemealableBlock
{
	static
	{
		AGE = BlockStateProperties.AGE_3;
		BLOOMING = BlockStateProperties.BLOOM;
		SMALL_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
		LARGE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	}

	public static final IntegerProperty AGE;
	public static final BooleanProperty BLOOMING;
	private static final VoxelShape SMALL_SHAPE;
	private static final VoxelShape LARGE_SHAPE;

	public MoloShrubBlock(Properties settings)
	{
		super(settings.randomTicks());
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(BLOOMING, false));
	}

	@Override
	protected MapCodec<? extends VegetationBlock> codec()
	{
		return simpleCodec(MoloShrubBlock::new);
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData)
	{
		return new ItemStack(GalaxiesItems.MOLO_FLOWER);
	}

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos)
	{
		return floor.is(GalaxiesBlocks.Tags.BUSH_PLACEABLE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		if (state.getValue(AGE) == 0)
			return SMALL_SHAPE;
		else
			return state.getValue(AGE) < 3 ? LARGE_SHAPE : super.getShape(state, world, pos, context);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		int i = state.getValue(AGE);
		var finalState = state;

		if (random.nextInt(5) == 0 && i < 3)
			finalState = finalState.setValue(AGE, i + 1);

		finalState = finalState.setValue(BLOOMING, WorldUtil.isNightTime(world));

		world.setBlock(pos, finalState, Block.UPDATE_CLIENTS);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(AGE, BLOOMING);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state)
	{
		return state.getValue(AGE) < 3;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state)
	{
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state)
	{
		var i = Math.min(3, state.getValue(AGE) + 1);
		world.setBlock(pos, state.setValue(AGE, i), Block.UPDATE_CLIENTS);
	}
}
