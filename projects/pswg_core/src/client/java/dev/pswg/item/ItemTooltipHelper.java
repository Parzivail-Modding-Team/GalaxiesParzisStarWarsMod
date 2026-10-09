package dev.pswg.item;

import dev.pswg.GalaxiesClient;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
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

	/**
	 * Adds one colored key hint line.
	 */
	public static Component keyHint(KeyMapping key, String hintKey)
	{
		return color(GalaxiesClient.getKeybindHint(key, Component.translatable(hintKey)), ChatFormatting.AQUA);
	}

	/**
	 * Applies one display color to an immutable component value.
	 */
	public static Component color(Component component, ChatFormatting color)
	{
		return component.copy().withStyle(color);
	}

	/**
	 * Adds an extended-stat line with muted labels and bright values.
	 */
	public static Component detail(String translationKey, Component... values)
	{
		return Component.translatable(translationKey, (Object[])values).withStyle(ChatFormatting.GRAY);
	}

	/**
	 * Creates a highlighted tooltip value.
	 */
	public static Component value(String text)
	{
		return Component.literal(text).withStyle(ChatFormatting.WHITE);
	}
}
