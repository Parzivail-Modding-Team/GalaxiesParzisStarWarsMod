package com.parzivail.pswg.entity.rodent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.parzivail.pswg.container.SwgMemoryModuleTypes;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.pswg.entity.ai.AvoidEntityTask;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class SandSkitterBrain
{
	protected static Brain<?> create(Brain<SandSkitterEntity> brain)
	{
		addCoreActivities(brain);
		addIdleActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	public static void addCoreActivities(Brain<SandSkitterEntity> brain)
	{
		brain.setTaskList(
				Activity.CORE,
				0,
				ImmutableList.of(
						new StayAboveWaterTask(0.8F),
						new FleeTask(2.5F),
						new AvoidEntityTask(2.5F),
						new LookAroundTask(45, 90),
						new WanderAroundTask(),
						new TemptationCooldownTask(SwgMemoryModuleTypes.FORAGE_COOLDOWN)
				)
		);
	}

	public static void addIdleActivities(Brain<SandSkitterEntity> brain)
	{
		brain.setTaskList(
				Activity.IDLE,
				ImmutableList.of(
						Pair.of(0, new Nibble()),
						Pair.of(1, new RandomTask<>(ImmutableList.of(
								Pair.of(StrollTask.create(1.0F), 2),
								Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 2),
								Pair.of(new WaitTask(30, 60), 1)
						)))
				)
		);
	}

	public static void updateActivities(SandSkitterEntity sandSkitter)
	{
		sandSkitter.getBrain().resetPossibleActivities(ImmutableList.of(Activity.AVOID, Activity.IDLE));
	}

	private static class Nibble extends MultiTickTask<SandSkitterEntity> {
		private final UniformIntProvider cooldown = UniformIntProvider.create(2400, 6000);

		public Nibble()
		{
			super(ImmutableMap.of(
					MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT,
					SwgMemoryModuleTypes.FORAGE_COOLDOWN, MemoryModuleState.VALUE_ABSENT,
					SwgMemoryModuleTypes.FORAGE_TIME, MemoryModuleState.REGISTERED
			), 36);
		}

		@Override
		protected boolean shouldRun(ServerWorld world, SandSkitterEntity entity)
		{
			return !entity.isInsideWall() && entity.getSteppingBlockState().isIn(SwgTags.Blocks.SAND_SKITTER_CAN_DIG);
		}

		@Override
		protected boolean shouldKeepRunning(ServerWorld world, SandSkitterEntity entity, long time)
		{
			return true;
		}

		@Override
		protected void run(ServerWorld world, SandSkitterEntity entity, long time)
		{
			entity.getWorld().sendEntityStatus(entity, (byte)6);
			entity.getBrain().remember(SwgMemoryModuleTypes.FORAGE_TIME, Unit.INSTANCE, 23);
		}

		@Override
		protected void keepRunning(ServerWorld world, SandSkitterEntity entity, long time)
		{
			entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
			if (entity.isOnGround() && !entity.getBrain().hasMemoryModule(SwgMemoryModuleTypes.FORAGE_TIME))
			{
				BlockState blockState = entity.getSteppingBlockState();
				if (blockState.getRenderType() != BlockRenderType.INVISIBLE)
				{
					for (int i = 0; i < 10; ++i)
					{
						Vec3d lookVec = entity.getRotationVector();
						int scale = 2;
						double d = entity.getX() + lookVec.x / scale;
						double e = entity.getY();
						double f = entity.getZ() + lookVec.z / scale;
						world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 1, 0.0, 0.0, 0.0, 1);
					}
				}
				if (time % 5 == 0) {
					entity.playSound(blockState.getSoundGroup().getBreakSound(), 1.0F, 1.0F);
				}
			}
		}

		@Override
		protected void finishRunning(ServerWorld world, SandSkitterEntity entity, long time)
		{
			entity.getBrain().remember(SwgMemoryModuleTypes.FORAGE_COOLDOWN, cooldown.get(entity.getRandom()));
		}
	}

}
