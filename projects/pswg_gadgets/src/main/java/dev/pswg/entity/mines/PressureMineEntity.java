package dev.pswg.entity.mines;

import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
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
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PressureMineEntity extends Entity implements Ownable
{
	@Nullable
	private BlockState inBlockState;
	private static final TrackedData<Boolean> IN_GROUND = DataTracker.registerData(PressureMineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private int PRIMING_TIME = 50;
	private boolean primed;

	@Nullable
	private UUID ownerUuid;
	@Nullable
	private Entity owner;

	public PressureMineEntity(EntityType<?> type, World world)
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
		if (getEntityWorld() instanceof ServerWorld serverWorld)
		{
			var explosion = new ExplosionImpl(serverWorld, this, getDamageSources().create(DamageTypes.EXPLOSION), (ExplosionBehavior)null, this.getEntityPos().add(0, 0.05f, 0), 2f, false, Explosion.DestructionType.DESTROY_WITH_DECAY);
			explosion.explode();
			createParticles(getX(), getY(), getZ(), serverWorld);
		}
		this.discard();
	}

	protected void createParticles(double x, double y, double z, ServerWorld serverWorld)
	{

		for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers())
		{
			serverWorld.spawnParticles(serverPlayerEntity, TintedParticleEffect.create(GadgetsParticleTypes.SMALL_FLASH_PARTICLE, Colors.WHITE), true, true, x, y, z, 1, 0, 0, 0, 0);
		}
	}

	private void applyDrag()
	{
		Vec3d vec3d = this.getVelocity();
		Vec3d vec3d2 = this.getEntityPos();
		float g;
		if (this.isTouchingWater())
		{
			for (int i = 0; i < 4; i++)
			{
				float f = 0.25F;
				this.getEntityWorld().addParticleClient(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
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
		BlockState blockState = this.getEntityWorld().getBlockState(blockPos);
		if (!blockState.isAir())
		{
			VoxelShape voxelShape = blockState.getCollisionShape(this.getEntityWorld(), blockPos);
			if (!voxelShape.isEmpty())
			{
				Vec3d vec3d2 = this.getEntityPos();

				for (Box box : voxelShape.getBoundingBoxes())
				{
					if (box.offset(blockPos).contains(vec3d2))
					{
						this.setInGround(true);
						break;
					}
				}
			}
		}

		if (this.isInGround())
		{
			if (!this.getEntityWorld().isClient())
			{
				if (this.inBlockState != blockState)
				{
					this.fall();
				}
			}
		}

		var world = getEntityWorld();
		if (!isInGround())
			this.applyGravity();
		else
			this.setVelocity(this.getVelocity().multiply(1, -0.5, 1));
		this.applyDrag();
		if (this.age == PRIMING_TIME)
		{
			primed = true;
			playSound(GadgetsSounds.ARM, 1, 1);
		}
		var entityCollisions = world.getEntitiesByClass(Entity.class, this.getBoundingBox(), entity -> entity != this);

		if (primed && !entityCollisions.isEmpty())
		{
			explode();
		}

		HitResult hitResult = ProjectileUtil.getCollision(this, entity -> true);
		if (hitResult.getType() != HitResult.Type.MISS)
		{
			vec3d = hitResult.getPos();
		}
		else
		{
			vec3d = this.getEntityPos().add(this.getVelocity());
		}
		this.setPosition(vec3d);
		this.tickBlockCollision();

		super.tick();
	}

	@Override
	protected void onBlockCollision(BlockState state)
	{
		inBlockState = state;
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
	protected void readCustomData(ReadView view)
	{
		if (view.contains("owner"))
		{
			this.setOwner(UUID.fromString(view.getString("owner", "")));
		}
		this.setInGround(view.getBoolean("inGround", false));
		if (view.contains("inBlockState"))
		{
			this.inBlockState = view.read("inBlockState", BlockState.CODEC).get();
		}
	}

	@Override
	protected void writeCustomData(WriteView view)
	{
		if (this.ownerUuid != null)
		{
			view.putString("owner", this.ownerUuid.toString());
		}
		view.putBoolean("inGround", this.isInGround());
		if (this.inBlockState != null)
		{
			view.put("inBlockState", BlockState.CODEC, inBlockState);
		}
	}


	@Nullable
	protected Entity getEntity(UUID uuid)
	{
		return this.getEntityWorld() instanceof ServerWorld serverWorld ? serverWorld.getEntity(uuid) : null;
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
