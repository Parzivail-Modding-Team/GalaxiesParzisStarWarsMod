package com.parzivail.pswg.entity.rodent;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class SandSkitterEntity extends PathAwareEntity
{
	protected static final ImmutableList<SensorType<? extends Sensor<? super SandSkitterEntity>>> SENSORS = ImmutableList.of(
			SensorType.NEAREST_LIVING_ENTITIES,
			SensorType.NEAREST_PLAYERS
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
			MemoryModuleType.PATH,
			MemoryModuleType.LOOK_TARGET,
			MemoryModuleType.VISIBLE_MOBS,
			MemoryModuleType.WALK_TARGET,
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
			MemoryModuleType.HURT_BY,
			MemoryModuleType.LIKED_PLAYER
	);

	public SandSkitterEntity(EntityType<? extends SandSkitterEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	protected Brain.Profile<SandSkitterEntity> createBrainProfile()
	{
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic)
	{
		return SandSkitterBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<SandSkitterEntity> getBrain()
	{
		return (Brain<SandSkitterEntity>)super.getBrain();
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity
				.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 8.0);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions)
	{
		return dimensions.height * 0.6F;
	}

	@Override
	protected void mobTick()
	{
		this.world.getProfiler().push("sandSkitterBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("sandSkitterActivityUpdate");
		SandSkitterBrain.updateActivities(this);
		this.world.getProfiler().pop();
		super.mobTick();
	}

	@Override
	protected void sendAiDebugData()
	{
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}
}
