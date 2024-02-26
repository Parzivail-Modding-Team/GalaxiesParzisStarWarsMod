package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.ThermalDetonatorBlock;
import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.entity.FragmentationGrenadeEntity;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.item.ICooldownItem;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FragmentationGrenadeItem extends BlockItem implements ILeftClickConsumer, IDefaultNbtProvider, ICooldownItem, ICustomVisualItemEquality
{
	public final int baseTicksToExplosion = 150;
	@TarkinLang
	public static final String I18N_TOOLTIP_CONTROLS = Resources.tooltip("fragmentation_grenade.controls");

	public FragmentationGrenadeItem(Settings settings)
	{
		super(SwgBlocks.Misc.ThermalDetonatorBlock, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		var stack = context.getStack();
		FragmentationGrenadeTag tdt = new FragmentationGrenadeTag(stack.getOrCreateNbt());
		if (context.getPlayer().isSneaking() && !tdt.primed)
		{
			var state = context.getWorld().getBlockState(context.getBlockPos());
			if (state.isOf(SwgBlocks.Misc.ThermalDetonatorBlock) && state.get(ThermalDetonatorBlock.CLUSTER_SIZE) < 5)
			{
				context.getWorld().setBlockState(context.getBlockPos(), state.with(ThermalDetonatorBlock.CLUSTER_SIZE, state.get(ThermalDetonatorBlock.CLUSTER_SIZE) + 1));
				if (!context.getPlayer().isCreative())
				{
					context.getStack().decrement(1);
				}
				return ActionResult.SUCCESS;
			}
			return super.useOnBlock(context);
		}
		use(context.getWorld(), context.getPlayer(), context.getHand());
		return ActionResult.PASS;
	}

	public FragmentationGrenadeEntity createFragmentationGrenade(World world, int life, boolean primed, ItemStack stack, PlayerEntity player)
	{
		FragmentationGrenadeEntity td = new FragmentationGrenadeEntity(SwgEntities.Misc.FragmentationGrenade, world);
		FragmentationGrenadeTag tdt = new FragmentationGrenadeTag(stack.getOrCreateNbt());
		td.setLife(life);
		td.setPrimed(primed);
		td.setVisible(tdt.shouldRender);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		return td;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		FragmentationGrenadeTag tdt = new FragmentationGrenadeTag(stack.getOrCreateNbt());
		tdt.tick();
		if (entity.isOnFire())
		{
			PlayerEntity player = (PlayerEntity)entity;
			var tdItem = (FragmentationGrenadeItem)stack.getItem();
			FragmentationGrenadeEntity fgEnt = tdItem.createFragmentationGrenade(world, 0, true, stack, player);
			fgEnt.setVisible(false);
			int power = player.getInventory().count(this);
			for (int i = power; i >= 0; i--)
			{
				player.getInventory().removeOne(stack);
			}
			fgEnt.setExplosionPower(power * 2f);
			world.spawnEntity(fgEnt);
		}

		if ((tdt.primed && tdt.ticksToExplosion <= 0))
		{
			PlayerEntity player = (PlayerEntity)entity;
			player.damage(new DamageSource(player.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(SwgDamageTypes.SELF_EXPLODE), player), 100f);
			FragmentationGrenadeItem tdi = (FragmentationGrenadeItem)(stack.getItem() instanceof FragmentationGrenadeItem ? stack.getItem() : SwgItems.Explosives.FragmentationGrenade);
			FragmentationGrenadeEntity tdEntity = tdi.createFragmentationGrenade(world, 0, true, stack, player);
			tdEntity.setVisible(false);
			world.spawnEntity(tdEntity);
			player.getItemCooldownManager().set(stack.getItem(), 0);
			if (!player.isCreative())
			{
				stack.decrement(1);
			}
			tdt.ticksToExplosion = baseTicksToExplosion;
			tdt.shouldRender = true;
			tdt.primed = false;
		}

		tdt.serializeAsSubtag(stack);
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

		FragmentationGrenadeTag tdt = new FragmentationGrenadeTag(stack.getOrCreateNbt());
		if (user instanceof PlayerEntity playerEntity)
		{
			boolean inCreative = playerEntity.getAbilities().creativeMode;
			ItemStack itemStack = playerEntity.getStackInHand(Hand.MAIN_HAND);
			if (!itemStack.isEmpty())
			{
				FragmentationGrenadeItem fragmentationGrenadeItem = (FragmentationGrenadeItem)(itemStack.getItem() instanceof FragmentationGrenadeItem ? itemStack.getItem() : SwgItems.Explosives.FragmentationGrenade);
				FragmentationGrenadeEntity fragmentationGrenadeEntity = fragmentationGrenadeItem.createFragmentationGrenade(world, tdt.ticksToExplosion, tdt.primed, itemStack, playerEntity);
				fragmentationGrenadeEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), playerEntity.getRoll(), 1.0F, 0F);
				fragmentationGrenadeEntity.setOwner(playerEntity);

				world.spawnEntity(fragmentationGrenadeEntity);
				playerEntity.getItemCooldownManager().remove(itemStack.getItem());
				tdt.primed = false;

				world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SwgSounds.Explosives.THERMAL_DETONATOR_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		tdt.ticksToExplosion = baseTicksToExplosion;
		tdt.serializeAsSubtag(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);

		var mc = MinecraftClient.getInstance();
		var opt = mc.options;
		tooltip.add(Text.translatable(I18N_TOOLTIP_CONTROLS, TextUtil.stylizeKeybind(opt.attackKey.getBoundKeyLocalizedText()), TextUtil.stylizeKeybind(opt.useKey.getBoundKeyLocalizedText())));
	}

	@Override
	public int getMaxUseTime(ItemStack stack)
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
		FragmentationGrenadeTag tdt = new FragmentationGrenadeTag(user.getMainHandStack().getOrCreateNbt());
		if (!tdt.primed)
		{
			tdt.primed = true;
			tdt.ticksToExplosion = baseTicksToExplosion;
			if (world.isClient())
			{
				user.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_ARM, 1f, 1f);
				SoundHelper.playDetonatorItemSound(user);
			}
		}
		else
		{
			tdt.primed = false;
			user.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_DISARM, 1f, 1f);
		}

		tdt.serializeAsSubtag(user.getMainHandStack());
		return TypedActionResult.success(user.getMainHandStack());
	}

	@Override
	public boolean allowRepeatedLeftHold(World world, PlayerEntity player, Hand mainHand)
	{
		return false;
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		return new FragmentationGrenadeTag().toSubtag();
	}

	@Override
	public float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta)
	{
		FragmentationGrenadeTag tdt = new FragmentationGrenadeTag(stack.getOrCreateNbt());
		if (tdt.primed)
		{
			return (float)(-baseTicksToExplosion + tdt.ticksToExplosion) / -baseTicksToExplosion;
		}
		else
		{
			return 0;
		}
	}
}
