package dev.pswg.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class HudRenderEvents
{
	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Crosshair
	{
		void crosshair(DrawContext context, RenderTickCounter tickCounter);
	}

	public static final Event<Crosshair> CROSSHAIR = EventFactory.createArrayBacked(
			Crosshair.class,
			(context, tickCounter) -> {
			},
			(callbacks) -> (context, tickCounter) -> {
				for (Crosshair callback : callbacks)
					callback.crosshair(context, tickCounter);
			}
	);
}
