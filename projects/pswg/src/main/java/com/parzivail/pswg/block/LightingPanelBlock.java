package com.parzivail.pswg.block;

import com.parzivail.util.block.InvertedLampBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightingPanelBlock extends InvertedLampBlock
{
	public static final BooleanProperty LIT = Properties.LIT;

	public LightingPanelBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, true));

	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);

	}

	public static int getBrightness(BlockState bs, int brightness)
	{
		if(bs.get(LIT)){
			return brightness;
		}else{
			return 0;
		}
	}

}
