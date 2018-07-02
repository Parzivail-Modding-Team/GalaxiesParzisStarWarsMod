package com.parzivail.swg.entity.multipart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EntityPartTest extends EntityPart
{
	public EntityPartTest(EntityMultipart parent, String name, float width, float height)
	{
		super(parent, name, width, height);
	}

	@Override
	public void setLocation()
	{
		setLocationAndAngles(parent.posX, parent.posY + 2.5f, parent.posZ, 0, 0);
	}

	@Override
	public boolean hitByEntity(Entity entityIn)
	{
		return super.hitByEntity(entityIn);
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
