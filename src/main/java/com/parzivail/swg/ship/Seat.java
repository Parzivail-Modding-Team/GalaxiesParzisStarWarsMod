package com.parzivail.swg.ship;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/27/2017.
 */
public class Seat extends Entity
{
	private BasicFlightModel ship;
	public Vector3f offset;

	public Seat(BasicFlightModel ship, Vector3f offset)
	{
		super(ship.worldObj);
		setSize(0.5F, 0.5F);
		this.ship = ship;
		this.offset = offset;
	}

	protected void entityInit()
	{
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound t)
	{
		this.offset = new Vector3f(t.getFloat("ox"), t.getFloat("oy"), t.getFloat("oz"));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound t)
	{
		t.setFloat("ox", offset.x);
		t.setFloat("oy", offset.y);
		t.setFloat("oz", offset.z);
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
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
	{
		return !this.isEntityInvulnerable();
	}

	/**
	 * Returns true if Entity argument is equal to this Entity
	 */
	public boolean isEntityEqual(Entity other)
	{
		return this == other || this.ship == other;
	}

	void setLocationAndAngles(BasicFlightModel ship)
	{
		Vector3f o = ship.orientation.findLocalVectorGlobally(offset);
		this.setLocationAndAngles(ship.posX + o.x, ship.posY + o.y, ship.posZ + o.z, 0, 0);
	}
}
