package com.parzivail.pswg.entity;

import com.parzivail.pswg.GalaxiesMain;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.util.MathUtil;
import com.parzivail.pswg.util.Rotation;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ShipEntity extends Entity
{
	private static final TrackedData<Rotation> ROTATION = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.ROTATION);

	private Rotation rotation = new Rotation();
	private Rotation prevRotation = new Rotation();

	public ShipEntity(EntityType<?> type, World world)
	{
		super(type, world);
		this.inanimate = true;
	}

	public static void handleRotationPacket(PacketContext packetContext, PacketByteBuf attachedData)
	{
		float dx = attachedData.readFloat();
		float dy = attachedData.readFloat();

		packetContext.getTaskQueue().execute(() -> {
			PlayerEntity player = packetContext.getPlayer();
			ShipEntity ship = getShip(player);

			if (ship != null)
				ship.acceptInput(dx, dy);
		});
	}

	public static ShipEntity getShip(PlayerEntity player)
	{
		Entity vehicle = player.getVehicle();

		if (vehicle instanceof ShipEntity)
		{
			ShipEntity ship = (ShipEntity)vehicle;

			if (ship.getPrimaryPassenger() == player)
				return ship;
		}

		return null;
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
		getDataTracker().startTracking(ROTATION, new Rotation());
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

		prevRotation = rotation.clone();
		Rotation rotation = getRotation();

		//		setRotation(rotation);
		updateEulerRotation(rotation);
	}

	public boolean interact(PlayerEntity player, Hand hand)
	{
		if (player.shouldCancelInteraction())
			return false;
		else
			return !this.world.isClient && player.startRiding(this);
	}

	protected boolean canAddPassenger(Entity passenger)
	{
		return this.getPassengerList().size() < 2;
	}

	public void updatePassengerPosition(Entity passenger)
	{
		if (this.hasPassenger(passenger))
		{
			Vec3d vec3d = new Vec3d(0, 0, 0);
			vec3d = getRotation().unproject(vec3d);

			passenger.updatePosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
			this.copyEntityData(passenger);
		}
	}

	protected void copyEntityData(Entity entity)
	{
		//		entity.setYaw(this.pitch);
		//		entity.setHeadYaw(this.pitch);
		//		entity.setYaw(this.pitch);
		//		float f = MathHelper.wrapDegrees(entity.yaw - this.pitch);
		//		float g = MathHelper.clamp(f, -105.0F, 105.0F);
		//		entity.prevYaw += g - f;
		//		entity.yaw += g - f;
		//		entity.setHeadYaw(this.pitch);
	}

	@Nullable
	public Entity getPrimaryPassenger()
	{
		List<Entity> list = this.getPassengerList();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
	}

	public Rotation getRotation()
	{
		return getDataTracker().get(ROTATION);
	}

	public Rotation getRotation(float t)
	{
		Rotation start = prevRotation;
		Rotation end = getRotation();

		return MathUtil.lerp(start, end, t);
	}

	public void setRotation(Rotation q)
	{
		rotation = q;

		if (!world.isClient)
			getDataTracker().set(ROTATION, q);
	}

	private void updateEulerRotation(Rotation rotation)
	{
		//		EulerAngle eulerAngle = rotation.toEulerAngles();
		//		yaw = eulerAngle.getYaw();
		//		pitch = eulerAngle.getPitch();
		//
		//		while (this.pitch - this.prevPitch >= 180.0F)
		//			this.prevPitch += 360.0F;
		//
		//		while (this.yaw - this.prevYaw < -180.0F)
		//			this.prevYaw -= 360.0F;
		//
		//		while (this.yaw - this.prevYaw >= 180.0F)
		//			this.prevYaw += 360.0F;
	}

	@Override
	public Vec3d getRotationVecClient()
	{
		return super.getRotationVecClient();
	}

	@Override
	public Vec3d getRotationVec(float tickDelta)
	{
		return super.getRotationVec(tickDelta);
	}

	public static ShipEntity create(World world)
	{
		ShipEntity ship = new ShipEntity(GalaxiesMain.EntityTypeShip, world);
		//		ship.setSettings(settings);
		return ship;
	}

	public void acceptInput(double mouseDx, double mouseDy)
	{
		Rotation rotation = getRotation();
		rotation.rotateGlobal(MathUtil.POSZ, (float)mouseDx);
		//		rotation.rotatePitch((float)mouseDy);
		setRotation(rotation);

		if (this.world.isClient)
		{
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeFloat((float)mouseDx);
			passedData.writeFloat((float)mouseDy);
			ClientSidePacketRegistry.INSTANCE.sendToServer(GalaxiesMain.PacketShipRotation, passedData);
		}
	}
}
