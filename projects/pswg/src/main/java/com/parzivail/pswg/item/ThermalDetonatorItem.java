package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgDamageTypes;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.item.ICooldownItem;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThermalDetonatorItem extends Item implements ILeftClickConsumer, IDefaultNbtProvider, ICooldownItem
{
	@TarkinLang
	public static final String I18N_TOOLTIP_CONTROLS = Resources.tooltip("thermal_detonator.controls");

	public ThermalDetonatorItem(Settings settings)
	{
		super(settings);
	}

	public ThermalDetonatorEntity createThermalDetonator(World world, int life, boolean primed, ItemStack stack, PlayerEntity player)
	{
		ThermalDetonatorEntity td = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		ThermalDetonatorTag tdt = new ThermalDetonatorTag(stack.getOrCreateNbt());
		td.setLife(life);
		td.setPrimed(primed);
		td.setVisible(tdt.shouldRender);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		return td;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		ThermalDetonatorTag tdt = new ThermalDetonatorTag(stack.getOrCreateNbt());
		tdt.tick();
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
		}

		if ((tdt.primed && tdt.ticksToExplosion <= 0))
		{
			PlayerEntity player = (PlayerEntity)entity;
			player.damage(new DamageSource(player.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(SwgDamageTypes.SELF_EXPLODE), player), 100f);
			ThermalDetonatorItem tdi = (ThermalDetonatorItem)(stack.getItem() instanceof ThermalDetonatorItem ? stack.getItem() : SwgItems.Explosives.ThermalDetonator);
			ThermalDetonatorEntity tdEntity = tdi.createThermalDetonator(world, 0, true, stack, player);
			tdEntity.setVisible(false);
			world.spawnEntity(tdEntity);
			player.getItemCooldownManager().set(stack.getItem(), 0);
			if (!player.isCreative())
			{
				stack.decrement(1);
			}
			tdt.ticksToExplosion = 150;
			tdt.shouldRender = true;
			tdt.primed = false;
		}

		tdt.serializeAsSubtag(stack);
		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		ThermalDetonatorTag tdt = new ThermalDetonatorTag(stack.getOrCreateNbt());
		if (user instanceof PlayerEntity playerEntity)
		{
			boolean inCreative = playerEntity.getAbilities().creativeMode;
			ItemStack itemStack = playerEntity.getStackInHand(Hand.MAIN_HAND);
			if (!itemStack.isEmpty() || inCreative)
			{
				if (itemStack.isEmpty())
				{
					itemStack = new ItemStack(SwgItems.Explosives.ThermalDetonator);
				}
				if (!world.isClient)
				{
					ThermalDetonatorItem thermalDetonatorItem = (ThermalDetonatorItem)(itemStack.getItem() instanceof ThermalDetonatorItem ? itemStack.getItem() : SwgItems.Explosives.ThermalDetonator);
					ThermalDetonatorEntity thermalDetonatorEntity = thermalDetonatorItem.createThermalDetonator(world, tdt.ticksToExplosion, tdt.primed, itemStack, playerEntity);
					thermalDetonatorEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), playerEntity.getRoll(), 1.0F, 0F);
					thermalDetonatorEntity.setOwner(playerEntity);

					world.spawnEntity(thermalDetonatorEntity);
					playerEntity.getItemCooldownManager().remove(itemStack.getItem());
					tdt.primed = false;
				}

				world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		tdt.ticksToExplosion = 150;
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
		ThermalDetonatorTag tdt = new ThermalDetonatorTag(user.getMainHandStack().getOrCreateNbt());
		if (!tdt.primed)
		{
			tdt.primed = true;
			tdt.ticksToExplosion = 150;
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
		return new ThermalDetonatorTag().toSubtag();
	}

	@Override
	public float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta)
	{
		ThermalDetonatorTag tdt = new ThermalDetonatorTag(stack.getOrCreateNbt());
		if (tdt.primed)
		{
			return (float)(-150 + tdt.ticksToExplosion) / -150;
		}
		else
		{
			return 0;
		}
	}
}
