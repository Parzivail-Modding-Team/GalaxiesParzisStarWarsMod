package dev.pswg.feature.scrapping.table;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GadgetsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ScrappingTableBlock extends BlockWithEntity
{
	public ScrappingTableBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec()
	{
		return createCodec(ScrappingTableBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new ScrappingTableBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (type != GadgetsBlockEntities.SCRAPPING_TABLE_BLOCK_ENTITY)
			return null;
		return world.isClient() ? null : ScrappingTableBlockEntity::tick;
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved)
	{
		if(!moved)
			ItemScatterer.onStateReplaced(state, world, pos);
		super.onStateReplaced(state, world, pos, moved);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
	{
		if (!world.isClient())
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));

		return ActionResult.SUCCESS;
	}
}
