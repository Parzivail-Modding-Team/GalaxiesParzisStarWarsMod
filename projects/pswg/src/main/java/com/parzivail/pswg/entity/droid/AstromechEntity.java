package com.parzivail.pswg.entity.droid;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.EntityWithInventory;
import com.parzivail.pswg.screen.AstromechScreenHandler;
import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.entity.ai.SlowTurningMoveControl;
import com.parzivail.util.nbt.TagSerializer;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

public class AstromechEntity extends PathAwareEntity implements EntityWithInventory<AstromechScreenHandler>
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

	public static class AstromechParameters extends TagSerializer
	{
		private static final Identifier SLUG = Resources.id("astromech_parameters");

		public static AstromechParameters fromRootTag(NbtCompound tag)
		{
			var parent = new NbtCompound();
			parent.put(SLUG.toString(), tag);
			return new AstromechParameters(parent);
		}

		public static Optional<AstromechParameters> get(NbtCompound tag)
		{
			if (tag.contains(SLUG.toString()))
				return Optional.of(new AstromechParameters(tag));
			return Optional.empty();
		}

		public String series;

		public boolean usingPaintPreset;
		public String paintPreset;

		public int baseTint;
		public int domeTint;
		public int paintTint;

		public AstromechParameters(String preset)
		{
			super(SLUG);
			series = "r";

			usingPaintPreset = true;
			paintPreset = preset;

			baseTint = 0;
			domeTint = 0;
			paintTint = 0;
		}

		public AstromechParameters(NbtCompound nbt)
		{
			super(SLUG, nbt);
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			AstromechParameters that = (AstromechParameters)o;
			return usingPaintPreset == that.usingPaintPreset && baseTint == that.baseTint && paintTint == that.paintTint && domeTint == that.domeTint && series.equals(that.series) && paintPreset.equals(that.paintPreset);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(series, usingPaintPreset, paintPreset, paintTint, domeTint, baseTint);
		}
	}

	private static final TrackedData<Byte> LEG_ANIM = DataTracker.registerData(AstromechEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<NbtCompound> PARAMETERS = DataTracker.registerData(AstromechEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	private static final byte LEG_ANIM_LENGTH = 18;

	protected SimpleInventory inventory = new SimpleInventory(5);

	private ExtendLegGoal extendLegGoal;
	private byte prevLegExtensionTimer;
	private final AstromechParameters defaultParameters;

	public AstromechEntity(EntityType<? extends PathAwareEntity> type, World world, AstromechParameters defaultParameters)
	{
		super(type, world);
		this.defaultParameters = defaultParameters;
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
	public ActionResult interactMob(PlayerEntity player, Hand hand)
	{
		if (player.shouldCancelInteraction())
		{
			if (!this.world.isClient)
				EntityWithInventory.openScreen(SwgPackets.S2C.OpenEntityInventory, (ServerPlayerEntity)player, this);
			return ActionResult.success(this.world.isClient);
		}

		return super.interactMob(player, hand);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared)
	{
		return false;
	}

	@Override
	public int getEntityId()
	{
		return getId();
	}

	@Override
	public Inventory getInventory()
	{
		return inventory;
	}

	@Override
	public AstromechScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		return new AstromechScreenHandler(syncId, playerInventory, inventory, this);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(LEG_ANIM, (byte)0);
		this.dataTracker.startTracking(PARAMETERS, new AstromechParameters("missingno").toTag());
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
	public Iterable<ItemStack> getHandItems()
	{
		return DefaultedList.of();
	}

	@Override
	public Iterable<ItemStack> getArmorItems()
	{
		return DefaultedList.of();
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot)
	{
		return ItemStack.EMPTY;
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
	}

	public AstromechParameters getParameters()
	{
		return AstromechParameters.fromRootTag(dataTracker.get(PARAMETERS));
	}

	public void setParameters(AstromechParameters parameters)
	{
		dataTracker.set(PARAMETERS, parameters.toTag());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
		setParameters(AstromechParameters.get(nbt).orElse(defaultParameters));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);

		nbt.put("Inventory", inventory.toNbtList());
		getParameters().serializeAsSubtag(nbt);
	}

	@Override
	public Arm getMainArm()
	{
		return Arm.RIGHT;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
	}
}
