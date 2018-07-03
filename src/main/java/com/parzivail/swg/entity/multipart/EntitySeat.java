package com.parzivail.swg.entity.multipart;

import com.parzivail.util.common.Lumberjack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySeat extends Entity
{
	/**
	 * The dragon entity this dragon part belongs to
	 */
	public EntityMultipart parent;
	public String name;

	public EntitySeat(World world)
	{
		super(world);
		setSize(0.5f, 0.5f);
		noClip = true;
	}

	public EntitySeat(EntityMultipart parent, String name)
	{
		this(parent.worldObj);
		this.parent = parent;
		this.name = name;
	}

	protected void entityInit()
	{
	}

	@Override
	public void onUpdate()
	{
		//setDead();
		super.onUpdate();
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund)
	{
		name = tagCompund.getString("name");
		//		setParent(tagCompund.getInteger("parent"));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setString("name", name);
		//		if (parent != null)
		//			tagCompound.setInteger("parent", parent.getEntityId());
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith()
	{
		return true;
	}

	/**
	 * Returns true if Entity argument is equal to this Entity
	 */
	public boolean isEntityEqual(Entity entityIn)
	{
		return this == entityIn || parent == entityIn;
	}

	public void setLocation()
	{
		if (parent != null)
			setLocationAndAngles(parent.posX, parent.posY + 2.5f, parent.posZ, 0, 0);
	}

	public void setParent(int parentId)
	{
		Entity entity = worldObj.getEntityByID(parentId);

		if (entity == null)
		{
			Lumberjack.warn("Part failed to locate parent");
			//setDead();
		}
		else
		{
			if (!(entity instanceof EntityMultipart))
				return;

			parent = (EntityMultipart)entity;
			Lumberjack.warn("Part located parent");
		}
	}

	public void updateRiderPosition()
	{
		if (riddenByEntity != null)
		{
			riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ);
		}
	}

	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && riddenByEntity != player)
			return true;
		else
		{
			player.mountEntity(this);

			return true;
		}
	}
}
