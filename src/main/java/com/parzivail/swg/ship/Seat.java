package com.parzivail.swg.ship;

import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/27/2017.
 */
public class Seat extends EntityBase
{
	private BasicFlightModel ship;
	private Vector3f offset;

	public Seat(World world)
	{
		super(world);
	}

	public Seat(BasicFlightModel ship, Vector3f offset)
	{
		this(ship.worldObj);
		setSize(0.5F, 0.5F);
		this.ship = ship;
		this.offset = offset;
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		if (this.ship != null)
			tag.setInteger("shipId", this.ship.getEntityId());
		else
			tag.setInteger("shipId", -1);
		tag.setFloat("offsetX", offset.x);
		tag.setFloat("offsetY", offset.y);
		tag.setFloat("offsetZ", offset.z);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		int id = tag.getInteger("shipId");
		if (id != -1)
			this.ship = (BasicFlightModel)this.worldObj.getEntityByID(id);
		this.offset = new Vector3f(tag.getFloat("offsetX"), tag.getFloat("offsetY"), tag.getFloat("offsetZ"));
	}

	public boolean canBeCollidedWith()
	{
		return true;
	}

	public void applyEntityCollision(Entity other)
	{
	}

	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX - (this.boundingBox.maxX - this.boundingBox.minX) / 2f + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ - (this.boundingBox.maxZ - this.boundingBox.minZ) / 2f + d1);
		}
	}

	@Override
	public void onUpdate()
	{
		if (this.posY < -64.0D)
			this.setDead();

		if (this.ship != null)
		{
			this.moveEntity(ship.motionX, ship.motionY, ship.motionZ);

			// if we're over 3 blocks away from where we should be, snap back
			Vector3f nextPosition = ship.orientation.findLocalVectorGlobally(offset);
			if (Math.pow(nextPosition.x - posX, 2) + Math.pow(nextPosition.x - posX, 2) + Math.pow(nextPosition.x - posX, 2) > 9)
			{
				this.posX = nextPosition.x;
				this.posY = nextPosition.y;
				this.posZ = nextPosition.z;
			}
		}
		else
		{
			Lumberjack.log("Ship is null, worldObj.isRemote=%s", this.worldObj.isRemote);
		}

		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}

	void setLocationAndAngles(BasicFlightModel ship)
	{
		this.setLocationAndAngles(ship.posX + offset.x, ship.posY + offset.y, ship.posZ + offset.z, 0, 0);
	}
}
