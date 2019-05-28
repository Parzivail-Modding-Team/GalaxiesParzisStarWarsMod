package com.parzivail.swg.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityBlasterBolt extends Entity
{
	private static final DataParameter<Float> DATA_DX = EntityDataManager.createKey(EntityBlasterBolt.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> DATA_DY = EntityDataManager.createKey(EntityBlasterBolt.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> DATA_DZ = EntityDataManager.createKey(EntityBlasterBolt.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> DATA_LENGTH = EntityDataManager.createKey(EntityBlasterBolt.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntityBlasterBolt.class, DataSerializers.VARINT);

	public EntityBlasterBolt(World world)
	{
		this(world, new Vec3d(0, 0, 0), 0, 0xFF0000);
	}

	public EntityBlasterBolt(World world, Vec3d direction, float length, int rgb)
	{
		super(world);
		setSize(0.1f, 0.1f);
		setDx((float)direction.x);
		setDy((float)direction.y);
		setDz((float)direction.z);
		setLength(length);
		setColor(rgb);
	}

	@Override
	protected void entityInit()
	{
		dataManager.register(DATA_DX, 0f); // dx
		dataManager.register(DATA_DY, 0f); // dy
		dataManager.register(DATA_DZ, 0f); // dz
		dataManager.register(DATA_LENGTH, 1f); // length
		dataManager.register(DATA_COLOR, 0xFF0000); // length
	}

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.motionX = this.getDx() * 3;
		this.motionY = this.getDy() * 3;
		this.motionZ = this.getDz() * 3;

		super.onUpdate();

		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

		if (this.collided)
			this.setDead();
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
		return dataManager.get(DATA_DX);
	}

	public void setDx(float f)
	{
		dataManager.set(DATA_DX, f);
	}

	public float getDy()
	{
		return dataManager.get(DATA_DY);
	}

	public void setDy(float f)
	{
		dataManager.set(DATA_DY, f);
	}

	public float getDz()
	{
		return dataManager.get(DATA_DZ);
	}

	public void setDz(float f)
	{
		dataManager.set(DATA_DZ, f);
	}

	public float getLength()
	{
		return dataManager.get(DATA_LENGTH);
	}

	public void setLength(float f)
	{
		dataManager.set(DATA_LENGTH, f);
	}

	public int getColor()
	{
		return dataManager.get(DATA_COLOR);
	}

	public void setColor(int f)
	{
		dataManager.set(DATA_COLOR, f);
	}
}
