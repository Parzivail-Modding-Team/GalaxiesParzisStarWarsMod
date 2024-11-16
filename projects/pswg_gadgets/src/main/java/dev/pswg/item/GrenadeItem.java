package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.world.TickConstants;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GrenadeItem extends BlockItem implements ILeftClickUsable
{
	public final int baseTicksToExplosion;
	public final Block block;
	public final Item item;
	//public final ExplosionSoundGroup sounds;

	public GrenadeItem(Settings settings, Block block, Item item, int baseTicksToExplosion)
	{
		super(block, settings);
		this.block = block;
		this.item = item;
		//this.sounds = sounds;
		this.baseTicksToExplosion = baseTicksToExplosion;
	}


	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		/*var stack = context.getStack();
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
		return ActionResult.PASS;*/
		return super.useOnBlock(context);
	}

	public void throwEntity(World world, ItemStack stack, PlayerEntity player)
	{

	}

	public void spawnEntity(World world, int power, ItemStack stack ,Entity player)
	{

	}


	public void createExplosion(World world, int power, Entity player)
	{
		if(player instanceof LivingEntity livingEntity)
			spawnEntity(world, power, livingEntity.getMainHandStack(), player);
	}

	;

	public void createExplosion(World world, Entity player)
	{
		createExplosion(world, 4, player);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if(entity instanceof PlayerEntity player && stack.contains(Gadgets.PRIMING_TIME)){
			player.sendMessage(Text.of(""+ (stack.get(Gadgets.PRIMING_TIME)+baseTicksToExplosion - world.getTime())), true);
		}
		if (entity.isOnFire())
		{
			PlayerEntity player = (PlayerEntity)entity;
			var teItem = (GrenadeItem)stack.getItem();
			int power = player.getInventory().count(this);
			for (int i = power; i >= 0; i--)
			{
				player.getInventory().removeOne(stack);
			}
			teItem.createExplosion(world, power * 2, player);
		}

		if (stack.contains(Gadgets.PRIMING_TIME) && world.getTime() >= stack.get(Gadgets.PRIMING_TIME) + baseTicksToExplosion )
		{
			PlayerEntity player = (PlayerEntity)entity;
			if(player.getWorld() instanceof ServerWorld serverWorld)
				player.damage(serverWorld, new DamageSource(player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(DamageTypes.EXPLOSION.getValue()).get()), 40f);
			GrenadeItem tei = (GrenadeItem)(stack.getItem() instanceof GrenadeItem ? stack.getItem() : item);
			createExplosion(world, player);
			player.getItemCooldownManager().set(stack, 0);
			if (!player.isCreative())
			{
				stack.decrement(1);
			}
			stack.remove(Gadgets.PRIMING_TIME);
		}
		
		super.inventoryTick(stack, world, entity, slot, selected);
	}
	/*
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
				GrenadeItem GrenadeItem = (GrenadeItem)(itemStack.getItem() instanceof GrenadeItem ? itemStack.getItem() : item);
				throwEntity(world, tag, itemStack, playerEntity);

				playerEntity.getItemCooldownManager().remove(itemStack.getItem());
				tag.primed = false;

				//sounds.playThrowSound(playerEntity);
				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		tag.ticksToExplosion = baseTicksToExplosion;
		tag.serializeAsSubtag(stack);
	}*/

	@Override
	public int getMaxUseLeftTime(ItemStack stack, LivingEntity user)
	{
		return TickConstants.ONE_HOUR;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		return TickConstants.ONE_HOUR;
	}
	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.NONE;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		user.setCurrentHand(hand);
		throwEntity(world, itemStack, user);

		return ActionResult.CONSUME;
	}

	@Override
	public ActionResult useLeft(World world, LivingEntity user, Hand hand)
	{
		ItemStack stack = user.getMainHandStack();
		if (!stack.contains(Gadgets.PRIMING_TIME))
		{
			if(user instanceof PlayerEntity player){
				player.sendMessage(Text.of("Primed"), true);
			}
			stack.set(Gadgets.PRIMING_TIME, world.getTime());
			if (world.isClient())
			{

				//sounds.playArmSound(user);
				//sounds.playBeepingSound(user);
			}
		}
		else
		{
			stack.remove(Gadgets.PRIMING_TIME);
			//sounds.playDisarmSound(user);
		}
		return ActionResult.SUCCESS;
	}
	/*
	@Override
	public boolean allowRepeatedLeftHold(World world, PlayerEntity player, Hand mainHand)
	{
		return false;
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

	 */
}
