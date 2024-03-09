package com.parzivail.pswg.item;

import com.parzivail.pswg.block.ThermalDetonatorBlock;
import com.parzivail.pswg.container.*;
import com.parzivail.util.item.ICooldownItem;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThrowableExplosiveItem extends BlockItem implements ILeftClickConsumer, IDefaultNbtProvider, ICooldownItem, ICustomVisualItemEquality
{
	public final int baseTicksToExplosion = 150;
	public final Block block;
	public final Item item;
	public final ExplosionSoundGroup sounds;

	public ThrowableExplosiveItem(Settings settings, Block block, Item item, ExplosionSoundGroup sounds)
	{
		super(block, settings);
		this.block = block;
		this.item = item;
		this.sounds = sounds;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		var stack = context.getStack();
		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		if (context.getPlayer().isSneaking() && !tag.primed)
		{
			var state = context.getWorld().getBlockState(context.getBlockPos());
			if (state.isOf(block) && state.get(ThermalDetonatorBlock.CLUSTER_SIZE) < 5)
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

	public abstract void throwEntity(World world, ThrowableExplosiveTag tag, ItemStack stack, PlayerEntity player);

	public abstract void spawnEntity(World world, int power, int life, boolean primed, PlayerEntity player);

	public void createExplosion(World world, int power, PlayerEntity player)
	{
		spawnEntity(world, power, 0, true, player);
	}

	;

	public void createExplosion(World world, PlayerEntity player)
	{
		createExplosion(world, 4, player);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		ThrowableExplosiveTag tag = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		tag.tick();
		if (entity.isOnFire())
		{
			PlayerEntity player = (PlayerEntity)entity;
			var teItem = (ThrowableExplosiveItem)stack.getItem();
			int power = player.getInventory().count(this);
			for (int i = power; i >= 0; i--)
			{
				player.getInventory().removeOne(stack);
			}
			teItem.createExplosion(world, power * 2, player);
		}

		if ((tag.primed && tag.ticksToExplosion <= 0))
		{
			PlayerEntity player = (PlayerEntity)entity;
			player.damage(new DamageSource(player.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(SwgDamageTypes.SELF_EXPLODE), player), 100f);
			ThrowableExplosiveItem tei = (ThrowableExplosiveItem)(stack.getItem() instanceof ThrowableExplosiveItem ? stack.getItem() : item);
			createExplosion(world, player);
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
				ThrowableExplosiveItem throwableExplosiveItem = (ThrowableExplosiveItem)(itemStack.getItem() instanceof ThrowableExplosiveItem ? itemStack.getItem() : item);
				throwEntity(world, tag, itemStack, playerEntity);

				playerEntity.getItemCooldownManager().remove(itemStack.getItem());
				tag.primed = false;

				sounds.playThrowSound(playerEntity);
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
				sounds.playArmSound(user);
				sounds.playBeepingSound(user);
			}
		}
		else
		{
			tag.primed = false;
			sounds.playDisarmSound(user);
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
