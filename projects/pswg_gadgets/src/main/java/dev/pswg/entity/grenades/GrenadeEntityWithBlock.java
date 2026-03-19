package dev.pswg.entity.grenades;

import dev.pswg.block.GrenadeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GrenadeEntityWithBlock extends GrenadeEntity
{
	public static final int BLOCK_TIME = 300;
	public int timer = 0;

	public GrenadeEntityWithBlock(EntityType<? extends ThrowableProjectile> entityType, Level world, CollisionType collisionType)
	{
		super(entityType, world, collisionType);
	}
	public GrenadeEntityWithBlock(EntityType<? extends ThrowableProjectile> entityType, Level world)
	{
		super(entityType, world);
	}

	public abstract GrenadeBlock getBlock();

	@Override
	public void tick()
	{
		if (getDeltaMovement().length() <= 0.01f && !this.isPrimed())
			timer++;
		else
		{
			timer = 0;
		}
		if (timer >= BLOCK_TIME)
		{
			BlockPos pos = blockPosition();
			BlockState state = level().getBlockState(pos);
			if (state.isAir())
			{
				this.discard();
				level().setBlockAndUpdate(pos, getBlock().defaultBlockState());
			}
			else if (level().getBlockState(pos.relative(Direction.UP)).isAir())
			{
				this.discard();
				level().setBlockAndUpdate(pos.relative(Direction.UP), getBlock().defaultBlockState().setValue(GrenadeBlock.CLUSTER_SIZE, 1));
			}
		}
		super.tick();
	}
}
