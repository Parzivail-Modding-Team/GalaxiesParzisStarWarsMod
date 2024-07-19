package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgParticleTypes;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.rigs.RigRZ1;
import com.parzivail.pswg.features.blasters.BlasterUtil;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.entity.collision.CapsuleVolume;
import com.parzivail.util.entity.collision.ICollisionVolume;
import com.parzivail.util.entity.collision.IComplexEntityHitbox;
import com.parzivail.util.entity.collision.SweptTriangleVolume;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import com.parzivail.util.math.Transform;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.Function;

public class RZ1Awing extends ShipEntity implements IComplexEntityHitbox
{
	private static final CapsuleVolume COCKPIT = new CapsuleVolume(new Vec3d(0, -0.077873, -1.0924), new Vec3d(0, 0.172123, 2.7826), 0.8);
	private static final CapsuleVolume VOL_CANNON_RIGHT = new CapsuleVolume(new Vec3d(4, -0.702874, -1.0924), new Vec3d(4, -0.702874, 2.4076), 0.3);
	private static final CapsuleVolume VOL_CANNON_LEFT = new CapsuleVolume(new Vec3d(-4, -0.702874, -1.0924), new Vec3d(-4, -0.702874, 2.4076), 0.3);
	private static final CapsuleVolume VOL_ENGINE_RIGHT = new CapsuleVolume(new Vec3d(-2.1875, -1.015378, 4.5326), new Vec3d(-2.1875, -1.01538, 0.9076), 0.9);
	private static final CapsuleVolume VOL_ENGINE_LEFT = new CapsuleVolume(new Vec3d(2.1875, -1.015378, 4.5326), new Vec3d(2.1875, -1.01538, 0.9076), 0.9);
	private static final CapsuleVolume VOL_GEAR_FRONT = new CapsuleVolume(new Vec3d(0, -2.75, -3.9674), new Vec3d(0, -2.75, -2.8424), 0.2);
	private static final CapsuleVolume VOL_GEAR_BACK_LEFT = new CapsuleVolume(new Vec3d(-1, -2.75, 3.76535), new Vec3d(-1, -2.75, 2.64035), 0.2);
	private static final CapsuleVolume VOL_GEAR_BACK_RIGHT = new CapsuleVolume(new Vec3d(1, -2.75, 3.76535), new Vec3d(1, -2.75, 2.64035), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDA = new SweptTriangleVolume(new Vec3d(-2.0, 0.29711, 1.6576), new Vec3d(2.0, 0.297125, 1.6576), new Vec3d(1.0625, -0.702869, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDB = new SweptTriangleVolume(new Vec3d(1.0625, -0.702872, -5.4674), new Vec3d(-1.0625, -0.702872, -5.4674), new Vec3d(-2.0, 0.29711, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHTA = new SweptTriangleVolume(new Vec3d(-3.5, -0.452875, 0.4076), new Vec3d(-1.0625, -0.702872, -5.4674), new Vec3d(-2.0, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHTB = new SweptTriangleVolume(new Vec3d(-3.5, -0.452875, 0.4076), new Vec3d(-3.5, -0.515389, 1.6576), new Vec3d(-2.0, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFTA = new SweptTriangleVolume(new Vec3d(3.5, -0.452875, 0.4076), new Vec3d(1.0625, -0.702872, -5.4674), new Vec3d(2.0, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFTB = new SweptTriangleVolume(new Vec3d(3.5, -0.452875, 0.4076), new Vec3d(3.5, -0.515389, 1.6576), new Vec3d(2.0, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_BACKA = new SweptTriangleVolume(new Vec3d(2.0, 0.297123, 3.5326), new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(-2.0, 0.297123, 3.5326), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_BACKB = new SweptTriangleVolume(new Vec3d(2.0, 0.297123, 3.5326), new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(2.0, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_MIDA = new SweptTriangleVolume(new Vec3d(-2.5, -1.70289, 1.6576), new Vec3d(2.5, -1.70289, 1.6576), new Vec3d(-1.125, -0.827873, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_MIDB = new SweptTriangleVolume(new Vec3d(1.125, -0.827873, -5.4674), new Vec3d(2.5, -1.70289, 1.6576), new Vec3d(-1.125, -0.827873, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_RIGHT = new SweptTriangleVolume(new Vec3d(-3.5, -1.07788, 0.4076), new Vec3d(-2.5, -1.70289, 1.6576), new Vec3d(-1.125, -0.827873, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_LEFT = new SweptTriangleVolume(new Vec3d(3.5, -1.07788, 0.4076), new Vec3d(2.5, -1.70289, 1.6576), new Vec3d(1.125, -0.827873, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_BACKA = new SweptTriangleVolume(new Vec3d(-2.5, -1.70289, 1.6576), new Vec3d(-2.5, -1.70288, 3.5326), new Vec3d(2.5, -1.70289, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_BACKB = new SweptTriangleVolume(new Vec3d(2.5, -1.70288, 3.5326), new Vec3d(-2.5, -1.70288, 3.5326), new Vec3d(2.5, -1.70289, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACKA = new SweptTriangleVolume(new Vec3d(-2.4375, -1.57788, 3.4076), new Vec3d(-2.0, 0.17212, 3.4076), new Vec3d(-2.4375, -1.57788, 3.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACKB = new SweptTriangleVolume(new Vec3d(-2.4375, -1.57788, 3.4076), new Vec3d(-2.0, 0.17212, 3.4076), new Vec3d(2.0, 0.17212, 3.4076), 0.2);

	private static final TrackedData<Byte> CANNON_BITS = DataTracker.registerData(RZ1Awing.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CANNON_STATE_MASK = 0b00000001;
	private static final String[] CANNON_ORDER = { RigRZ1.CANNON_LEFT, RigRZ1.CANNON_RIGHT };
	private static final TrackedData<Byte> LANDING_GEAR_ANIM = DataTracker.registerData(RZ1Awing.class, TrackedDataHandlerRegistry.BYTE);
	public static final int GEAR_ANIM_LENGTH = 40;
	public byte prevGearAnim;

	public RZ1Awing(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		getDataTracker().startTracking(CANNON_BITS, (byte)0);
		getDataTracker().startTracking(LANDING_GEAR_ANIM, (byte)0);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);

		dataTracker.set(LANDING_GEAR_ANIM, tag.getByte("gearAnim"));

		setCannonState(tag.getByte("cannonState"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);

		tag.putByte("gearAnim", dataTracker.get(LANDING_GEAR_ANIM));

		tag.putByte("cannonState", getCannonState());
	}

	public byte getGearAnim()
	{
		return dataTracker.get(LANDING_GEAR_ANIM);
	}

	@Override
	public boolean usePlayerPerspective()
	{
		return true;
	}

	@Override
	public Vec3d getPassengerSocket(int passengerIndex)
	{
		return new Vec3d(0, -1, 0);
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater)
	{
		//passenger.setHeadYaw(getHeadYaw());
		super.updatePassengerPosition(passenger, positionUpdater);
	}

	@Override
	public void tick()
	{
		super.tick();
		prevGearAnim = dataTracker.get(LANDING_GEAR_ANIM);

		if (!getWorld().isClient)
		{
			var controls = getControls();

			tickControlledAnim(LANDING_GEAR_ANIM, (byte)GEAR_ANIM_LENGTH, controls.contains(ShipControls.SPECIAL1));
		}
		else
		{
			var stack = new Transform();

			float maxDistance = 10;
			var velocityLength = getVelocity().length();
			var rayDir = MathUtil.V3D_NEG_Y;
			int maxParticles = (int)(15 * velocityLength * velocityLength);
			var steps = (5 * velocityLength + 1);

			for (var cannon : RigRZ1.CANNONS)
				spawnWakeParticles(maxDistance, (dt) -> RigRZ1.INSTANCE.getWorldPosition(stack, this, getViewRotation(dt), cannon, dt).add(this.getLerpedPos(dt)), velocityLength, rayDir, maxParticles, steps);
			for (var engine : RigRZ1.ENGINES)
				spawnWakeParticles(maxDistance, (dt) -> RigRZ1.INSTANCE.getWorldPosition(stack, this, getViewRotation(dt), engine, dt).add(this.getLerpedPos(dt)), velocityLength, rayDir, maxParticles, steps);
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
				var state = getWorld().getBlockState(blockHit.getBlockPos());
				var particle = state.getFluidState().isIn(FluidTags.WATER) ? SwgParticleTypes.WATER_WAKE : new BlockStateParticleEffect(SwgParticleTypes.WAKE, state);

				var hitPos = blockHit.getPos();
				var dist = Math.max(hitPos.distanceTo(sourcePos), 0.5f);
				var count = (MathHelper.clamp(15 / dist, 0, maxParticles)) / steps;

				var lateralVelocity = velSquared / (3.24 * dist);
				var verticalVelocity = velSquared / (6.25 * dist);

				for (int i = 0; i < count; i++)
				{
					if (this.random.nextFloat() * 2 > velocityLength)
						continue;
					this.getWorld().addParticle(particle, hitPos.x + this.random.nextGaussian() / 4, hitPos.y, hitPos.z + this.random.nextGaussian() / 4, lateralVelocity, verticalVelocity, lateralVelocity);
				}
			}
		}
	}

	@Override
	public void acceptFireInput()
	{
		var passenger = getControllingPassenger();
		if (!(passenger instanceof PlayerEntity player))
			return;

		var pos = this.getPos();
		var rotation = getRotation();
		var stack = new Transform();

		var cannonState = getCannonState();
		var p = RigRZ1.INSTANCE.getWorldPosition(stack, this, rotation, CANNON_ORDER[cannonState], 0).add(pos);

		var convergenceDistance = 40;
		var forward = QuatUtil.rotate(MathUtil.V3D_NEG_Z.multiply(convergenceDistance), rotation);
		var boltRotation = QuatUtil.lookAt(p, pos.add(forward));

		var pDir = QuatUtil.rotate(MathUtil.V3D_POS_Z.multiply(5f), boltRotation);

		BlasterUtil.fireBolt(getWorld(), player, pDir.normalize(), 100, distance -> (double)50, true, blasterBoltEntity -> {
			blasterBoltEntity.setVelocity(pDir);
			blasterBoltEntity.setPos(p.x, p.y, p.z);
			blasterBoltEntity.setColor(ColorUtil.packHsv(0.98f, 1, 1));
		});

		getWorld().playSound(null, player.getBlockPos(), SwgSounds.Ship.XWINGT65B_FIRE, SoundCategory.PLAYERS, 1, 1 + (float)getWorld().random.nextGaussian() / 10);

		cannonState++;
		setCannonState(cannonState);
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
		return new ICollisionVolume[] {
				COCKPIT.transform(rot).transform(posMat),
				VOL_CANNON_RIGHT.transform(rot).transform(posMat),
				VOL_CANNON_LEFT.transform(rot).transform(posMat),
				VOL_ENGINE_RIGHT.transform(rot).transform(posMat),
				VOL_ENGINE_LEFT.transform(rot).transform(posMat),
				VOL_GEAR_FRONT.transform(rot).transform(posMat),
				VOL_GEAR_BACK_LEFT.transform(rot).transform(posMat),
				VOL_GEAR_BACK_RIGHT.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDA.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDB.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHTA.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHTB.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFTA.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFTB.transform(rot).transform(posMat),
				VOL_BODY_TOP_BACKA.transform(rot).transform(posMat),
				VOL_BODY_TOP_BACKB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_MIDA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_MIDB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_RIGHT.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_LEFT.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_BACKA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_BACKB.transform(rot).transform(posMat),
				VOL_BODY_BACKA.transform(rot).transform(posMat),
				VOL_BODY_BACKB.transform(rot).transform(posMat),
				};
	}
}

