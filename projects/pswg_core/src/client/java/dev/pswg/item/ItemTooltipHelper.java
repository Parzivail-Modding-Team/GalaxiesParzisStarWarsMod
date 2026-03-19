package dev.pswg.item;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.world.item.Item;

/**
 * A set of utilities for working with item tooltips
 */
public class ItemTooltipHelper
{
	/**
	 * Registers a tooltip callback for the given item
	 *
	 * @param item     The item for which the tooltip will be registered
	 * @param callback The tooltip callback to register
	 */
	public static void registerTooltip(Item item, ItemTooltipCallback callback)
	{
		ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
			// Only run the callback on the given item
			if (!itemStack.is(item))
				return;

			callback.getTooltip(itemStack, tooltipContext, tooltipType, list);
		});
	}
}
