package dev.pswg.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Contains events for being notified when certain HUD elements are drawn
 */
public final class HudRenderEvents
{
	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface ElementRenderedCallback
	{
		void render(GuiGraphics context, DeltaTracker tickCounter);
	}

	/**
	 * Fired when the player's crosshair is about to be rendered
	 */
	public static final Event<ElementRenderedCallback> CROSSHAIR = EventFactory.createArrayBacked(
			ElementRenderedCallback.class,
			(context, tickCounter) -> {
			},
			(callbacks) -> (context, tickCounter) -> {
				for (ElementRenderedCallback callback : callbacks)
					callback.render(context, tickCounter);
			}
	);
}
