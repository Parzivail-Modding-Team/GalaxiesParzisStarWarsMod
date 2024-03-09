package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.FragmentationGrenadeBlock;
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

public class FragmentationGrenadeItem extends ThrowableExplosiveItem implements ILeftClickConsumer, IDefaultNbtProvider, ICooldownItem, ICustomVisualItemEquality
{
	public final int baseTicksToExplosion = 150;
	@TarkinLang
	public static final String I18N_TOOLTIP_CONTROLS = Resources.tooltip("fragmentation_grenade.controls");

	public FragmentationGrenadeItem(Settings settings)
	{
		super(settings, SwgBlocks.Misc.FragmentationGrenadeBlock, SwgItems.Explosives.FragmentationGrenade, new SonicImploderSoundGroup());
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		var stack = context.getStack();
		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		if (context.getPlayer().isSneaking() && !tag.primed)
		{
			var state = context.getWorld().getBlockState(context.getBlockPos());
			if (state.isOf(SwgBlocks.Misc.FragmentationGrenadeBlock) && state.get(FragmentationGrenadeBlock.CLUSTER_SIZE) < 5)
			{
				context.getWorld().setBlockState(context.getBlockPos(), state.with(FragmentationGrenadeBlock.CLUSTER_SIZE, state.get(FragmentationGrenadeBlock.CLUSTER_SIZE) + 1));
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

	@Override
	public void throwEntity(World world, ThrowableExplosiveTag tag, ItemStack stack, PlayerEntity player)
	{

	}

	@Override
	public void spawnEntity(World world, int power, int life, boolean primed, PlayerEntity player)
	{

	}

	public FragmentationGrenadeEntity createFragmentationGrenade(World world, int life, boolean primed, ItemStack stack, PlayerEntity player)
	{
		FragmentationGrenadeEntity td = new FragmentationGrenadeEntity(SwgEntities.Misc.FragmentationGrenade, world);
		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		td.setLife(life);
		td.setPrimed(primed);
		td.setVisible(tag.shouldRender);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		return td;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		tag.tick();
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

		if ((tag.primed && tag.ticksToExplosion <= 0))
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
			tag.ticksToExplosion = baseTicksToExplosion;
			tag.shouldRender = true;
			tag.primed = false;
		}

		tag.serializeAsSubtag(stack);
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

		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		if (user instanceof PlayerEntity playerEntity)
		{
			boolean inCreative = playerEntity.getAbilities().creativeMode;
			ItemStack itemStack = playerEntity.getStackInHand(Hand.MAIN_HAND);
			if (!itemStack.isEmpty())
			{
				FragmentationGrenadeItem fragmentationGrenadeItem = (FragmentationGrenadeItem)(itemStack.getItem() instanceof FragmentationGrenadeItem ? itemStack.getItem() : SwgItems.Explosives.FragmentationGrenade);
				FragmentationGrenadeEntity fragmentationGrenadeEntity = fragmentationGrenadeItem.createFragmentationGrenade(world, tag.ticksToExplosion, tag.primed, itemStack, playerEntity);
				fragmentationGrenadeEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), playerEntity.getRoll(), 1.0F, 0F);
				fragmentationGrenadeEntity.setOwner(playerEntity);

				world.spawnEntity(fragmentationGrenadeEntity);
				playerEntity.getItemCooldownManager().remove(itemStack.getItem());
				tag.primed = false;

				world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SwgSounds.Explosives.THERMAL_DETONATOR_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		tag.ticksToExplosion = baseTicksToExplosion;
		tag.serializeAsSubtag(stack);
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
		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(user.getMainHandStack().getOrCreateNbt());
		if (!tag.primed)
		{
			tag.primed = true;
			tag.ticksToExplosion = baseTicksToExplosion;
			if (world.isClient())
			{
				user.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_ARM, 1f, 1f);
				SoundHelper.playDetonatorItemSound(user);
			}
		}
		else
		{
			tag.primed = false;
			user.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_DISARM, 1f, 1f);
		}

		tag.serializeAsSubtag(user.getMainHandStack());
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
		return new ThrowableExplosiveTag().toSubtag();
	}

	@Override
	public float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta)
	{
		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		if (tag.primed)
		{
			return (float)(-baseTicksToExplosion + tag.ticksToExplosion) / -baseTicksToExplosion;
		}
		else
		{
			return 0;
		}
	}
}
