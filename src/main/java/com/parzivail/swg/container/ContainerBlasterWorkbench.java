package com.parzivail.swg.container;


import com.parzivail.swg.weapon.ItemBlasterRifle;
import com.parzivail.tile.TileBlasterWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlasterWorkbench extends Container
{
	private TileBlasterWorkbench tile;
	private final BlasterSlot slot;

	public ContainerBlasterWorkbench(IInventory inventory, TileBlasterWorkbench tile)
	{
		this.tile = tile;
		int i;
		int j;

		// container inventory
		this.addSlotToContainer(slot = new BlasterSlot(tile, 0, 62, 17));

		// player inventory
		for (i = 0; i < 3; ++i)
		{
			for (j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		return this.tile.isUseableByPlayer(player);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
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
		else if (!this.slot.getHasStack() && this.slot.isItemValid(itemstack1) && itemstack1.stackSize == 1)
			if (!this.mergeItemStack(itemstack1, 0, 1, false))
				return null;

		if (itemstack1.stackSize == 0)
			slot.putStack(null);
		else
			slot.onSlotChanged();

		if (itemstack1.stackSize == itemstack.stackSize)
			return null;

		slot.onPickupFromSlot(player, itemstack1);

		return itemstack;
	}

	class BlasterSlot extends Slot
	{
		public BlasterSlot(IInventory inventory, int id, int x, int y)
		{
			super(inventory, id, x, y);
		}

		/**
		 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
		 */
		public boolean isItemValid(ItemStack stack)
		{
			return stack != null && stack.getItem() instanceof ItemBlasterRifle;
		}

		/**
		 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
		 * case of armor slots)
		 */
		public int getSlotStackLimit()
		{
			return 1;
		}
	}
}