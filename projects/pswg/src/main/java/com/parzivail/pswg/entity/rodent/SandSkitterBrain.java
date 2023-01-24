package com.parzivail.pswg.entity.rodent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

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

	private static void addCoreActivities(Brain<SandSkitterEntity> brain)
	{
		brain.setTaskList(
				Activity.CORE,
				0,
				ImmutableList.of(
						new StayAboveWaterTask(0.8F),
						new WalkTask(2.5F),
						new LookAroundTask(45, 90),
						new WanderAroundTask(),
						new TemptationCooldownTask(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS),
						new TemptationCooldownTask(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)
				)
		);
	}

	private static void addIdleActivities(Brain<SandSkitterEntity> brain)
	{
		// TODO:
		//		brain.setTaskList(
		//				Activity.IDLE,
		//				ImmutableList.of(
		//						Pair.of(1, new WalkTowardsLookTargetTask<>(SandSkitterBrain::getLikedLookTarget, 4, 16, 2.25F)),
		//						Pair.of(2, new TimeLimitedTask<>(new FollowMobTask((sandSkitter) -> true, 6.0F), UniformIntProvider.create(30, 60))),
		//						Pair.of(3, new RandomTask<>(
		//								ImmutableList.of(
		//										Pair.of(new NoPenaltyStrollTask(1.0F), 2),
		//										Pair.of(new GoTowardsLookTarget(1.0F, 3), 2),
		//										Pair.of(new WaitTask(30, 60), 1)
		//								)
		//						))),
		//				ImmutableSet.of()
		//		);
	}

	public static void updateActivities(SandSkitterEntity allay)
	{
		allay.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
	}

	private static Optional<LookTarget> getLikedLookTarget(LivingEntity sandSkitter)
	{
		return getLikedPlayer(sandSkitter).map((player) -> new EntityLookTarget(player, true));
	}

	public static Optional<ServerPlayerEntity> getLikedPlayer(LivingEntity sandSkitter)
	{
		World world = sandSkitter.getWorld();
		if (world.isClient() || !(world instanceof ServerWorld serverWorld))
			return Optional.empty();

		Optional<UUID> optional = sandSkitter.getBrain().getOptionalMemory(MemoryModuleType.LIKED_PLAYER);
		if (optional.isEmpty())
			return Optional.empty();

		Entity entity = serverWorld.getEntity((UUID)optional.get());
		if (!(entity instanceof ServerPlayerEntity serverPlayerEntity))
			return Optional.empty();

		if ((serverPlayerEntity.interactionManager.isSurvivalLike() || serverPlayerEntity.interactionManager.isCreative()) && serverPlayerEntity.isInRange(sandSkitter, 64.0))
			return Optional.of(serverPlayerEntity);

		return Optional.empty();
	}
}
