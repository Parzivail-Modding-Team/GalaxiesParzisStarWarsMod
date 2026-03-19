package dev.pswg.blockEntity.screenHandler;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class CrateGenericSmallScreenHandler extends InventoryScreenHandler
{
	public CrateGenericSmallScreenHandler(MenuType<?> screenType, int syncId, Inventory playerInventory)
	{
		this(screenType, syncId, playerInventory, new SimpleContainer(15));
	}

	public CrateGenericSmallScreenHandler(MenuType<?> screenType, int syncId, Inventory playerInventory, Container inventory)
	{
		super(screenType, syncId, inventory);
		checkContainerSize(inventory, 15);
		inventory.startOpen(playerInventory.player);

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