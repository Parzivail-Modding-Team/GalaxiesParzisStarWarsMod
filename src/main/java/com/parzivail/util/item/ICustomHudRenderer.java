package com.parzivail.util.item;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.HashMap;

public interface ICustomHudRenderer
{
	/**
	 * Do not use this directly.
	 */
	HashMap<Item, ICustomHudRenderer> CUSTOM_HUD_RENDERERS = new HashMap<>();

	static void registerCustomHUD(Item item, ICustomHudRenderer customHUDRenderer)
	{
		CUSTOM_HUD_RENDERERS.put(item, customHUDRenderer);
	}

	boolean renderCustomHUD(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices);
}
