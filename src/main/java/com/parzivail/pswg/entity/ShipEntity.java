package com.parzivail.pswg.entity;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.util.ClientUtil;
import com.parzivail.pswg.util.EntityUtil;
import com.parzivail.pswg.util.MathUtil;
import com.parzivail.util.entity.FlyingVehicle;
import com.parzivail.util.math.QuatUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public abstract class ShipEntity extends Entity implements FlyingVehicle
{
	private static final TrackedData<Quaternion> ROTATION = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.QUATERNION);
	private static final TrackedData<Float> THROTTLE = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Short> CONTROLS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.SHORT);

	@Environment(EnvType.CLIENT)
	public ChaseCamEntity camera;

	@Environment(EnvType.CLIENT)
	private boolean firstTick = true;

	private Quaternion instRotation = new Quaternion(Quaternion.IDENTITY);
	private Quaternion viewRotation = new Quaternion(Quaternion.IDENTITY);
	private Quaternion viewPrevRotation = new Quaternion(Quaternion.IDENTITY);

	public ShipEntity(EntityType<?> type, World world)
	{
		super(type, world);
		this.inanimate = true;
	}

	public static void handleRotationPacket(PacketContext packetContext, PacketByteBuf attachedData)
	{
		float qa = attachedData.readFloat();
		float qb = attachedData.readFloat();
		float qc = attachedData.readFloat();
		float qd = attachedData.readFloat();

		packetContext.getTaskQueue().execute(() -> {
			PlayerEntity player = packetContext.getPlayer();
			ShipEntity ship = getShip(player);

			if (ship != null)
				ship.setRotation(new Quaternion(qb, qc, qd, qa));
		});
	}

	public static void handleControlPacket(PacketContext packetContext, PacketByteBuf attachedData)
	{
		short controls = attachedData.readShort();

		packetContext.getTaskQueue().execute(() -> {
			PlayerEntity player = packetContext.getPlayer();
			ShipEntity ship = getShip(player);

			if (ship != null)
				ship.acceptControlInput(ShipControls.unpack(controls));
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

	@Override
	protected boolean canClimb()
	{
		return false;
	}

	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions)
	{
		return getHeight() / 2f;
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
		getDataTracker().startTracking(ROTATION, new Quaternion(Quaternion.IDENTITY));
		getDataTracker().startTracking(THROTTLE, 0f);
		getDataTracker().startTracking(CONTROLS, (short)0);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag)
	{
		if (tag.contains("rotation"))
			setRotation(QuatUtil.getQuaternion(tag.getCompound("rotation")));
		setThrottle(tag.getFloat("throttle"));
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag)
	{
		CompoundTag qTag = new CompoundTag();
		QuatUtil.putQuaternion(qTag, getRotation());
		tag.put("rotation", qTag);

		tag.putFloat("throttle", getThrottle());
	}

	@Override
	public void tick()
	{
		super.tick();

		if (world.isClient)
		{
			if (camera == null)
			{
				camera = SwgEntities.Ship.ChaseCam.create(world);
				assert camera != null;

				camera.setParent(this);
				ClientUtil.spawnEntity(world, camera);
			}

			if (firstTick)
			{
				instRotation = getRotation();
				firstTick = false;
			}
		}

		viewPrevRotation = new Quaternion(viewRotation);
		viewRotation = new Quaternion(instRotation);

		float throttle = getThrottle();

		Entity pilot = getPrimaryPassenger();
		if (pilot instanceof PlayerEntity)
		{
			EnumSet<ShipControls> controls = getControls();

			if (controls.contains(ShipControls.THROTTLE_UP))
				throttle += 0.3f;
			if (controls.contains(ShipControls.THROTTLE_DOWN))
				throttle -= 0.3f;

			throttle = MathHelper.clamp(throttle, 0, 3);

			setThrottle(throttle);
		}

		Vec3d forward = QuatUtil.rotate(MathUtil.NEGZ, getRotation());
		setVelocity(forward.multiply(throttle));

		setRotation(viewRotation);

		move(MovementType.SELF, getVelocity());

		EntityUtil.updateEulerRotation(this, getRotation());
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate)
	{
		// Stray movement packets seem to make their way to the
		// client way after the ship has stopped so we just ignore
		// them
		if (getThrottle() > 0)
			this.updatePosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		if (player.shouldCancelInteraction())
			return ActionResult.FAIL;
		else
			return !this.world.isClient && player.startRiding(this) ? ActionResult.CONSUME : ActionResult.FAIL;
	}

	protected boolean canAddPassenger(Entity passenger)
	{
		return this.getPassengerList().size() < 2;
	}

	public void updatePassengerPosition(Entity passenger)
	{
		if (this.hasPassenger(passenger))
		{
			Vec3d vec3d = new Vec3d(0, 0, 3 * this.getPassengerList().indexOf(passenger));
			vec3d = QuatUtil.rotate(vec3d, getRotation());

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

	//	public Rotation getRotation(float t)
	//	{
	//		Rotation start = prevRotation;
	//		Rotation end = getRotation();
	//
	//		return MathUtil.lerp(start, end, t);
	//	}

	@Override
	public abstract Packet<?> createSpawnPacket();

	public EnumSet<ShipControls> getControls()
	{
		return ShipControls.unpack(getDataTracker().get(CONTROLS));
	}

	public void setControls(EnumSet<ShipControls> controls)
	{
		getDataTracker().set(CONTROLS, ShipControls.pack(controls));
	}

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

	public void setRotation(Quaternion q)
	{
		QuatUtil.normalize(q);
		instRotation = q.copy();
		getDataTracker().set(ROTATION, q);
	}

	@Environment(EnvType.CLIENT)
	public Quaternion getViewRotation(float t)
	{
		Quaternion start = viewPrevRotation;
		Quaternion end = viewRotation;
		return QuatUtil.slerp(start, end, t);
	}

	public void acceptControlInput(EnumSet<ShipControls> controls)
	{
		if (ShipControls.pack(controls) == getDataTracker().get(CONTROLS))
			return;

		setControls(controls);

		if (this.world.isClient)
		{
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeShort(ShipControls.pack(controls));
			ClientSidePacketRegistry.INSTANCE.sendToServer(SwgPackets.C2S.PacketShipControls, passedData);
		}
	}

	public void acceptMouseInput(double mouseDx, double mouseDy)
	{
		Quaternion rotation = new Quaternion(instRotation);

		boolean pitchRoll = false;

		if (pitchRoll)
			rotation.hamiltonProduct(new Quaternion(new Vector3f(0, 0, 1), -(float)mouseDx * 0.15f, true));
		else
		{
			Vec3d v = QuatUtil.project(MathUtil.POSY, rotation);
			rotation.hamiltonProduct(new Quaternion(new Vector3f(v), (float)(Math.asin(v.y) * -mouseDx * 0.1f), true));

			// TODO: roll back toward zero when this mode is switched to and the ship has a nonzero roll
		}

		rotation.hamiltonProduct(new Quaternion(new Vector3f(1, 0, 0), -(float)mouseDy * 0.1f, true));

		instRotation = rotation;

		PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeFloat(rotation.getW());
		passedData.writeFloat(rotation.getX());
		passedData.writeFloat(rotation.getY());
		passedData.writeFloat(rotation.getZ());
		ClientSidePacketRegistry.INSTANCE.sendToServer(SwgPackets.C2S.PacketShipRotation, passedData);
	}
}
