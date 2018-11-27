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
			for (int i = 0; i < Math.min(Math.pow(ticksExisted / 10, 2), 100); ++i)
			{
				double gaussX = (rand.nextGaussian() * 2 - 1);
				double gaussY = rand.nextGaussian() * 2;
				double gaussZ = (rand.nextGaussian() * 2 - 1);

				double gaussMx = (rand.nextGaussian() * 2 - 1) / 20 * gaussX;
				double gaussMy = (rand.nextGaussian() * 2 - 1) / 20 * gaussY;
				double gaussMz = (rand.nextGaussian() * 2 - 1) / 20 * gaussZ;

				StarWarsGalaxy.proxy.spawnSmokeParticle(worldObj, posX + gaussX, posY + gaussY, posZ + gaussZ, gaussMx, gaussMy, gaussMz);
			}
		}

		if (!worldObj.isRemote && ticksExisted > 300)
			setDead();
	}
}
