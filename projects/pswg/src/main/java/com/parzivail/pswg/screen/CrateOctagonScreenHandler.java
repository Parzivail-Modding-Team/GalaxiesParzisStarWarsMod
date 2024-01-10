package com.parzivail.pswg.screen;

import com.parzivail.pswg.container.SwgScreenTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

public class CrateOctagonScreenHandler extends InventoryScreenHandler
{
	public CrateOctagonScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(39));
	}

	public CrateOctagonScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		super(SwgScreenTypes.Crate.Kyber, syncId, inventory);
		checkSize(inventory, 39);
		inventory.onOpen(playerInventory.player);

		for (var row = 0; row < 3; ++row)
			for (var column = 0; column < 13; ++column)
				this.addSlot(new Slot(inventory, column + row * 13, column * 18 + 8, row * 18 + 18));

		for (var row = 0; row < 3; ++row)
			for (var column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 44, row * 18 + 86));

		for (var column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 44, 144));
	}
}
