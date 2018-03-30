package com.parzivail.tile;

import com.parzivail.swg.Resources;
import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.weapon.ItemBlasterRifle;
import com.parzivail.util.block.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileBlasterWorkbench extends TileEntity implements IInventory
{
	private ItemStack blaster;

	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return blaster;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (this.blaster == null)
			return null;

		ItemStack itemstack;

		if (this.blaster.stackSize <= amount)
		{
			itemstack = this.blaster;
			this.blaster = null;
			this.markDirty();
			return itemstack;
		}
		else
		{
			itemstack = this.blaster.splitStack(amount);

			if (this.blaster.stackSize == 0)
				this.blaster = null;

			this.markDirty();
			return itemstack;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.blaster == null)
			return null;

		ItemStack itemstack = this.blaster;
		this.blaster = null;
		return itemstack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack contents)
	{
		this.blaster = contents;

		if (contents != null && contents.stackSize > this.getInventoryStackLimit())
			contents.stackSize = this.getInventoryStackLimit();

		this.markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return Resources.containerDot(BlockRegister.blasterWorkbench.name);
	}

	@Override
	public boolean isCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return BlockUtils.canPlayerUse(this, player);
	}

	@Override
	public void openChest()
	{

	}

	@Override
	public void closeChest()
	{

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return stack != null && stack.getItem() instanceof ItemBlasterRifle;
	}

	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (!tag.hasKey("Item"))
		{
			blaster = null;
			return;
		}

		NBTTagCompound blasterNbt = tag.getCompoundTag("Item");
		this.blaster = ItemStack.loadItemStackFromNBT(blasterNbt);
	}

	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		if (blaster == null)
			return;

		NBTTagCompound blasterNbt = new NBTTagCompound();
		this.blaster.writeToNBT(blasterNbt);
		tag.setTag("Item", blasterNbt);
	}
}
