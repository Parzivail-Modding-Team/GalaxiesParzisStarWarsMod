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

public class CrateMosEisleyBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public CrateMosEisleyBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Crate.ToolboxBlockEntityType, pos, state, 15);
	}

	@Override
	public Text getDisplayName()
	{
		return Text.translatable(Resources.container(SwgScreenTypes.Crate.Toolbox));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new CrateGenericSmallScreenHandler(SwgScreenTypes.Crate.Toolbox, syncId, inv, this);
	}
}
