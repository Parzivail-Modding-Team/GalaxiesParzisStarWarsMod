package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.ThermalDetonatorBlock;
import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.item.ICooldownItem;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ThermalDetonatorItem extends BlockItem implements ILeftClickConsumer, ICooldownItem, ICustomVisualItemEquality
{
	public final int baseTicksToExplosion = 150;
	@TarkinLang
	public static final String I18N_TOOLTIP_CONTROLS = Resources.tooltip("thermal_detonator.controls");

	public ThermalDetonatorItem(Settings settings)
	{
		super(SwgBlocks.Misc.ThermalDetonatorBlock, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		var stack = context.getStack();
		ThermalDetonatorComponent tdc = stack.get(SwgComponents.ThermalDetonator);
		// FIXME: the block pretty much has a duplicate of this code
		if (context.getPlayer().isSneaking() && !tdc.primed())
		{
			var state = context.getWorld().getBlockState(context.getBlockPos());
			if (state.isOf(SwgBlocks.Misc.ThermalDetonatorBlock) && state.get(ThermalDetonatorBlock.CLUSTER_SIZE) < 5)
			{
				context.getWorld().setBlockState(context.getBlockPos(), state.with(ThermalDetonatorBlock.CLUSTER_SIZE, state.get(ThermalDetonatorBlock.CLUSTER_SIZE) + 1));
				context.getStack().decrementUnlessCreative(1, context.getPlayer());
				return ActionResult.SUCCESS;
			}
			return super.useOnBlock(context);
		}
		use(context.getWorld(), context.getPlayer(), context.getHand());
		return ActionResult.PASS;
	}

	public ThermalDetonatorEntity createThermalDetonator(World world, int life, boolean primed, ItemStack stack, LivingEntity player)
	{
		ThermalDetonatorEntity td = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		ThermalDetonatorComponent tdc = stack.get(SwgComponents.ThermalDetonator);
		td.setLife(life);
		td.setPrimed(primed);
		td.setVisible(tdc.shouldRender());
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		return td;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		ThermalDetonatorComponent tdc = stack.get(SwgComponents.ThermalDetonator);
		if (entity.isOnFire())
		{
			PlayerEntity player = (PlayerEntity)entity;
			var tdItem = (ThermalDetonatorItem)stack.getItem();
			ThermalDetonatorEntity tdEnt = tdItem.createThermalDetonator(world, 0, true, stack, player);
			tdEnt.setVisible(false);
			int power = player.getInventory().count(this);
			for (int i = power; i >= 0; i--)
			{
				player.getInventory().removeOne(stack);
			}
			tdEnt.setExplosionPower(power * 2f);
			world.spawnEntity(tdEnt);
			return; // no more detonators left
		}

		if (tdc.primed())
		{
			tdc = tdc.withTicksToExplosion(tdc.ticksToExplosion() - 1);
			stack.set(SwgComponents.ThermalDetonator, tdc);

			if (tdc.ticksToExplosion() > 0)
			{
				stack.set(SwgComponents.ThermalDetonator, tdc);
			}
			else
			{
				PlayerEntity player = (PlayerEntity)entity;
				player.damage(new DamageSource(player.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(SwgDamageTypes.SELF_EXPLODE), player), 100f);
				ThermalDetonatorEntity tdEntity = createThermalDetonator(world, 0, true, stack, player);
				tdEntity.setVisible(false);
				world.spawnEntity(tdEntity);
				stack.decrementUnlessCreative(1, player);
				stack.remove(SwgComponents.ThermalDetonator);
			}
		}

		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated)
	{
		return true;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		ThermalDetonatorComponent tdc = stack.get(SwgComponents.ThermalDetonator);
		ThermalDetonatorEntity thermalDetonatorEntity = createThermalDetonator(world, tdc.ticksToExplosion(), tdc.primed(), stack, user);
		thermalDetonatorEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.0F, 0F);
		thermalDetonatorEntity.setOwner(user);

		world.spawnEntity(thermalDetonatorEntity);

		world.playSound(null, user.getX(), user.getY(), user.getZ(), SwgSounds.Explosives.THERMAL_DETONATOR_THROW, SoundCategory.PLAYERS, 1.0F,
		                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
		stack.decrementUnlessCreative(1, user);
		if (user instanceof PlayerEntity playerEntity)
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		stack.remove(SwgComponents.ThermalDetonator);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		super.appendTooltip(stack, context, tooltip, type);

		var mc = MinecraftClient.getInstance();
		var opt = mc.options;
		tooltip.add(Text.translatable(I18N_TOOLTIP_CONTROLS, TextUtil.stylizeKeybind(opt.attackKey.getBoundKeyLocalizedText()), TextUtil.stylizeKeybind(opt.useKey.getBoundKeyLocalizedText())));
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		return 20000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.NONE;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		user.setCurrentHand(hand);

		return TypedActionResult.consume(itemStack);
	}

	@Override
	public TypedActionResult<ItemStack> useLeft(World world, PlayerEntity user, Hand hand, boolean isRepeatEvent)
	{
		ItemStack stack = user.getStackInHand(hand);
		if (!stack.get(SwgComponents.ThermalDetonator).primed())
		{
			stack.set(SwgComponents.ThermalDetonator, new ThermalDetonatorComponent(true, baseTicksToExplosion, true));
			if (world.isClient())
			{
				user.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_ARM, 1f, 1f);
				SoundHelper.playDetonatorItemSound(user);
			}
		}
		else
		{
			stack.remove(SwgComponents.ThermalDetonator);
			user.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_DISARM, 1f, 1f);
		}

		return TypedActionResult.success(stack);
	}

	@Override
	public boolean allowRepeatedLeftHold(World world, PlayerEntity player, Hand mainHand)
	{
		return false;
	}

	@Override
	public float getCooldownProgress(PlayerEntity player, ItemStack stack, float tickDelta)
	{
		ThermalDetonatorComponent tdc = stack.get(SwgComponents.ThermalDetonator);
		if (tdc.primed())
		{
			return (float)(-baseTicksToExplosion + tdc.ticksToExplosion()) / -baseTicksToExplosion;
		}
		else
		{
			return 0;
		}
	}
}
