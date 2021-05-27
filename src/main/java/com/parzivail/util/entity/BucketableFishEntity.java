package com.parzivail.util.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class BucketableFishEntity extends SchoolingFishEntity
{
	private final Item bucket;

	public BucketableFishEntity(EntityType<? extends BucketableFishEntity> entityType, World world, Item bucket)
	{
		super(entityType, world);
		this.bucket = bucket;
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 3.0D);
	}

	public ItemStack getBucketItem()
	{
		return new ItemStack(bucket);
	}

	protected SoundEvent getAmbientSound()
	{
		// TODO
		return SoundEvents.ENTITY_COD_AMBIENT;
	}

	protected SoundEvent getDeathSound()
	{
		// TODO
		return SoundEvents.ENTITY_COD_DEATH;
	}

	protected SoundEvent getHurtSound(DamageSource source)
	{
		// TODO
		return SoundEvents.ENTITY_COD_HURT;
	}

	protected SoundEvent getFlopSound()
	{
		// TODO
		return SoundEvents.ENTITY_COD_FLOP;
	}
}
