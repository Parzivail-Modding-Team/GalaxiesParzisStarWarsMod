package dev.pswg.entity;

import dev.pswg.data.BlasterBehaviorProfile;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
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
	 * Captured motion settings, also sent to clients.
	 */
	private BlasterBehaviorProfile.Flight _flight = BlasterBehaviorProfile.Flight.ENERGY;

	/**
	 * Fuse age in ticks.
	 */
	private int _flightAge;

	/**
	 * Prevents duplicate impact calls.
	 */
	private boolean _resolved;

	/**
	 * Excludes the reflector until the bolt leaves its hit box.
	 */
	private Entity _reflector;

	/**
	 * Keeps the global PvP gate when the owning player is offline.
	 */
	private boolean _playerOwned;

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
			setDeltaMovement(getDeltaMovement().add(0, -_flight.gravity(), 0));
			updateRotation();
			setPos(position().add(getDeltaMovement()));
			return;
		}

		if (_reflector != null && !_reflector.getBoundingBox().inflate(0.5).contains(position()))
		{
			_reflector = null;
		}

		_flightAge++;
		if (_flight.fuseTicks() > 0 && _flightAge >= _flight.fuseTicks())
		{
			detonate();
			return;
		}

		setDeltaMovement(getDeltaMovement().add(0, -_flight.gravity(), 0));
		updateRotation();

		var velocity = getDeltaMovement();
		var remaining = _shot == null ? Float.MAX_VALUE : _shot.stats().range() - _travelled;
		if (remaining <= 0 || velocity.lengthSqr() < 1.0E-12)
		{
			detonate();
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
		{
			detonate();
		}
	}

	/**
	 * Assigns shot before spawning.
	 */
	public void setShot(BlasterShot shot)
	{
		_shot = shot;
		_flight = shot.behavior().flight().orElse(BlasterBehaviorProfile.Flight.ENERGY);
	}

	/**
	 * Updates attribution when a bolt is fired or reflected.
	 */
	@Override
	public void setOwner(Entity owner)
	{
		super.setOwner(owner);
		_playerOwned = owner instanceof net.minecraft.world.entity.player.Player;
	}

	/**
	 * Returns the persisted player-owner fact even when the owner cannot resolve.
	 */
	public boolean playerOwned()
	{
		return _playerOwned;
	}

	/**
	 * Calculates the outcome of this projectile colliding with
	 * a block or entity
	 *
	 * @param hitResult The result of the hit projection
	 */
	protected void hitOrDeflect(HitResult hitResult)
	{
		if (_flight.reflectable() && hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity living
		    && BlasterReflection.canReflect(living, getDeltaMovement()))
		{
			var speed = getDeltaMovement().length();
			setOwner(living);
			deflect(ProjectileDeflection.AIM_DEFLECT, living, owner, true, speed);
			_reflector = living;
			setPos(position().add(getDeltaMovement().normalize().scale(0.05)));
			return;
		}

		if (_flight.reflectable())
		{
			hitTargetOrDeflectSelf(hitResult);
			return;
		}

		onHit(hitResult);
	}

	/**
	 * Runs the captured impact once after native deflection checks.
	 */
	@Override
	protected void onHit(HitResult result)
	{
		if (_resolved)
		{
			return;
		}

		_resolved = true;
		if (_shot != null && level() instanceof ServerLevel world)
		{
			_shot.hit(world, this, getOwner(), result, (float)_travelled);
		}

		onCollision(result);
	}

	/**
	 * Detonates on fuse or range expiry, then removes the projectile.
	 */
	private void detonate()
	{
		if (!_resolved && _shot != null && level() instanceof ServerLevel world)
		{
			_resolved = true;
			_shot.detonate(world, this, getOwner(), position(), (float)_travelled);
		}

		discard();
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
		return entity != _reflector && !(entity instanceof BlasterBoltEntity) && super.canHitEntity(entity);
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
		nbt.store("flight", BlasterBehaviorProfile.Flight.CODEC, _flight);

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
		_flight = view.read("flight", BlasterBehaviorProfile.Flight.CODEC)
		              .orElse(_shot == null ? BlasterBehaviorProfile.Flight.ENERGY : _shot.behavior().flight().orElse(BlasterBehaviorProfile.Flight.ENERGY));
		_flightAge = Math.clamp(view.getIntOr("flightAge", 0), 0, 200);
		_playerOwned = view.getBooleanOr("playerOwned", getOwner() instanceof net.minecraft.world.entity.player.Player);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view)
	{
		super.addAdditionalSaveData(view);

		if (_shot != null)
			view.store("shot", BlasterShot.CODEC, _shot);

		view.putDouble("travelled", _travelled);
		view.store("flight", BlasterBehaviorProfile.Flight.CODEC, _flight);
		view.putInt("flightAge", _flightAge);
		view.putBoolean("playerOwned", _playerOwned);
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
