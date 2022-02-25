package com.parzivail.pswg.entity.droid;

import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.entity.ai.SlowTurningMoveControl;
import com.parzivail.util.world.InventoryUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class AstromechEntity extends PathAwareEntity
{
	private static class ExtendLegGoal extends Goal
	{
		private final AstromechEntity droid;
		private final boolean extend;

		private boolean isRequested = false;

		public ExtendLegGoal(AstromechEntity droid, boolean extend)
		{
			this.droid = droid;
			this.extend = extend;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart()
		{
			var leg = droid.dataTracker.get(LEG_ANIM);
			if (!TrackedAnimationValue.isStopped(leg))
				return false;

			var currentlyExtended = TrackedAnimationValue.isPositiveDirection(leg);
			if (extend)
			{
				if (droid.isLeashed() && !currentlyExtended)
					return true;

				if (!isRequested)
					return false;
				this.isRequested = false;
				return !currentlyExtended;
			}
			else
			{
				if (droid.isLeashed())
					return false;
				return currentlyExtended;
			}
		}

		@Override
		public boolean shouldContinue()
		{
			return !TrackedAnimationValue.isStopped(droid.dataTracker.get(LEG_ANIM));
		}

		@Override
		public void start()
		{
			droid.dataTracker.set(LEG_ANIM, TrackedAnimationValue.set(extend, LEG_ANIM_LENGTH));
		}

		@Override
		public boolean shouldRunEveryTick()
		{
			return true;
		}

		@Override
		public void tick()
		{
			droid.dataTracker.set(LEG_ANIM, TrackedAnimationValue.tick(droid.dataTracker.get(LEG_ANIM)));
		}
	}

	public static class WanderAroundGoal extends Goal
	{
		protected final AstromechEntity droid;
		private final boolean canDespawn;
		protected final double speed;

		protected double targetX;
		protected double targetY;
		protected double targetZ;
		protected int chance;
		protected boolean ignoringChance;
		private boolean shouldRestart = false;

		public WanderAroundGoal(AstromechEntity entity, double speed, int chance, boolean canDespawn)
		{
			this.droid = entity;
			this.speed = speed;
			this.chance = chance;
			this.canDespawn = canDespawn;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart()
		{
			if (!TrackedAnimationValue.isStopped(droid.dataTracker.get(LEG_ANIM)))
				return false;

			if (shouldRestart)
			{
				shouldRestart = false;
				return true;
			}

			if (!this.ignoringChance)
			{
				if (this.canDespawn && this.droid.getDespawnCounter() >= 100)
				{
					return false;
				}

				if (this.droid.getRandom().nextInt(toGoalTicks(this.chance)) != 0)
				{
					return false;
				}
			}

			Vec3d vec3d = this.getWanderTarget();
			if (vec3d == null)
			{
				return false;
			}
			else
			{
				this.targetX = vec3d.x;
				this.targetY = vec3d.y;
				this.targetZ = vec3d.z;
				this.ignoringChance = false;

				return true;
			}
		}

		@Nullable
		protected Vec3d getWanderTarget()
		{
			return NoPenaltyTargeting.find(this.droid, 10, 7);
		}

		@Override
		public boolean shouldContinue()
		{
			return !this.droid.getNavigation().isIdle();
		}

		@Override
		public void start()
		{
			this.droid.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
		}

		@Override
		public void tick()
		{
			if (!TrackedAnimationValue.isPositiveDirection(droid.dataTracker.get(LEG_ANIM)))
			{
				droid.requestLegExtension();
				shouldRestart = true;
			}
		}

		@Override
		public void stop()
		{
			this.droid.getNavigation().stop();
			super.stop();
		}
	}

	private static final TrackedData<Byte> LEG_ANIM = DataTracker.registerData(AstromechEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final byte LEG_ANIM_LENGTH = 18;

	private final DefaultedList<ItemStack> heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);

	private ExtendLegGoal extendLegGoal;
	private byte prevLegExtensionTimer;

	public AstromechEntity(EntityType<? extends PathAwareEntity> type, World world)
	{
		super(type, world);
		this.moveControl = new SlowTurningMoveControl(this, 6);
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D);
	}

	@Override
	protected void initGoals()
	{
		this.goalSelector.add(3, this.extendLegGoal = new ExtendLegGoal(this, true));
		this.goalSelector.add(4, new WanderAroundGoal(this, 0.2, 100, false));
		this.goalSelector.add(5, new ExtendLegGoal(this, false));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(LEG_ANIM, (byte)0);
	}

	public float getLegDeltaExtension(float tickDelta)
	{
		var anim = dataTracker.get(LEG_ANIM);
		var timer = TrackedAnimationValue.getTimer(anim, prevLegExtensionTimer, tickDelta) / LEG_ANIM_LENGTH;

		if (TrackedAnimationValue.isPositiveDirection(anim))
			timer = 1 - timer;

		return timer;
	}

	private void requestLegExtension()
	{
		extendLegGoal.isRequested = true;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		InventoryUtil.readFromNbt(nbt, "ArmorItems", armorItems);
		InventoryUtil.readFromNbt(nbt, "HandItems", heldItems);
	}

	@Override
	public Iterable<ItemStack> getItemsHand()
	{
		return heldItems;
	}

	@Override
	public Iterable<ItemStack> getArmorItems()
	{
		return armorItems;
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot)
	{
		switch (slot.getType())
		{
			case HAND:
				return this.heldItems.get(slot.getEntitySlotId());
			case ARMOR:
				return this.armorItems.get(slot.getEntitySlotId());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void tick()
	{
		super.tick();
		prevLegExtensionTimer = dataTracker.get(LEG_ANIM);
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack)
	{
		this.processEquippedStack(stack);
		switch (slot.getType())
		{
			case HAND:
				this.onEquipStack(stack);
				this.heldItems.set(slot.getEntitySlotId(), stack);
				break;
			case ARMOR:
				this.onEquipStack(stack);
				this.armorItems.set(slot.getEntitySlotId(), stack);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);

		InventoryUtil.writeToNbt(nbt, "ArmorItems", armorItems);
		InventoryUtil.writeToNbt(nbt, "HandItems", heldItems);
	}

	@Override
	public Arm getMainArm()
	{
		return Arm.RIGHT;
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
	}
}
