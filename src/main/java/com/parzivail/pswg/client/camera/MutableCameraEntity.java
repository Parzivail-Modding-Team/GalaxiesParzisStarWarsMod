package com.parzivail.pswg.client.camera;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.util.QuatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
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
	protected void readCustomDataFromNbt(NbtCompound tag)
	{

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag)
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
		setPosition(camera.pos.x, camera.pos.y, camera.pos.z);

		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		QuatUtil.updateEulerRotation(this, parent.getViewRotation(Client.minecraft.getTickDelta()));

		return this;
	}
}
