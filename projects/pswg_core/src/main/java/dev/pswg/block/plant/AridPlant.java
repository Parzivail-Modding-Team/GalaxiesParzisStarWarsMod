package dev.pswg.block.plant;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GalaxiesBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class AridPlant extends PlantBlock
{
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public AridPlant(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends PlantBlock> getCodec()
	{
		return createCodec(AridPlant::new);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos)
	{
		return floor.isIn(GalaxiesBlocks.Tags.ARID_PLANT_PLACEABLE);
	}
}