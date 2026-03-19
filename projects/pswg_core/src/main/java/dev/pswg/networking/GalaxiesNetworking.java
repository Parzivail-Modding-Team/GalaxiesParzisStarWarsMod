package dev.pswg.networking;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

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
	public static void sendToTracking(Entity entity, CustomPacketPayload packet)
	{
		for (ServerPlayer player : PlayerLookup.tracking(entity))
			ServerPlayNetworking.send(player, packet);
	}

	/**
	 * Creates a {@link ClientGamePacketListener} packet from the given {@link CustomPacketPayload} packet
	 *
	 * @param packet The packet to convert
	 *
	 * @return The converted packet
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CustomPacketPayload> Packet<ClientGamePacketListener> createPlayS2CPacket(T packet)
	{
		return (Packet<ClientGamePacketListener>)(Packet<?>)ServerPlayNetworking.createS2CPacket(packet);
	}
}
