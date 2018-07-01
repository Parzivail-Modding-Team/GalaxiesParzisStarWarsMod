package com.parzivail.swg.entity.multipart;

import net.minecraft.entity.Entity;

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
}
