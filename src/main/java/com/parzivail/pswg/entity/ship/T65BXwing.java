package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.pswg.util.QuatUtil;
import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.Transform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class T65BXwing extends ShipEntity
{
	private static final TrackedData<TrackedAnimationValue> WING_ANIM = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.ANIM);
	private static final TrackedData<TrackedAnimationValue> COCKPIT_ANIM = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.ANIM);

	private static final TrackedData<Byte> CANNON_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CANNON_STATE_MASK = 0b00000011;

	private static final RigT65B.Socket[] CANNON_ORDER = { RigT65B.Socket.CannonTopLeft, RigT65B.Socket.CannonBottomLeft, RigT65B.Socket.CannonTopRight, RigT65B.Socket.CannonBottomRight };

	@Environment(EnvType.CLIENT)
	public TrackedAnimationValue clientWingAnim;

	@Environment(EnvType.CLIENT)
	public TrackedAnimationValue clientCockpitAnim;

	public T65BXwing(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		getDataTracker().startTracking(WING_ANIM, new TrackedAnimationValue());
		getDataTracker().startTracking(COCKPIT_ANIM, new TrackedAnimationValue());
		getDataTracker().startTracking(CANNON_BITS, (byte)0);
	}

	@Override
	protected SoundEvent getExteriorSoundEvent()
	{
		return SwgSounds.Ship.XWINGT65B_EXTERIOR;
	}

	public TrackedAnimationValue getWingAnim()
	{
		return dataTracker.get(WING_ANIM);
	}

	public void setWingAnim(TrackedAnimationValue value)
	{
		dataTracker.set(WING_ANIM, value);
	}

	public TrackedAnimationValue getCockpitAnim()
	{
		return dataTracker.get(COCKPIT_ANIM);
	}

	public void setCockpitAnim(TrackedAnimationValue value)
	{
		dataTracker.set(COCKPIT_ANIM, value);
	}

	@Override
	public void acceptFireInput()
	{
		var passenger = getPrimaryPassenger();
		if (!(passenger instanceof PlayerEntity player))
			return;

		var pDir = QuatUtil.rotate(MathUtil.NEGZ.multiply(4f), getRotation());
		var stack = new Transform();

		var cannonState = getCannonState();

		var p = CANNON_ORDER[cannonState].getWorldPosition(stack, this);

		BlasterUtil.fireBolt(world, player, pDir, 100, 50, blasterBoltEntity -> {
			blasterBoltEntity.setVelocity(pDir);
			blasterBoltEntity.setPos(this.getX() + p.x, this.getY() + p.y, this.getZ() + p.z);
		});

		world.playSound(null, player.getBlockPos(), SwgSounds.Ship.XWINGT65B_FIRE, SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 10);

		cannonState++;
		setCannonState(cannonState);
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions)
	{
		return 0f;
//		return super.getEyeHeight(pose, dimensions);
	}

	@Override
	public void tick()
	{
		super.tick();

		var controls = getControls();

		tickControlledAnim(WING_ANIM, (byte)20, controls.contains(ShipControls.SPECIAL1));
		tickControlledAnim(COCKPIT_ANIM, (byte)20, controls.contains(ShipControls.SPECIAL2));

		clientCockpitAnim = getCockpitAnim();
		clientWingAnim = getWingAnim();
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);

		getWingAnim().write(tag, "wingAnim");
		getCockpitAnim().write(tag, "cockpitAnim");

		setCannonState(tag.getByte("cannonState"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);

		setWingAnim(TrackedAnimationValue.read(tag, "wingAnim"));
		setCockpitAnim(TrackedAnimationValue.read(tag, "cockpitAnim"));

		tag.putByte("cannonState", getCannonState());
	}

	public byte getCannonState()
	{
		return (byte)(getDataTracker().get(CANNON_BITS) & CANNON_STATE_MASK);
	}

	public void setCannonState(byte cannonState)
	{
		byte cannons = getDataTracker().get(CANNON_BITS);

		cannons &= ~CANNON_STATE_MASK;
		cannons |= cannonState & CANNON_STATE_MASK;

		getDataTracker().set(CANNON_BITS, cannons);
	}
}
