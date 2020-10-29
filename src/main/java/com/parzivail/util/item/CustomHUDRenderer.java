package com.parzivail.util.item;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public interface CustomHUDRenderer
{
	/**
	 * Do not use this directly.
	 */
	HashMap<Item, CustomHUDRenderer> CUSTOM_HUD_RENDERERS = new HashMap<>();

	static void registerCustomHUD(Item item, CustomHUDRenderer customHUDRenderer) {
		CUSTOM_HUD_RENDERERS.put(item, customHUDRenderer);
	}

	void renderCustomHUD(ItemStack stack, MatrixStack matrices);
}
