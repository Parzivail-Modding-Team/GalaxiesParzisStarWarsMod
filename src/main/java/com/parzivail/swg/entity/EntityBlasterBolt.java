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
		this.setSize(0.1f, 0.1f);
		this.setDx(dx);
		this.setDy(dy);
		this.setDz(dz);
		this.setLength(length);
		this.setColor(rgb);
	}

	@Override
	protected void entityInit()
	{
		this.dataWatcher.addObject(DATA_DX, 0f); // dx
		this.dataWatcher.addObject(DATA_DY, 0f); // dy
		this.dataWatcher.addObject(DATA_DZ, 0f); // dz
		this.dataWatcher.addObject(DATA_LENGTH, 1f); // length
		this.dataWatcher.addObject(DATA_COLOR, 0xFF0000); // length
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		this.motionX = getDx() * 3;
		this.motionY = getDy() * 3;
		this.motionZ = getDz() * 3;

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;

		if (this.ticksExisted > 60)
			this.setDead();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		this.setDx(tag.getFloat("dx"));
		this.setDy(tag.getFloat("dy"));
		this.setDz(tag.getFloat("dz"));
		this.setLength(tag.getFloat("length"));
		this.setColor(tag.getInteger("color"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		tag.setFloat("dx", this.getDx());
		tag.setFloat("dy", this.getDy());
		tag.setFloat("dz", this.getDz());
		tag.setFloat("length", this.getLength());
		tag.setInteger("color", this.getColor());
	}

	public float getDx()
	{
		return this.dataWatcher.getWatchableObjectFloat(DATA_DX);
	}

	public float getDy()
	{
		return this.dataWatcher.getWatchableObjectFloat(DATA_DY);
	}

	public float getDz()
	{
		return this.dataWatcher.getWatchableObjectFloat(DATA_DZ);
	}

	public float getLength()
	{
		return this.dataWatcher.getWatchableObjectFloat(DATA_LENGTH);
	}

	public int getColor()
	{
		return this.dataWatcher.getWatchableObjectInt(DATA_COLOR);
	}

	public void setDx(float f)
	{
		this.dataWatcher.updateObject(DATA_DX, f);
	}

	public void setDy(float f)
	{
		this.dataWatcher.updateObject(DATA_DY, f);
	}

	public void setDz(float f)
	{
		this.dataWatcher.updateObject(DATA_DZ, f);
	}

	public void setLength(float f)
	{
		this.dataWatcher.updateObject(DATA_LENGTH, f);
	}

	public void setColor(int f)
	{
		this.dataWatcher.updateObject(DATA_COLOR, f);
	}
}
