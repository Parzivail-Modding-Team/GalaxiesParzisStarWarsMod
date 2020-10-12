package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.item.data.BlasterTag;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlasterItem extends Item implements ILeftClickConsumer
{
	public static class Settings extends Item.Settings
	{
		private Double damage;

		public Settings damage(double damage)
		{
			this.damage = damage;
			return this;
		}

		@Override
		public Settings maxCount(int maxCount)
		{
			super.maxCount(maxCount);
			return this;
		}

		@Override
		public Settings group(ItemGroup group)
		{
			super.group(group);
			return this;
		}
	}

	private final double damage;

	public static DamageSource blaster(PersistentProjectileEntity projectile, @Nullable Entity attacker)
	{
		return (new ProjectileDamageSource("pswg.blaster", projectile, attacker)).setProjectile();
	}

	public BlasterItem(com.parzivail.pswg.item.BlasterItem.Settings settings)
	{
		super(settings);
		this.damage = settings.damage;
	}

	@Override
	public TypedActionResult<ItemStack> useLeft(World world, PlayerEntity user, Hand hand)
	{
		// TODO: raycast aiming and damage, entity is for effect only
		final ItemStack stack = user.getStackInHand(hand);
		BlasterTag bt = new BlasterTag(stack.getOrCreateTag());
		if (!world.isClient && bt.shotTimer == 0)
		{
			final BlasterBoltEntity entity = new BlasterBoltEntity(SwgEntities.Misc.BlasterBolt, user, world);
			entity.setDamage(this.damage);
			entity.setProperties(user, user.pitch, user.yaw, 0.0F, 3.0F, 1.0F);
			entity.setNoGravity(true);
			entity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
			world.spawnEntity(entity);

			BlasterTag.mutate(stack, blasterTag -> blasterTag.shotTimer = 10);
		}
		return TypedActionResult.consume(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		BlasterTag.mutate(stack, BlasterTag::tick);
	}
}
