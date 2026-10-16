package dev.pswg.entity.grenades;

import dev.pswg.block.GrenadeBlock;
import dev.pswg.entity.gas.GasEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GasGrenadeEntityWithBlock extends GasGrenadeEntity{
    public static final int BLOCK_TIME = 300;
    public int timer = 0;

    public GasGrenadeEntityWithBlock(EntityType<? extends ThrowableProjectile> entityType, Level world, EntityType<? extends GasEntity> gasEntityType, CollisionType collisionType, int maxExpellingTime) {
        super(entityType, world, gasEntityType, collisionType, maxExpellingTime);
    }

    public abstract GrenadeBlock getBlock();

    @Override
    public void tick()
    {
        if (getDeltaMovement().length() <= 0.01f && !this.isPrimed() && !this.isExpellingGas())
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
                level().setBlockAndUpdate(pos.relative(Direction.UP), getBlock().defaultBlockState().setValue(getBlock().getClusterSizeProperty(), 1));
            }
        }
        super.tick();
    }

}
