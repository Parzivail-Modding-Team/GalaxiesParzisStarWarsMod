package com.parzivail.pswg.entity;

import com.parzivail.pswg.container.SwgTags;
import com.parzivail.util.entity.IPrecisionSpawnEntity;
import com.parzivail.util.entity.IPrecisionVelocityEntity;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.network.PreciseEntitySpawnS2CPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

public abstract class ThrowableExplosive extends ThrownEntity implements IPrecisionSpawnEntity, IPrecisionVelocityEntity
{
	private static final TrackedData<Integer> LIFE = DataTracker.registerData(ThrowableExplosive.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> PRIMED = DataTracker.registerData(ThrowableExplosive.class, TrackedDataHandlerRegistry.BOOLEAN);

	private int delay = 0;
	private boolean shouldExplode = false;
	private float explosionPower = 5f;
	private boolean isVisible = true;

	protected ThrowableExplosive(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);

		if (packet instanceof PreciseEntitySpawnS2CPacket pes)
		{
			this.setVelocity(pes.getVelocity());
			this.readSpawnData(pes.getData());
		}
	}

	protected void bounce(HitResult hit)
	{
		if (hit.getType() == HitResult.Type.BLOCK)
		{
			var velocity = this.getVelocity();
			BlockHitResult blockHit = (BlockHitResult)hit;

			var hitState = this.getWorld().getBlockState(blockHit.getBlockPos());
			var hardness = hitState.getHardness(getWorld(), blockHit.getBlockPos());
			var restitution = Math.max(0.8 - 0.5 / hardness, 0.1);

			if (blockHit.getSide().equals(Direction.UP) && velocity.lengthSquared() < 0.01)
			{
				this.setVelocity(0f, 0f, 0f);
				return;
			}

			var dir = velocity.normalize();

			var normal = new Vec3d(blockHit.getSide().getUnitVector());
			var newDir = MathUtil.reflect(dir, normal);
			this.setVelocity(newDir.multiply(velocity.length() * restitution));
		}
	}

	@Override
	public void tick()
	{
		if (shouldExplode)
		{
			this.delay--;
			if (this.delay <= 0)
				this.explode();
		}

		if (this.isInLava())
			this.explode();

		if (!getWorld().isClient && this.age > this.getLife())
		{
			if (isPrimed())
				this.explode();
			else
				this.discard();
		}

		super.tick();
	}

	public float getExplosionPower()
	{
		return explosionPower;
	}

	public int getLife()
	{
		return dataTracker.get(LIFE);
	}

	public boolean isPrimed()
	{
		return dataTracker.get(PRIMED);
	}

	public void setLife(int life)
	{
		dataTracker.set(LIFE, life);
	}

	public void setPrimed(boolean isPrimed)
	{
		dataTracker.set(PRIMED, isPrimed);
	}

	@Override
	protected void initDataTracker()
	{
		dataTracker.startTracking(LIFE, 75);
		dataTracker.startTracking(PRIMED, false);
	}

	public void setExplosionPower(float explosionPower)
	{
		this.explosionPower = explosionPower;
	}

	@Override
	public void writeSpawnData(NbtCompound tag)
	{
		tag.putInt("life", getLife());
		tag.putBoolean("primed", isPrimed());
	}

	@Override
	public void readSpawnData(NbtCompound tag)
	{
		setLife(tag.getInt("life"));
		setPrimed(tag.getBoolean("primed"));
	}

	@Override
	public boolean shouldRender(double distance)
	{
		return isVisible() && super.shouldRender(distance);
	}

	protected abstract void createParticles(ServerWorld world, double x, double y, double z);

	@Override
	public boolean isInvulnerableTo(DamageSource source)
	{
		if (source.isIn(SwgTags.DamageTypes.IGNITES_EXPLOSIVE))
			return false;
		return super.isInvulnerableTo(source);
	}

	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if (source.isIn(DamageTypeTags.IS_EXPLOSION))
		{
			if (!this.shouldExplode)
			{
				this.delay = 2;
				this.shouldExplode = true;
			}

			return true;
		}
		else if (source.isIn(SwgTags.DamageTypes.IGNITES_EXPLOSIVE))
			if (!this.shouldExplode)
				this.explode();

		return super.damage(source, amount);
	}

	public void explode()
	{
		if (!this.getWorld().isClient)
		{
			this.discard();
			this.getWorld().createExplosion(this, (DamageSource)null, (ExplosionBehavior)null, this.getX(), this.getY(), this.getZ(), explosionPower, false, World.ExplosionSourceType.TNT, true);
		}
		if (this.getWorld() instanceof ServerWorld serverWorld)
			createParticles(serverWorld, this.getX(), this.getY(), this.getZ());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);
		writeSpawnData(tag);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);
		readSpawnData(tag);
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setVisible(boolean visible)
	{
		this.isVisible = visible;
	}
}
