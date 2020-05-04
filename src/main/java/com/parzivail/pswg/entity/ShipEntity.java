package com.parzivail.pswg.entity;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.util.ClientUtil;
import com.parzivail.pswg.util.EntityUtil;
import com.parzivail.pswg.util.MathUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class ShipEntity extends Entity
{
	private static final TrackedData<Quaternion> ROTATION = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.QUATERNION);
	private static final TrackedData<Float> THROTTLE = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.FLOAT);

	@Environment(EnvType.CLIENT)
	public ChaseCamEntity camera;

	private Quaternion viewRotation = Quaternion.IDENTITY;
	private Quaternion viewPrevRotation = Quaternion.IDENTITY;

	public ShipEntity(EntityType<?> type, World world)
	{
		super(type, world);
		this.inanimate = true;
	}

	@Override
	public void kill()
	{
		super.kill();

		if (world.isClient)
			killCamera();
	}

	private void killCamera()
	{
		if (camera != null)
			camera.kill();
	}

	public static void handleRotationPacket(PacketContext packetContext, PacketByteBuf attachedData)
	{
		float dx = attachedData.readFloat();
		float dy = attachedData.readFloat();

		packetContext.getTaskQueue().execute(() -> {
			PlayerEntity player = packetContext.getPlayer();
			ShipEntity ship = getShip(player);

			if (ship != null)
				ship.acceptMouseInput(dx, dy);
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

	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions)
	{
		return 0.0F;
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
		getDataTracker().startTracking(ROTATION, Quaternion.IDENTITY);
		getDataTracker().startTracking(THROTTLE, 0f);
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

		viewPrevRotation = viewRotation.copy();
		viewRotation = getRotation().copy();

		if (world.isClient && camera == null)
		{
			camera = SwgEntities.Ship.ChaseCam.create(world);
			assert camera != null;

			camera.setParent(this);
			ClientUtil.spawnEntity(world, camera);
		}

		//		Entity pilot = getPrimaryPassenger();
		//		if (!world.isClient && pilot != null)
		//		{
		//			if (pilot instanceof PlayerEntity)
		//				setThrottle(((PlayerEntity)pilot).getMainHandStack().getCount() > 0 ? 5 : 0);
		//		}
		//
		//		float throttle = getThrottle();
		//		Vec3d forward = MathUtil.rotate(MathUtil.NEGZ, getRotation());
		//
		//		move(MovementType.SELF, forward.multiply(throttle));

		Quaternion rotation = new Quaternion(getRotation());
		setRotation(rotation);
		EntityUtil.updateEulerRotation(this, rotation);
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
			vec3d = MathUtil.rotate(vec3d, getRotation());

			passenger.updatePosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
			this.copyEntityData(passenger);
		}
	}

	protected void copyEntityData(Entity entity)
	{
		entity.yaw = this.yaw;
		entity.pitch = this.pitch;
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

	//	public Rotation getRotation(float t)
	//	{
	//		Rotation start = prevRotation;
	//		Rotation end = getRotation();
	//
	//		return MathUtil.lerp(start, end, t);
	//	}

	public float getThrottle()
	{
		return getDataTracker().get(THROTTLE);
	}

	public void setThrottle(float t)
	{
		getDataTracker().set(THROTTLE, t);
	}

	public Quaternion getRotation()
	{
		return getDataTracker().get(ROTATION);
	}

	@Environment(EnvType.CLIENT)
	public Quaternion getViewRotation(float t)
	{
		Quaternion start = viewPrevRotation;
		Quaternion end = viewRotation;
		return MathUtil.slerp(start, end, t);
	}

	public void setRotation(Quaternion q)
	{
		q.normalize();
		getDataTracker().set(ROTATION, q);
	}

	public static ShipEntity create(World world)
	{
		ShipEntity ship = new ShipEntity(SwgEntities.Ship.T65bXwing, world);
		//		ship.setSettings(settings);
		return ship;
	}

	public void acceptControlInput(EnumSet<ShipControls> controls)
	{

	}

	public void acceptMouseInput(double mouseDx, double mouseDy)
	{
		Quaternion rotation = getRotation();

		boolean pitchRoll = false;

		if (pitchRoll)
			rotation.hamiltonProduct(new Quaternion(new Vector3f(0, 0, 1), -(float)mouseDx * 0.15f, true));
		else
		{
			Vec3d v = MathUtil.project(MathUtil.POSY, rotation);
			rotation.hamiltonProduct(new Quaternion(new Vector3f(v), (float)(Math.asin(v.y) * -mouseDx * 0.15f), true));

			// TODO: roll back toward zero when this mode is switched to and the ship has a nonzero roll
		}

		rotation.hamiltonProduct(new Quaternion(new Vector3f(1, 0, 0), -(float)mouseDy * 0.15f, true));

		setRotation(rotation);

		if (this.world.isClient)
		{
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeFloat((float)mouseDx);
			passedData.writeFloat((float)mouseDy);
			ClientSidePacketRegistry.INSTANCE.sendToServer(SwgPackets.C2S.PacketShipRotation, passedData);
		}
	}
}
