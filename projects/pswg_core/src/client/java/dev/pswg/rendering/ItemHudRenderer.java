package dev.pswg.rendering;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

/**
 * Defines an interface for items that have elements that can be rendered into a HUD
 */
@FunctionalInterface
public interface ItemHudRenderer
{
	void render(ItemStack stack, GuiGraphicsExtractor context, DeltaTracker tickCounter);

	/**
	 * Draws this HUD in a vertically offset row.
	 */
	default void render(ItemStack stack, GuiGraphicsExtractor context, DeltaTracker tickCounter, int offsetY)
	{
		var pose = context.pose();
		pose.pushMatrix();
		try
		{
			pose.translate(0, offsetY);
			render(stack, context, tickCounter);
		}
		finally
		{
			pose.popMatrix();
		}
	}

	/**
	 * Reserves vertical space for the next weapon's HUD.
	 */
	default int rowHeight()
	{
		return 36;
	}
}
