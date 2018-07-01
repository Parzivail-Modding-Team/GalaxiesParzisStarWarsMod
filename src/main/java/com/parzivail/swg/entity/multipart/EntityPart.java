package com.parzivail.swg.entity.multipart;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public abstract class EntityPart extends Entity
{
	/**
	 * The dragon entity this dragon part belongs to
	 */
	public final EntityMultipart parent;
	public final String name;

	public EntityPart(EntityMultipart parent, String name, float width, float height)
	{
		super(parent.worldObj);
		setSize(width, height);
		this.parent = parent;
		this.name = name;

		noClip = true;
	}

	protected void entityInit()
	{
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund)
	{
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith()
	{
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return !isEntityInvulnerable() && parent.attackEntityFrom(source, amount);
	}

	/**
	 * Returns true if Entity argument is equal to this Entity
	 */
	public boolean isEntityEqual(Entity entityIn)
	{
		return this == entityIn || parent == entityIn;
	}

	public abstract void setLocation();
}
