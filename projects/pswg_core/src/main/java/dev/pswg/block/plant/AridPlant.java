package dev.pswg.block.plant;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GalaxiesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AridPlant extends VegetationBlock
{
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public AridPlant(Properties settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends VegetationBlock> codec()
	{
		return simpleCodec(AridPlant::new);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos)
	{
		return floor.is(GalaxiesBlocks.Tags.ARID_PLANT_PLACEABLE);
	}
}