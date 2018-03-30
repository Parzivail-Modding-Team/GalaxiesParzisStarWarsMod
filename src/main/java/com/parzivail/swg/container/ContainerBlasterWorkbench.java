package com.parzivail.swg.container;


import com.parzivail.swg.weapon.ItemBlasterRifle;
import com.parzivail.tile.TileBlasterWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class ContainerBlasterWorkbench extends Container
{
	private TileBlasterWorkbench tile;
	private final SpecificSlot blasterSlot;
	private final SpecificSlot scopeSlot;
	private final SpecificSlot barrelSlot;
	private final SpecificSlot gripSlot;

	public ContainerBlasterWorkbench(IInventory inventory, TileBlasterWorkbench tile)
	{
		this.tile = tile;
		int i;
		int j;

		// container inventory
		this.addSlotToContainer(blasterSlot = new SpecificSlot(tile, 0, 14, 63, s -> s.getItem() instanceof ItemBlasterRifle));
		this.addSlotToContainer(scopeSlot = new SpecificSlot(tile, 1, 100, 29, s -> s.getItem() instanceof ItemBlasterRifle));
		this.addSlotToContainer(barrelSlot = new SpecificSlot(tile, 2, 196, 43, s -> s.getItem() instanceof ItemBlasterRifle));
		this.addSlotToContainer(gripSlot = new SpecificSlot(tile, 3, 162, 84, s -> s.getItem() instanceof ItemBlasterRifle));

		// player inventory
		for (i = 0; i < 3; ++i)
		{
			for (j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 48 + j * 18, 159 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(inventory, i, 48 + i * 18, 217));
		}
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		return this.tile.isUseableByPlayer(player);
	}

	/**
	 * Called when a player shift-clicks on a blasterSlot. You must override this or you will crash when someone does that.
	 */
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIdx)
	{
		Slot slot = (Slot)this.inventorySlots.get(slotIdx);

		if (slot == null || !slot.getHasStack())
			return null;

		ItemStack itemstack1 = slot.getStack();
		ItemStack itemstack = itemstack1.copy();

		// Transfer into player inventory
		if (slotIdx < tile.getSizeInventory())
		{
			int maxSlotId = tile.getSizeInventory();
			if (!this.mergeItemStack(itemstack1, maxSlotId, maxSlotId + 36, true))
				return null;
		}
		else if (!this.blasterSlot.getHasStack() && this.blasterSlot.isItemValid(itemstack1) && itemstack1.stackSize == 1)
		{
			if (!this.mergeItemStack(itemstack1, blasterSlot.slotNumber, 1, false))
				return null;
		}
		else if (!this.scopeSlot.getHasStack() && this.scopeSlot.isItemValid(itemstack1) && itemstack1.stackSize == 1)
		{
			if (!this.mergeItemStack(itemstack1, scopeSlot.slotNumber, 1, false))
				return null;
		}
		else if (!this.barrelSlot.getHasStack() && this.barrelSlot.isItemValid(itemstack1) && itemstack1.stackSize == 1)
		{
			if (!this.mergeItemStack(itemstack1, barrelSlot.slotNumber, 1, false))
				return null;
		}
		else if (!this.gripSlot.getHasStack() && this.gripSlot.isItemValid(itemstack1) && itemstack1.stackSize == 1)
		{
			if (!this.mergeItemStack(itemstack1, gripSlot.slotNumber, 1, false))
				return null;
		}

		if (itemstack1.stackSize == 0)
			slot.putStack(null);
		else
			slot.onSlotChanged();

		if (itemstack1.stackSize == itemstack.stackSize)
			return null;

		slot.onPickupFromSlot(player, itemstack1);

		return itemstack;
	}

	class SpecificSlot extends Slot
	{
		private final Function<ItemStack, Boolean> funcIsItemValid;

		public SpecificSlot(IInventory inventory, int id, int x, int y, Function<ItemStack, Boolean> isItemValid)
		{
			super(inventory, id, x, y);
			this.funcIsItemValid = isItemValid;
		}

		/**
		 * Check if the stack is a valid item for this blasterSlot. Always true beside for the armor slots.
		 */
		public boolean isItemValid(ItemStack stack)
		{
			return stack != null && funcIsItemValid.apply(stack);
		}

		/**
		 * Returns the maximum stack size for a given blasterSlot (usually the same as getInventoryStackLimit(), but 1 in the
		 * case of armor slots)
		 */
		public int getSlotStackLimit()
		{
			return 1;
		}
	}
}