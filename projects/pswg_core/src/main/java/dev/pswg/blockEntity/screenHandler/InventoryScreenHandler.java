package dev.pswg.blockEntity.screenHandler;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InventoryScreenHandler extends AbstractContainerMenu
{
	protected final Container inventory;

	public InventoryScreenHandler(@Nullable MenuType<?> type, int syncId, Container inventory)
	{
		super(type, syncId);
		this.inventory = inventory;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot != null && slot.hasItem())
		{
			var itemStack2 = slot.getItem();
			itemStack = itemStack2.copy();
			if (index < this.inventory.getContainerSize())
			{
				if (!this.moveItemStackTo(itemStack2, this.inventory.getContainerSize(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.moveItemStackTo(itemStack2, 0, this.inventory.getContainerSize(), false))
			{
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty())
			{
				slot.setByPlayer(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}

		return itemStack;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return this.inventory.stillValid(player);
	}
}