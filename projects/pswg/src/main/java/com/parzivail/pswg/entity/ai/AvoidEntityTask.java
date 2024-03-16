package com.parzivail.pswg.entity.ai;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class AvoidEntityTask extends MultiTickTask<PathAwareEntity>
{
	private final float speed;

	public AvoidEntityTask(float speed)
	{
		super(ImmutableMap.of(
				MemoryModuleType.AVOID_TARGET, MemoryModuleState.VALUE_PRESENT
		), 100, 120);
		this.speed = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, PathAwareEntity entity)
	{
		return true;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, PathAwareEntity entity, long time)
	{
		return this.getAvoidTarget(entity) != null && this.getAvoidTarget(entity).isAlive();
	}

	@Override
	protected void run(ServerWorld world, PathAwareEntity entity, long time)
	{
		entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
	}

	@Override
	protected void keepRunning(ServerWorld world, PathAwareEntity entity, long time)
	{
		LivingEntity avoidTarget = this.getAvoidTarget(entity);

		if (avoidTarget != null && entity.getNavigation().isIdle())
		{
			Vec3d vec3d = NoPenaltyTargeting.findFrom(entity, 16, 7, avoidTarget.getPos());
			if (vec3d != null)
			{
				entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
			}
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, PathAwareEntity entity, long time)
	{
		entity.getBrain().forget(MemoryModuleType.AVOID_TARGET);
	}

	private LivingEntity getAvoidTarget(PathAwareEntity entity)
	{
		return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.AVOID_TARGET).orElse(null);
	}

}
