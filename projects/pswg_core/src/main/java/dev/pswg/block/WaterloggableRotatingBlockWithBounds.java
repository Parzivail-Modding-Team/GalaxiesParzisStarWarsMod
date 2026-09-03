package dev.pswg.block;

import dev.pswg.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;
import org.jspecify.annotations.Nullable;

public class WaterloggableRotatingBlockWithBounds extends WaterloggableRotatingBlock
{
	public enum Substrate
	{
		BEHIND,
		BELOW,
		NONE
	}

	private final VoxelShape shape;
	private final Substrate requiresSubstrate;

	public WaterloggableRotatingBlockWithBounds(VoxelShape shape, Substrate requiresSubstrate, BlockBehaviour.Properties settings)
	{
		super(settings.dynamicShape());
		this.shape = shape;
		this.requiresSubstrate = requiresSubstrate;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return VoxelShapeUtil.rotateToFace(shape, state.getValue(FACING));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		if (requiresSubstrate == Substrate.NONE)
			return super.canSurvive(state, level, pos);

		var substrateDirection = getSubstrateDirection(state);

		//TODO: figure this out
		/*switch (requiresSubstrate)
		{
			case BEHIND -> {
				BlockPos blockPos = pos.offset(substrateDirection.getUnitVec3i());
				return sideCoversSmallSquare(level, blockPos, substrateDirection.getOpposite());
			}
			case BELOW -> {
				BlockPos blockPos = pos.below();
				return hasTopRim(level, blockPos) || sideCoversSmallSquare(level, blockPos, Direction.UP);
			}
			default -> throw new IllegalStateException("Unexpected value: " + requiresSubstrate);
		}*/
		return super.canSurvive(state, level, pos);
	}


	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
	{
		if (state.getValue(BlockStateProperties.WATERLOGGED))
			level.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, pos, 0, 0));
		super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
	}

	private Direction getSubstrateDirection(BlockState state)
	{
		if (requiresSubstrate == Substrate.BELOW)
			return Direction.DOWN;

		return state.getValue(FACING).getOpposite();
	}
}