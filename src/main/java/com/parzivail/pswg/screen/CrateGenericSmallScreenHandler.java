package com.parzivail.pswg.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class CrateGenericSmallScreenHandler extends InventoryScreenHandler
{
	public CrateGenericSmallScreenHandler(ScreenHandlerType<?> screenType, int syncId, PlayerInventory playerInventory)
	{
		this(screenType, syncId, playerInventory, new SimpleInventory(15));
	}

	public CrateGenericSmallScreenHandler(ScreenHandlerType<?> screenType, int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		super(screenType, syncId, inventory);
		checkSize(inventory, 15);
		inventory.onOpen(playerInventory.player);

		for (var row = 0; row < 3; ++row)
			for (var column = 0; column < 5; ++column)
				this.addSlot(new Slot(inventory, column + row * 5, column * 18 + 44, row * 18 + 18));

		for (var row = 0; row < 3; ++row)
			for (var column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 86));

		for (var column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 8, 144));
	}
}
