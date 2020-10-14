package com.parzivail.pswg.screen;

import com.parzivail.pswg.container.SwgScreenTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BlasterWorkbenchScreenHandler extends ScreenHandler
{
	private final Inventory inventory;

	public BlasterWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(1));
	}

	public BlasterWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		super(SwgScreenTypes.Workbench.Blaster, syncId);
		checkSize(inventory, 1);
		this.inventory = inventory;
		inventory.onOpen(playerInventory.player);

		for (int row = 0; row < 3; ++row)
		{
			for (int column = 0; column < 9; ++column)
			{
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 122 + row * 18));
			}
		}

		for (int column = 0; column < 9; ++column)
		{
			this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 180));
		}

		this.addSlot(new Slot(inventory, 0, -26, 26));
	}

	public ItemStack transferSlot(PlayerEntity player, int index)
	{
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack())
		{
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index < 9)
			{
				if (!this.insertItem(itemStack2, 9, 45, true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.insertItem(itemStack2, 0, 9, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty())
			{
				slot.setStack(ItemStack.EMPTY);
			}
			else
			{
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
