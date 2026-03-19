package dev.pswg.item;

import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.world.TickConstants;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TripwireMineItem extends Item
{
	public TripwireMineItem(Properties settings)
	{
		super(settings);
	}

	public void throwEntity(Level world, Player player)
	{
		TripwireMineEntity mine = GadgetsEntities.TRIPWIRE_MINE_ENTITY.create(world, EntitySpawnReason.EVENT);

		mine.recreateFromPacket(new ClientboundAddEntityPacket(mine.getId(), mine.getUUID(), player.getX(), player.getY() + 1.5, player.getZ(), -player.getXRot(), -player.getYRot(), mine.getType(), 0, Vec3.ZERO, player.getYHeadRot()));
		mine.setOwner(player);
		mine.setDeltaMovement(player.getForward().x * 0.6, player.getForward().y * 0.4, player.getForward().z * 0.6);

		world.addFreshEntity(mine);
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
				throwEntity(world, playerEntity);

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
				throwEntity(world, playerEntity);

				if (!inCreative)
				{
					stack.shrink(1);
				}
				playerEntity.awardStat(Stats.ITEM_USED.get(this));
			}
		}
		return super.releaseUsing(stack, world, user, remainingUseTicks);
	}
}
