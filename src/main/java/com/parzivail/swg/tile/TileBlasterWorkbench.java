package com.parzivail.swg.tile;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.blaster.ItemBlasterRifle;
import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.util.block.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileBlasterWorkbench extends TileEntity implements IInventory
{
	private ItemStack[] stacks = new ItemStack[1];

	@Override
	public int getSizeInventory()
	{
		return stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return stacks[slot];
	}

	public ItemStack getBlaster()
	{
		return stacks[0];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (stacks[slot] == null)
			return null;

		ItemStack itemstack;

		if (stacks[slot].stackSize <= amount)
		{
			itemstack = stacks[slot];
			stacks[slot] = null;
			markDirty();
			return itemstack;
		}
		else
		{
			itemstack = stacks[slot].splitStack(amount);

			if (stacks[slot].stackSize == 0)
				stacks[slot] = null;

			markDirty();
			return itemstack;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (stacks[slot] == null)
			return null;

		ItemStack itemstack = stacks[slot];
		stacks[slot] = null;
		return itemstack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack contents)
	{
		stacks[slot] = contents;

		if (contents != null && contents.stackSize > getInventoryStackLimit())
			contents.stackSize = getInventoryStackLimit();

		markDirty();
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

		NBTTagList nbttaglist = tag.getTagList("Items", 10);
		stacks = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < stacks.length)
			{
				stacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < stacks.length; ++i)
		{
			if (stacks[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				stacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		tag.setTag("Items", nbttaglist);
	}
}
