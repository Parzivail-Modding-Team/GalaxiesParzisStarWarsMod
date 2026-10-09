package dev.pswg.world;

import dev.pswg.networking.GameTimeOffsetPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Installs the server clock adapter.
 */
public final class GameTimeClient
{
	public static void register()
	{
		ClientPlayNetworking.registerGlobalReceiver(GameTimeOffsetPayload.TYPE, (payload, context) -> {
			var owner = context.player();
			context.client().execute(() -> {
				var level = context.client().level;
				if (context.client().player == owner && level != null && level.dimension().identifier().equals(payload.dimension()))
					level.setAttached(GameTime.OFFSET, payload.offset());
			});
		});
	}

	/**
	 * Utility class.
	 */
	private GameTimeClient()
	{
	}
}
