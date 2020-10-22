package com.parzivail.pswg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ThrownLightsaberEntity extends ThrownEntity
{
	private static final TrackedData<Byte> LIFE = DataTracker.registerData(ThrownLightsaberEntity.class, TrackedDataHandlerRegistry.BYTE);

	public ThrownLightsaberEntity(EntityType<? extends ThrownLightsaberEntity> type, World world)
	{
		super(type, world);
	}

	public ThrownLightsaberEntity(EntityType<? extends ThrownLightsaberEntity> type, LivingEntity owner, World world)
	{
		super(type, owner, world);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag)
	{
		super.writeCustomDataToTag(tag);
		tag.putByte("life", getLife());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag)
	{
		super.readCustomDataFromTag(tag);
		setLife(tag.getByte("life"));
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	protected void initDataTracker()
	{
		dataTracker.startTracking(LIFE, (byte)0);
	}

	private byte getLife()
	{
		return dataTracker.get(LIFE);
	}

	private void setLife(byte life)
	{
		dataTracker.set(LIFE, life);
	}

	@Override
	public void tick()
	{
		final byte life = (byte)(getLife() + 1);
		setLife(life);

		if (life >= 60)
		{
			this.remove();
			return;
		}

		super.tick();
	}

	protected void onCollision(HitResult hitResult)
	{
		super.onCollision(hitResult);
		if (!this.world.isClient)
		{
			this.world.sendEntityStatus(this, (byte)3);
			this.remove();
		}
	}
}
