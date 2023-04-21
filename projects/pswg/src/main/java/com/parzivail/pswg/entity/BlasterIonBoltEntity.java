package com.parzivail.pswg.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
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

		var pos = this.getPos();
		this.world.addParticle(ParticleTypes.EFFECT, pos.x, pos.y + this.getHeight() / 2, pos.z, 0, 0, 0);
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
}
