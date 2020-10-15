package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.screen.OctagonCrateScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class OctagonCrateBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public OctagonCrateBlockEntity()
	{
		super(SwgBlocks.Crate.OctagonBlockEntityType, 39);
	}

	@Override
	public Text getDisplayName()
	{
		return new TranslatableText(Resources.container("crate_octagon"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new OctagonCrateScreenHandler(syncId, inv, this);
	}
}
