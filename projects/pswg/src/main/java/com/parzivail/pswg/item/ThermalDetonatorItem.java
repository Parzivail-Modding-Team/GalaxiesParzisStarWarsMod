package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThermalDetonatorItem extends Item implements ILeftClickConsumer
{
	public ThermalDetonatorItem(Settings settings)
	{
		super(settings);
	}

	public ThermalDetonatorEntity createThermalDetonator(World world, ItemStack stack, int life, boolean primed, PlayerEntity player)
	{
		ThermalDetonatorEntity td = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		td.setLife(life);
		td.setPrimed(primed);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		return td;
	}

	int ticksToExplosion = 10000;
	boolean isPrimed;

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if (isPrimed)
		{
			ticksToExplosion--;
		}
		if (entity.isOnFire()) {
			PlayerEntity player = (PlayerEntity)entity;
			var tdItem = (ThermalDetonatorItem)stack.getItem();
			ThermalDetonatorEntity tdEnt = tdItem.createThermalDetonator(world, stack, 0, true, player);
			tdEnt.shouldRenderVar = false;
			int power = player.getInventory().count(this);
			for (int i = power; i >= 0; i--) {
				player.getInventory().removeOne(stack);
			}
			tdEnt.setExplosionPower(power * 2f);
			world.spawnEntity(tdEnt);
		}

		if ((isPrimed && ticksToExplosion <= 0))
		{
			PlayerEntity player = (PlayerEntity)entity;
			ThermalDetonatorItem tdi = (ThermalDetonatorItem)(stack.getItem() instanceof ThermalDetonatorItem ? stack.getItem() : SwgItems.Explosives.ThermalDetonator);
			ThermalDetonatorEntity thermalDetonatorEntity = tdi.createThermalDetonator(world, stack, 0, true, player);
			thermalDetonatorEntity.shouldRenderVar = false;
			world.spawnEntity(thermalDetonatorEntity);
			ticksToExplosion = 150;
			isPrimed = false;
			player.getItemCooldownManager().set(stack.getItem(), 0);
			if (!player.isCreative())
			{
				stack.decrement(1);
			}
		}
		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
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
					ThermalDetonatorItem ThermalDetonatorItem = (ThermalDetonatorItem)(itemStack.getItem() instanceof ThermalDetonatorItem ? itemStack.getItem() : SwgItems.Explosives.ThermalDetonator);
					ThermalDetonatorEntity thermalDetonatorEntity = ThermalDetonatorItem.createThermalDetonator(world, itemStack, ticksToExplosion / 2, isPrimed, playerEntity);
					thermalDetonatorEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), playerEntity.getRoll(), 1.0F, 1.0F);
					thermalDetonatorEntity.setOwner(playerEntity);

					world.spawnEntity(thermalDetonatorEntity);
					playerEntity.getItemCooldownManager().remove(itemStack.getItem());
					isPrimed = false;
				}

				world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
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
		if (!isPrimed)
		{
			isPrimed = true;
			user.getItemCooldownManager().set(user.getMainHandStack().getItem(), 75);
			ticksToExplosion = 150;
		}
		return TypedActionResult.success(user.getMainHandStack());
	}

	@Override
	public boolean allowRepeatedLeftHold(World world, PlayerEntity player, Hand mainHand)
	{
		return false;
	}
}
