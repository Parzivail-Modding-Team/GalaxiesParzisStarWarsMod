package com.parzivail.util.item;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface ILeftClickConsumer
{
	TypedActionResult<ItemStack> useLeft(World world, PlayerEntity user, Hand hand);

	boolean allowRepeatedLeftHold(World world, ClientPlayerEntity player, Hand mainHand);
}
