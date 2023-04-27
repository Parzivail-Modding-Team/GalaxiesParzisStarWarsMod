package com.parzivail.pswg.entity;

import com.parzivail.pswg.container.SwgParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class BlasterIonBoltEntity extends BlasterBoltEntity
{
	public BlasterIonBoltEntity(EntityType<? extends BlasterIonBoltEntity> type, World world)
	{
		super(type, world);
	}

	public BlasterIonBoltEntity(EntityType<? extends BlasterIonBoltEntity> type, LivingEntity owner, World world, boolean ignoreWater)
	{
		super(type, owner, world, ignoreWater);
	}

	@Override
	public void tick()
	{
		super.tick();

		if (world.isClient)
		{
			var normal = this.getVelocity().normalize().multiply(-1);

			var count = 16;
			for (var i = 0; i < count; i++)
			{
				var pos = this.getLerpedPos(i / (float)count);

				var vx = world.random.nextGaussian() * 0.03;
				var vy = world.random.nextGaussian() * 0.03;
				var vz = world.random.nextGaussian() * 0.03;

				var sparkVelocity = normal.multiply(0.3f * (world.random.nextDouble() * 0.5 + 0.5));
				world.addParticle(SwgParticles.SPARK, pos.x, pos.y, pos.z, sparkVelocity.x + vx, sparkVelocity.y + vy, sparkVelocity.z + vz);
			}
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		super.onEntityHit(entityHitResult);
		Entity hitEntity = entityHitResult.getEntity();
		Entity owner = this.getOwner();

		//			this.playSound(this.sound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

		// TODO: electrical effects

		this.discard();
	}

	@Override
	protected boolean deflect(LivingEntity entity)
	{
		return false;
	}

	@Override
	protected boolean shouldDestroyBlocks()
	{
		return false;
	}
}
