package com.parzivail.swg.container;


import com.parzivail.swg.item.blaster.ItemBlasterRifle;
import com.parzivail.swg.tile.TileBlasterWorkbench;
import com.parzivail.util.ui.SpecificSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlasterWorkbench extends Container
{
	public static final int SLOT_BLASTER = 0;

	private TileBlasterWorkbench tile;
	private final SpecificSlot blasterSlot;

	public ContainerBlasterWorkbench(IInventory playerInventory, TileBlasterWorkbench tile)
	{
		this.tile = tile;
		int i;
		int j;

		// container inventory
		this.addSlotToContainer(blasterSlot = new SpecificSlot(tile, SLOT_BLASTER, 14, 63, s -> s.getItem() instanceof ItemBlasterRifle));

		// player inventory
		for (i = 0; i < 3; ++i)
			for (j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 48 + j * 18, 159 + i * 18));

		for (i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(playerInventory, i, 48 + i * 18, 217));
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

		if (itemstack1.stackSize == 0)
			slot.putStack(null);
		else
			slot.onSlotChanged();

		if (itemstack1.stackSize == itemstack.stackSize)
			return null;

		slot.onPickupFromSlot(player, itemstack1);

		return itemstack;
	}
}