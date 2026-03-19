package dev.pswg.feature.brewing;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GadgetsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MixerBlock extends BaseEntityBlock
{
	public MixerBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return simpleCodec(MixerBlock::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new MixerBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type)
	{
		if (type != GadgetsBlockEntities.MIXER_BLOCK_ENTITY)
			return null;
		return world.isClientSide() ? null : MixerBlockEntity::tick;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved)
	{
		if (!moved)
			Containers.updateNeighboursAfterDestroy(state, world, pos);
		super.affectNeighborsAfterRemoval(state, world, pos, moved);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
	{
		if (!world.isClientSide())
			player.openMenu(state.getMenuProvider(world, pos));
		return super.useWithoutItem(state, world, pos, player, hit);
	}
}
