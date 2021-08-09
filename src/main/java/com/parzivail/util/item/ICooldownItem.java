package com.parzivail.util.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICooldownItem
{
	float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta);
}
