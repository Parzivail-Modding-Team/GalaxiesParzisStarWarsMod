package dev.pswg.entity;

import dev.pswg.networking.GalaxiesEntitySpawnS2CPacket;
import dev.pswg.networking.GalaxiesNetworking;
import dev.pswg.networking.IPreciseSpawnDataEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LinearInterpolationHandler;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Blaster bolt entity.
 */
public class BlasterBoltEntity extends Projectile implements IPreciseSpawnDataEntity
{
	/**
	 * Short correction interval for fast bolts.
	 */
	public static final int UPDATE_INTERVAL_TICKS = 2;

	/**
	 * Captured server shot data.
	 */
	private BlasterShot _shot;

	/**
	 * Traveled distance.
	 */
	private double _travelled;

	/**
	 * Creates an unlaunched bolt.
	 */
	public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> type, Level world)
	{
		super(type, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
	}

	/**
	 * Smooths corrections while vanilla accounts for the bolt's predicted travel between updates.
	 */
	@Override
	protected InterpolationHandler createInterpolationHandler()
	{
		return LinearInterpolationHandler.create(this, UPDATE_INTERVAL_TICKS);
	}

	@Override
	public boolean hurtServer(ServerLevel world, DamageSource source, float amount)
	{
		return false;
	}

	@Override
	public void tick()
	{
		super.tick();

		// Clients predict straight travel only
		if (level().isClientSide())
		{
			setPos(position().add(getDeltaMovement()));
			return;
		}

		var velocity = getDeltaMovement();
		var remaining = _shot == null ? Float.MAX_VALUE : _shot.stats().range() - _travelled;
		if (remaining <= 0 || velocity.lengthSqr() < 1.0E-12)
		{
			discard();
			return;
		}
		if (velocity.length() > remaining)
			setDeltaMovement(velocity.normalize().scale(remaining));
		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		Vec3 nextPos;

		if (hitResult.getType() != HitResult.Type.MISS)
			nextPos = hitResult.getLocation();
		else
			nextPos = this.position().add(this.getDeltaMovement());

		_travelled += position().distanceTo(nextPos);
		this.setPos(nextPos);
		setDeltaMovement(velocity);

		if (hitResult.getType() != HitResult.Type.MISS && this.isAlive())
		{
			this.hitOrDeflect(hitResult);
		}

		if ((_shot != null && _travelled >= _shot.stats().range()) || (_shot == null && this.tickCount > 20))
			discard();
	}

	/**
	 * Assigns shot before spawning.
	 */
	public void setShot(BlasterShot shot)
	{
		_shot = shot;
	}

	/**
	 * Calculates the outcome of this projectile colliding with
	 * a block or entity
	 *
	 * @param hitResult The result of the hit projection
	 */
	protected void hitOrDeflect(HitResult hitResult)
	{
		if (_shot != null && level() instanceof ServerLevel world)
			_shot.hit(world, this, getOwner(), hitResult, (float)_travelled);

		this.onCollision(hitResult);
	}

	/**
	 * Called in response to a detected collision between this
	 * projectile and a block or entity
	 *
	 * @param hitResult The result of the hit projection
	 */
	protected void onCollision(HitResult hitResult)
	{
		discard();
	}

	/**
	 * Determines if this projectile can collide with the given
	 * entity
	 *
	 * @param entity The entity this projectile might hit
	 *
	 * @return True if the collision is allowed
	 */
	@Override
	protected boolean canHitEntity(Entity entity)
	{
		return !(entity instanceof BlasterBoltEntity) && super.canHitEntity(entity);
	}

	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet)
	{
		super.recreateFromPacket(packet);

		float yaw = packet.getYRot();
		float pitch = packet.getXRot();
		absSnapRotationTo(yaw, pitch);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entityTrackerEntry)
	{
		var nbt = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);

		super.addAdditionalSaveData(nbt);

		return GalaxiesNetworking.createPlayS2CPacket(new GalaxiesEntitySpawnS2CPacket(
				this,
				entityTrackerEntry,
				CompoundTag.CODEC,
				nbt.buildResult()
		));
	}

	@Override
	protected void readAdditionalSaveData(ValueInput view)
	{
		super.readAdditionalSaveData(view);

		_shot = view.read("shot", BlasterShot.CODEC).orElse(null);
		_travelled = view.getDoubleOr("travelled", 0);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view)
	{
		super.addAdditionalSaveData(view);

		if (_shot != null)
			view.store("shot", BlasterShot.CODEC, _shot);

		view.putDouble("travelled", _travelled);
	}

	@Override
	public void applySpawnData(GalaxiesEntitySpawnS2CPacket packet)
	{
		absSnapRotationTo(packet.getYaw(), packet.getPitch());
		setDeltaMovement(packet.getVelocity());

		var nbt = packet.getCustomData(this, CompoundTag.CODEC).getOrThrow();
		var readView = TagValueInput.create(ProblemReporter.DISCARDING, level().registryAccess(), nbt);
		readAdditionalSaveData(readView);
	}
}
