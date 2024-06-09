package com.parzivail.pswg.features.lightsabers.client;

import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.features.lightsabers.data.LightsaberTag;
import com.parzivail.util.entity.IPrecisionVelocityEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ThrownLightsaberEntity extends ThrownEntity implements IPrecisionVelocityEntity
{
	private static final TrackedData<Byte> LIFE = DataTracker.registerData(ThrownLightsaberEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<NbtCompound> LIGHTSABER_DATA = DataTracker.registerData(ThrownLightsaberEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

	public ThrownLightsaberEntity(EntityType<? extends ThrownLightsaberEntity> type, World world)
	{
		super(type, world);
	}

	public ThrownLightsaberEntity(EntityType<? extends ThrownLightsaberEntity> type, LivingEntity owner, World world, LightsaberTag tag)
	{
		super(type, owner, world);
		setLightsaberData(tag);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);
		tag.putByte("life", getLife());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);
		setLife(tag.getByte("life"));
	}

	@Override
	public boolean shouldRender(double distance)
	{
		return true;
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		SoundHelper.playThrownLightsaberSound(this);
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
		builder.add(LIFE, (byte)0);
		builder.add(LIGHTSABER_DATA, new LightsaberTag().toTag());
	}

	private byte getLife()
	{
		return dataTracker.get(LIFE);
	}

	private void setLife(byte life)
	{
		dataTracker.set(LIFE, life);
	}

	public LightsaberTag getLightsaberData()
	{
		return LightsaberTag.fromRootTag(dataTracker.get(LIGHTSABER_DATA));
	}

	private void setLightsaberData(LightsaberTag data)
	{
		dataTracker.set(LIGHTSABER_DATA, data.toTag());
	}

	@Override
	public void tick()
	{
		final var life = (byte)(getLife() + 1);
		setLife(life);

		if (life >= 60)
		{
			this.discard();
			return;
		}

		super.tick();
	}

	@Override
	protected void onCollision(HitResult hitResult)
	{
		super.onCollision(hitResult);
		if (!this.getWorld().isClient)
		{
			this.getWorld().sendEntityStatus(this, (byte)3);
			this.discard();
		}
	}
}
