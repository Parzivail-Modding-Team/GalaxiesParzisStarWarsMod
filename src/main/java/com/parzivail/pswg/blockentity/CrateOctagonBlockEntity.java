package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.screen.CrateOctagonScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class CrateOctagonBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public CrateOctagonBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Crate.KyberCrateBlockEntityType, pos, state, 39);
	}

	@Override
	public Text getDisplayName()
	{
		return Text.translatable(Resources.container("kyber_crate"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new CrateOctagonScreenHandler(syncId, inv, this);
	}
}
