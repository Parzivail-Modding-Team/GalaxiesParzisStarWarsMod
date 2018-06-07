package com.parzivail.swg.tile;

import com.parzivail.util.block.TileEntityRotate;

public class TileGunRack extends TileEntityRotate
{
	//	private ItemStack[] guns = new ItemStack[12];
	//
	//	@Override
	//	public void readFromNBT(NBTTagCompound p_145839_1_)
	//	{
	//		super.readFromNBT(p_145839_1_);
	//
	//		NBTTagList nbttaglist = p_145839_1_.getTagList("guns", 10);
	//
	//		guns = new ItemStack[12];
	//
	//		for (int i = 0; i < nbttaglist.tagCount(); ++i)
	//		{
	//			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
	//
	//			if (i < guns.length)
	//				this.guns[i] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
	//		}
	//	}
	//
	//	public ItemStack[] getGuns()
	//	{
	//		return guns;
	//	}
	//
	//	public boolean pushGun(ItemStack gun)
	//	{
	//		boolean b = false;
	//		for (int i = 0; i < this.guns.length; i++)
	//			if (this.guns[i] == null)
	//			{
	//				this.guns[i] = gun;
	//				b = true;
	//				break;
	//			}
	//		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	//		return b;
	//	}
	//
	//	public ItemStack popGun()
	//	{
	//		ItemStack s = null;
	//		for (int i = this.guns.length - 1; i >= 0; i--)
	//			if (this.guns[i] != null)
	//			{
	//				s = this.guns[i];
	//				this.guns[i] = null;
	//				break;
	//			}
	//		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	//		return s;
	//	}
	//
	//	@Override
	//	public void writeToNBT(NBTTagCompound p_145841_1_)
	//	{
	//		super.writeToNBT(p_145841_1_);
	//
	//		NBTTagList nbttaglist = new NBTTagList();
	//
	//		for (ItemStack gun : this.guns)
	//		{
	//			if (gun == null)
	//				continue;
	//			nbttaglist.appendTag(gun.writeToNBT(new NBTTagCompound()));
	//		}
	//
	//		p_145841_1_.setTag("guns", nbttaglist);
	//	}
}
