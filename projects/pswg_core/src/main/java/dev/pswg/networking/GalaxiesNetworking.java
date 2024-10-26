package dev.pswg.networking;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
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

	/**
	 * Creates a {@link ClientPlayPacketListener} packet from the given {@link CustomPayload} packet
	 *
	 * @param packet The packet to convert
	 *
	 * @return The converted packet
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CustomPayload> Packet<ClientPlayPacketListener> createPlayS2CPacket(T packet)
	{
		return (Packet<ClientPlayPacketListener>)(Packet<?>)ServerPlayNetworking.createS2CPacket(packet);
	}
}
