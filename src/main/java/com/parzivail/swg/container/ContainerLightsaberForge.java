package com.parzivail.swg.container;

import com.parzivail.swg.item.lightsaber.ItemLightsaber;
import com.parzivail.swg.tile.TileLightsaberForge;
import com.parzivail.util.ui.SpecificSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLightsaberForge extends Container
{
	public static final int SLOT_SABER = 0;

	private final TileLightsaberForge tile;
	private final SpecificSlot saberSlot;

	public ContainerLightsaberForge(IInventory playerInventory, TileLightsaberForge tile)
	{
		this.tile = tile;
		int i;
		int j;

		// container inventory
		addSlotToContainer(saberSlot = new SpecificSlot(tile, SLOT_SABER, 14, 63, s -> s.getItem() instanceof ItemLightsaber));

		// player inventory
		for (i = 0; i < 3; ++i)
			for (j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 48 + j * 18, 159 + i * 18));

		for (i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(playerInventory, i, 48 + i * 18, 217));
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		return tile.isUseableByPlayer(player);
	}

	/**
	 * Called when a player shift-clicks on a blasterSlot. You must override this or you will crash when someone does that.
	 */
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIdx)
	{
		Slot slot = (Slot)inventorySlots.get(slotIdx);

		if (slot == null || !slot.getHasStack())
			return null;

		ItemStack itemstack1 = slot.getStack();
		ItemStack itemstack = itemstack1.copy();

		// Transfer into player inventory
		if (slotIdx < tile.getSizeInventory())
		{
			int maxSlotId = tile.getSizeInventory();
			if (!mergeItemStack(itemstack1, maxSlotId, maxSlotId + 36, true))
				return null;
		}
		else if (!saberSlot.getHasStack() && saberSlot.isItemValid(itemstack1) && itemstack1.stackSize == 1)
		{
			if (!mergeItemStack(itemstack1, saberSlot.slotNumber, 1, false))
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
