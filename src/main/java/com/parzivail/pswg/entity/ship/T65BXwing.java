package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.collision.CapsuleVolume;
import com.parzivail.pswg.entity.collision.IComplexEntityHitbox;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import com.parzivail.util.math.Transform;
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
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class T65BXwing extends ShipEntity implements IComplexEntityHitbox
{
	private static final CapsuleVolume VOL_FUSELAGE = new CapsuleVolume(new Vec3d(0, 0.12, 1), new Vec3d(0, 0.12, -4.65), 0.3);
	private static final CapsuleVolume VOL_MECHANICS = new CapsuleVolume(new Vec3d(0, 0, 3.7), new Vec3d(0, 0, 1), 0.8);

	private static final CapsuleVolume VOL_WING_TOP_RIGHT = new CapsuleVolume(new Vec3d(4.2, 0.27, 0.3), new Vec3d(4.2, 0.27, -5.5), 0.2);
	private static final CapsuleVolume VOL_WING_BOTTOM_RIGHT = new CapsuleVolume(new Vec3d(4.2, -0.27, 0.3), new Vec3d(4.2, -0.27, -5.5), 0.2);
	private static final CapsuleVolume VOL_WING_TOP_LEFT = new CapsuleVolume(new Vec3d(-4.2, 0.27, 0.3), new Vec3d(-4.2, 0.27, -5.5), 0.2);
	private static final CapsuleVolume VOL_WING_BOTTOM_LEFT = new CapsuleVolume(new Vec3d(-4.2, -0.27, 0.3), new Vec3d(-4.2, -0.27, -5.5), 0.2);

	private static final CapsuleVolume VOL_ENGINE_TOP_RIGHT = new CapsuleVolume(new Vec3d(1.17, 0.42, 1.6), new Vec3d(1.17, 0.42, -1.3), 0.5);
	private static final CapsuleVolume VOL_ENGINE_BOTTOM_RIGHT = new CapsuleVolume(new Vec3d(1.17, -0.42, 1.6), new Vec3d(1.17, -0.42, -1.3), 0.5);
	private static final CapsuleVolume VOL_ENGINE_TOP_LEFT = new CapsuleVolume(new Vec3d(-1.17, 0.42, 1.6), new Vec3d(-1.17, 0.42, -1.3), 0.5);
	private static final CapsuleVolume VOL_ENGINE_BOTTOM_LEFT = new CapsuleVolume(new Vec3d(-1.17, -0.42, 1.6), new Vec3d(-1.17, -0.42, -1.3), 0.5);

	private static final TrackedData<Byte> WING_ANIM = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> COCKPIT_ANIM = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);

	private static final TrackedData<Byte> CANNON_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CANNON_STATE_MASK = 0b00000011;

	private static final String[] CANNON_ORDER = { "CannonTopLeft", "CannonBottomLeft", "CannonTopRight", "CannonBottomRight" };

	public static final int WING_ANIM_LENGTH = 40;
	public static final int COCKPIT_ANIM_LENGTH = 20;

	public byte prevWingAnim;
	public byte prevCockpitAnim;

	public T65BXwing(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		getDataTracker().startTracking(WING_ANIM, (byte)0);
		getDataTracker().startTracking(COCKPIT_ANIM, (byte)0);
		getDataTracker().startTracking(CANNON_BITS, (byte)0);
	}

	@Override
	protected SoundEvent getExteriorSoundEvent()
	{
		return SwgSounds.Ship.XWINGT65B_EXTERIOR;
	}

	public byte getWingAnim()
	{
		return dataTracker.get(WING_ANIM);
	}

	public byte getCockpitAnim()
	{
		return dataTracker.get(COCKPIT_ANIM);
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

		var p = RigT65B.INSTANCE.getWorldPosition(stack, this, this.getRotation(), CANNON_ORDER[cannonState], 0);

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

		prevWingAnim = dataTracker.get(WING_ANIM);
		prevCockpitAnim = dataTracker.get(COCKPIT_ANIM);

		if (!world.isClient)
		{
			var controls = getControls();

			tickControlledAnim(WING_ANIM, (byte)WING_ANIM_LENGTH, controls.contains(ShipControls.SPECIAL1));
			tickControlledAnim(COCKPIT_ANIM, (byte)COCKPIT_ANIM_LENGTH, controls.contains(ShipControls.SPECIAL2));
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);

		dataTracker.set(WING_ANIM, tag.getByte("wingAnim"));
		dataTracker.set(COCKPIT_ANIM, tag.getByte("cockpitAni"));

		setCannonState(tag.getByte("cannonState"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);

		tag.putByte("wingAnim", dataTracker.get(WING_ANIM));
		tag.putByte("cockpitAnim", dataTracker.get(COCKPIT_ANIM));

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

	@Override
	public CapsuleVolume[] getCollision()
	{
		var pos = this.getPos();
		var rot = getRotation();

		var posMat = Matrix4f.translate((float)pos.x, (float)pos.y, (float)pos.z);

		var transformTopRight = getWingCollisionTransform(rot, posMat, "WingTopRight");
		var transformBottomRight = getWingCollisionTransform(rot, posMat, "WingBottomRight");
		var transformTopLeft = getWingCollisionTransform(rot, posMat, "WingTopLeft");
		var transformBottomLeft = getWingCollisionTransform(rot, posMat, "WingBottomLeft");

		return new CapsuleVolume[] {
				VOL_FUSELAGE.transform(rot).transform(posMat),
				VOL_MECHANICS.transform(rot).transform(posMat),
				VOL_WING_TOP_RIGHT.transform(transformTopRight),
				VOL_WING_BOTTOM_RIGHT.transform(transformBottomRight),
				VOL_WING_TOP_LEFT.transform(transformTopLeft),
				VOL_WING_BOTTOM_LEFT.transform(transformBottomLeft),
				VOL_ENGINE_TOP_RIGHT.transform(transformTopRight),
				VOL_ENGINE_BOTTOM_RIGHT.transform(transformBottomRight),
				VOL_ENGINE_TOP_LEFT.transform(transformTopLeft),
				VOL_ENGINE_BOTTOM_LEFT.transform(transformBottomLeft)
		};
	}

	@NotNull
	private Matrix4f getWingCollisionTransform(Quaternion rot, Matrix4f posMat, String wing)
	{
		var wingTransform = posMat.copy();
		wingTransform.multiply(RigT65B.INSTANCE.getTransform(this, rot, wing, 0));
		wingTransform.multiply(RigT65B.INSTANCE.getPartTransformation(this, wing, 0));

		// TODO: why?
		wingTransform.multiply(new Quaternion(90, 0, 0, true));

		return wingTransform;
	}
}
