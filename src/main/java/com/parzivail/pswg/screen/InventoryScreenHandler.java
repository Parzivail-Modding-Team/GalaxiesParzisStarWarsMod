package com.parzivail.pswg.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public class InventoryScreenHandler extends ScreenHandler
{
	protected final Inventory inventory;

	public InventoryScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, Inventory inventory)
	{
		super(type, syncId);
		this.inventory = inventory;
	}

	public ItemStack transferSlot(PlayerEntity player, int index)
	{
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack())
		{
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index < this.inventory.size())
			{
				if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false))
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
		}

		return itemStack;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
