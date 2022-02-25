package com.parzivail.pswg.entity.droid;

import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.world.InventoryUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class AstromechEntity extends MobEntity
{
	private static final TrackedData<Byte> LEG_ANIM = DataTracker.registerData(AstromechEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final byte LEG_ANIM_LENGTH = 18;

	private final DefaultedList<ItemStack> heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);

	private byte prevLegExtensionTimer;

	public AstromechEntity(EntityType<? extends MobEntity> type, World world)
	{
		super(type, world);
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D);
	}

	@Override
	protected void initGoals()
	{
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

		var anim = TrackedAnimationValue.tick(prevLegExtensionTimer);

		boolean shouldToggleLeg = this.age % 200 == 0;
		if (TrackedAnimationValue.isStopped(anim) && shouldToggleLeg)
			anim = TrackedAnimationValue.startToggled(anim, LEG_ANIM_LENGTH);

		dataTracker.set(LEG_ANIM, anim);
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
