package com.parzivail.pswg.entity.mammal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgMemoryModuleTypes;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.pswg.mixin.BreedTaskAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class BanthaBrain
{

	protected static Brain<?> create(Brain<BanthaEntity> brain)
	{
		addCoreActivities(brain);
		addIdleActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	public static void addCoreActivities(Brain<BanthaEntity> brain)
	{
		brain.setTaskList(
				Activity.CORE,
				0,
				ImmutableList.of(
						new StayAboveWaterTask(0.8F),
						new FleeTask(2.5F),
						new LookAroundTask(45, 90),
						new WanderAroundTask(),
						new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
						new TemptationCooldownTask(SwgMemoryModuleTypes.CALL_COOLDOWN),
						new TemptationCooldownTask(SwgMemoryModuleTypes.FORAGE_COOLDOWN)
				)
		);
	}

	public static void addIdleActivities(Brain<BanthaEntity> brain)
	{
		brain.setTaskList(
				Activity.IDLE,
				ImmutableList.of(
						Pair.of(0, new BanthaBreed(SwgEntities.Mammal.Bantha, 1.0F)),
						Pair.of(1, new TemptTask(entity -> 1.25F)),
						Pair.of(2, new Bray()),
						Pair.of(3, new Forage()),
						Pair.of(4, new RandomTask<>(ImmutableList.of(
								Pair.of(StrollTask.create(1.0F), 2),
								Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 2),
								Pair.of(new WaitTask(30, 60), 1)
						)))
				)
		);
	}

	public static void updateActivities(BanthaEntity bantha)
	{
		bantha.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
	}

	public static Ingredient getTemptItems() {
		return Ingredient.fromTag(SwgTags.Items.BANTHA_TEMPT_ITEMS);
	}

	public static class Bray extends MultiTickTask<BanthaEntity> {
		private final UniformIntProvider cooldown = UniformIntProvider.create(2400, 6000);

		public Bray()
		{
			super(ImmutableMap.of(SwgMemoryModuleTypes.CALL_COOLDOWN, MemoryModuleState.VALUE_ABSENT));
		}

		@Override
		protected boolean shouldRun(ServerWorld world, BanthaEntity entity)
		{
			return entity.isOnGround();
		}

		@Override
		protected void run(ServerWorld world, BanthaEntity entity, long time)
		{
			entity.playSound(SoundEvents.ENTITY_CAMEL_SIT, 3.0F, -0.5F);
			entity.getWorld().sendEntityStatus(entity, (byte)4);
			entity.getBrain().remember(SwgMemoryModuleTypes.CALL_COOLDOWN, cooldown.get(entity.getRandom()));
		}

	}

	public static class Forage extends MultiTickTask<BanthaEntity> {
		private final UniformIntProvider cooldown = UniformIntProvider.create(2400, 6000);

		public Forage()
		{
			super(ImmutableMap.of(
					MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT,
					SwgMemoryModuleTypes.FORAGE_COOLDOWN, MemoryModuleState.VALUE_ABSENT,
					SwgMemoryModuleTypes.FORAGE_TIME, MemoryModuleState.REGISTERED
			), 26);
		}

		@Override
		protected boolean shouldRun(ServerWorld world, BanthaEntity entity)
		{
			return isGrassBlock(world, entity);
		}

		@Override
		protected boolean shouldKeepRunning(ServerWorld world, BanthaEntity entity, long time)
		{
			return true;
		}

		@Override
		protected void run(ServerWorld world, BanthaEntity entity, long time)
		{
			entity.getWorld().sendEntityStatus(entity, (byte)6);
			entity.getBrain().remember(SwgMemoryModuleTypes.FORAGE_TIME, Unit.INSTANCE, 25);
		}

		@Override
		protected void keepRunning(ServerWorld world, BanthaEntity entity, long time)
		{
			entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
			if (!entity.getBrain().hasMemoryModule(SwgMemoryModuleTypes.FORAGE_TIME))
			{
				Vec3d add = entity.getPos().add(entity.getRotationVector().multiply(1.4));
				BlockPos pos = BlockPos.ofFloored(add.x, add.y, add.z).down();
				Vec3d center = Vec3d.ofCenter(pos);
				BlockPos centerPos = BlockPos.ofFloored(center.x, center.y, center.z);
				if (isGrassBlock(world, entity))
				{
					world.setBlockState(centerPos, Blocks.DIRT.getDefaultState(), 2);
					world.syncWorldEvent(2001, centerPos, Block.getRawIdFromState(Blocks.GRASS_BLOCK.getDefaultState()));
				}
			}
		}

		@Override
		protected void finishRunning(ServerWorld world, BanthaEntity entity, long time)
		{
			entity.getBrain().remember(SwgMemoryModuleTypes.FORAGE_COOLDOWN, cooldown.get(entity.getRandom()));
		}

		private boolean isGrassBlock(ServerWorld world, BanthaEntity entity) {
			Vec3d add = entity.getPos().add(entity.getRotationVector().multiply(1.4));
			BlockPos pos = BlockPos.ofFloored(add.x, add.y, add.z).down();
			Vec3d center = Vec3d.ofCenter(pos);
			BlockPos centerPos = BlockPos.ofFloored(center.x, center.y, center.z);
			return world.getBlockState(centerPos).isOf(Blocks.GRASS_BLOCK);
		}

	}

	public static class BanthaBreed extends BreedTask {
		private final float speed;

		public BanthaBreed(EntityType<? extends AnimalEntity> targetType, float speed)
		{
			super(targetType, speed);
			this.speed = speed;
		}

		@Override
		protected void keepRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l)
		{
			AnimalEntity animalEntity2 = this.getBreedTarget(animalEntity);
			LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2, speed);
			if (animalEntity.isInRange(animalEntity2, 4.0))
			{
				if (l >= ((BreedTaskAccessor)this).getBreedTime())
				{
					animalEntity.breed(serverWorld, animalEntity2);
					animalEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
					animalEntity2.getBrain().forget(MemoryModuleType.BREED_TARGET);
				}
			}
		}

		private AnimalEntity getBreedTarget(AnimalEntity animal) {
			return (AnimalEntity)animal.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).get();
		}
	}

}
