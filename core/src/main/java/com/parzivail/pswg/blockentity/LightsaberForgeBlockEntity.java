package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.screen.LightsaberForgeScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class LightsaberForgeBlockEntity extends BlockEntity implements NamedScreenHandlerFactory
{
	public LightsaberForgeBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Workbench.LightsaberBlockEntityType, pos, state);
	}

	@Override
	public Text getDisplayName()
	{
		return Text.translatable(Resources.container("lightsaber_forge"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new LightsaberForgeScreenHandler(syncId, inv, ScreenHandlerContext.create(world, pos));
	}
}
