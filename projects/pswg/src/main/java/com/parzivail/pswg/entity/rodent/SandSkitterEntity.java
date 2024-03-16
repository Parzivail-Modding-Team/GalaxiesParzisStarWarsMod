package com.parzivail.pswg.entity.rodent;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.parzivail.pswg.container.SwgMemoryModuleTypes;
import com.parzivail.pswg.container.SwgSensorTypes;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AxolotlBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SandSkitterEntity extends PathAwareEntity
{
	protected static final ImmutableList<SensorType<? extends Sensor<? super SandSkitterEntity>>> SENSORS = ImmutableList.of(
			SensorType.NEAREST_LIVING_ENTITIES,
			SensorType.NEAREST_PLAYERS,
			SensorType.HURT_BY,
			SwgSensorTypes.SAND_SKITTER_SPECIFIC_SENSOR
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
			MemoryModuleType.PATH,
			MemoryModuleType.LOOK_TARGET,
			MemoryModuleType.WALK_TARGET,
			MemoryModuleType.VISIBLE_MOBS,
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
			MemoryModuleType.HURT_BY,
			MemoryModuleType.LIKED_PLAYER,
			MemoryModuleType.IS_PANICKING,
			MemoryModuleType.AVOID_TARGET,
			SwgMemoryModuleTypes.FORAGE_COOLDOWN,
			SwgMemoryModuleTypes.FORAGE_TIME
	);
	public final AnimationState nibbleAnimationState = new AnimationState();

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
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
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
		this.getWorld().getProfiler().push("sandskitterBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("sandskitterActivityUpdate");
		SandSkitterBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	@Override
	protected void sendAiDebugData()
	{
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt)
	{
		this.getBrain().remember(SwgMemoryModuleTypes.FORAGE_COOLDOWN, 1000);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public void handleStatus(byte status)
	{
		if (status == 6)
		{
			this.nibbleAnimationState.start(this.age);
		}
		else
		{
			super.handleStatus(status);
		}
	}
}
