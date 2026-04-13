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
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlasterBoltEntity extends Entity implements IPreciseSpawnDataEntity
{
	public BlasterBoltEntity(EntityType<?> type, Level world)
	{
		super(type, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
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
//		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHit);
//		Vec3 nextPos;
//
//		if (hitResult.getType() != HitResult.Type.MISS)
//			nextPos = hitResult.getLocation();
//		else
//			nextPos = this.position().add(this.getDeltaMovement());
//
//		this.setPos(nextPos);
//
//		super.tick();
//
//		if (hitResult.getType() != HitResult.Type.MISS && this.isAlive())
//		{
//			this.hitOrDeflect(hitResult);
//		}
//
//		if (this.tickCount > 20)
//			discard();
	}

	/**
	 * Calculates the outcome of this projectile colliding with
	 * a block or entity
	 *
	 * @param hitResult The result of the hit projection
	 */
	protected void hitOrDeflect(HitResult hitResult)
	{
		if (hitResult.getType() == HitResult.Type.ENTITY)
		{
			EntityHitResult entityHitResult = (EntityHitResult)hitResult;
			Entity entity = entityHitResult.getEntity();

			// TODO: deflect against blocking player
		}

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
	protected boolean canHit(Entity entity)
	{
		return true;
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
		addAdditionalSaveData(nbt);

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

	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view)
	{

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
