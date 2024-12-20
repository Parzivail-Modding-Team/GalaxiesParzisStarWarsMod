package dev.pswg.rendering;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;

/**
 * Defines an interface for items that have elements that can be rendered into a HUD
 */
@FunctionalInterface
public interface ItemHudRenderer
{
	void render(ItemStack stack, DrawContext context, RenderTickCounter tickCounter);
}
