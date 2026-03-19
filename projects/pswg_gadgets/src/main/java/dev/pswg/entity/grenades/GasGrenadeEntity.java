package dev.pswg.entity.grenades;

import dev.pswg.entity.gas.GasEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public abstract class GasGrenadeEntity extends GrenadeEntity
{
	private int MAX_EXPELLING_TIME;
	private int expellingTime = 0;
	private boolean expellingGas = false;
	//private int volume ;
	//private float expellingRate;
	private final EntityType<? extends GasEntity> gasEntityType;

	public GasGrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world, EntityType<? extends GasEntity> gasEntityType, CollisionType collisionType, int maxExpellingTime)
	{
		super(entityType, world, collisionType);
		this.gasEntityType = gasEntityType;
		MAX_EXPELLING_TIME = maxExpellingTime;
	}

	public GasGrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world, int maxExpellingTime, EntityType<? extends GasEntity> gasEntityType)
	{
		super(entityType, world);
		MAX_EXPELLING_TIME = maxExpellingTime;
		this.gasEntityType = gasEntityType;
	}

	@Override
	public void explode()
	{
		if (!expellingGas)
		{
			var world = level();
			var gasEntity = gasEntityType.create(world, EntitySpawnReason.TRIGGERED);
			gasEntity.setPos(getX(), getY(), getZ());

			expellingGas = true;

			world.addFreshEntity(gasEntity);
		}
	}

	@Override
	public void tick()
	{
		if (expellingGas)
		{
			//TODO: IMPLEMENT GAS EXPELLING
			expellingTime++;
		}
		if (expellingTime >= MAX_EXPELLING_TIME)
			this.discard();
		super.tick();
	}
}
