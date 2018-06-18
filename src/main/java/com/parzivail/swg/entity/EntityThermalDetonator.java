package com.parzivail.swg.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
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
			this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if (!this.worldObj.isRemote)
		{
			if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				switch (hit.sideHit)
				{
					case 0:
					case 1:
						this.motionY = -this.motionY * 0.2;
						break;
					case 2:
					case 3:
						this.motionZ = -this.motionZ * 0.2;
						break;
					case 4:
					case 5:
						this.motionX = -this.motionX * 0.2;
						break;
				}
			}

			if (this.motionY * this.motionY < 0.005f)
			{
				Explosion e = this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 2, true);
				this.setDead();
			}
		}
	}
}
