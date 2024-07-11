package com.parzivail.pswg.block;

import com.mojang.serialization.MapCodec;
import com.parzivail.pswg.blockentity.MoistureVaporatorBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.IMoistureProvider;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MoistureVaporatorBlock extends WaterloggableRotatingBlockWithEntity implements IMoistureProvider
{
	private static final MapCodec<MoistureVaporatorBlock> CODEC = createCodec(MoistureVaporatorBlock::new);
	private final VoxelShape shape = VoxelShapeUtil.getCenteredCube(10, 40);

	public MoistureVaporatorBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<MoistureVaporatorBlock> getCodec()
	{
		return CODEC;
	}

	@Override
	public boolean providesMoisture(WorldView world, BlockPos blockPos, BlockState blockState)
	{
		return true;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return shape;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new MoistureVaporatorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (type != SwgBlocks.MoistureVaporator.Gx8BlockEntityType)
			return null;
		return world.isClient ? null : MoistureVaporatorBlockEntity::serverTick;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
	{
		if (world.isClient)
			return ActionResult.SUCCESS;
		else
		{
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			return ActionResult.CONSUME;
		}
	}
}
