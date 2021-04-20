package com.parzivail.pswg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlasterBoltEntity extends ThrownEntity
{
	private static final TrackedData<Integer> LIFE = DataTracker.registerData(BlasterBoltEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> type, World world)
	{
		super(type, world);
	}

	public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> type, LivingEntity owner, World world)
	{
		super(type, owner, world);
	}

	public void setRange(float range)
	{
		int ticksToLive = (int)(range / getVelocity().length());
		setLife(ticksToLive);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag)
	{
		super.writeCustomDataToTag(tag);
		tag.putInt("life", getLife());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag)
	{
		super.readCustomDataFromTag(tag);
		setLife(tag.getInt("life"));
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	protected void initDataTracker()
	{
		dataTracker.startTracking(LIFE, 0);
	}

	private int getLife()
	{
		return dataTracker.get(LIFE);
	}

	private void setLife(int life)
	{
		dataTracker.set(LIFE, life);
	}

	@Override
	public void tick()
	{
		final int life = getLife() - 1;
		setLife(life);

		if (life <= 0)
		{
			this.remove();
			return;
		}

		pitch = 0;//(float)Math.asin(-fromDir.y) * MathUtil.toDegreesf;
		yaw = 0;//(float)Math.atan2(fromDir.x, fromDir.z) * MathUtil.toDegreesf;

		super.tick();
	}

	protected void onCollision(HitResult hitResult)
	{
		super.onCollision(hitResult);
		if (this.world.isClient)
		{
			for (int i = 0; i < 8; i++)
			{
				Vec3d pos = hitResult.getPos().add(0, getHeight() / 2f, 0);

				double vx = this.random.nextGaussian() * 0.02;
				double vy = (this.random.nextGaussian() * 0.5 + 1) * 0.03f;
				double vz = this.random.nextGaussian() * 0.02;

				if (hitResult.getType() == HitResult.Type.BLOCK)
				{
					BlockHitResult blockHit = (BlockHitResult)hitResult;

					if (blockHit.getSide() == Direction.DOWN)
						vy *= -2;
				}

				this.world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, vx, vy, vz);
			}
		}
		else
		{
			this.world.sendEntityStatus(this, (byte)3);
			this.remove();
		}
	}
}
