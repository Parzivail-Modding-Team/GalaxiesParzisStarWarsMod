package com.parzivail.pswg.block;

import com.parzivail.util.block.InvertedLampBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightFixtureBlock extends InvertedLampBlock
{
	public static final IntProperty BRIGHTNESS = Properties.LEVEL_3;

	public LightFixtureBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(BRIGHTNESS, 3));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(BRIGHTNESS);
	}

	public static int getBrightness(int brightness)
	{
		return switch (brightness)
				{
					case 1 -> 5;
					case 2 -> 10;
					case 3 -> 15;
					default -> throw new IllegalStateException("Unexpected value: " + brightness);
				};
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!player.getAbilities().allowModifyWorld)
			return ActionResult.PASS;
		else
		{
			world.setBlockState(pos, state.cycle(BRIGHTNESS), Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS);
			return ActionResult.success(world.isClient);
		}
	}
}
