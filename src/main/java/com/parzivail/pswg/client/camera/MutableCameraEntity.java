package com.parzivail.pswg.client.camera;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.entity.ChaseCam;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.util.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;

public class MutableCameraEntity extends Entity
{
	public ShipEntity parent;
	public ChaseCam camera;

	public MutableCameraEntity()
	{
		super(EntityType.ITEM, null);
	}

	@Override
	protected void initDataTracker()
	{

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
	public Packet<?> createSpawnPacket()
	{
		return null;
	}

	public MutableCameraEntity with(ShipEntity parent, ChaseCam camera)
	{
		this.parent = parent;
		this.camera = camera;

		setWorld(parent.world);
		this.prevX = camera.prevPos.x;
		this.prevY = camera.prevPos.y;
		this.prevZ = camera.prevPos.z;
		updatePosition(camera.pos.x, camera.pos.y, camera.pos.z);

		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		EntityUtil.updateEulerRotation(this, parent.getViewRotation(Client.minecraft.getTickDelta()));

		return this;
	}
}
