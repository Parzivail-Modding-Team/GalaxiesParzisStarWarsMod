package dev.pswg.block.plant;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GalaxiesItems;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class VaporatorMushroomBlock extends PlantBlock
{
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

	public VaporatorMushroomBlock(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends PlantBlock> getCodec()
	{
		return createCodec(VaporatorMushroomBlock::new);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	@Override
	protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData)
	{
		return new ItemStack(GalaxiesItems.VAPORATOR_MUSHROOM);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos)
	{
		return floor.isOpaqueFullCube() && floor.isIn(BlockTags.SAND);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return this.canPlantOnTop(blockState, world, blockPos);
	}
}