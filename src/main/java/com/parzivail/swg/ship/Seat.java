package com.parzivail.swg.ship;

import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/27/2017.
 */
public class Seat extends EntityBase
{
	public BasicFlightModel ship;
	public Vector3f offset;

	public Seat(World world)
	{
		super(world);
		setSize(0.5F, 0.5F);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound t)
	{
		this.offset = new Vector3f(t.getFloat("ox"), t.getFloat("oy"), t.getFloat("oz"));

		if (t.hasKey("ship", 10))
		{
			Entity entity1 = EntityList.createEntityFromNBT(t.getCompoundTag("ship"), this.worldObj);

			if (entity1 != null)
			{
				BasicFlightModel ship = (BasicFlightModel)entity1;
				this.worldObj.spawnEntityInWorld(entity1);
				this.ship = ship;
			}
			else
			{
				Lumberjack.err("Failed to load ship from seat");
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound t)
	{
		t.setFloat("ox", offset.x);
		t.setFloat("oy", offset.y);
		t.setFloat("oz", offset.z);
		if (this.ship != null)
		{
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			if (this.ship.writeMountToNBT(nbttagcompound1))
				t.setTag("ship", nbttagcompound1);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	public boolean canBeCollidedWith()
	{
		return true;
	}

	public void applyEntityCollision(Entity other)
	{
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return true;
	}

	/**
	 * First layer of player interaction
	 */
	public boolean interactFirst(EntityPlayer player)
	{
		if (this.riddenByEntity == null)
		{
			if (!this.worldObj.isRemote)
				player.mountEntity(this);
			return true;
		}
		return true;
	}

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}

	@Override
	public void updateRiderPosition()
	{
		super.updateRiderPosition();
		//		if (ship == null || this.riddenByEntity == null)
		//			return;
		//		Vector3f o = ship.orientation.findLocalVectorGlobally(offset);
		//		this.riddenByEntity.setLocationAndAngles(ship.posX + o.x, ship.posY + o.y, ship.posZ + o.z, 0, 0);
	}

	void setLocationAndAngles(BasicFlightModel ship)
	{
		Vector3f o = ship.orientation.findLocalVectorGlobally(offset);
		this.setLocationAndAngles(ship.posX + o.x, ship.posY + o.y, ship.posZ + o.z, 0, 0);
	}

	void attachToShip(BasicFlightModel ship, Vector3f offset)
	{
		this.ship = ship;
		this.offset = offset;
	}
}
