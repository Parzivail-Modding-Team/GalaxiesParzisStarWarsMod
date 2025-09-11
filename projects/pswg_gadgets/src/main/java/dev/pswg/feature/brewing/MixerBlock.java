package dev.pswg.feature.brewing;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GadgetsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MixerBlock extends BlockWithEntity
{
	public MixerBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec()
	{
		return createCodec(MixerBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new MixerBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (type != GadgetsBlockEntities.MIXER_BLOCK_ENTITY)
			return null;
		return world.isClient ? null : MixerBlockEntity::tick;
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if (!moved)
			ItemScatterer.onStateReplaced(state, newState, world, pos);
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
	{
		return super.onUse(state, world, pos, player, hit);
	}
}
