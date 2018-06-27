package com.parzivail.swg.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityBlasterBolt extends Entity
{
	private static final int DATA_DX = 11;
	private static final int DATA_DY = 12;
	private static final int DATA_DZ = 13;
	private static final int DATA_LENGTH = 14;
	private static final int DATA_COLOR = 15;

	public EntityBlasterBolt(World world)
	{
		this(world, 0, 0, 0, 0, 0xFF0000);
	}

	public EntityBlasterBolt(World world, float dx, float dy, float dz, float length, int rgb)
	{
		super(world);
		setSize(0.1f, 0.1f);
		setDx(dx);
		setDy(dy);
		setDz(dz);
		setLength(length);
		setColor(rgb);
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(DATA_DX, 0f); // dx
		dataWatcher.addObject(DATA_DY, 0f); // dy
		dataWatcher.addObject(DATA_DZ, 0f); // dz
		dataWatcher.addObject(DATA_LENGTH, 1f); // length
		dataWatcher.addObject(DATA_COLOR, 0xFF0000); // length
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		motionX = getDx() * 3;
		motionY = getDy() * 3;
		motionZ = getDz() * 3;

		posX += motionX;
		posY += motionY;
		posZ += motionZ;

		if (ticksExisted > 60)
			setDead();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		setDx(tag.getFloat("dx"));
		setDy(tag.getFloat("dy"));
		setDz(tag.getFloat("dz"));
		setLength(tag.getFloat("length"));
		setColor(tag.getInteger("color"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		tag.setFloat("dx", getDx());
		tag.setFloat("dy", getDy());
		tag.setFloat("dz", getDz());
		tag.setFloat("length", getLength());
		tag.setInteger("color", getColor());
	}

	public float getDx()
	{
		return dataWatcher.getWatchableObjectFloat(DATA_DX);
	}

	public void setDx(float f)
	{
		dataWatcher.updateObject(DATA_DX, f);
	}

	public float getDy()
	{
		return dataWatcher.getWatchableObjectFloat(DATA_DY);
	}

	public void setDy(float f)
	{
		dataWatcher.updateObject(DATA_DY, f);
	}

	public float getDz()
	{
		return dataWatcher.getWatchableObjectFloat(DATA_DZ);
	}

	public void setDz(float f)
	{
		dataWatcher.updateObject(DATA_DZ, f);
	}

	public float getLength()
	{
		return dataWatcher.getWatchableObjectFloat(DATA_LENGTH);
	}

	public void setLength(float f)
	{
		dataWatcher.updateObject(DATA_LENGTH, f);
	}

	public int getColor()
	{
		return dataWatcher.getWatchableObjectInt(DATA_COLOR);
	}

	public void setColor(int f)
	{
		dataWatcher.updateObject(DATA_COLOR, f);
	}
}
