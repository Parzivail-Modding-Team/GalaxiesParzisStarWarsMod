package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.gas.NerveGasEntity;
import dev.pswg.item.GrenadeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class NerveGasGrenadeEntity extends GrenadeEntity
{
	private boolean EXPELLING_GAS = false;
	private int EXPELLING_TIME = 0;
	public NerveGasEntity gasEntity;

	public NerveGasGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.NERVE_GAS_GRENADE_ITEM;
	}

	@Override
	public void explode()
	{
		if (this.getWorld() instanceof ServerWorld)
		{
			gasEntity = GadgetsEntities.NERVE_GAS.create(getWorld(), SpawnReason.TRIGGERED);
			if (getWorld().getBlockState(getBlockPos().up()).isAir())
				gasEntity.setOriginalPos(this.getBlockPos().up());
			else
				Direction.stream().forEach(direction -> {
					if (getWorld().getBlockState(getBlockPos().offset(direction)).isAir())
					{
						gasEntity.setOriginalPos(this.getBlockPos());
					}
				});

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
			//if (getWorld() instanceof ServerWorld serverWorld)
			//{
			//	serverWorld.spawnParticles(GadgetsParticleTypes.NERVE_GAS_PARTICLE, getX(), getY() + 1, getZ(), 4, Random.create().nextBetween(1, 10) / 100d, 0.5d, Random.create().nextBetween(1, 10) / 100d, 0);
			//}
			EXPELLING_TIME++;
		}
		if (EXPELLING_TIME >= 30)
			this.discard();
		super.tick();
	}
}
