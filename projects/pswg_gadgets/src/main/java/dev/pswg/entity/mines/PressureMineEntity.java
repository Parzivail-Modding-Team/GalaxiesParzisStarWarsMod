package dev.pswg.entity.mines;

import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.container.GalaxiesParticleTypes;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PressureMineEntity extends Entity implements TraceableEntity
{
	@Nullable
	private BlockState inBlockState;
	private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.defineId(PressureMineEntity.class, EntityDataSerializers.BOOLEAN);

	private int PRIMING_TIME = 50;
	private boolean primed;

	@Nullable
	private UUID ownerUuid;
	@Nullable
	private Entity owner;

	public PressureMineEntity(EntityType<?> type, Level world)
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
			var explosion = new ServerExplosion(serverWorld, this, damageSources().source(DamageTypes.EXPLOSION), (ExplosionDamageCalculator)null, this.position().add(0, 0.05f, 0), 2f, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
			explosion.explode();
			createParticles(getX(), getY(), getZ(), serverWorld);
		}
		this.discard();
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
				this.level().addParticle(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
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
		Vec3 vec3d = this.getDeltaMovement();
		BlockPos blockPos = this.blockPosition();
		BlockState blockState = this.level().getBlockState(blockPos);
		if (!blockState.isAir())
		{
			VoxelShape voxelShape = blockState.getCollisionShape(this.level(), blockPos);
			if (!voxelShape.isEmpty())
			{
				Vec3 vec3d2 = this.position();

				for (AABB box : voxelShape.toAabbs())
				{
					if (box.move(blockPos).contains(vec3d2))
					{
						this.setInGround(true);
						break;
					}
				}
			}
		}

		if (this.isInGround())
		{
			if (!this.level().isClientSide())
			{
				if (this.inBlockState != blockState)
				{
					this.fall();
				}
			}
		}

		var world = level();
		if (!isInGround())
			this.applyGravity();
		else
			this.setDeltaMovement(this.getDeltaMovement().multiply(1, -0.5, 1));
		this.applyDrag();
		if (this.tickCount == PRIMING_TIME)
		{
			primed = true;
			playSound(GadgetsSounds.ARM, 1, 1);
		}
		var entityCollisions = world.getEntitiesOfClass(Entity.class, this.getBoundingBox(), entity -> entity != this);

		if (primed && !entityCollisions.isEmpty())
		{
			explode();
		}

		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, entity -> true);
		if (hitResult.getType() != HitResult.Type.MISS)
		{
			vec3d = hitResult.getLocation();
		}
		else
		{
			vec3d = this.position().add(this.getDeltaMovement());
		}
		this.setPos(vec3d);
		this.applyEffectsFromBlocks();

		super.tick();
	}

	@Override
	protected void onInsideBlock(BlockState state)
	{
		inBlockState = state;
		super.onInsideBlock(state);
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
