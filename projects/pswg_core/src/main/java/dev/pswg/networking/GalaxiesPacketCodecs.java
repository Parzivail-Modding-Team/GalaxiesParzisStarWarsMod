package dev.pswg.networking;

import dev.pswg.interaction.PlayerAction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

/**
 * Defines packet codecs for common data types
 */
public class GalaxiesPacketCodecs
{
	private static final IntFunction<Hand> HAND_IDS = ValueLists.createIdToValueFunction(Hand::ordinal, Hand.values(), ValueLists.OutOfBoundsHandling.ZERO);

	/**
	 * A packet codec that can serialize and deserialize {@link Hand} enum values
	 */
	public static final PacketCodec<ByteBuf, Hand> HAND = PacketCodecs.indexed(HAND_IDS, Hand::ordinal);

	private static final IntFunction<PlayerAction> PLAYER_ACTION_IDS = ValueLists.createIdToValueFunction(PlayerAction::ordinal, PlayerAction.values(), ValueLists.OutOfBoundsHandling.ZERO);

	/**
	 * A packet codec that can serialize and deserialize {@link PlayerAction} enum values
	 */
	public static final PacketCodec<ByteBuf, PlayerAction> PLAYER_ACTION = PacketCodecs.indexed(PLAYER_ACTION_IDS, PlayerAction::ordinal);
}
