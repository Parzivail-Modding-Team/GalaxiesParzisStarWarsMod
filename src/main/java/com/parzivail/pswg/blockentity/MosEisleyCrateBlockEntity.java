package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.screen.MosEisleyCrateScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class MosEisleyCrateBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public MosEisleyCrateBlockEntity()
	{
		super(SwgBlocks.Crate.MosEisleyBlockEntityType, 15);
	}

	@Override
	public Text getDisplayName()
	{
		return new TranslatableText(Resources.container("crate_mos_eisley"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new MosEisleyCrateScreenHandler(syncId, inv, this);
	}
}
