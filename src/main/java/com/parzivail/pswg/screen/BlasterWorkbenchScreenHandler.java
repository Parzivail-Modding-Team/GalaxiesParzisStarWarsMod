package com.parzivail.pswg.screen;

import com.parzivail.pswg.container.SwgScreenTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

public class BlasterWorkbenchScreenHandler extends InventoryScreenHandler
{
	public BlasterWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(1));
	}

	public BlasterWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		super(SwgScreenTypes.Workbench.Blaster, syncId, inventory);
		checkSize(inventory, 1);
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 14, 63));

		for (var row = 0; row < 3; ++row)
			for (var column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 48, row * 18 + 159));

		for (var column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 48, 217));
	}
}
