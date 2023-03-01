package com.parzivail.pswg.screen;

import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.util.inventory.StrictSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class AstromechScreenHandler extends ScreenHandler
{
	private final Inventory inventory;
	private final AstromechEntity entity;

	public AstromechScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, AstromechEntity entity)
	{
		super(null, syncId);
		this.inventory = inventory;
		this.entity = entity;
		inventory.onOpen(playerInventory.player);

		this.addSlot(new StrictSlot(inventory, 0, 8, 18, itemStack -> true));
		this.addSlot(new StrictSlot(inventory, 1, 26, 18, itemStack -> true));
		this.addSlot(new StrictSlot(inventory, 2, 44, 18, itemStack -> true));
		this.addSlot(new StrictSlot(inventory, 3, 62, 18, itemStack -> true));
		this.addSlot(new StrictSlot(inventory, 4, 80, 18, itemStack -> true));

		for (var k = 0; k < 3; ++k)
		{
			for (var l = 0; l < 9; ++l)
			{
				this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 102 + k * 18 + -18));
			}
		}

		for (var k = 0; k < 9; ++k)
		{
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.entity.getInventory() == this.inventory
		       && this.inventory.canPlayerUse(player)
		       && this.entity.isAlive()
		       && this.entity.distanceTo(player) < 8.0F;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index)
	{
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot != null && slot.hasStack())
		{
			var itemStack2 = slot.getStack();
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
	public void onClosed(PlayerEntity player)
	{
		super.onClosed(player);
		this.inventory.onClose(player);
	}
}
