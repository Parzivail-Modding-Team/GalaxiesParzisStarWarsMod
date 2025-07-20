package dev.pswg.entity.mines;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.container.entity.GadgetsDamage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TripwireMineEntity extends Entity implements Ownable
{
	@Nullable
	private BlockState inBlockState;
	private static final TrackedData<Boolean> IN_GROUND = DataTracker.registerData(TripwireMineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private int PRIMING_TIME = 60;
	public boolean primed;
	public float tripwireDistance;

	@Nullable
	private UUID ownerUuid;
	@Nullable
	private Entity owner;

	public TripwireMineEntity(EntityType<?> type, World world)
	{
		super(type, world);
		primed = false;
	}

	public void setOwner(@Nullable Entity entity)
	{
		if (entity != null)
		{
			this.ownerUuid = entity.getUuid();
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
		if (getWorld() instanceof ServerWorld serverWorld)
		{
			var explosion = new ExplosionImpl(serverWorld, this, getDamageSources().create(DamageTypes.EXPLOSION), (ExplosionBehavior)null, this.getPos().add(0, 0.05f, 0), 2.5f, false, Explosion.DestructionType.DESTROY_WITH_DECAY);
			explosion.explode();
			createParticles(getX(), getY(), getZ(), serverWorld);
		}
		this.discard();
	}

	@Override
	public void onDamaged(DamageSource damageSource)
	{
		if (damageSource.isIn(GadgetsDamage.DamageTags.IGNITES_EXPLOSIVES))
			explode();
		super.onDamaged(damageSource);
	}

	protected void createParticles(double x, double y, double z, ServerWorld serverWorld)
	{

		for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers())
		{
			serverWorld.spawnParticles(serverPlayerEntity, GadgetsParticleTypes.SMALL_FLASH_PARTICLE, true, true, x, y, z, 1, 0, 0, 0, 0);
		}
	}

	private void applyDrag()
	{
		Vec3d vec3d = this.getVelocity();
		Vec3d vec3d2 = this.getPos();
		float g;
		if (this.isTouchingWater())
		{
			for (int i = 0; i < 4; i++)
			{
				float f = 0.25F;
				this.getWorld()
				    .addParticle(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
			}

			g = 0.8F;
		}
		else
		{
			g = 0.99F;
		}

		this.setVelocity(vec3d.multiply((double)g));
	}

	@Override
	protected double getGravity()
	{
		return 0.075;
	}

	@Override
	public boolean canUsePortals(boolean allowVehicles)
	{
		return true;
	}

	protected void setInGround(boolean inGround)
	{
		this.dataTracker.set(IN_GROUND, inGround);
	}

	protected boolean isInGround()
	{
		return this.dataTracker.get(IN_GROUND);
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
		Vec3d vec3d = this.getVelocity();
		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.getWorld().getBlockState(blockPos);

		if (this.isInGround())
		{
			if (!this.getWorld().isClient())
			{
				if (this.inBlockState != blockState)
				{
					this.fall();
				}
			}
		}

		if (!isInGround())
			this.applyGravity();
		else
		{
			this.setVelocity(this.getVelocity().multiply(0, 0, 0));
			this.velocityModified = true;
		}
		this.applyDrag();
		if (this.age == PRIMING_TIME)
		{
			primed = true;
			playSound(GadgetsSounds.ARM, 1, 1);
		}
		float maxDist = 3;
		var rotVec = getRotationVector();

		var blockRaycast = getWorld().raycast(new RaycastContext(
				this.getPos().add(0, 0, 0),
				this.getPos().add(rotVec.multiply(maxDist)),
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.ANY,
				this
		));
		tripwireDistance = blockRaycast.getType() == HitResult.Type.MISS ? maxDist : (float)(blockRaycast.getPos().distanceTo(getPos()));

		if (this.primed)
		{
			for (float f = 0.015f; f < tripwireDistance; f += 0.015f)
			{
				if (getWorld() instanceof ServerWorld serverWorld)
				{
					serverWorld.spawnParticles(GadgetsParticleTypes.TRIPWIRE_LASER_PARTICLE, getX() + getRotationVector().multiply(f).x, getY() + getRotationVector().multiply(f).y, getZ() + getRotationVector().multiply(f).z, 1, getWorld().random.nextBetween(1, 100) / 30000f, 0, getWorld().random.nextBetween(1, 100) / 30000f, 0);
					//serverWorld.spawnParticles(GadgetsParticleTypes.TRIPWIRE_LASER_PARTICLE, getX(), getY() + f, getZ(), 1, getWorld().random.nextBetween(1, 100) / 30000f, 0, getWorld().random.nextBetween(1, 100) / 30000f, 0);
				}
			}
		}

		var entityRaycast = ProjectileUtil.raycast(
				this,
				this.getPos().add(0, 0.05, 0),
				this.getPos().add(0, 0.05, 0).add(rotVec.multiply(tripwireDistance)),
				this.getBoundingBox().stretch(rotVec.multiply(tripwireDistance)).expand(1.0, 1.0, 1.0),
				entity -> true,
				tripwireDistance);

		if (entityRaycast != null && entityRaycast.getType() == HitResult.Type.ENTITY && this.primed)
			explode();

		HitResult hitResult = ProjectileUtil.getCollision(this, entity -> true);


		if (hitResult.getType() != HitResult.Type.MISS)
		{
			if (hitResult.getType() == HitResult.Type.BLOCK && !isInGround())
			{
				var blockHit = (BlockHitResult)hitResult;
				var normal = new Vec3d(blockHit.getSide().getUnitVector());

				if (!getWorld().getBlockState(blockHit.getBlockPos()).isAir())
				{
					inBlockState = getWorld().getBlockState(blockHit.getBlockPos());
					this.setPosition(this.getPos().add(getRotationVector().multiply(0.05f)));
					//inBlockState = getWorld().getBlockState(blockHit.getBlockPos());
					setRotation(normal);
					setInGround(true);
					this.velocityModified = true;
				}
			}
			vec3d = hitResult.getPos();
		}

		else
		{
			vec3d = this.getPos().add(this.getVelocity());
		}
		this.setPosition(vec3d);
		this.tickBlockCollision();

		super.tick();
	}

	public void setRotation(Vec3d vec)
	{
		var pitch = Math.asin(-vec.y) / Math.PI * 180f;
		var yaw = Math.atan2(vec.x, vec.z) / Math.PI * 180f;
		//Gadgets.LOGGER.info(yaw+ " "+ pitch);
		setRotation((float)-yaw, (float)pitch);
	}

	@Override
	protected void onBlockCollision(BlockState state)
	{
		if (!state.isAir())
		{
			inBlockState = getBlockStateAtPos();
			setInGround(true);
		}
		super.onBlockCollision(state);
	}

	@Override
	public boolean hasNoGravity()
	{
		return false;
	}

	@Override
	public int getDefaultPortalCooldown()
	{
		return 1;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
		builder.add(IN_GROUND, false);
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount)
	{
		return true;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt)
	{
		if (nbt.containsUuid("Owner"))
		{
			this.setOwner(nbt.getUuid("Owner"));
		}
		this.setInGround(nbt.getBoolean("inGround"));
		if (nbt.contains("inBlockState", NbtElement.COMPOUND_TYPE))
		{
			this.inBlockState = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("inBlockState"));
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{
		if (this.ownerUuid != null)
		{
			nbt.putUuid("Owner", this.ownerUuid);
		}
		nbt.putBoolean("inGround", this.isInGround());
		if (this.inBlockState != null)
		{
			nbt.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
		}
	}

	@Nullable
	protected Entity getEntity(UUID uuid)
	{
		return this.getWorld() instanceof ServerWorld serverWorld ? serverWorld.getEntity(uuid) : null;
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
