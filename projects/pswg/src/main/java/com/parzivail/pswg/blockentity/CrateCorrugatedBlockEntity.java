package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgScreenTypes;
import com.parzivail.pswg.screen.CrateGenericSmallScreenHandler;
import com.parzivail.util.blockentity.InventoryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class CrateCorrugatedBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public CrateCorrugatedBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Crate.CorrugatedCrateBlockEntityType, pos, state, 15);
	}

	@Override
	public Text getDisplayName()
	{
		return Text.translatable(Resources.container(SwgScreenTypes.Crate.Corrugated));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new CrateGenericSmallScreenHandler(SwgScreenTypes.Crate.Corrugated, syncId, inv, this);
	}
}
