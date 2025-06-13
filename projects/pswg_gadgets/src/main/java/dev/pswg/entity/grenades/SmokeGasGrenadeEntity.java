package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.gas.SmokeGasEntity;
import dev.pswg.item.GrenadeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SmokeGasGrenadeEntity extends GrenadeEntity
{
	private boolean EXPELLING_GAS = false;
	private int EXPELLING_TIME = 0;
	public SmokeGasEntity gasEntity;

	public SmokeGasGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.SMOKE_GRENADE_ITEM;
	}

	@Override
	public void explode()
	{
		if (this.getWorld() instanceof ServerWorld)
		{
			gasEntity = GadgetsEntities.SMOKE_GAS.create(getWorld(), SpawnReason.TRIGGERED);
			gasEntity.setPosition(getX(), getY(), getZ());
			EXPELLING_GAS = true;
			getWorld().spawnEntity(gasEntity);
		}
	}

	@Override
	protected void onCollision(HitResult hitResult)
	{
		if (hitResult.getType() == HitResult.Type.BLOCK)
		{
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			bounce(blockHitResult);
			playCollisionSound(blockHitResult);
		}
		super.onCollision(hitResult);
	}

	@Override
	public void tick()
	{
		if (EXPELLING_GAS)
		{
			EXPELLING_TIME++;
		}
		if (EXPELLING_TIME >= 30)
			this.discard();
		super.tick();
	}
}
