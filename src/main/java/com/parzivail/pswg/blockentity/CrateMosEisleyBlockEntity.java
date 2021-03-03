package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgScreenTypes;
import com.parzivail.pswg.screen.CrateGenericSmallScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CrateMosEisleyBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public CrateMosEisleyBlockEntity()
	{
		super(SwgBlocks.Crate.ToolboxBlockEntityType, 15);
	}

	@Override
	public Text getDisplayName()
	{
		return new TranslatableText(Resources.container("crate_mos_eisley"));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new CrateGenericSmallScreenHandler(SwgScreenTypes.Crate.MosEisley, syncId, inv, this);
	}
}
