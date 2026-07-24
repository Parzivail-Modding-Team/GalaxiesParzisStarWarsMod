package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.item.grenades.soundGroups.ExplosionSoundGroup;
import dev.pswg.item.ILeftClickUsable;
import dev.pswg.world.TickConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class GrenadeItem extends Item implements ILeftClickUsable, ProjectileItem
{
	public final int baseTicksToExplosion;
	public final Item item;
	public final ExplosionSoundGroup sounds;

	public GrenadeItem(Item.Properties settings, Item item, int baseTicksToExplosion, ExplosionSoundGroup sounds)
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
	public void throwEntity(Level world, ItemStack stack, Player player)
	{
		GrenadeEntity grenade = getEntityType().create(world, EntitySpawnReason.EVENT);
		if (stack.has(GadgetsItems.Components.PRIMING_TIME))
		{
			// By checking if the stack contains PRIMING_TIME, it's impossible to get an NPE
			grenade.setLife((int)(stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion - world.getGameTime()));
			grenade.setPrimed(true);
		}
		else
		{
			grenade.setLife(1);
			grenade.setPrimed(false);
		}
		grenade.setVisible(true);
		grenade.recreateFromPacket(new ClientboundAddEntityPacket(grenade.getId(), grenade.getUUID(), player.getX(), player.getY() + 1.5, player.getZ(), -player.getXRot(), -player.getYRot(), grenade.getType(), 0, Vec3.ZERO, player.getYHeadRot()));
		grenade.setOwner(player);
		grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), (float)player.getLookAngle().z * 10, 1.0F, 0F);

		world.addFreshEntity(grenade);

		if (world.isClientSide())
			sounds.playThrowSound(player);
	}

	/**
	 * Method called when a grenade explodes in inventory or through a grenade block, not to be confused with throwEntity
	 */
	public void spawnEntity(Level world, int power, ItemStack stack, Entity player)
	{
		GrenadeEntity grenade = getEntityType().create(world, EntitySpawnReason.EVENT);
		grenade.setLife(stack.has(GadgetsItems.Components.PRIMING_TIME) ? (int)(stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion - world.getGameTime()) : 1);
		grenade.setPrimed(stack.has(GadgetsItems.Components.PRIMING_TIME));
		grenade.setExplosionPower(power);
		grenade.recreateFromPacket(new ClientboundAddEntityPacket(grenade.getId(), grenade.getUUID(), player.getX(), player.getY() + 1, player.getZ(), -player.getXRot(), -player.getYRot(), grenade.getType(), 0, Vec3.ZERO, player.getYHeadRot()));
		world.addFreshEntity(grenade);
	}

	public void createExplosion(Level world, int power, Entity player)
	{
		if(player instanceof LivingEntity livingEntity)
			spawnEntity(world, power, livingEntity.getMainHandItem(), player);
	}

	public void createExplosion(Level world, Entity player)
	{
		createExplosion(world, 4, player);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot)
	{
		if (entity instanceof Player player && stack.has(GadgetsItems.Components.PRIMING_TIME))
		{
			player.sendOverlayMessage(Component.nullToEmpty("" + (stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion - world.getGameTime())));
		}
		if (entity.isOnFire())
		{
			Player player = (Player)entity;
			var teItem = (GrenadeItem)stack.getItem();
			int power = player.getInventory().countItem(this);
			for (int i = power; i >= 0; i--)
			{
				player.getInventory().removeItem(stack);
			}
			teItem.createExplosion(world, power * 2, player);
		}

		if (stack.has(GadgetsItems.Components.PRIMING_TIME) && world.getGameTime() >= stack.get(GadgetsItems.Components.PRIMING_TIME) + baseTicksToExplosion)
		{
			Player player = (Player)entity;
			createExplosion(world, player);
			if (!player.isCreative())
			{
				stack.shrink(1);
			}
			stack.remove(GadgetsItems.Components.PRIMING_TIME);
		}
		super.inventoryTick(stack, world, entity, slot);
	}

	@Override
	public int getMaxUseLeftTime(ItemStack stack, LivingEntity user)
	{
		return TickConstants.ONE_HOUR;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user)
	{
		return TickConstants.ONE_HOUR;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack)
	{
		return ItemUseAnimation.NONE;
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand)
	{
		ItemStack stack = user.getItemInHand(hand);
		if (user instanceof Player playerEntity)
		{
			boolean inCreative = playerEntity.getAbilities().instabuild;
			ItemStack itemStack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
			if (!itemStack.isEmpty())
			{
				GrenadeItem throwableExplosiveItem = (GrenadeItem)(itemStack.getItem() instanceof GrenadeItem ? itemStack.getItem() : item);
				throwEntity(world, itemStack, playerEntity);

				//playerEntity.getItemCooldownManager().remove(itemStack.getItem().);
				stack.remove(GadgetsItems.Components.PRIMING_TIME);

				//sounds.playThrowSound(playerEntity);
				if (!inCreative)
				{
					stack.shrink(1);
				}
				playerEntity.awardStat(Stats.ITEM_USED.get(this));
			}
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks)
	{
		if (user instanceof Player playerEntity)
		{
			boolean inCreative = playerEntity.getAbilities().instabuild;
			ItemStack itemStack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
			if (!itemStack.isEmpty())
			{
				GrenadeItem throwableExplosiveItem = (GrenadeItem)(itemStack.getItem() instanceof GrenadeItem ? itemStack.getItem() : item);
				throwEntity(world, itemStack, playerEntity);

				//playerEntity.getItemCooldownManager().remove(itemStack.getItem().);
				stack.remove(GadgetsItems.Components.PRIMING_TIME);

				//sounds.playThrowSound(playerEntity);
				if (!inCreative)
				{
					stack.shrink(1);
				}
				playerEntity.awardStat(Stats.ITEM_USED.get(this));
			}
		}
		return super.releaseUsing(stack, world, user, remainingUseTicks);
	}

	@Override
	public InteractionResult useLeft(Level world, LivingEntity user, InteractionHand hand, boolean repeatEvent)
	{
		ItemStack stack = user.getMainHandItem();
		if (!stack.has(GadgetsItems.Components.PRIMING_TIME))
		{

			stack.set(GadgetsItems.Components.PRIMING_TIME, world.getGameTime());
			if (world.isClientSide())
			{
				sounds.playArmSound(user);
				//sounds.playBeepingSound(user);
			}
		}
		else
		{
			if (world.isClientSide())
				sounds.playDisarmSound(user);
			stack.remove(GadgetsItems.Components.PRIMING_TIME);
			//sounds.playDisarmSound(user);
		}
		return InteractionResult.SUCCESS;
	}

	/**
	 * methods used for dispenser behavior
	 * createEntity should be overwritten, or a NPE will be caused
	 */
	@Override
	public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction)
	{
		GrenadeEntity grenade = getEntityType().create(world, EntitySpawnReason.EVENT);
		shoot(grenade, pos.x(), pos.y(), pos.z(), 1f, 0);
		return grenade;
	}

	@Override
	public ProjectileItem.DispenseConfig createDispenseConfig()
	{
		return ProjectileItem.super.createDispenseConfig();
	}

	@Override
	public void shoot(Projectile entity, double x, double y, double z, float power, float uncertainty)
	{
		GrenadeEntity grenade = (GrenadeEntity)entity;
		grenade.setLife(baseTicksToExplosion);
		grenade.setPrimed(true);
		grenade.setPosRaw(x, y, z);
		ProjectileItem.super.shoot(entity, x, y, z, power, uncertainty);
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
