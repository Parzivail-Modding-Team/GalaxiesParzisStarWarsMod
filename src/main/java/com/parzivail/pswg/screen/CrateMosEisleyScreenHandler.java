package com.parzivail.pswg.screen;

import com.parzivail.pswg.container.SwgScreenTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

public class CrateMosEisleyScreenHandler extends InventoryScreenHandler
{
	public CrateMosEisleyScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(15));
	}

	public CrateMosEisleyScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		super(SwgScreenTypes.Crate.MosEisley, syncId, inventory);
		checkSize(inventory, 15);
		inventory.onOpen(playerInventory.player);

		for (int row = 0; row < 3; ++row)
			for (int column = 0; column < 5; ++column)
				this.addSlot(new Slot(inventory, column + row * 5, column * 18 + 44, row * 18 + 18));

		for (int row = 0; row < 3; ++row)
			for (int column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 86));

		for (int column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 8, 144));
	}
}
