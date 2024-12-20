package dev.pswg.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/**
 * Contains events for being notified when item-related components are drawn
 */
public final class ItemRenderEvents
{
	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface StackRenderedCallback
	{
		void render(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y);
	}

	/**
	 * Fired when an item stack is about to be rendered in a UI element
	 */
	public static final Event<StackRenderedCallback> STACK = EventFactory.createArrayBacked(
			StackRenderedCallback.class,
			(context, textRenderer, stack, x, y) -> {
			},
			(callbacks) -> (context, textRenderer, stack, x, y) -> {
				for (StackRenderedCallback callback : callbacks)
					callback.render(context, textRenderer, stack, x, y);
			}
	);
}
