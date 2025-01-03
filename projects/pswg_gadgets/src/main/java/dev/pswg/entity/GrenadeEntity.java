package dev.pswg.entity;

import dev.pswg.Gadgets;
import dev.pswg.item.GrenadeItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

public class GrenadeEntity extends ThrownEntity
{
	private static final TrackedData<Integer> LIFE = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> PRIMED = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private int delay = 0;
	private boolean shouldExplode = false;
	private float explosionPower = 4f;
	private boolean isVisible = true;
	private float clientYaw;

	public GrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
		builder.add(LIFE, 75);
		builder.add(PRIMED, false);
	}

	public GrenadeItem getItem()
	{
		return null;
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		clientYaw = packet.getYaw();
	}

	public float getClientYaw()
	{
		return clientYaw;
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

		if (this.age > this.getLife())
		{
			if (isPrimed())
				this.explode();
		}
		super.tick();
	}
	@Override
	protected void onBlockCollision(BlockState state)
	{
		if (state.isIn(Gadgets.DETONATES_GRENADE))
			explode();
		super.onBlockCollision(state);
	}

	protected void bounce(HitResult hit)
	{
		if (hit.getType() == HitResult.Type.BLOCK)
		{
			var velocity = this.getVelocity();
			BlockHitResult blockHit = (BlockHitResult)hit;

			var hitState = this.getWorld().getBlockState(blockHit.getBlockPos());
			var hardness = hitState.getHardness(getWorld(), blockHit.getBlockPos());
			var restitution = MathHelper.clamp(0.4 - 0.25 / hardness, 0.1, 1);
			var blockMultiplier = 1.;

			if (getWorld().getBlockState(blockHit.getBlockPos()).getBlock() == Blocks.SLIME_BLOCK)
				blockMultiplier = 3;
			if (getWorld().getBlockState(blockHit.getBlockPos()).getBlock() == Blocks.HONEY_BLOCK)
				blockMultiplier = 2;
			if (getWorld().getBlockState(blockHit.getBlockPos()).isIn(BlockTags.WOOL) || getWorld().getBlockState(blockHit.getBlockPos()).isIn(BlockTags.LEAVES))
				blockMultiplier = 0.75;

			if (blockHit.getSide().equals(Direction.UP) && velocity.lengthSquared() < 0.01)
			{
				this.setVelocity(0f, 0f, 0f);
				return;
			}

			var dir = velocity.normalize();

			var normal = new Vec3d(blockHit.getSide().getUnitVector());
			var newDir = normal.multiply(2 * normal.dotProduct(dir)).subtract(dir).multiply(-1);
			this.setVelocity(newDir.multiply(velocity.length() * restitution * blockMultiplier));
			if (Math.abs(getVelocity().length()) > 0.2f)
				clientYaw = (float)(MathHelper.atan2(getVelocity().y, getVelocity().horizontalLength()) * (double)(180F / (float)Math.PI));
		}

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

	public void setExplosionPower(float explosionPower)
	{
		this.explosionPower = explosionPower;
	}
	@Override
	public boolean shouldRender(double distance)
	{
		return isVisible() && super.shouldRender(distance);
	}

	protected void createParticles(double x, double y, double z, ServerWorld serverWorld)
	{
	}
	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount)
	{
		if (source.isIn(DamageTypeTags.IS_EXPLOSION))
		{
			if (!this.shouldExplode)
			{
				this.delay = 2;
				this.shouldExplode = true;
			}
		}
		else if (source.isIn(Gadgets.IGNITES_EXPLOSIVES))
			if (!this.shouldExplode)
				this.explode();

			if(!getWorld().isClient())
				return super.damage((ServerWorld)getWorld(), source, amount);
			else
				return false;
	}
	public void explode()
	{
		if (getWorld() instanceof ServerWorld serverWorld)
		{
			getWorld().createExplosion(this, (DamageSource)null, (ExplosionBehavior)null, this.getX(), this.getY() + 0.1f, this.getZ(), explosionPower, false, World.ExplosionSourceType.TNT);
			createParticles(getX(), getY(), getZ(), serverWorld);
		}
		this.discard();
	}
	@Override
	public void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);
		tag.putInt("life", getLife());
		tag.putBoolean("primed", isPrimed());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);
		setLife(tag.getInt("life"));
		setPrimed(tag.getBoolean("primed"));
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
