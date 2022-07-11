package com.parzivail.pswg.item;

import net.minecraft.block.FluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Objects;

public class SpawnEntityItem extends Item
{
	private final EntityType<?> type;
	private final int yShift;

	public SpawnEntityItem(EntityType<?> type, Item.Settings settings, int yShift)
	{
		super(settings);
		this.type = type;
		this.yShift = yShift;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		var world = context.getWorld();
		if (!(world instanceof ServerWorld))
		{
			return ActionResult.SUCCESS;
		}
		else
		{
			var itemStack = context.getStack();
			var blockPos = context.getBlockPos();
			var direction = context.getSide();
			var blockState = world.getBlockState(blockPos);

			BlockPos blockPos3;
			if (blockState.getCollisionShape(world, blockPos).isEmpty())
				blockPos3 = blockPos;
			else
				blockPos3 = blockPos.offset(direction);

			blockPos3 = blockPos3.add(0, yShift, 0);

			if (this.type.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockPos3, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos3) && direction == Direction.UP) != null)
			{
				itemStack.decrement(1);
			}

			return ActionResult.CONSUME;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		var itemStack = user.getStackInHand(hand);
		var hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
		if (hitResult.getType() != HitResult.Type.BLOCK)
		{
			return TypedActionResult.pass(itemStack);
		}
		else if (!(world instanceof ServerWorld))
		{
			return TypedActionResult.success(itemStack);
		}
		else
		{
			var blockHitResult = hitResult;
			var blockPos = blockHitResult.getBlockPos();
			if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock))
			{
				return TypedActionResult.pass(itemStack);
			}
			else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack))
			{
				if (this.type.spawnFromItemStack((ServerWorld)world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false) == null)
				{
					return TypedActionResult.pass(itemStack);
				}
				else
				{
					if (!user.getAbilities().creativeMode)
						itemStack.decrement(1);

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					return TypedActionResult.consume(itemStack);
				}
			}
			else
			{
				return TypedActionResult.fail(itemStack);
			}
		}
	}
}
