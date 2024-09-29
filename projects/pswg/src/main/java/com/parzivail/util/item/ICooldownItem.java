package com.parzivail.util.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ICooldownItem
{
	float getCooldownProgress(PlayerEntity player, ItemStack stack, float tickDelta);
}
