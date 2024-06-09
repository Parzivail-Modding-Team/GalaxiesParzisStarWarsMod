package com.parzivail.pswg.client.render.camera;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

public class MutableCameraEntity extends Entity
{
	private boolean forceEnable = false;

	public MutableCameraEntity()
	{
		super(EntityType.ITEM, null);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
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
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry)
	{
		return null;
	}

	public boolean shouldForce()
	{
		return forceEnable;
	}

	public MutableCameraEntity with(World world, Vec3d position, Quaternionf rotation, boolean forceEnable)
	{
		this.setWorld(world);

		var pos = this.getPos();
		this.prevX = pos.x;
		this.prevY = pos.y;
		this.prevZ = pos.z;
		setPosition(position);

		this.prevPitch = this.getPitch();
		this.prevYaw = this.getYaw();
		EntityUtil.updateEulerRotation(this, rotation);

		this.forceEnable = forceEnable;

		return this;
	}

	public MutableCameraEntity with(ShipEntity parent, ChaseCam camera)
	{
		this.setWorld(parent.getWorld());

		this.prevX = camera.prevPos.x;
		this.prevY = camera.prevPos.y;
		this.prevZ = camera.prevPos.z;
		setPosition(camera.pos.x, camera.pos.y, camera.pos.z);

		this.prevPitch = this.getPitch();
		this.prevYaw = this.getYaw();
		EntityUtil.updateEulerRotation(this, parent.getViewRotation(Client.getTickDelta()));

		this.forceEnable = false;

		return this;
	}
}
