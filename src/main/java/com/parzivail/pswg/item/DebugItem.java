package com.parzivail.pswg.item;

import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
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
//		final ThrownLightsaberEntity entity = new ThrownLightsaberEntity(SwgEntities.Misc.ThrownLightsaber, player, world);
//		entity.setProperties(player, MathHelper.clamp(player.pitch, -89.9f, 89.9f), player.yaw, 0.0F, 0.6f, 0);
//		world.spawnEntity(entity);

		SwgEntityComponents.getPersistent(player).setSpecies(SwgSpeciesRegistry.deserialize("pswg:aqualish/m;pswg:body=beige"));

		return TypedActionResult.consume(player.getStackInHand(hand));
	}
}
