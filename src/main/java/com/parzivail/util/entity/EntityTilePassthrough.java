package com.parzivail.util.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class EntityTilePassthrough extends Entity
{
	public TileEntity tileEntity;

	public EntityTilePassthrough(TileEntity tileEntity)
	{
		super(tileEntity.getWorld());
		this.tileEntity = tileEntity;
	}

	@Override
	protected void entityInit()
	{
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
	{
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
	{
	}
}
