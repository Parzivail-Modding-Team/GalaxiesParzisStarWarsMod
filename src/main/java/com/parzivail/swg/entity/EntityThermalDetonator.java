package com.parzivail.swg.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.Explosion;
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
		for (int i = 0; i < 8; ++i)
		{
			worldObj.spawnParticle("smoke", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		}

		if (!worldObj.isRemote)
		{
			if (hit.typeOfHit == MovingObjectType.BLOCK)
			{
				switch (hit.sideHit)
				{
					case 0:
					case 1:
						motionY = -motionY * 0.2;
						break;
					case 2:
					case 3:
						motionZ = -motionZ * 0.2;
						break;
					case 4:
					case 5:
						motionX = -motionX * 0.2;
						break;
				}
			}

			if (motionY * motionY < 0.005f)
			{
				Explosion e = worldObj.createExplosion(this, posX, posY, posZ, 2, true);
				setDead();
			}
		}
	}
}
