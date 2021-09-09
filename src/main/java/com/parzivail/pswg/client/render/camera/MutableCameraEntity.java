package com.parzivail.pswg.client.render.camera;

import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.MinecraftClient;
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
		var minecraft = MinecraftClient.getInstance();

		this.parent = parent;
		this.camera = camera;

		this.world = parent.world;
		this.prevX = camera.prevPos.x;
		this.prevY = camera.prevPos.y;
		this.prevZ = camera.prevPos.z;
		setPosition(camera.pos.x, camera.pos.y, camera.pos.z);

		this.prevPitch = this.getPitch();
		this.prevYaw = this.getYaw();
		QuatUtil.updateEulerRotation(this, parent.getViewRotation(minecraft.getTickDelta()));

		return this;
	}
}
