package dev.pswg.entity.grenades;

import dev.pswg.entity.gas.GasEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

public abstract class GasGrenadeEntity extends GrenadeEntity
{
	private int MAX_EXPELLING_TIME;
	private int expellingTime = 0;
	private boolean expellingGas = false;
	//private int volume ;
	//private float expellingRate;
	private final EntityType<? extends GasEntity> gasEntityType;

	public GasGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world, EntityType<? extends GasEntity> gasEntityType, CollisionType collisionType, int maxExpellingTime)
	{
		super(entityType, world, collisionType);
		this.gasEntityType = gasEntityType;
		MAX_EXPELLING_TIME = maxExpellingTime;
	}

	public GasGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world, int maxExpellingTime, EntityType<? extends GasEntity> gasEntityType)
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
			var world = getEntityWorld();
			var gasEntity = gasEntityType.create(world, SpawnReason.TRIGGERED);
			gasEntity.setPosition(getX(), getY(), getZ());

			expellingGas = true;

			world.spawnEntity(gasEntity);
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
