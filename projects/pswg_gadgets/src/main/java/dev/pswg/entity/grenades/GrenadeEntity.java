package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;

public abstract class GrenadeEntity extends ThrownEntity
{
	public enum CollisionType
	{
		BOUNCE,
		STOP,
		EXPLODE
	}

	private static final TrackedData<Integer> LIFE = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> PRIMED = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> IN_GROUND = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private BlockState inBlockState;
	private CollisionType collisionType;
	private int delay = 0;
	private boolean shouldExplode = false;
	private float explosionPower = 4f;
	private boolean isVisible = true;
	private float clientYaw;

	public GrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world, CollisionType collisionType)
	{
		super(entityType, world);
		this.collisionType = collisionType;
	}
	public GrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
		this.collisionType = CollisionType.BOUNCE;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
		builder.add(LIFE, 75);
		builder.add(PRIMED, false);
		builder.add(IN_GROUND, false);
	}

	public abstract GrenadeItem getItem();

	public int getMinPickUpTime()
	{
		return 40;
	}

	public boolean hasDrag()
	{
		return true;
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		clientYaw = packet.getYaw();
	}

	public boolean isInGround()
	{
		return this.dataTracker.get(IN_GROUND);
	}

	protected void setInGround(boolean inGround)
	{
		this.dataTracker.set(IN_GROUND, inGround);
	}

	@Override
	public boolean hasNoGravity()
	{
		return isInGround();
	}

	public float getClientYaw()
	{
		return clientYaw;
	}

	private void fall()
	{
		this.setInGround(false);
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
	}

	@Override
	public void tick()
	{
		BlockState blockState = getBlockStateAtPos();

		if (this.inBlockState != blockState && !this.getWorld().isClient() && this.isInGround())
			this.fall();

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
		if (hasDrag())
		{
			this.setVelocity(getVelocity().multiply(0.975d));
			velocityModified = true;
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		if (!isPrimed() && age > getMinPickUpTime() && player.getMainHandStack().isEmpty())
		{
			player.giveItemStack(new ItemStack(getItem()));
			this.remove(RemovalReason.DISCARDED);
		}
		return super.interact(player, hand);
	}
	@Override
	protected void onBlockCollision(BlockState state)
	{
		if (state.isIn(GadgetsBlocks.Tags.DETONATES_GRENADE))
			explode();
		super.onBlockCollision(state);
	}

	@Override
	protected void onCollision(HitResult hitResult)
	{
		switch (collisionType)
		{
			case EXPLODE -> this.explode();
			case STOP -> this.setVelocity(0, 0, 0, 0, 0);
			case BOUNCE -> this.bounce(hitResult);
		}
		super.onCollision(hitResult);
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

			if (getWorld().getBlockState(blockHit.getBlockPos()).isIn(GadgetsBlocks.Tags.BOUNCY))
				blockMultiplier = 2.5;
			if (getWorld().getBlockState(blockHit.getBlockPos()).isIn(BlockTags.WOOL) || getWorld().getBlockState(blockHit.getBlockPos()).isIn(BlockTags.LEAVES))
				blockMultiplier = 0.75;

			if (blockHit.getSide().equals(Direction.UP) && velocity.lengthSquared() < 0.01)
			{
				inBlockState = getWorld().getBlockState(blockHit.getBlockPos());
				setInGround(true);
				setVelocity(Vec3d.ZERO);
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

	public void playCollisionSound(BlockHitResult blockHitResult)
	{
		BlockState state = getWorld().getBlockState(blockHitResult.getBlockPos());
		if (getVelocity().length() > 0.05f)
			this.playSound(state.getSoundGroup().getHitSound(), 0.5f, 1f);
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
		else if (source.isIn(GadgetsDamage.DamageTags.IGNITES_EXPLOSIVES))
			if (!this.shouldExplode)
				this.explode();

			if(!getWorld().isClient())
				return super.damage((ServerWorld)getWorld(), source, amount);
			else
				return false;
	}

	@Override
	public boolean canBeHitByProjectile()
	{
		return true;
	}

	public void explode()
	{
		Vec3d pos = new Vec3d(getX(), getY(), getZ());
		explode(pos);
	}

	@Override
	public float getTargetingMargin()
	{
		return (float)getBoundingBox().getAverageSideLength();
	}

	@Override
	public boolean canHit()
	{
		return true;
	}

	public void explode(Vec3d pos)
	{
		if (getWorld() instanceof ServerWorld serverWorld)
		{
			var explosion = new ExplosionImpl(serverWorld, this, getDamageSources().create(DamageTypes.EXPLOSION), (ExplosionBehavior)null, pos.add(0, 0.05f, 0), getExplosionPower(), false, Explosion.DestructionType.DESTROY_WITH_DECAY);
			explosion.explode();
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
		tag.putBoolean("in_ground", isInGround());
		if (this.inBlockState != null)
			tag.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);
		setLife(tag.getInt("life"));
		setPrimed(tag.getBoolean("primed"));
		setInGround(tag.getBoolean("in_ground"));
		if (tag.contains("inBlockState", NbtElement.COMPOUND_TYPE))
			this.inBlockState = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), tag.getCompound("inBlockState"));
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
