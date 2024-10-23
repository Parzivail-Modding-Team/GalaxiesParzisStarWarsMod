package dev.pswg.networking;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Contains utilities for networking
 */
public final class GalaxiesNetworking
{
	/**
	 * Sends a custom payload packet to all players tracking the specified entity.
	 *
	 * @param entity The entity being tracked
	 * @param packet The custom payload packet to send to the tracking players
	 */
	public static void sendToTracking(Entity entity, CustomPayload packet)
	{
		for (ServerPlayerEntity player : PlayerLookup.tracking(entity))
			ServerPlayNetworking.send(player, packet);
	}
}
