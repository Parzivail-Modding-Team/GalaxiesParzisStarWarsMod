package dev.pswg.entity.grenades;

import dev.pswg.block.GrenadeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class GrenadeEntityWithBlock extends GrenadeEntity
{
	public static final int BLOCK_TIME = 300;
	public int timer = 0;

	public GrenadeEntityWithBlock(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}

	public abstract GrenadeBlock getBlock();

	@Override
	public void tick()
	{
		if (getVelocity().length() <= 0.01f && !this.isPrimed())
			timer++;
		if (timer >= BLOCK_TIME)
		{
			BlockPos pos = getBlockPos();
			BlockState state = getWorld().getBlockState(pos);
			if (state.isAir())
			{
				this.discard();
				getWorld().setBlockState(pos, getBlock().getDefaultState());
			}
			else if (getWorld().getBlockState(pos.offset(Direction.UP)).isAir())
			{
				this.discard();
				getWorld().setBlockState(pos.offset(Direction.UP), getBlock().getDefaultState().with(GrenadeBlock.CLUSTER_SIZE, 1));
			}
		}
		super.tick();
	}
}
