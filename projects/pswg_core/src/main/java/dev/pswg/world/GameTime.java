package dev.pswg.world;

import dev.pswg.Galaxies;
import dev.pswg.networking.GameTimeOffsetPayload;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * Saved Overworld game time for cross-dimension item timers, with a client level-clock adapter.
 * Day-time commands do not affect this clock.
 */
public final class GameTime
{
	/**
	 * Last offset sent to this connection and dimension instance.
	 */
	private record Sample(ServerLevel level, long offset)
	{
	}

	public static void register()
	{
		PayloadTypeRegistry.clientboundPlay().register(GameTimeOffsetPayload.TYPE, GameTimeOffsetPayload.PACKET_CODEC);
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (var player : server.getPlayerList().getPlayers())
			{
				var level = player.level();
				var sample = new Sample(level, server.overworld().getGameTime() - level.getGameTime());
				if (!sample.equals(player.getAttached(LAST_SENT)) && ServerPlayNetworking.canSend(player, GameTimeOffsetPayload.TYPE))
				{
					ServerPlayNetworking.send(player, new GameTimeOffsetPayload(level.dimension().identifier(), sample.offset()));
					player.setAttached(LAST_SENT, sample);
				}
			}
		});
	}

	/**
	 * Reads authority on the server and the dimension-local synchronized adapter on clients.
	 */
	public static long now(Level level)
	{
		if (level instanceof ServerLevel serverLevel)
			return serverLevel.getServer().overworld().getGameTime();

		return level.getGameTime() + level.getAttachedOrElse(OFFSET, 0L);
	}

	/**
	 * Unsaved offset attached to the client level, so world/connection changes discard it.
	 */
	public static final AttachmentType<Long> OFFSET = AttachmentRegistry.create(Galaxies.id("game_time_offset"));

	/**
	 * Unsaved connection-local send cache.
	 */
	private static final AttachmentType<Sample> LAST_SENT = AttachmentRegistry.create(Galaxies.id("game_time_sample"));

	/**
	 * Utility class.
	 */
	private GameTime()
	{
	}
}
