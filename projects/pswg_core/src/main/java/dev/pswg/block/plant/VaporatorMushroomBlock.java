package dev.pswg.block.plant;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GalaxiesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VaporatorMushroomBlock extends VegetationBlock
{
	protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

	public VaporatorMushroomBlock(BlockBehaviour.Properties settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends VegetationBlock> codec()
	{
		return simpleCodec(VaporatorMushroomBlock::new);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData)
	{
		return new ItemStack(GalaxiesItems.VAPORATOR_MUSHROOM);
	}

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos)
	{
		return floor.isSolidRender() && floor.is(BlockTags.SAND);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
	{
		BlockPos blockPos = pos.below();
		BlockState blockState = world.getBlockState(blockPos);
		return this.mayPlaceOn(blockState, world, blockPos);
	}
}
