package dev.pswg.blockEntity;

import dev.pswg.util.VoxelShapeUtil;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterloggableRotatingBlockWithBoundsGuiEntity extends WaterloggableRotatingBlockWithGuiEntity
{
	private final VoxelShape shape;

	public WaterloggableRotatingBlockWithBoundsGuiEntity(VoxelShape shape, Properties settings, BiFunction<BlockPos, BlockState, BlockEntity> blockEntitySupplier)
	{
		super(settings.dynamicShape(), blockEntitySupplier);
		this.shape = shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		// East isn't zero, but everything defaults to facing east
		return VoxelShapeUtil.rotate(shape, (state.getValue(FACING).get2DDataValue() + 1) % 4);
	}
}
