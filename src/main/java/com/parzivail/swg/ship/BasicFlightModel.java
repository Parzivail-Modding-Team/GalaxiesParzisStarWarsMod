package com.parzivail.swg.ship;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by colby on 12/26/2017.
 */
public class BasicFlightModel extends Entity
{
	public BasicFlightModel(World world)
	{
		super(world);
		setSize(1, 1);
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
