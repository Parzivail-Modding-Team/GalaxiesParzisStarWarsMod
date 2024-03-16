package com.parzivail.pswg.entity.amphibian;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.parzivail.pswg.container.SwgSensorTypes;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WorrtEntity extends AnimalEntity
{
	protected static final ImmutableList<SensorType<? extends Sensor<? super WorrtEntity>>> SENSORS = ImmutableList.of(
			SensorType.NEAREST_LIVING_ENTITIES,
			SensorType.NEAREST_PLAYERS,
			SensorType.HURT_BY,
			SensorType.NEAREST_ADULT,
			SwgSensorTypes.WORRT_SPECIFIC_SENSOR,
			SwgSensorTypes.WORRT_ATTACKABLES_SENSOR
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
			MemoryModuleType.PATH,
			MemoryModuleType.LOOK_TARGET,
			MemoryModuleType.WALK_TARGET,
			MemoryModuleType.VISIBLE_MOBS,
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
			MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
			MemoryModuleType.IS_TEMPTED,
			MemoryModuleType.TEMPTING_PLAYER,
			MemoryModuleType.HURT_BY,
			MemoryModuleType.LIKED_PLAYER,
			MemoryModuleType.BREED_TARGET,
			MemoryModuleType.IS_PANICKING,
			MemoryModuleType.ATTACK_COOLING_DOWN,
			MemoryModuleType.ATTACK_TARGET,
			MemoryModuleType.AVOID_TARGET
	);
	public AnimationState idleAnimationState = new AnimationState();
	public AnimationState attackAnimationState = new AnimationState();
	public AnimationState burpAnimationState = new AnimationState();

	public WorrtEntity(EntityType<? extends WorrtEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	protected Brain.Profile<WorrtEntity> createBrainProfile()
	{
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic)
	{
		return WorrtBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<WorrtEntity> getBrain()
	{
		return (Brain<WorrtEntity>)super.getBrain();
	}

	@Override
	public void tick()
	{
		super.tick();
		if (this.getWorld().isClient()) {
			this.idleAnimationState.setRunning(!this.limbAnimator.isLimbMoving(), this.age);
		}
	}

	@Override
	protected void mobTick()
	{
		this.getWorld().getProfiler().push("worrtBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("worrtActivityUpdate");
		WorrtBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity.createMobAttributes()
		                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D)
		                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
		                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0);
	}

	@Environment(EnvType.CLIENT)
	public float getAirborneLerp(float tickDelta)
	{
		var pos = this.getLerpedPos(tickDelta);
		var hit = EntityUtil.raycastBlocks(pos, MathUtil.V3D_NEG_Y, 1f, this, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE);
		if (hit.getType() == HitResult.Type.MISS)
			return 1;

		return (float)MathHelper.clamp(hit.getPos().distanceTo(pos), 0, 1);
	}

	@Override
	public boolean shouldSpawnSprintingParticles()
	{
		return false;
	}

	@Override
	public WorrtEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity)
	{
		return null;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status)
	{
		if (status == 1)
		{
			this.spawnSprintingParticles();
		}
		else if (status == 4)
		{
			this.burpAnimationState.start(this.age);
		}
		else if (status == 5)
		{
			this.attackAnimationState.start(this.age);
		}
		else
		{
			super.handleStatus(status);
		}
	}

	public static boolean isValidWorrtFood(LivingEntity entity) {
		return entity.getType().isIn(SwgTags.EntityTypes.WORRT_FOOD);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Vec3d getLeashOffset()
	{
		return new Vec3d(0.0D, 0.6F * this.getStandingEyeHeight(), this.getWidth() * 0.4F);
	}

}
