package com.parzivail.pswg.block;

import com.mojang.serialization.MapCodec;
import com.parzivail.util.block.connecting.SelfConnectingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InteractableConnectingInvertedLampBlock extends SelfConnectingBlock
{
	private static final MapCodec<InteractableConnectingInvertedLampBlock> CODEC = createCodec(InteractableConnectingInvertedLampBlock::new);
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty INVERTED = Properties.INVERTED;

	public static boolean isLit(BlockState state)
	{
		return state.get(POWERED) ^ state.get(INVERTED);
	}

	public InteractableConnectingInvertedLampBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(POWERED, false).with(INVERTED, true).with(LIT, true));
	}

	@Override
	protected MapCodec<InteractableConnectingInvertedLampBlock> getCodec()
	{
		return CODEC;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!player.getStackInHand(hand).isEmpty())
			return super.onUse(state, world, pos, player, hand, hit);

		if (!player.getAbilities().allowModifyWorld)
			return ActionResult.PASS;
		else
		{
			updateState(state.cycle(INVERTED), world, pos);
			return ActionResult.success(world.isClient);
		}
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
	{
		if (!world.isClient)
			updateState(state.with(POWERED, world.isReceivingRedstonePower(pos)), world, pos);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (state.get(POWERED) && !world.isReceivingRedstonePower(pos))
			updateState(state.cycle(POWERED), world, pos);
	}

	protected static boolean updateState(BlockState state, World world, BlockPos pos)
	{
		state = state.with(LIT, isLit(state));
		return world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LIT, POWERED, INVERTED);
		super.appendProperties(builder);
	}
}
