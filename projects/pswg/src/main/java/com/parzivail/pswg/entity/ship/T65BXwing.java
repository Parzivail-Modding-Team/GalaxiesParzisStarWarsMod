package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgParticles;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.collision.CapsuleVolume;
import com.parzivail.pswg.entity.collision.ICollisionVolume;
import com.parzivail.pswg.entity.collision.IComplexEntityHitbox;
import com.parzivail.pswg.entity.collision.SweptTriangleVolume;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.ColorUtil;
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
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.function.Function;

public class T65BXwing extends ShipEntity implements IComplexEntityHitbox
{
	private static final CapsuleVolume VOL_FUSELAGE = new CapsuleVolume(new Vec3d(0, 0.12, 1), new Vec3d(0, 0.12, -4.65), 0.3);
	private static final CapsuleVolume VOL_MECHANICS = new CapsuleVolume(new Vec3d(0, 0, 3.7), new Vec3d(0, 0, 1), 0.8);

	// Root model is in Y-up space when rendered but all sub-parts stay in Z-up space
	private static final CapsuleVolume VOL_CANNON_TOP_RIGHT = new CapsuleVolume(new Vec3d(4.2, 0.27, 0.3), new Vec3d(4.2, 0.27, -5.5), 0.2);
	private static final CapsuleVolume VOL_CANNON_BOTTOM_RIGHT = new CapsuleVolume(new Vec3d(4.2, -0.27, 0.3), new Vec3d(4.2, -0.27, -5.5), 0.2);
	private static final CapsuleVolume VOL_CANNON_TOP_LEFT = new CapsuleVolume(new Vec3d(-4.2, 0.27, 0.3), new Vec3d(-4.2, 0.27, -5.5), 0.2);
	private static final CapsuleVolume VOL_CANNON_BOTTOM_LEFT = new CapsuleVolume(new Vec3d(-4.2, -0.27, 0.3), new Vec3d(-4.2, -0.27, -5.5), 0.2);

	private static final CapsuleVolume VOL_ENGINE_TOP_RIGHT = new CapsuleVolume(new Vec3d(1.17, 0.42, 1.6), new Vec3d(1.17, 0.42, -1.3), 0.5);
	private static final CapsuleVolume VOL_ENGINE_BOTTOM_RIGHT = new CapsuleVolume(new Vec3d(1.17, -0.42, 1.6), new Vec3d(1.17, -0.42, -1.3), 0.5);
	private static final CapsuleVolume VOL_ENGINE_TOP_LEFT = new CapsuleVolume(new Vec3d(-1.17, 0.42, 1.6), new Vec3d(-1.17, 0.42, -1.3), 0.5);
	private static final CapsuleVolume VOL_ENGINE_BOTTOM_LEFT = new CapsuleVolume(new Vec3d(-1.17, -0.42, 1.6), new Vec3d(-1.17, -0.42, -1.3), 0.5);

	private static final SweptTriangleVolume VOL_WING_TOP_RIGHT_A = new SweptTriangleVolume(new Vec3d(-1.7, -0.05, -0.95), new Vec3d(-4.3, -0.05, -0.95), new Vec3d(-4.3, -0.05, 0), 0.2);
	private static final SweptTriangleVolume VOL_WING_TOP_RIGHT_B = new SweptTriangleVolume(new Vec3d(-1.7, -0.05, -0.95), new Vec3d(-1.7, -0.05, 0.8), new Vec3d(-4.3, -0.05, 0), 0.2);
	private static final SweptTriangleVolume VOL_WING_BOTTOM_RIGHT_A = new SweptTriangleVolume(new Vec3d(-1.7, 0.05, -0.95), new Vec3d(-4.3, 0.05, -0.95), new Vec3d(-4.3, 0.05, 0), 0.2);
	private static final SweptTriangleVolume VOL_WING_BOTTOM_RIGHT_B = new SweptTriangleVolume(new Vec3d(-1.7, 0.05, -0.95), new Vec3d(-1.7, 0.05, 0.8), new Vec3d(-4.3, 0.05, 0), 0.2);
	private static final SweptTriangleVolume VOL_WING_TOP_LEFT_A = new SweptTriangleVolume(new Vec3d(1.7, -0.05, -0.95), new Vec3d(4.3, -0.05, -0.95), new Vec3d(4.3, -0.05, 0), 0.2);
	private static final SweptTriangleVolume VOL_WING_TOP_LEFT_B = new SweptTriangleVolume(new Vec3d(1.7, -0.05, -0.95), new Vec3d(1.7, -0.05, 0.8), new Vec3d(4.3, -0.05, 0), 0.2);
	private static final SweptTriangleVolume VOL_WING_BOTTOM_LEFT_A = new SweptTriangleVolume(new Vec3d(1.7, 0.05, -0.95), new Vec3d(4.3, 0.05, -0.95), new Vec3d(4.3, 0.05, 0), 0.2);
	private static final SweptTriangleVolume VOL_WING_BOTTOM_LEFT_B = new SweptTriangleVolume(new Vec3d(1.7, 0.05, -0.95), new Vec3d(1.7, 0.05, 0.8), new Vec3d(4.3, 0.05, 0), 0.2);

