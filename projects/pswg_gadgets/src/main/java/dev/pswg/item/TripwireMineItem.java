package dev.pswg.item;

import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.world.TickConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TripwireMineItem extends Item
{
	public TripwireMineItem(Settings settings)
	{
		super(settings);
	}

	public void throwEntity(World world, PlayerEntity player)
	{
		TripwireMineEntity mine = GadgetsEntities.TRIPWIRE_MINE_ENTITY.create(world, SpawnReason.EVENT);

		mine.onSpawnPacket(new EntitySpawnS2CPacket(mine.getId(), mine.getUuid(), player.getX(), player.getY() + 1.5, player.getZ(), -player.getPitch(), -player.getYaw(), mine.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		mine.setOwner(player);
		mine.setVelocity(player.getRotationVecClient().x * 0.6, player.getRotationVecClient().y * 0.4, player.getRotationVecClient().z * 0.6);

		world.spawnEntity(mine);
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
				throwEntity(world, playerEntity);

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
				throwEntity(world, playerEntity);

				if (!inCreative)
				{
					stack.decrement(1);
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
		return super.onStoppedUsing(stack, world, user, remainingUseTicks);
	}
}
