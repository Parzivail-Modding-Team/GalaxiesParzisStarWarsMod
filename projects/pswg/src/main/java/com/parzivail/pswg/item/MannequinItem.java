package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.MannequinEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Consumer;

public class MannequinItem extends Item
{
	public MannequinItem(Item.Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		Direction direction = context.getSide();
		if (direction == Direction.DOWN)
			return ActionResult.FAIL;
		else
		{
			World world = context.getWorld();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			ItemStack itemStack = context.getStack();
			Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
			Box box = SwgEntities.Misc.Mannequin.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
			if (world.isSpaceEmpty(null, box) && world.getOtherEntities(null, box).isEmpty())
			{
				if (world instanceof ServerWorld serverWorld)
				{
					Consumer<MannequinEntity> consumer = EntityType.copier(serverWorld, itemStack, context.getPlayer());
					MannequinEntity mannequin = SwgEntities.Misc.Mannequin.create(serverWorld, itemStack.getNbt(), consumer, blockPos, SpawnReason.SPAWN_EGG, true, true);
					if (mannequin == null)
						return ActionResult.FAIL;

					float f = (float)MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
					mannequin.refreshPositionAndAngles(mannequin.getX(), mannequin.getY(), mannequin.getZ(), f, 0.0F);
					serverWorld.spawnEntityAndPassengers(mannequin);
					world.playSound(
							null, mannequin.getX(), mannequin.getY(), mannequin.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F
					);
					mannequin.emitGameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
				}

				itemStack.decrement(1);
				return ActionResult.success(world.isClient);
			}
			else
			{
				return ActionResult.FAIL;
			}
		}
	}
}