	private static final TrackedData<Byte> WING_ANIM = DataTracker.registerData(T65BXwing.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> COCKPIT_ANIM = DataTracker.registerData(T65BXwing.class, TrackedDataHandlerRegistry.BYTE);

	private static final TrackedData<Byte> CANNON_BITS = DataTracker.registerData(T65BXwing.class, TrackedDataHandlerRegistry.BYTE);
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

		var pos = this.getPos();
		var rotation = getRotation();
		var stack = new Transform();

		var cannonState = getCannonState();
		var p = RigT65B.INSTANCE.getWorldPosition(stack, this, rotation, CANNON_ORDER[cannonState], 0).add(pos);

		var convergenceDistance = 40;
		var forward = QuatUtil.rotate(MathUtil.NEGZ.multiply(convergenceDistance), rotation);
		var boltRotation = QuatUtil.lookAt(p, pos.add(forward));

		var pDir = QuatUtil.rotate(MathUtil.POSZ.multiply(5f), boltRotation);

		BlasterUtil.fireBolt(world, player, pDir.normalize(), 100, distance -> (double)50, true, blasterBoltEntity -> {
			blasterBoltEntity.setVelocity(pDir);
			blasterBoltEntity.setPos(p.x, p.y, p.z);
			blasterBoltEntity.setColor(ColorUtil.packHsv(0.98f, 1, 1));
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
		else
		{
			var stack = new Transform();

			float maxDistance = 10;
			var velocityLength = getVelocity().length();
			var rayDir = MathUtil.NEGY;
			int maxParticles = (int)(15 * velocityLength * velocityLength);
			var steps = (5 * velocityLength + 1);

			for (var cannon : RigT65B.CANNONS)
				spawnWakeParticles(maxDistance, (dt) -> RigT65B.INSTANCE.getWorldPosition(stack, this, getViewRotation(dt), cannon, dt).add(this.getLerpedPos(dt)), velocityLength, rayDir, maxParticles, steps);
			for (var engine : RigT65B.ENGINES)
				spawnWakeParticles(maxDistance, (dt) -> RigT65B.INSTANCE.getWorldPosition(stack, this, getViewRotation(dt), engine, dt).add(this.getLerpedPos(dt)), velocityLength, rayDir, maxParticles, steps);
		}
	}

	private void spawnWakeParticles(float maxDistance, Function<Float, Vec3d> sourcePosSupplier, double velocityLength, Vec3d rayDir, int maxParticles, double steps)
	{
		var velSquared = velocityLength * velocityLength;

		var ddt = 1 / steps;
		for (var dt = 0f; dt <= 1; dt += ddt)
		{
			var sourcePos = sourcePosSupplier.apply(dt);
			var blockHit = EntityUtil.raycastBlocks(sourcePos, rayDir, maxDistance, this, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY);
			if (blockHit.getType() != HitResult.Type.MISS)
			{
				var state = world.getBlockState(blockHit.getBlockPos());
				var particle = state.getFluidState().isIn(FluidTags.WATER) ? SwgParticles.WATER_WAKE : new BlockStateParticleEffect(SwgParticles.WAKE, state);

				var hitPos = blockHit.getPos();
				var dist = Math.max(hitPos.distanceTo(sourcePos), 0.5f);
				var count = (MathHelper.clamp(15 / dist, 0, maxParticles)) / steps;

				var lateralVelocity = velSquared / (3.24 * dist);
				var verticalVelocity = velSquared / (6.25 * dist);

				for (int i = 0; i < count; i++)
				{
					if (this.random.nextFloat() * 2 > velocityLength)
						continue;
					this.world.addParticle(particle, hitPos.x + this.random.nextGaussian() / 4, hitPos.y, hitPos.z + this.random.nextGaussian() / 4, lateralVelocity, verticalVelocity, lateralVelocity);
				}
			}
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
	public ICollisionVolume[] getCollision()
	{
		var pos = this.getPos();
		var rot = getRotation();

		var posMat = new Matrix4f().translate((float)pos.x, (float)pos.y, (float)pos.z);

		var transformTopRight = getWingCollisionTransform(rot, posMat, "WingTopRight");
		var transformBottomRight = getWingCollisionTransform(rot, posMat, "WingBottomRight");
		var transformTopLeft = getWingCollisionTransform(rot, posMat, "WingTopLeft");
		var transformBottomLeft = getWingCollisionTransform(rot, posMat, "WingBottomLeft");

		return new ICollisionVolume[] {
				VOL_FUSELAGE.transform(rot).transform(posMat),
				VOL_MECHANICS.transform(rot).transform(posMat),
				VOL_CANNON_TOP_RIGHT.transform(transformTopRight),
				VOL_CANNON_BOTTOM_RIGHT.transform(transformBottomRight),
				VOL_CANNON_TOP_LEFT.transform(transformTopLeft),
				VOL_CANNON_BOTTOM_LEFT.transform(transformBottomLeft),
				VOL_ENGINE_TOP_RIGHT.transform(transformTopRight),
				VOL_ENGINE_BOTTOM_RIGHT.transform(transformBottomRight),
				VOL_ENGINE_TOP_LEFT.transform(transformTopLeft),
				VOL_ENGINE_BOTTOM_LEFT.transform(transformBottomLeft),
				VOL_WING_TOP_RIGHT_A.transform(transformTopRight),
				VOL_WING_TOP_RIGHT_B.transform(transformTopRight),
				VOL_WING_BOTTOM_RIGHT_A.transform(transformBottomRight),
				VOL_WING_BOTTOM_RIGHT_B.transform(transformBottomRight),
				VOL_WING_TOP_LEFT_A.transform(transformTopLeft),
				VOL_WING_TOP_LEFT_B.transform(transformTopLeft),
				VOL_WING_BOTTOM_LEFT_A.transform(transformBottomLeft),
				VOL_WING_BOTTOM_LEFT_B.transform(transformBottomLeft),
				};
	}

	@NotNull
	private Matrix4f getWingCollisionTransform(Quaternionf rot, Matrix4f posMat, String wing)
	{
		var wingTransform = new Matrix4f(posMat);
		wingTransform.mul(RigT65B.INSTANCE.getTransform(this, rot, wing, 0));
		wingTransform.mul(RigT65B.INSTANCE.getPartTransformation(this, wing, 0));

		return wingTransform;
	}
}
