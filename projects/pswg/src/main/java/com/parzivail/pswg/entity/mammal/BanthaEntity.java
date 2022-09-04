package com.parzivail.pswg.entity.mammal;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import java.util.List;

public class BanthaEntity extends MobEntity
{
	public BanthaEntity(EntityType<? extends MobEntity> type, World world)
	{
		super(type, world);
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity
				.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0D);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		super.readCustomDataFromNbt(nbt);
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
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);
	}
}
