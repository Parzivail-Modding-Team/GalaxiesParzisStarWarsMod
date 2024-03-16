package com.parzivail.pswg.entity.mammal;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgMemoryModuleTypes;
import com.parzivail.pswg.container.SwgSensorTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BanthaEntity extends AnimalEntity implements Saddleable
{
	protected static final ImmutableList<SensorType<? extends Sensor<? super BanthaEntity>>> SENSORS = ImmutableList.of(
			SensorType.NEAREST_LIVING_ENTITIES,
			SensorType.NEAREST_PLAYERS,
			SensorType.HURT_BY,
			SensorType.NEAREST_ADULT,
			SwgSensorTypes.BANTHA_TEMPTATIONS
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
			SwgMemoryModuleTypes.CALL_COOLDOWN,
			SwgMemoryModuleTypes.FORAGE_COOLDOWN,
			SwgMemoryModuleTypes.FORAGE_TIME
	);
	private static final TrackedData<Boolean> PLAYER_BRED = DataTracker.registerData(BanthaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public final AnimationState callingAnimationState = new AnimationState();
	public final AnimationState forageAnimationState = new AnimationState();
	public final AnimationState sprintingAnimationState = new AnimationState();

	public BanthaEntity(EntityType<? extends AnimalEntity> type, World world)
	{
		super(type, world);
	}

	@Override
	protected Brain.Profile<BanthaEntity> createBrainProfile()
	{
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic)
	{
		return BanthaBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<BanthaEntity> getBrain()
	{
		return (Brain<BanthaEntity>)super.getBrain();
	}

	@Override
	protected void mobTick()
	{
		this.getWorld().getProfiler().push("banthaBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("banthaActivityUpdate");
		BanthaBrain.updateActivities(this);
		this.getWorld().getProfiler().pop();
		super.mobTick();
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity
				.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F);
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt)
	{
		this.getBrain().remember(SwgMemoryModuleTypes.FORAGE_COOLDOWN, 10);
		this.getBrain().remember(SwgMemoryModuleTypes.CALL_COOLDOWN, 1200);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity)
	{
		BanthaEntity banthaEntity = SwgEntities.Mammal.Bantha.create(world);
		banthaEntity.setPlayerBred(true);
		return banthaEntity;
	}

	@Override
	public void breed(ServerWorld world, AnimalEntity other)
	{
		int count = MathHelper.nextInt(random, 1, 2);
		for (int i = 0; i < count; i++) {
			super.breed(world, other);
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand)
	{
		ItemStack itemStack = player.getStackInHand(hand);
		ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
		if (actionResult.isAccepted())
		{
			return actionResult;
		}
		else if (this.isBreedingItem(itemStack))
		{
			boolean bl = this.receiveFood(player, itemStack);
			if (!player.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

			if (this.getWorld().isClient) {
				return ActionResult.CONSUME;
			} else {
				return bl ? ActionResult.SUCCESS : ActionResult.PASS;
			}
		}
		else {
			if (this.getPassengerList().size() < 2 && !this.isBaby())
			{
				if (!this.getWorld().isClient)
				{
					player.setYaw(this.getYaw());
					player.setPitch(this.getPitch());
					player.startRiding(this);
				}
			}

			return ActionResult.success(this.getWorld().isClient);
		}
	}

	@Override
	public void tick()
	{
		super.tick();
	}

	@Override
	public void handleStatus(byte status)
	{
		if (status == 4)
		{
			this.callingAnimationState.start(this.age);
		}
		else if (status == 6)
		{
			this.forageAnimationState.start(this.age);
		}
		else if (status == 7)
		{
			ParticleEffect particleEffect = ParticleTypes.SMOKE;
			for (int i = 0; i < 7; ++i)
			{
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
			}
		}
		else
		{
			super.handleStatus(status);
		}
	}

	private boolean receiveFood(PlayerEntity player, ItemStack item) {
		if (!this.isBreedingItem(item))
		{
			return false;
		}
		else
		{
			boolean bl = this.getHealth() < this.getMaxHealth();
			if (bl)
			{
				this.heal(2.0F);
			}
			boolean bl2 = this.getBreedingAge() == 0 && this.canEat();
			if (bl2)
			{
				this.lovePlayer(player);
			}
			boolean bl3 = this.isBaby();
			if (bl3)
			{
				this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
				if (!this.getWorld().isClient)
				{
					this.growUp(10);
				}
			}
			if (!bl && !bl2 && !bl3)
			{
				return false;
			} else {
				if (!this.isSilent())
				{
					SoundEvent soundEvent = SoundEvents.ENTITY_CAMEL_EAT;
					if (soundEvent != null)
					{
						this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
					}
				}
				this.emitGameEvent(GameEvent.EAT);
				return true;
			}
		}
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(PLAYER_BRED, false);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		super.readCustomDataFromNbt(nbt);
		this.setPlayerBred(nbt.getBoolean("PlayerBred"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("PlayerBred", this.isPlayerBred());
	}

	public boolean isPlayerBred() {
		return this.dataTracker.get(PLAYER_BRED);
	}

	public void setPlayerBred(boolean playerBred)
	{
		this.dataTracker.set(PLAYER_BRED, playerBred);
	}

	@Override
	public Iterable<ItemStack> getArmorItems()
	{
		return List.of();
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack)
	{

	}

	@Override
	public Arm getMainArm()
	{
		return Arm.LEFT;
	}

	@Override
	public boolean canBeSaddled()
	{
		return this.isAlive() && !this.isBaby();
	}

	@Override
	public void saddle(@Nullable SoundCategory sound)
	{
	}

	@Override
	public boolean isSaddled()
	{
		return true;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack)
	{
		return BanthaBrain.getTemptItems().test(stack);
	}

	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput)
	{
		super.tickControlled(controllingPlayer, movementInput);
		if (!this.isPlayerBred()) {
			this.removeAllPassengers();
		}
		Vec2f vec2f = this.getControlledRotation(controllingPlayer);
		this.setRotation(vec2f.y, vec2f.x);
		this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
	}

	protected Vec2f getControlledRotation(LivingEntity controllingPassenger)
	{
		return new Vec2f(controllingPassenger.getPitch() * 0.5F, controllingPassenger.getYaw());
	}

	@Override
	protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput)
	{
		if (!this.isPlayerBred()) return Vec3d.ZERO;

		float f = controllingPlayer.sidewaysSpeed * 0.5F;
		float g = controllingPlayer.forwardSpeed;
		if (g <= 0.0F) {
			g *= 0.25F;
		}
		return new Vec3d(f, 0.0, g);
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger()
	{
		Entity entity = this.getFirstPassenger();
		if (entity instanceof PlayerEntity playerEntity)
		{
			return playerEntity;
		}
		return super.getControllingPassenger();
	}

	@Override
	protected float getSaddledSpeed(PlayerEntity controllingPlayer)
	{
		float f = controllingPlayer.isSprinting() ? 0.1F : 0.0F;
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + f;
	}

	@Override
	public boolean canSprintAsVehicle()
	{
		return true;
	}

	@Override
	protected Vec3d getLeashOffset()
	{
		return new Vec3d(0.0, 0.675F * this.getStandingEyeHeight(), this.getWidth() * 0.4F);
	}
}
