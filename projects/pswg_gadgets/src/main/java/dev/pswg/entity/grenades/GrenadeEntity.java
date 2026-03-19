package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class GrenadeEntity extends ThrowableProjectile
{
	public enum CollisionType
	{
		BOUNCE,
		STOP,
		EXPLODE
	}

	private static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> PRIMED = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.BOOLEAN);

	private BlockState inBlockState;
	private CollisionType collisionType;
	private int delay = 0;
	private boolean shouldExplode = false;
	private float explosionPower = 4f;
	private boolean isVisible = true;
	private float clientYaw;

	public GrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world, CollisionType collisionType)
	{
		super(entityType, world);
		this.collisionType = collisionType;
	}
	public GrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world)
	{
		super(entityType, world);
		this.collisionType = CollisionType.BOUNCE;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(LIFE, 75);
		builder.define(PRIMED, false);
		builder.define(IN_GROUND, false);
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
	public void recreateFromPacket(ClientboundAddEntityPacket packet)
	{
		super.recreateFromPacket(packet);
		clientYaw = packet.getYRot();
	}

	public boolean isInGround()
	{
		return this.entityData.get(IN_GROUND);
	}

	protected void setInGround(boolean inGround)
	{
		this.entityData.set(IN_GROUND, inGround);
	}

	@Override
	public boolean isNoGravity()
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
		Vec3 vec3d = this.getDeltaMovement();
		this.setDeltaMovement(vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
	}

	@Override
	public void tick()
	{
		BlockState blockState = getInBlockState();

		if (this.inBlockState != blockState && !this.level().isClientSide() && this.isInGround())
			this.fall();

		if (shouldExplode)
		{
			this.delay--;
			if (this.delay <= 0)
				this.explode();
		}
		if (this.isInLava())
			this.explode();

		if (this.tickCount > this.getLife())
		{
			if (isPrimed())
				this.explode();
		}
		super.tick();
		if (hasDrag())
		{
			this.setDeltaMovement(getDeltaMovement().scale(0.975d));
			hurtMarked = true;
		}
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand)
	{
		if (!isPrimed() && tickCount > getMinPickUpTime() && player.getMainHandItem().isEmpty())
		{
			player.addItem(new ItemStack(getItem()));
			this.remove(RemovalReason.DISCARDED);
		}
		return super.interact(player, hand);
	}
	@Override
	protected void onInsideBlock(BlockState state)
	{
		if (state.is(GadgetsBlocks.Tags.DETONATES_GRENADE))
			explode();
		super.onInsideBlock(state);
	}

	@Override
	protected void onHit(HitResult hitResult)
	{
		switch (collisionType)
		{
			case EXPLODE -> this.explode();
			case STOP -> this.shoot(0, 0, 0, 0, 0);
			case BOUNCE -> this.bounce(hitResult);
		}
		super.onHit(hitResult);
	}

	protected void bounce(HitResult hit)
	{
		if (hit.getType() == HitResult.Type.BLOCK)
		{
			Vec3 velocity = this.getDeltaMovement();
			BlockHitResult blockHit = (BlockHitResult)hit;

			BlockState hitState = this.level().getBlockState(blockHit.getBlockPos());
			double hardness = hitState.getDestroySpeed(level(), blockHit.getBlockPos());
			double restitution = Mth.clamp(0.4 - 0.25 / hardness, 0.1, 1);
			double blockMultiplier = 1;

			if (level().getBlockState(blockHit.getBlockPos()).is(GalaxiesBlocks.Tags.BOUNCY))
				blockMultiplier = 2.5;
			if (level().getBlockState(blockHit.getBlockPos()).is(GalaxiesBlocks.Tags.SOFT))
				blockMultiplier = 0.75;

			if (blockHit.getDirection().equals(Direction.UP) && velocity.lengthSqr() < 0.01)
			{
				inBlockState = level().getBlockState(blockHit.getBlockPos());
				setInGround(true);
				setDeltaMovement(Vec3.ZERO);
				return;
			}

			Vec3 dir = velocity.normalize();

			Vec3 normal = new Vec3(blockHit.getDirection().step());
			Vec3 newDir = normal.scale(2 * normal.dot(dir)).subtract(dir).scale(-1);
			this.setDeltaMovement(newDir.scale(velocity.length() * restitution * blockMultiplier));
			if (Math.abs(getDeltaMovement().length()) > 0.2f)
				clientYaw = (float)(Mth.atan2(getDeltaMovement().y, getDeltaMovement().horizontalDistance()) * (double)(180F / (float)Math.PI));
		}
	}

	public void playCollisionSound(BlockHitResult blockHitResult)
	{
		BlockState state = level().getBlockState(blockHitResult.getBlockPos());
		if (getDeltaMovement().length() > 0.05f)
			this.playSound(state.getSoundType().getHitSound(), 0.5f, 1f);
	}

	public float getExplosionPower()
	{
		return explosionPower;
	}

	public int getLife()
	{
		return entityData.get(LIFE);
	}

	public boolean isPrimed()
	{
		return entityData.get(PRIMED);
	}

	public void setLife(int life)
	{
		entityData.set(LIFE, life);
	}

	public void setPrimed(boolean isPrimed)
	{
		entityData.set(PRIMED, isPrimed);
	}

	public void setExplosionPower(float explosionPower)
	{
		this.explosionPower = explosionPower;
	}
	@Override
	public boolean shouldRenderAtSqrDistance(double distance)
	{
		return isVisible() && super.shouldRenderAtSqrDistance(distance);
	}

	protected void createParticles(double x, double y, double z, ServerLevel serverWorld)
	{
	}
	@Override
	public boolean hurtServer(ServerLevel world, DamageSource source, float amount)
	{
		if (source.is(DamageTypeTags.IS_EXPLOSION))
		{
			if (!this.shouldExplode)
			{
				this.delay = 2;
				this.shouldExplode = true;
			}
		}
		else if (source.is(GadgetsDamage.DamageTags.IGNITES_EXPLOSIVES))
			if (!this.shouldExplode)
				this.explode();

		if (!level().isClientSide())
			return super.hurtServer((ServerLevel)level(), source, amount);
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
		Vec3 pos = new Vec3(getX(), getY(), getZ());
		explode(pos);
	}

	@Override
	public float getPickRadius()
	{
		return (float)getBoundingBox().getSize() / 2f;
	}

	@Override
	public boolean isPickable()
	{
		return true;
	}

	public void explode(Vec3 pos)
	{
		if (level() instanceof ServerLevel serverWorld)
		{
			var explosion = new ServerExplosion(serverWorld, this, damageSources().source(DamageTypes.EXPLOSION), (ExplosionDamageCalculator)null, pos.add(0, 0.05f, 0), getExplosionPower(), false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
			explosion.explode();
			createParticles(getX(), getY(), getZ(), serverWorld);
		}
		this.discard();
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view)
	{
		super.addAdditionalSaveData(view);

		view.putInt("life", getLife());
		view.putBoolean("primed", isPrimed());
		view.putBoolean("in_ground", isInGround());
		if (this.inBlockState != null)
			view.store("inBlockState", BlockState.CODEC, this.inBlockState);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput view)
	{
		super.readAdditionalSaveData(view);
		setLife(view.getIntOr("life", 1));
		setPrimed(view.getBooleanOr("primed", false));
		setInGround(view.getBooleanOr("in_ground", false));
		if (view.contains("inBlockState"))
			this.inBlockState = view.read("inBlockState", BlockState.CODEC).get();
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
