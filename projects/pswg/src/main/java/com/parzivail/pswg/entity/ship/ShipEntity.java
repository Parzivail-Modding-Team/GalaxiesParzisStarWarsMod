package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.client.render.camera.ChaseCam;
import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.data.PacketByteBufHelper;
import com.parzivail.util.entity.*;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class ShipEntity extends Entity implements IFlyingVehicle, IPrecisionVelocityEntity
{
	private static final TrackedData<Quaternionf> ROTATION = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.QUATERNION);
	private static final TrackedData<Float> THROTTLE = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Short> CONTROL_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.SHORT);
	private static final TrackedData<Short> SHIELD_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.SHORT);
	private static final TrackedData<Integer> FUEL_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.INTEGER);

	@Environment(EnvType.CLIENT)
	private ChaseCam camera;

	@Environment(EnvType.CLIENT)
	protected Quaternionf clientInstRotation = new Quaternionf(QuatUtil.IDENTITY);
	@Environment(EnvType.CLIENT)
	private Quaternionf clientRotation = new Quaternionf(QuatUtil.IDENTITY);
	@Environment(EnvType.CLIENT)
	private Quaternionf clientPrevRotation = new Quaternionf(QuatUtil.IDENTITY);
	@Environment(EnvType.CLIENT)
	private boolean firstRotationUpdate = true;

	private Quaternionf viewRotation = new Quaternionf(QuatUtil.IDENTITY);
	private Quaternionf viewPrevRotation = new Quaternionf(QuatUtil.IDENTITY);

	public ShipEntity(EntityType<?> type, World world)
	{
		super(type, world);
		this.intersectionChecked = true;
	}

	@Environment(EnvType.CLIENT)
	public static boolean handleMouseInput(double cursorDeltaX, double cursorDeltaY)
	{
		var minecraft = MinecraftClient.getInstance();

		var player = minecraft.player;

		assert player != null;

		var ship = getShip(player);

		if (ship != null && ship.isPilot(player) && ship.useMouseInput(player))
		{
			ship.acceptMouseInput(cursorDeltaX, cursorDeltaY);
			return !ship.usePlayerPerspective();
		}

		return false;
	}

	public static void handleFirePacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		server.execute(() -> {
			var ship = getShip(player);

			if (ship != null && ship.isPilot(player))
				ship.acceptFireInput();
		});
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		var sound = getExteriorSoundEvent();
		if (sound != null)
			SoundHelper.playShipExteriorSound(this, sound);
	}

	@Override
	public boolean shouldRender(double distance)
	{
		return true;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state)
	{
	}

	@Override
	protected void playSwimSound(float volume)
	{
	}

	protected SoundEvent getExteriorSoundEvent()
	{
		return null;
	}

	public static void handleRotationPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var q = PacketByteBufHelper.readQuaternion(buf);

		server.execute(() -> {
			var ship = getShip(player);

			if (ship != null && ship.isPilot(player))
				ship.setRotation(q);
		});
	}

	public static void handleControlPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var controls = buf.readShort();

		server.execute(() -> {
			var ship = getShip(player);

			if (ship != null && ship.isPilot(player))
				ship.acceptControlInput(ShipControls.unpack(controls));
		});
	}

	public static ShipEntity getShip(LivingEntity player)
	{
		if (player == null)
			return null;

		var vehicle = player.getVehicle();

		if (vehicle instanceof ShipEntity ship)
		{
			if (ship.getControllingPassenger() == player)
				return ship;
		}

		return null;
	}

	@Override
	public Box getVisibilityBoundingBox()
	{
		return getBoundingBox().expand(5);
	}

	@Override
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
	public boolean canHit()
	{
		return !this.isRemoved();
	}

	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if (this.isInvulnerableTo(source) || source.getSource() == null)
			return false;

		this.kill();

		return true;
	}

	@Override
	protected void initDataTracker()
	{
		getDataTracker().startTracking(ROTATION, new Quaternionf(QuatUtil.IDENTITY));
		getDataTracker().startTracking(THROTTLE, 0f);
		getDataTracker().startTracking(CONTROL_BITS, (short)0);
		getDataTracker().startTracking(SHIELD_BITS, (short)0);
		getDataTracker().startTracking(FUEL_BITS, 0);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound tag)
	{
		if (tag.contains("rotation"))
			setRotation(QuatUtil.getQuaternion(tag, "rotation"));
		setThrottle(tag.getFloat("throttle"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag)
	{
		QuatUtil.putQuaternion(tag, "rotation", getRotation());

		tag.putFloat("throttle", getThrottle());
	}

	@Environment(EnvType.CLIENT)
	public ChaseCam getCamera()
	{
		if (camera == null)
			camera = new ChaseCam();

		return camera;
	}

	public boolean usePlayerPerspective()
	{
		return false;
	}

	public boolean isPilot(PlayerEntity player)
	{
		return getControllingPassenger() == player;
	}

	public boolean useMouseInput(PlayerEntity player)
	{
		return isPilot(player);
	}

	@Override
	public void tick()
	{
		viewPrevRotation = new Quaternionf(viewRotation);

		super.tick();
		if (this.isLogicalSideForUpdatingMovement())
			this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());

		viewRotation = new Quaternionf(getRotation());

		EntityUtil.updateEulerRotation(this, viewRotation);

		if (getWorld().isClient)
		{
			if (Client.isShipClientControlled(this))
			{
				clientPrevRotation = new Quaternionf(clientRotation);
				clientRotation = new Quaternionf(clientInstRotation);
			}
			else
			{
				clientPrevRotation = new Quaternionf(viewPrevRotation);
				clientRotation = new Quaternionf(viewRotation);
			}

			var camera = getCamera();
			camera.tick(this);
		}

		var throttle = getThrottle();

		var pilot = getControllingPassenger();
		if (pilot instanceof PlayerEntity)
		{
			var controls = getControls();

			if (controls.contains(ShipControls.THROTTLE_UP))
				throttle += 0.3f;
			if (controls.contains(ShipControls.THROTTLE_DOWN))
				throttle -= 0.3f;

			throttle = MathHelper.clamp(throttle, 0, 3);
			setThrottle(throttle);
		}
		else if (throttle > 0)
		{
			if (throttle > 0.5f)
				throttle *= 0.8f;
			else
				throttle = 0;

			throttle = MathHelper.clamp(throttle, 0, 3);
			setThrottle(throttle);
		}

		var forward = getThrottleVelocity(throttle);
		setVelocity(forward);

		move(MovementType.SELF, getVelocity());
	}

	protected Vec3d getThrottleVelocity(float throttle)
	{
		return QuatUtil.rotate(MathUtil.V3D_NEG_Z, getRotation()).multiply(throttle);
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		var config = Resources.CONFIG.get();
		if (!player.hasPermissionLevel(config.server.vehiclePermissionLevel))
			return ActionResult.FAIL;

		if (player.shouldCancelInteraction())
			return ActionResult.FAIL;
		else
			return !this.getWorld().isClient && player.startRiding(this) ? ActionResult.CONSUME : ActionResult.FAIL;
	}

	protected int getMaxPassengers()
	{
		return 1;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger)
	{
		return this.getPassengerList().size() < getMaxPassengers();
	}

	public Vec3d getPassengerSocket(int passengerIndex)
	{
		return new Vec3d(0, 0, 3);
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater)
	{
		if (this.hasPassenger(passenger))
		{
			var q = getRotation();

			var vec3d = getPassengerSocket(this.getPassengerList().indexOf(passenger));
			vec3d = QuatUtil.rotate(vec3d, q);

			if (!usePlayerPerspective())
				EntityUtil.updateEulerRotation(passenger, q);
			else
				passenger.setBodyYaw(this.getYaw());

			positionUpdater.accept(passenger, this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
			//			this.copyEntityData(passenger);
		}
	}

	protected void copyEntityData(Entity entity)
	{
		this.setPitch(entity.getPitch());
		this.setYaw(entity.getYaw());
	}

	@Override
	@Nullable
	public LivingEntity getControllingPassenger()
	{
		if (getFirstPassenger() instanceof LivingEntity livingEntity)
			return livingEntity;
		return null;
	}

	//	public Rotation getRotation(float t)
	//	{
	//		Rotation start = prevRotation;
	//		Rotation end = getRotation();
	//
	//		return MathUtil.lerp(start, end, t);
	//	}

	protected void tickControlledAnim(TrackedData<Byte> data, byte animLength, boolean keyInput)
	{
		var anim = dataTracker.get(data);
		anim = TrackedAnimationValue.tick(anim);

		if (getControllingPassenger() instanceof PlayerEntity)
		{
			if (keyInput && TrackedAnimationValue.isStopped(anim))
				anim = TrackedAnimationValue.startToggled(anim, animLength);
		}

		dataTracker.set(data, anim);
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
	}

	public EnumSet<ShipControls> getControls()
	{
		return ShipControls.unpack(getDataTracker().get(CONTROL_BITS));
	}

	public void setControls(EnumSet<ShipControls> controls)
	{
		getDataTracker().set(CONTROL_BITS, ShipControls.pack(controls));
	}

	public float getThrottle()
	{
		return getDataTracker().get(THROTTLE);
	}

	public void setThrottle(float t)
	{
		if (t != getThrottle())
			getDataTracker().set(THROTTLE, t);
	}

	public Quaternionf getRotation()
	{
		return getDataTracker().get(ROTATION);
	}

	public void setRotation(Quaternionf q)
	{
		q.normalize();
		getDataTracker().set(ROTATION, q);
	}

	@Environment(EnvType.CLIENT)
	public Quaternionf getViewRotation(float t)
	{
		var start = clientPrevRotation;
		var end = clientRotation;
		return QuatUtil.slerp(start, end, t);
	}

	public void acceptControlInput(EnumSet<ShipControls> controls)
	{
		if (ShipControls.pack(controls) == getDataTracker().get(CONTROL_BITS))
			return;

		setControls(controls);

		if (this.getWorld().isClient)
		{
			var passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeShort(ShipControls.pack(controls));
			ClientPlayNetworking.send(SwgPackets.C2S.ShipControls, passedData);
		}
	}

	public void acceptFireInput()
	{
	}

	@Environment(EnvType.CLIENT)
	public void acceptMouseInput(double mouseDx, double mouseDy)
	{
		if (this.firstUpdate)
			return;

		if (!allowPitchMovement())
			mouseDy = 0;

		var rotation = new Quaternionf(clientInstRotation);
		if (firstRotationUpdate)
		{
			rotation = new Quaternionf(getRotation());
			firstRotationUpdate = false;
		}

		var shipRollPriority = Resources.CONFIG.get().input.shipRollPriority;

		if (!usePlayerPerspective() && Client.KEY_SHIP_INPUT_MODE_OVERRIDE.isPressed())
			shipRollPriority = !shipRollPriority;

		if (shipRollPriority)
			rotation.rotateZ(MathUtil.toRadians(-(float)mouseDx * 0.1f));
		else
		{
			rotation.rotateY(MathUtil.toRadians(-(float)mouseDx * 0.1f));

			var ea = QuatUtil.toEulerAngles(rotation);
			var currentUp = QuatUtil.rotate(new Vec3d(0, 1, 0), rotation);
			var currentForward = QuatUtil.rotate(new Vec3d(0, 0, 1), rotation);

			var targetYaw = ea.getYaw();
			var targetPitch = ea.getPitch();

			if (currentUp.y < 0)
				targetPitch += 180;

			// up pitch is 90deg out of phase with the forward pitch
			var zeroRollUp = MathUtil.anglesToLook(targetPitch - 90, targetYaw);

			var angle = Math.acos(currentUp.dotProduct(zeroRollUp) / (currentUp.length() * zeroRollUp.length()));

			if (Math.abs(angle) > 0.01)
			{
				Vec3d vec3d = currentUp.crossProduct(zeroRollUp).normalize();
				var zeroRollRotation = new Quaternionf().rotationAxis((float)angle, vec3d.toVector3f());
				zeroRollRotation.mul(rotation);

				// Prevent getting stuck spinning at poles by tapering off max lerp near the poles
				var maxRotationSpeed = (float)(1 - Math.abs(Math.asin(currentForward.y) / MathHelper.HALF_PI));
				maxRotationSpeed *= maxRotationSpeed;

				rotation = QuatUtil.slerp(rotation, zeroRollRotation, 0.005f * maxRotationSpeed);
			}
		}

		rotation.rotateX(MathUtil.toRadians(-(float)mouseDy * 0.1f));

		setRotation(rotation);

		clientInstRotation = new Quaternionf(rotation);

		var passedData = new PacketByteBuf(Unpooled.buffer());
		PacketByteBufHelper.writeQuaternion(passedData, rotation);
		ClientPlayNetworking.send(SwgPackets.C2S.ShipRotation, passedData);
	}

	protected boolean allowPitchMovement()
	{
		return true;
	}

	@Environment(EnvType.CLIENT)
	public boolean acceptLeftClick(PlayerEntity player)
	{
		if (!isPilot(player))
			return false;

		ClientPlayNetworking.send(SwgPackets.C2S.ShipFire, new PacketByteBuf(Unpooled.buffer()));
		return true;
	}

	@Environment(EnvType.CLIENT)
	public float getCameraLerp()
	{
		return MathHelper.clamp(Resources.CONFIG.get().view.shipCameraStiffness, 0.1f, 1f);
	}
}
