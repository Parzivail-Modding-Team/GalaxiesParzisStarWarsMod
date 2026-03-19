package dev.pswg.blockEntity;

import dev.pswg.block.WaterloggableRotatingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class WaterloggableRotatingBlockWithEntity extends WaterloggableRotatingBlock implements EntityBlock
{
	protected WaterloggableRotatingBlockWithEntity(BlockBehaviour.Properties settings)
	{
		super(settings);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int type, int data)
	{
		super.triggerEvent(state, world, pos, type, data);
		var blockEntity = world.getBlockEntity(pos);
		return blockEntity != null && blockEntity.triggerEvent(type, data);
	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos)
	{
		var blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof MenuProvider ? (MenuProvider)blockEntity : null;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved)
	{
			var blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Container)
			{
				Containers.dropContents(world, pos, (Container)blockEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
		super.affectNeighborsAfterRemoval(state, world, pos, moved);
	}
}