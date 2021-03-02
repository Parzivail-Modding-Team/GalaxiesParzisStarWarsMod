package com.parzivail.pswg.item.blaster;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.access.util.Matrix4fAccessUtil;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.item.blaster.data.*;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.pswg.util.QuatUtil;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.ILeftClickConsumer;
import com.parzivail.util.item.IZoomingItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;

public class BlasterItem extends Item implements ILeftClickConsumer, ICustomVisualItemEquality, IZoomingItem, IDefaultNbtProvider
{
	public BlasterItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return false;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		final ItemStack stack = player.getStackInHand(hand);

		if (!world.isClient)
			BlasterTag.mutate(stack, t -> t.isAimingDownSights = !t.isAimingDownSights);

		return TypedActionResult.fail(stack);
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return true;
	}

	@Override
	public TypedActionResult<ItemStack> useLeft(World world, PlayerEntity player, Hand hand)
	{
		final ItemStack stack = player.getStackInHand(hand);
		BlasterDescriptor bd = new BlasterDescriptor(stack.getOrCreateTag());
		BlasterTag bt = new BlasterTag(stack.getOrCreateTag());

		if (!bt.isReady())
			return TypedActionResult.fail(stack);

		bt.shotTimer = bd.automaticRepeatTime;

		if (bt.isOverheatCooling())
		{
			if (world.isClient || !bt.canBypassOverheat)
			{
				bt.serializeAsSubtag(stack);
				return TypedActionResult.fail(stack);
			}

			BlasterCoolingBypassProfile profile = bd.cooling;

			final float cooldownTime = bt.overheatTimer / (float)bd.heat.capacity;

			final float primaryBypassStart = profile.primaryBypassTime - profile.primaryBypassTolerance;
			final float primaryBypassEnd = profile.primaryBypassTime + profile.primaryBypassTolerance;
			final float secondaryBypassStart = profile.secondaryBypassTime - profile.secondaryBypassTolerance;
			final float secondaryBypassEnd = profile.secondaryBypassTime + profile.secondaryBypassTolerance;

			TypedActionResult<ItemStack> result = TypedActionResult.fail(stack);

			if (profile.primaryBypassTolerance > 0 && cooldownTime >= primaryBypassStart && cooldownTime <= primaryBypassEnd)
			{
				// TODO: primary bypass sound
				bt.overheatTimer = 0;

				result = TypedActionResult.success(stack);
			}
			else if (profile.secondaryBypassTolerance > 0 && cooldownTime >= secondaryBypassStart && cooldownTime <= secondaryBypassEnd)
			{
				// TODO: secondary bypass sound
				bt.overheatTimer = 0;

				result = TypedActionResult.success(stack);
			}
			else
			{
				// TODO: failed bypass sound
				bt.canBypassOverheat = false;
			}

			bt.serializeAsSubtag(stack);

			return result;
		}

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

					bt.serializeAsSubtag(stack);
					return TypedActionResult.fail(stack);
				}
				else if (!world.isClient)
				{
					bt.shotsRemaining = nextPack.getRight().numShots;
					player.inventory.removeStack(nextPack.getLeft(), 1);
					world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.RELOAD, SoundCategory.PLAYERS, 1f, 1f);
				}
			}
		}

		bt.passiveCooldownTimer = bd.heat.passiveCooldownDelay;
		bt.heat += bd.heat.perRound;
		bt.shotsRemaining--;

		if (bt.heat > bd.heat.capacity)
		{
			// TODO: overheat sound
			bt.overheatTimer = bd.heat.capacity + bd.heat.overheatPenalty;
			bt.canBypassOverheat = true;
			bt.heat = 0;
		}

		if (!world.isClient)
		{
			Matrix4f m = new Matrix4f();
			Matrix4fAccessUtil.loadIdentity(m);

			Matrix4fAccessUtil.multiply(m, QuatUtil.of(0, -player.yaw, 0, true));
			Matrix4fAccessUtil.multiply(m, QuatUtil.of(player.pitch, 0, 0, true));

			// TODO
			float hS = (world.random.nextFloat() * 2 - 1) * bd.spread.horizontal;
			float vS = (world.random.nextFloat() * 2 - 1) * bd.spread.vertical;

			float hSR = 1; // - bd.getBarrel().getHorizontalSpreadReduction();
			float vSR = 1; // - bd.getBarrel().getVerticalSpreadReduction();

			Matrix4fAccessUtil.multiply(m, QuatUtil.of(0, hS * hSR, 0, true));
			Matrix4fAccessUtil.multiply(m, QuatUtil.of(vS * vSR, 0, 0, true));

			Vec3d fromDir = Matrix4fAccessUtil.transform(com.parzivail.util.math.MathUtil.POSZ, m);
			world.playSound(null, player.getBlockPos(), SwgSounds.getOrDefault(getSound(bd.id), SwgSounds.Blaster.FIRE_A280), SoundCategory.PLAYERS, 1 /* 1 - bd.getBarrel().getNoiseReduction() */, 1 + (float)world.random.nextGaussian() / 10);

			float range = bd.range;
			float damage = bd.damage;

			BlasterUtil.fireBolt(world, player, fromDir, range, damage, entity -> {
				entity.setProperties(player, player.pitch + vS * vSR, player.yaw + hS * hSR, 0.0F, 4.0F, 0);
				entity.setPos(player.getX(), player.getEyeY() - entity.getHeight() / 2f, player.getZ());
			});

			bt.serializeAsSubtag(stack);
		}

		return TypedActionResult.success(stack);
	}

	private Identifier getSound(Identifier id)
	{
		return new Identifier(id.getNamespace(), "blaster.fire." + id.getPath());
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		BlasterDescriptor bd = new BlasterDescriptor(stack.getOrCreateTag());
		return "item." + bd.id.getNamespace() + ".blaster_" + bd.id.getPath();
	}

	@Override
	public CompoundTag getDefaultTag(ItemConvertible item, int count)
	{
		BlasterDescriptor d = new BlasterDescriptor(
				Resources.identifier("a280"),
				10,
				50,
				1,
				0xFF0000,
				10,
				10,
				new BlasterSpreadInfo(0, 0),
				new BlasterHeatInfo(100, 15, 8, 30, 14, 15),
				BlasterCoolingBypassProfile.DEFAULT
		);

		return d.toSubtag();
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks)
	{
		if (group != Galaxies.Tab)
			return;

		SwgBlasterManager blasterLoader = Client.getBlasterLoader();

		for (Map.Entry<Identifier, BlasterDescriptor> entry : blasterLoader.getBlasters().entrySet())
			stacks.add(forType(entry.getValue()));
	}

	private ItemStack forType(BlasterDescriptor blasterDescriptor)
	{
		ItemStack stack = new ItemStack(this);
		blasterDescriptor.serializeAsSubtag(stack);
		return stack;
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
		BlasterDescriptor bd = new BlasterDescriptor(stack.getOrCreateTag());

		BlasterTag.mutate(stack, blasterTag -> {
			if (blasterTag.overheatTimer > 0)
				blasterTag.overheatTimer -= bd.heat.overheatDrainSpeed;

			if (blasterTag.heat > 0 && blasterTag.passiveCooldownTimer == 0)
				blasterTag.heat -= bd.heat.drainSpeed;

			blasterTag.tick();
		});
	}

	@Override
	public boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated)
	{
		BlasterDescriptor bdOriginal = new BlasterDescriptor(original.getOrCreateTag());
		BlasterDescriptor bdUpdated = new BlasterDescriptor(updated.getOrCreateTag());
		return bdOriginal.id.equals(bdUpdated.id);
	}

	@Override
	public double getFovMultiplier(ItemStack stack, World world, PlayerEntity entity)
	{
		BlasterTag bt = new BlasterTag(stack.getOrCreateTag());

		if (bt.isAimingDownSights)
			return 0.2f;

		return 1;
	}
}
