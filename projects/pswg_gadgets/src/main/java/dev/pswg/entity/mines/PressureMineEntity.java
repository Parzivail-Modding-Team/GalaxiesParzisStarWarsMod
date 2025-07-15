package dev.pswg.entity.mines;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PressureMineEntity extends Entity implements Ownable
{
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
		if (getWorld() instanceof ServerWorld serverWorld)
		{
			var explosion = new ExplosionImpl(serverWorld, this, getDamageSources().create(DamageTypes.EXPLOSION), (ExplosionBehavior)null, this.getPos(), 3f, false, Explosion.DestructionType.DESTROY_WITH_DECAY);
			explosion.explode();
			//createParticles(getX(), getY(), getZ(), serverWorld);
		}
		this.discard();
	}

	@Override
	public void tick()
	{
		var world = getWorld();
		applyGravity();
		if (this.age == PRIMING_TIME)
		{
			primed = true;
			playSound(GadgetsSounds.ARM, 1, 1);
		}
		var entityCollisions = world.getEntitiesByClass(Entity.class, this.getBoundingBox(), entity -> entity != this);

		if (primed && !entityCollisions.isEmpty())
		{
			entityCollisions.forEach(entity -> Gadgets.LOGGER.info(entity.getName().toString()));
			explode();
		}

		super.tick();
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
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{
		if (this.ownerUuid != null)
		{
			nbt.putUuid("Owner", this.ownerUuid);
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
