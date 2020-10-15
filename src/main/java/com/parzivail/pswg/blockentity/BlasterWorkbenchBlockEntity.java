package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BlasterWorkbenchBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public BlasterWorkbenchBlockEntity()
	{
		super(SwgBlocks.Workbench.BlasterBlockEntityType, 1);
	}

	@Override
	public Text getDisplayName()
	{
		return new TranslatableText(Resources.modcolon("container.workbench_blaster"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new BlasterWorkbenchScreenHandler(syncId, inv, this);
	}
}
