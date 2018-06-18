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
		this.setVelocity(0, 0, 0);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (worldObj.isRemote && this.ticksExisted > 30 && this.ticksExisted % 2 == 0)
		{
			for (int i = 0; i < 50; ++i)
			{
				double gaussX = (this.rand.nextGaussian() * 2 - 1);
				double gaussY = this.rand.nextGaussian() * 2;
				double gaussZ = (this.rand.nextGaussian() * 2 - 1);

				double gaussMx = (this.rand.nextGaussian() * 2 - 1) / 20;
				double gaussMy = (this.rand.nextGaussian() * 2 - 1) / 20;
				double gaussMz = (this.rand.nextGaussian() * 2 - 1) / 20;

				StarWarsGalaxy.proxy.spawnSmokeParticle(this.worldObj, this.posX + gaussX, this.posY + gaussY, this.posZ + gaussZ, gaussMx, gaussMy, gaussMz);
			}
		}

		if (!this.worldObj.isRemote && this.ticksExisted > 200)
			this.setDead();
	}
}
