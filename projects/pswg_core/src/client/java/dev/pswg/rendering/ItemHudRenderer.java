package dev.pswg.rendering;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Defines an interface for items that have elements that can be rendered into a HUD
 */
@FunctionalInterface
public interface ItemHudRenderer
{
	void render(ItemStack stack, GuiGraphics context, DeltaTracker tickCounter);
}
