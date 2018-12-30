package com.parzivail.swg.entity;

import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmokeGrenade extends EntityThrowable
{
	public EntitySmokeGrenade(World world)
	{
		super(world);
	}

	public EntitySmokeGrenade(World world, EntityLivingBase thrower)
	{
		super(world, thrower);
	}

	@Override
	protected void onImpact(MovingObjectPosition hit)
	{
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (worldObj.isRemote && ticksExisted > 30 && ticksExisted % 5 == 0)
		{
			for (int i = 0; i < Math.min(Math.pow(ticksExisted / 10f, 2), 75); ++i)
			{
				double gaussMx = (rand.nextGaussian() * 2 - 1) / 2;
				double gaussMy = (rand.nextGaussian() * 2) / 2;
				double gaussMz = (rand.nextGaussian() * 2 - 1) / 2;

				StarWarsGalaxy.proxy.spawnSmokeParticle(worldObj, posX, posY, posZ, gaussMx, gaussMy, gaussMz);
			}
		}

		if (!worldObj.isRemote && ticksExisted > 300)
			setDead();
	}
}
