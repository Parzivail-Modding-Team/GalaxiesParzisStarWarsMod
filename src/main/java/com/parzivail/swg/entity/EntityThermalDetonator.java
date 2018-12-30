package com.parzivail.swg.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityThermalDetonator extends EntityThrowable
{
	public EntityThermalDetonator(World world)
	{
		super(world);
	}

	public EntityThermalDetonator(World world, EntityLivingBase thrower)
	{
		super(world, thrower);
	}

	@Override
	protected void onImpact(MovingObjectPosition hit)
	{
		if (!worldObj.isRemote)
		{
			if (hit.typeOfHit == MovingObjectType.BLOCK)
			{
				switch (hit.sideHit)
				{
					case 0:
					case 1:
						motionY *= -0.05f;
						break;
					case 2:
					case 3:
						motionZ *= -0.05f;
						break;
					case 4:
					case 5:
						motionX *= -0.05f;
						break;
				}
			}

			if (Math.abs(motionY * motionY) < 0.0025f)
			{
				worldObj.createExplosion(this, posX, posY, posZ, 2, true);
				setDead();
			}
		}
	}
}
