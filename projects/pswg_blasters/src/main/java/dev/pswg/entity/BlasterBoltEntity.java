package dev.pswg.entity;

import dev.pswg.networking.GalaxiesEntitySpawnS2CPacket;
import dev.pswg.networking.GalaxiesNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlasterBoltEntity extends Entity
{
	public BlasterBoltEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount)
	{
		return false;
	}

	@Override
	public void tick()
	{
		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
		Vec3d nextPos;

		if (hitResult.getType() != HitResult.Type.MISS)
			nextPos = hitResult.getPos();
		else
			nextPos = this.getPos().add(this.getVelocity());

		this.setPosition(nextPos);

		super.tick();

		if (hitResult.getType() != HitResult.Type.MISS && this.isAlive())
		{
			this.hitOrDeflect(hitResult);
		}

		if (this.age > 20)
			discard();
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
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);

		float yaw = packet.getYaw();
		float pitch = packet.getPitch();
		setAngles(yaw, pitch);

		if (packet instanceof GalaxiesEntitySpawnS2CPacket precisePacket)
		{
			setVelocity(precisePacket.getVelocity());
			readCustomDataFromNbt(precisePacket.getCustomData(this, NbtCompound.CODEC).getOrThrow());
		}
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry)
	{
		var nbt = new NbtCompound();
		writeCustomDataToNbt(nbt);

		return GalaxiesNetworking.createPlayS2CPacket(new GalaxiesEntitySpawnS2CPacket(
				this,
				entityTrackerEntry,
				NbtCompound.CODEC,
				nbt
		));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt)
	{
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{
	}
}
