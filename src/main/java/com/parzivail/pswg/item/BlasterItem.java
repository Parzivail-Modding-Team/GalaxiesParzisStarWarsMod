package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlasterItem extends Item
{
	private final double damage;

	public static DamageSource blaster(ProjectileEntity projectile, @Nullable Entity attacker)
	{
		return (new ProjectileDamageSource("pswg.blaster", projectile, attacker)).setProjectile();
	}

	public BlasterItem(Settings settings)
	{
		super(settings);
		this.damage = settings.damage;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		final ItemStack stack = user.getStackInHand(hand);
		if (!world.isClient)
		{
			final BlasterBoltEntity entity = new BlasterBoltEntity(SwgEntities.BlasterBolt, user, world);
			entity.setDamage(this.damage);
			entity.setProperties(user, user.pitch, user.yaw, 0.0F, 3.0F, 1.0F);
			entity.setNoGravity(true);
			entity.pickupType = ProjectileEntity.PickupPermission.DISALLOWED;
			world.spawnEntity(entity);
		}
		return TypedActionResult.consume(stack);
	}

	public static class Settings extends Item.Settings
	{
		private Double damage;

		/**
		 * Required.
		 */
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
}
