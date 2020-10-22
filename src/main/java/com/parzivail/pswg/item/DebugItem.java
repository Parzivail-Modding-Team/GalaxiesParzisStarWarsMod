package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DebugItem extends Item
{
	public DebugItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		final ThrownLightsaberEntity entity = new ThrownLightsaberEntity(SwgEntities.Misc.ThrownLightsaber, player, world);
		entity.setProperties(player, player.pitch, player.yaw, 0.0F, 0.6f, 0);
		world.spawnEntity(entity);

		return TypedActionResult.consume(player.getStackInHand(hand));
	}
}
