package dev.pswg.entity.mines;

import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.container.GalaxiesParticleTypes;
import dev.pswg.container.entity.GadgetsDamage;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TripwireMineEntity extends Entity implements TraceableEntity
{
	@Nullable
	private BlockState inBlockState;
	private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.defineId(TripwireMineEntity.class, EntityDataSerializers.BOOLEAN);

	private int PRIMING_TIME = 60;
	public boolean primed;
	public float tripwireDistance;

	@Nullable
	private UUID ownerUuid;
	@Nullable
	private Entity owner;

	public TripwireMineEntity(EntityType<?> type, Level world)
	{
		super(type, world);
		primed = false;
	}

	public void setOwner(@Nullable Entity entity)
	{
		if (entity != null)
		{
			this.ownerUuid = entity.getUUID();
			this.owner = entity;
		}
	}

	protected void setOwner(UUID uuid)
	{
		if (this.ownerUuid != uuid)
		{
			this.ownerUuid = uuid;
			this.owner = this.getEntity(uuid);
		}
	}

	public void explode()
	{
		if (level() instanceof ServerLevel serverWorld)
		{
			var explosion = new ServerExplosion(serverWorld, this, damageSources().source(DamageTypes.EXPLOSION), null, this.position().add(0, 0.05f, 0), 2.5f, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
			explosion.explode();
			createParticles(getX(), getY(), getZ(), serverWorld);
		}
		this.discard();
	}

	@Override
	public void handleDamageEvent(DamageSource damageSource)
	{
		if (damageSource.is(GadgetsDamage.DamageTags.IGNITES_EXPLOSIVES))
			explode();
		super.handleDamageEvent(damageSource);
	}

	protected void createParticles(double x, double y, double z, ServerLevel serverWorld)
	{

		for (ServerPlayer serverPlayerEntity : serverWorld.players())
		{
			serverWorld.sendParticles(serverPlayerEntity, ColorParticleOption.create(GalaxiesParticleTypes.SMALL_FLASH_PARTICLE, CommonColors.WHITE), true, true, x, y, z, 1, 0, 0, 0, 0);
		}
	}

	private void applyDrag()
	{
		Vec3 vec3d = this.getDeltaMovement();
		Vec3 vec3d2 = this.position();
		float g;
		if (this.isInWater())
		{
			for (int i = 0; i < 4; i++)
			{
				float f = 0.25F;
				this.level()
				    .addParticle(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
			}

			g = 0.8F;
		}
		else
		{
			g = 0.99F;
		}

		this.setDeltaMovement(vec3d.scale((double)g));
	}

	@Override
	protected double getDefaultGravity()
	{
		return 0.075;
	}

	@Override
	public boolean canUsePortal(boolean allowVehicles)
	{
		return true;
	}

	protected void setInGround(boolean inGround)
	{
		this.entityData.set(IN_GROUND, inGround);
	}

	protected boolean isInGround()
	{
		return this.entityData.get(IN_GROUND);
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

		if (!isInGround())
			this.applyGravity();
		else
		{
			this.setDeltaMovement(this.getDeltaMovement().multiply(0, 0, 0));
			this.hurtMarked = true;
		}
		this.applyDrag();
		if (this.tickCount == PRIMING_TIME)
		{
			primed = true;
			playSound(GadgetsSounds.ARM, 1, 1);
		}
		float maxDist = 3;




		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, entity -> true);

		Vec3 newPos = this.position();
		if (hitResult.getType() != HitResult.Type.MISS)
		{
			newPos = hitResult.getLocation().add(getDeltaMovement().scale(0.005));
			if (hitResult.getType() == HitResult.Type.BLOCK && !isInGround())
			{
				var blockHit = (BlockHitResult)hitResult;
				var normal = new Vec3(blockHit.getDirection().step());

				if (!level().getBlockState(blockHit.getBlockPos()).isAir())
				{
					inBlockState = level().getBlockState(blockHit.getBlockPos());
					setRotation(normal);
					setInGround(true);
					this.hurtMarked = true;
				}
			}
		}
		else
		{
			newPos = this.position().add(this.getDeltaMovement());
		}
		this.setPos(newPos);
		this.applyEffectsFromBlocks();

		var rotVec = getLookAngle();

		var blockRaycast = level().clip(new ClipContext(
				this.position().add(rotVec.scale(0.05d)),
				this.position().add(rotVec.scale(maxDist)),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.ANY,
				this
		));
		var entityRaycast = ProjectileUtil.getEntityHitResult(
				this,
				this.position(),
				this.position().add(rotVec.scale(tripwireDistance)),
				this.getBoundingBox().expandTowards(rotVec.scale(tripwireDistance)).inflate(1.0, 1.0, 1.0),
				entity -> true,
				tripwireDistance);

		if (entityRaycast != null && entityRaycast.getType() == HitResult.Type.ENTITY && this.primed)
			explode();

		tripwireDistance = blockRaycast.getType() == HitResult.Type.MISS ? maxDist : (float)(blockRaycast.getLocation().distanceTo(position()));

		if (this.primed)
		{
			for (float f = 0.015f; f < tripwireDistance; f += 0.015f)
			{
				if (level() instanceof ServerLevel serverWorld)
				{
					serverWorld.sendParticles(GadgetsParticleTypes.TRIPWIRE_LASER_PARTICLE, getX() + getLookAngle().scale(f).x, getY() + getLookAngle().scale(f).y, getZ() + getLookAngle().scale(f).z, 1, level().getRandom().nextIntBetweenInclusive(1, 100) / 30000f, 0, level().getRandom().nextIntBetweenInclusive(1, 100) / 30000f, 0);
					//serverWorld.spawnParticles(GadgetsParticleTypes.TRIPWIRE_LASER_PARTICLE, getX(), getY() + f, getZ(), 1, getWorld().random.nextBetween(1, 100) / 30000f, 0, getWorld().random.nextBetween(1, 100) / 30000f, 0);
				}
			}
		}

		super.tick();
	}

	public void setRotation(Vec3 vec)
	{
		var pitch = Math.asin(-vec.y) / Math.PI * 180f;
		var yaw = Math.atan2(vec.x, vec.z) / Math.PI * 180f;
		setRot((float)-yaw, (float)pitch);
	}

	@Override
	public boolean isNoGravity()
	{
		return false;
	}

	@Override
	public int getDimensionChangingDelay()
	{
		return 1;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(IN_GROUND, false);
	}

	@Override
	public boolean hurtServer(ServerLevel world, DamageSource source, float amount)
	{
		return true;
	}

	// TODO: MAKE "OWNER" PART OF A BASE CLASS COMMON FOR MINES
	@Override
	protected void readAdditionalSaveData(ValueInput view)
	{
		if (view.contains("owner"))
		{
			this.setOwner(UUID.fromString(view.getStringOr("owner", "")));
		}
		this.setInGround(view.getBooleanOr("inGround", false));
		if (view.contains("inBlockState"))
		{
			this.inBlockState = view.read("inBlockState", BlockState.CODEC).get();
		}
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view)
	{
		if (this.ownerUuid != null)
		{
			view.putString("owner", this.ownerUuid.toString());
		}
		view.putBoolean("inGround", this.isInGround());
		if (this.inBlockState != null)
		{
			view.store("inBlockState", BlockState.CODEC, inBlockState);
		}
	}

	@Nullable
	protected Entity getEntity(UUID uuid)
	{
		return this.level() instanceof ServerLevel serverWorld ? serverWorld.getEntity(uuid) : null;
	}

	@Override
	public @Nullable Entity getOwner()
	{
		if (this.owner != null && !this.owner.isRemoved())
		{
			return this.owner;
		}
		else if (this.ownerUuid != null)
		{
			this.owner = this.getEntity(this.ownerUuid);
			return this.owner;
		}
		else
		{
			return null;
		}
	}
}
