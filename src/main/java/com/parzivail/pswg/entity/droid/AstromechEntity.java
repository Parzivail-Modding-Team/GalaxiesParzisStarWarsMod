package com.parzivail.pswg.entity.droid;

import com.parzivail.util.world.InventoryUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class AstromechEntity extends LivingEntity
{
	private final DefaultedList<ItemStack> heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);

	public AstromechEntity(EntityType<? extends LivingEntity> type, World world)
	{
		super(type, world);
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
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
