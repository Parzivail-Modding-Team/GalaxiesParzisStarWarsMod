package com.parzivail.util.client.render;

import net.minecraft.client.gui.DrawContext;
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
	HashMap<Class<? extends Item>, ICustomHudRenderer> REGISTRY = new HashMap<>();

	static void register(Class<? extends Item> item, ICustomHudRenderer renderer)
	{
		REGISTRY.put(item, renderer);
	}

	boolean renderCrosshair(DrawContext context, PlayerEntity player, Hand hand, ItemStack stack);

	void renderOverlay(DrawContext context, PlayerEntity player, Hand hand, ItemStack stack, int scaledWidth, int scaledHeight, float tickDelta);
}
