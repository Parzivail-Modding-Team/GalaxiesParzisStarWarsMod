package dev.pswg.gametest.blasters;

import dev.pswg.networking.BlasterInputPayload;
import dev.pswg.networking.BlasterInputPayload.BlasterInputAction;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/**
 * A connected test player that operates blasters the way a real client does.
 *
 * <p>Every control becomes a {@link BlasterInputPayload} delivered to the player's packet listener. The
 * payload therefore takes the same path as network input, through the registered receiver, the server-side
 * input sequencing, session and lease rules, and the per-tick scheduler. Nothing here calls into blaster
 * logic directly, so a scenario cannot pass through a shortcut that real input would not have.
 *
 * <p>Sequence numbers and trigger sessions follow the client protocol: sequences are positive and strictly
 * increasing per connection, a press opens a session identified by its own sequence, and heartbeats and
 * releases refer to that session.
 */
public final class BlasterTestPlayer
{
	/**
	 * The player's session value for packets that do not refer to a trigger session.
	 */
	private static final long NO_SESSION = 0;

	/**
	 * The joined server player.
	 */
	private final ServerPlayer _player;

	/**
	 * The sequence number of the last packet sent.
	 */
	private long _sequence;

	/**
	 * The open trigger session of each hand, identified by the sequence of the press that opened it.
	 */
	private final Map<InteractionHand, Long> _sessions;

	/**
	 * Creates the handle of one joined player.
	 *
	 * @param player the joined server player
	 */
	BlasterTestPlayer(ServerPlayer player)
	{
		_player = player;
		_sessions = new EnumMap<>(InteractionHand.class);
	}

	/**
	 * Gets the joined server player.
	 *
	 * @return the server player
	 */
	public ServerPlayer player()
	{
		return _player;
	}

	/**
	 * Puts a stack into a hand.
	 *
	 * @param hand  the hand
	 * @param stack the stack to hold
	 *
	 * @return the same stack, for chaining
	 */
	public ItemStack hold(InteractionHand hand, ItemStack stack)
	{
		_player.setItemInHand(hand, stack);
		return stack;
	}

	/**
	 * Turns the player towards the centre of an entity.
	 *
	 * @param target the entity to face
	 */
	public void lookAt(Entity target)
	{
		_player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getBoundingBox().getCenter());
	}

	/**
	 * Presses the trigger of a hand, opening a trigger session.
	 *
	 * @param hand the hand
	 */
	public void press(InteractionHand hand)
	{
		_sessions.put(hand, send(BlasterInputAction.PRESS, hand, NO_SESSION));
	}

	/**
	 * Renews the held-trigger lease of a hand, like the client's periodic heartbeat.
	 *
	 * @param hand the hand
	 */
	public void heartbeat(InteractionHand hand)
	{
		send(BlasterInputAction.HEARTBEAT, hand, requireSession(hand));
	}

	/**
	 * Releases the trigger of a hand and closes its session.
	 *
	 * @param hand the hand
	 */
	public void release(InteractionHand hand)
	{
		send(BlasterInputAction.RELEASE, hand, requireSession(hand));
		_sessions.remove(hand);
	}

	/**
	 * Starts a reload of the weapon in a hand.
	 *
	 * @param hand the hand
	 */
	public void reload(InteractionHand hand)
	{
		send(BlasterInputAction.RELOAD, hand, NO_SESSION);
	}

	/**
	 * Cycles the weapon in a hand to its next mode.
	 *
	 * @param hand the hand
	 */
	public void cycleMode(InteractionHand hand)
	{
		send(BlasterInputAction.CYCLE_MODE, hand, NO_SESSION);
	}

	/**
	 * Gets the open trigger session of a hand.
	 *
	 * @param hand the hand
	 *
	 * @return the session identifier
	 *
	 * @throws IllegalStateException if the trigger of the hand is not pressed
	 */
	private long requireSession(InteractionHand hand)
	{
		var session = _sessions.get(hand);

		if (session == null)
		{
			throw new IllegalStateException("The " + hand + " trigger is not pressed");
		}

		return session;
	}

	/**
	 * Delivers one input packet to the player's packet listener.
	 *
	 * @param action  the control
	 * @param hand    the hand
	 * @param session the trigger session the packet refers to
	 *
	 * @return the sequence number the packet was sent with
	 */
	private long send(BlasterInputAction action, InteractionHand hand, long session)
	{
		var sequence = ++_sequence;
		var payload = new BlasterInputPayload(action, hand, sequence, session);

		_player.connection.handleCustomPayload(new ServerboundCustomPayloadPacket(payload));
		return sequence;
	}
}
