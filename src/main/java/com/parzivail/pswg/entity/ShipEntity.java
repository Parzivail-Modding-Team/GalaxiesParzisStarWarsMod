package com.parzivail.pswg.entity;

import com.parzivail.pswg.GalaxiesMain;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.util.QuatUtil;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ShipEntity extends Entity
{
	private static final TrackedData<Quaternion> ROTATION = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.QUATERNION);

	private Quaternion prevRotation = Quaternion.IDENTITY;

	public ShipEntity(EntityType<?> type, World world)
	{
		super(type, world);
		this.inanimate = true;
	}

	@Override
	protected boolean canClimb()
	{
		return false;
	}

	@Nullable
	@Override
	public Box getHardCollisionBox(Entity collidingEntity)
	{
		return collidingEntity.isPushable() ? collidingEntity.getBoundingBox() : null;
	}

	@Nullable
	@Override
	public Box getCollisionBox()
	{
		return this.getBoundingBox();
	}

	@Override
	public boolean isPushable()
	{
		return true;
	}

	@Override
	public boolean collides()
	{
		return !this.removed;
	}

	@Override
	protected void initDataTracker()
	{
		getDataTracker().startTracking(ROTATION, QuatUtil.copy(Quaternion.IDENTITY));
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag)
	{

	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag)
	{

	}

	@Override
	public void tick()
	{
		super.tick();

		prevRotation = QuatUtil.copy(getRotation());
		Quaternion rotation = QuatUtil.copy(getRotation());

		//		QuatUtil.rotateTowards(rotation, QuatUtil.POSY, QuatUtil.rotate(QuatUtil.POSY, rotation), null, null);

		rotation.hamiltonProduct(new Quaternion(5, 0, 0, true));

		setRotation(rotation);
		updateEulerRotation(rotation);
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
	}

	public Quaternion getRotation()
	{
		return getDataTracker().get(ROTATION);
	}

	public Quaternion getRotation(float t)
	{
		Quaternion start = prevRotation;
		Quaternion end = getDataTracker().get(ROTATION);

		return QuatUtil.slerp(start, end, t);
	}

	public void setRotation(Quaternion q)
	{
		getDataTracker().set(ROTATION, q);
	}

	private void updateEulerRotation(Quaternion rotation)
	{
		EulerAngle eulerAngle = QuatUtil.toEulerAngles(rotation);
		yaw = eulerAngle.getYaw() * QuatUtil.toDegreesf;
		pitch = eulerAngle.getPitch() * QuatUtil.toDegreesf;
	}

	public static ShipEntity create(World world)
	{
		ShipEntity ship = new ShipEntity(GalaxiesMain.SHIP, world);
		//		ship.setSettings(settings);
		return ship;
	}
}
