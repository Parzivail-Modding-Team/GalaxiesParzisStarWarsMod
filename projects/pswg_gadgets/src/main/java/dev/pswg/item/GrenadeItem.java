package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.world.TickConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class GrenadeItem extends Item implements ILeftClickUsable, ProjectileItem
{
	public final int baseTicksToExplosion;
	public final Item item;
	public final ExplosionSoundGroup sounds;

	public GrenadeItem(Item.Settings settings, Item item, int baseTicksToExplosion, ExplosionSoundGroup sounds)
	{
		super(settings);
		this.item = item;
		this.sounds = sounds;
		this.baseTicksToExplosion = baseTicksToExplosion;
	}

	public abstract EntityType<? extends GrenadeEntity> getEntityType();

	/**
	 * Method called when a grenade is thrown by a player, not to be confused with spawnEntity
	 */
	public void throwEntity(World world, ItemStack stack, PlayerEntity player)
	{
		GrenadeEntity grenade = getEntityType().create(world, SpawnReason.EVENT);
		if (stack.contains(GadgetsItems.Components.PRIMING_TIME))
		{
			// By checking if the stack contains PRIMING_TIME, it's impossible to get an NPE
			grenade.setLife((int)(stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion - world.getTime()));
			grenade.setPrimed(true);
		}
		else
		{
			grenade.setLife(1);
			grenade.setPrimed(false);
		}
		grenade.setVisible(true);
		grenade.onSpawnPacket(new EntitySpawnS2CPacket(grenade.getId(), grenade.getUuid(), player.getX(), player.getY() + 1.5, player.getZ(), -player.getPitch(), -player.getYaw(), grenade.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		grenade.setOwner(player);
		grenade.setVelocity(player, player.getPitch(), player.getYaw(), (float)player.getRotationVector().z * 10, 1.0F, 0F);

		world.spawnEntity(grenade);

		if (world.isClient())
			sounds.playThrowSound(player);
	}

	/**
	 * Method called when a grenade explodes in inventory or through a grenade block, not to be confused with throwEntity
	 */
	public void spawnEntity(World world, int power, ItemStack stack, Entity player)
	{
		GrenadeEntity grenade = getEntityType().create(world, SpawnReason.EVENT);
		grenade.setLife(stack.contains(GadgetsItems.Components.PRIMING_TIME) ? (int)(stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion - world.getTime()) : 1);
		grenade.setPrimed(stack.contains(GadgetsItems.Components.PRIMING_TIME));
		grenade.setExplosionPower(power);
		grenade.onSpawnPacket(new EntitySpawnS2CPacket(grenade.getId(), grenade.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), grenade.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		world.spawnEntity(grenade);
	}

	public void createExplosion(World world, int power, Entity player)
	{
		if(player instanceof LivingEntity livingEntity)
			spawnEntity(world, power, livingEntity.getMainHandStack(), player);
	}

	public void createExplosion(World world, Entity player)
	{
		createExplosion(world, 4, player);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if (entity instanceof PlayerEntity player && stack.contains(GadgetsItems.Components.PRIMING_TIME))
		{
			player.sendMessage(Text.of("" + (stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion - world.getTime())), true);
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

		if (stack.contains(GadgetsItems.Components.PRIMING_TIME) && world.getTime() >= stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion)
		{
			PlayerEntity player = (PlayerEntity)entity;
			if(player.getWorld() instanceof ServerWorld serverWorld)
				player.damage(serverWorld, new DamageSource(player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(DamageTypes.EXPLOSION.getValue()).get()), 40f);
			GrenadeItem tei = (GrenadeItem)(stack.getItem() instanceof GrenadeItem ? stack.getItem() : item);
			createExplosion(world, player);
			if (!player.isCreative())
			{
				stack.decrement(1);
			}
			stack.remove(GadgetsItems.Components.PRIMING_TIME);
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
		ItemStack stack = user.getStackInHand(hand);
		if (user instanceof PlayerEntity playerEntity)
		{
			boolean inCreative = playerEntity.getAbilities().creativeMode;
			ItemStack itemStack = playerEntity.getStackInHand(Hand.MAIN_HAND);
			if (!itemStack.isEmpty())
			{
				GrenadeItem throwableExplosiveItem = (GrenadeItem)(itemStack.getItem() instanceof GrenadeItem ? itemStack.getItem() : item);
				throwEntity(world, itemStack, playerEntity);

				//playerEntity.getItemCooldownManager().remove(itemStack.getItem().);
				stack.remove(GadgetsItems.Components.PRIMING_TIME);

				//sounds.playThrowSound(playerEntity);
				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		return ActionResult.CONSUME;
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if (user instanceof PlayerEntity playerEntity)
		{
			boolean inCreative = playerEntity.getAbilities().creativeMode;
			ItemStack itemStack = playerEntity.getStackInHand(Hand.MAIN_HAND);
			if (!itemStack.isEmpty())
			{
				GrenadeItem throwableExplosiveItem = (GrenadeItem)(itemStack.getItem() instanceof GrenadeItem ? itemStack.getItem() : item);
				throwEntity(world, itemStack, playerEntity);

				//playerEntity.getItemCooldownManager().remove(itemStack.getItem().);
				stack.remove(GadgetsItems.Components.PRIMING_TIME);

				//sounds.playThrowSound(playerEntity);
				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		return super.onStoppedUsing(stack, world, user, remainingUseTicks);
	}

	@Override
	public ActionResult useLeft(World world, LivingEntity user, Hand hand)
	{
		ItemStack stack = user.getMainHandStack();
		if (!stack.contains(GadgetsItems.Components.PRIMING_TIME))
		{

			stack.set(GadgetsItems.Components.PRIMING_TIME, world.getTime());
			if (world.isClient())
			{
				sounds.playArmSound(user);
				//sounds.playBeepingSound(user);
			}
		}
		else
		{
			if (world.isClient())
				sounds.playDisarmSound(user);
			stack.remove(GadgetsItems.Components.PRIMING_TIME);
			//sounds.playDisarmSound(user);
		}
		return ActionResult.SUCCESS;
	}

	/**
	 * methods used for dispenser behavior
	 * createEntity should be overwritten, or a NPE will be caused
	 */
	@Override
	public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction)
	{
		GrenadeEntity grenade = getEntityType().create(world, SpawnReason.EVENT);
		initializeProjectile(grenade, pos.getX(), pos.getY(), pos.getZ(), 1f, 0);
		return grenade;
	}

	@Override
	public ProjectileItem.Settings getProjectileSettings()
	{
		return ProjectileItem.super.getProjectileSettings();
	}

	@Override
	public void initializeProjectile(ProjectileEntity entity, double x, double y, double z, float power, float uncertainty)
	{
		GrenadeEntity grenade = (GrenadeEntity)entity;
		grenade.setLife(baseTicksToExplosion);
		grenade.setPrimed(true);
		grenade.setPos(x, y, z);
		ProjectileItem.super.initializeProjectile(entity, x, y, z, power, uncertainty);
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
