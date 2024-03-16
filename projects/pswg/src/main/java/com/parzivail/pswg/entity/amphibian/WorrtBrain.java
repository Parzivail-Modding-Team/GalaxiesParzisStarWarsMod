package com.parzivail.pswg.entity.amphibian;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.parzivail.pswg.entity.ai.AvoidEntityTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.Optional;

public class WorrtBrain
{

	protected static Brain<?> create(Brain<WorrtEntity> brain)
	{
		addCoreActivities(brain);
		addIdleActivities(brain);
		addTongueActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	public static void addCoreActivities(Brain<WorrtEntity> brain)
	{
		brain.setTaskList(
				Activity.CORE,
				0,
				ImmutableList.of(
						new StayAboveWaterTask(0.8F),
						new FleeTask(2.0F),
						new AvoidEntityTask(2.0F),
						new LookAroundTask(45, 90),
						new WanderAroundTask()
				)
		);
	}

	public static void addIdleActivities(Brain<WorrtEntity> brain)
	{
		brain.setTaskList(
				Activity.IDLE,
				ImmutableList.of(
						Pair.of(0, new TemptTask(entity -> 1.25F)),
						Pair.of(1, new RandomTask<>(ImmutableList.of(
								Pair.of(StrollTask.create(1.0F), 2),
								Pair.of(GoTowardsLookTargetTask.create(0.2F, 3), 2),
								Pair.of(new WaitTask(30, 60), 1)
						)))
				)
		);
	}

	private static void addTongueActivities(Brain<WorrtEntity> brain) {
		brain.setTaskList(
				Activity.TONGUE,
				0,
				ImmutableList.of(
						new WorrtEatEntityTask()
				),
				MemoryModuleType.ATTACK_TARGET
		);
	}

	public static void updateActivities(WorrtEntity worrt)
	{
		worrt.getBrain().resetPossibleActivities(ImmutableList.of(Activity.TONGUE, Activity.IDLE));
	}

	public static class WorrtEatEntityTask extends MultiTickTask<WorrtEntity>
	{

		public WorrtEatEntityTask()
		{
			super(ImmutableMap.of(
					MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED,
					MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT,
					MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT
			), 20);
		}

		@Override
		protected boolean shouldRun(ServerWorld world, WorrtEntity entity)
		{
			LivingEntity livingEntity = this.getAttackTarget(entity);
			boolean bl = this.isTargetReachable(entity, livingEntity);
			if (!bl)
			{
				entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
			}
			return bl && WorrtEntity.isValidWorrtFood(livingEntity);
		}

		@Override
		protected boolean shouldKeepRunning(ServerWorld world, WorrtEntity entity, long time)
		{
			Optional<LivingEntity> optionalMemory = entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
			if (optionalMemory.isPresent() && !optionalMemory.get().isAlive())
			{
				return false;
			}
			return entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && !entity.getBrain().hasMemoryModule(MemoryModuleType.IS_PANICKING);
		}

		@Override
		protected void run(ServerWorld world, WorrtEntity entity, long time)
		{
			LivingEntity livingEntity = this.getAttackTarget(entity);
			LookTargetUtil.lookAt(entity, livingEntity);
			entity.getWorld().sendEntityStatus(entity, (byte)5);
			entity.playSound(SoundEvents.ENTITY_FROG_TONGUE, 1.0F, 1.0F);
		}

		@Override
		protected void keepRunning(ServerWorld world, WorrtEntity entity, long time)
		{
			entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
			LivingEntity livingEntity = this.getAttackTarget(entity);
			LookTargetUtil.lookAt(entity, livingEntity);
			livingEntity.setVelocity(livingEntity.getPos().relativize(entity.getPos()).normalize().multiply(0.75));
			if (livingEntity.distanceTo(entity) <= 0.5F)
			{
				livingEntity.discard();
			}
		}

		@Override
		protected void finishRunning(ServerWorld world, WorrtEntity entity, long time)
		{
			Optional<LivingEntity> optionalMemory = entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
			if (optionalMemory.isPresent() && !optionalMemory.get().isAlive())
			{
				entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
				entity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, 20);
				entity.getWorld().sendEntityStatus(entity, (byte)4);
				entity.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
			}
		}

		private boolean isTargetReachable(WorrtEntity entity, LivingEntity target)
		{
			Path path = entity.getNavigation().findPathTo(target, 0);
			return path != null && path.getManhattanDistanceFromTarget() < 1.75F;
		}

		public LivingEntity getAttackTarget(WorrtEntity entity)
		{
			return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get();
		}
	}

}
