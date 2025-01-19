package dev.pswg.entity;

import dev.pswg.Gadgets;
import dev.pswg.item.GrenadeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class NerveGasGrenadeEntity extends GrenadeEntity
{
	private boolean EXPELLING_GAS = false;
	private int EXPELLING_TIME = 0;

	public NerveGasGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public GrenadeItem getItem()
	{
		return null;
	}

	@Override
	public void explode()
	{
		EXPELLING_GAS = true;
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
			if (getWorld() instanceof ServerWorld serverWorld)
			{
				serverWorld.spawnParticles(Gadgets.NERVE_GAS_PARTICLE, getX(), getY() + 1, getZ(), 4, 0.05d, 0.5, 0.05d, 0);
			}
			EXPELLING_TIME++;
		}
		if (EXPELLING_TIME >= 30)
			this.discard();
		super.tick();
	}
}
