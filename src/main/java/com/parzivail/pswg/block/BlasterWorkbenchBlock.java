package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.BlasterWorkbenchBlockEntity;
import com.parzivail.util.block.rotating.RotatingBlockWithGuiEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;

public class BlasterWorkbenchBlock extends RotatingBlockWithGuiEntity
{
	public BlasterWorkbenchBlock(Settings settings)
	{
		super(settings, BlasterWorkbenchBlockEntity::new);
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}
}
