package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SelfConnectingGlassBlock extends SelfConnectingBlock
{
	public SelfConnectingGlassBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return Shapes.empty();
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state)
	{
		return true;
	}
}