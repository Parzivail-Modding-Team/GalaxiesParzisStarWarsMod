package dev.pswg.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.GameType;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real {@link ServerPlayer} joined to the test server over an in-memory connection.
 *
 * <p>Vanilla's mock players are never added to the player list, so they have no packet listener and mod
 * code that sends or receives packets cannot be exercised through them. A connected test player joins
 * through {@link net.minecraft.server.players.PlayerList#placeNewPlayer}, exactly like a client would, which
 * gives it a game mode, an inventory menu and a packet listener. Serverbound payloads can then be delivered
 * to {@link ServerPlayer#connection} and take the same path as network input.
 *
 * <p>The connection is an {@link EmbeddedChannel} that is never driven by the server's network thread, so
 * the player is not subject to keep-alive timeouts, and outbound packets are discarded when the player
 * leaves.
 */
public final class ConnectedTestPlayer
{
	/**
	 * The profile-name prefix of test players; vanilla limits profile names to sixteen characters.
	 */
	private static final String NAME_PREFIX = "tp-";

	/**
	 * Numbers the players of one server run, so concurrently running tests never share a profile.
	 */
	private static final AtomicInteger NEXT_PLAYER_NUMBER = new AtomicInteger(1);

	/**
	 * The joined player.
	 */
	private final ServerPlayer _player;

	/**
	 * The in-memory channel behind the player's connection.
	 */
	private final EmbeddedChannel _channel;

	/**
	 * Creates the handle of one joined player.
	 *
	 * @param player  the joined player
	 * @param channel the in-memory channel behind the connection
	 */
	private ConnectedTestPlayer(ServerPlayer player, EmbeddedChannel channel)
	{
		_player = player;
		_channel = channel;
	}

	/**
	 * Joins a new player to the server of a level.
	 *
	 * @param level    the level the player spawns in
	 * @param gameType the game mode the player is switched to after joining
	 *
	 * @return the joined player
	 */
	public static ConnectedTestPlayer join(ServerLevel level, GameType gameType)
	{
		var name = NAME_PREFIX + NEXT_PLAYER_NUMBER.getAndIncrement();
		var profile = new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name);
		var cookie = CommonListenerCookie.createInitial(profile, false);
		var player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());
		var connection = new Connection(PacketFlow.SERVERBOUND);
		var channel = new EmbeddedChannel(connection);

		level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
		player.setGameMode(gameType);

		return new ConnectedTestPlayer(player, channel);
	}

	/**
	 * Gets the joined player.
	 *
	 * @return the server player
	 */
	public ServerPlayer player()
	{
		return _player;
	}

	/**
	 * Removes the player from the server and releases its connection.
	 */
	public void leave()
	{
		_player.level().getServer().getPlayerList().remove(_player);
		_channel.releaseOutbound();
		_channel.finishAndReleaseAll();
	}
}
