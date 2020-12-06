package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.item.data.BlasterPowerPack;
import com.parzivail.pswg.item.data.BlasterTag;
import com.parzivail.pswg.util.MathUtil;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.ILeftClickConsumer;
import com.parzivail.util.math.EntityHitResult;
import com.parzivail.util.math.MatrixExtUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlasterItem extends Item implements ILeftClickConsumer, ICustomVisualItemEquality
{
	private final float damage;
	private final SoundEvent sound;

	public BlasterItem(BlasterItem.Settings settings)
	{
		super(settings);
		this.damage = settings.damage;
		this.sound = settings.sound;
	}

	public static DamageSource getDamageSource(Entity attacker)
	{
		return (new EntityDamageSource("pswg.blaster", attacker)).setProjectile();
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return false;
	}

	@Override
	public TypedActionResult<ItemStack> useLeft(World world, PlayerEntity player, Hand hand)
	{
		final ItemStack stack = player.getStackInHand(hand);
		BlasterTag bt = new BlasterTag(stack.getOrCreateTag());

		if (!bt.isReady() || bt.isCoolingDown())
			return TypedActionResult.pass(stack);

		if (!player.isCreative())
		{
			if (bt.shotsRemaining <= 0)
			{
				Pair<Integer, BlasterPowerPack> nextPack = getAnotherPack(player);

				if (nextPack == null)
				{
					if (!world.isClient)
					{
						world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.DRYFIRE, SoundCategory.PLAYERS, 1f, 1f);
					}
					return TypedActionResult.fail(stack);
				}
				else if (!world.isClient)
				{
					bt.shotsRemaining = nextPack.getRight().numShots;
					player.inventory.removeStack(nextPack.getLeft(), 1);
					world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.RELOAD, SoundCategory.PLAYERS, 1f, 1f);
				}
			}

			bt.heat += 20;
			bt.shotsRemaining--;
		}

		if (!world.isClient)
		{
			float spread = getSpreadAmount(stack, player);
			Matrix4f m = new Matrix4f();
			MatrixExtUtil.loadIdentity(m);

			MatrixExtUtil.multiply(m, QuatUtil.of(0, -player.yaw, 0, true));
			MatrixExtUtil.multiply(m, QuatUtil.of(player.pitch, 0, 0, true));

			float hS = (world.random.nextFloat() * 2 - 1) * spread;
			float vS = (world.random.nextFloat() * 2 - 1) * spread;

			float hSR = 1; // - bd.getBarrel().getHorizontalSpreadReduction();
			float vSR = 1; // - bd.getBarrel().getVerticalSpreadReduction();

			MatrixExtUtil.multiply(m, QuatUtil.of(0, hS * hSR, 0, true));
			MatrixExtUtil.multiply(m, QuatUtil.of(vS * vSR, 0, 0, true));

			Vec3d look = MathUtil.transform(MathUtil.POSZ, m);

			final BlasterBoltEntity entity = new BlasterBoltEntity(SwgEntities.Misc.BlasterBolt, player, world);
			entity.setProperties(player, player.pitch + vS * vSR, player.yaw + hS * hSR, 0.0F, 3.0F, 0);
			entity.setPos(player.getX(), player.getY() + 1.2f, player.getZ());
			world.spawnEntity(entity);

			world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS, 1 /* 1 - bd.getBarrel().getNoiseReduction() */, 1 + (float)world.random.nextGaussian() / 10);

			int range = 20;

			Vec3d start = new Vec3d(entity.getX(), entity.getY(), entity.getZ());

			EntityHitResult hit = EntityUtil.raycastEntities(start, look, range, player, new Entity[] { player });

			if (hit == null)
			{
				BlockHitResult blockHit = EntityUtil.raycastBlocks(start, look, range, player);
				// smoke poof
			}
			else
			{
				hit.entity.damage(getDamageSource(player), damage);
			}

			bt.shotTimer = 10;

			stack.setTag(bt.serialize());
		}

		return TypedActionResult.success(stack);
	}

	private float getSpreadAmount(ItemStack stack, PlayerEntity player)
	{
		return 0;
	}

	private Pair<Integer, BlasterPowerPack> getAnotherPack(PlayerEntity player)
	{
		for (int i = 0; i < player.inventory.size(); i++)
		{
			ItemStack s = player.inventory.getStack(i);
			BlasterPowerPack a = BlasterPowerPackItem.getPackType(s);
			if (a == null)
				continue;

			return new Pair<>(i, a);
		}
		return null;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		BlasterTag.mutate(stack, BlasterTag::tick);
	}

	@Override
	public boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated)
	{
		return true;
	}

	public static class Settings extends Item.Settings
	{
		private float damage = 1;
		private SoundEvent sound = SwgSounds.Blaster.FIRE_DL44;

		public Settings damage(float damage)
		{
			this.damage = damage;
			return this;
		}

		public Settings sound(SoundEvent sound)
		{
			this.sound = sound;
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
