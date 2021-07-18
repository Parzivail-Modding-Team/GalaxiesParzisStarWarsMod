package com.parzivail.util.client.render;

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

	static void register(Item item, ICustomHudRenderer renderer)
	{
		CUSTOM_HUD_RENDERERS.put(item, renderer);
	}

	boolean renderCrosshair(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices);

	void renderOverlay(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices, int scaledWidth, int scaledHeight, float tickDelta);
}
