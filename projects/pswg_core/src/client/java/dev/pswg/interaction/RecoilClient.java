package dev.pswg.interaction;

import dev.pswg.networking.RecoilImpulsePayload;
import dev.pswg.world.GameTime;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Adds accepted recoil events to the current player's local view state.
 */
public final class RecoilClient
{
	public static void register()
	{
		ClientPlayNetworking.registerGlobalReceiver(RecoilImpulsePayload.TYPE, (packet, context) -> {
			var owner = context.player();
			context.client().execute(() -> {
				var level = context.client().level;
				if (context.client().player != owner || level == null || owner.getId() != packet.entityId())
				{
					return;
				}

				if (!level.dimension().identifier().equals(packet.dimension()))
				{
					return;
				}

				RecoilEntityAttachment.queueImpulse(
						owner,
						packet.impulse(),
						packet.recoveryTicks(),
						GameTime.now(level),
						packet.eventSequence(),
						packet.shotSequence(),
						packet.sourceSerial()
				);
			});
		});
	}

	/**
	 * Utility class.
	 */
	private RecoilClient()
	{
	}
}
