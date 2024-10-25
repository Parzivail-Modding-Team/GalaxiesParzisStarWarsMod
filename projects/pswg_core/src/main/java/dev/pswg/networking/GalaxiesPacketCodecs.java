package dev.pswg.networking;

import dev.pswg.interaction.ClientPlayerAction;
import dev.pswg.interaction.ServerPlayerAction;
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

	private static final IntFunction<ClientPlayerAction> CLIENT_PLAYER_ACTION_IDS = ValueLists.createIdToValueFunction(ClientPlayerAction::ordinal, ClientPlayerAction.values(), ValueLists.OutOfBoundsHandling.ZERO);

	/**
	 * A packet codec that can serialize and deserialize {@link ClientPlayerAction} enum values
	 */
	public static final PacketCodec<ByteBuf, ClientPlayerAction> CLIENT_PLAYER_ACTION = PacketCodecs.indexed(CLIENT_PLAYER_ACTION_IDS, ClientPlayerAction::ordinal);

	private static final IntFunction<ServerPlayerAction> SERVER_PLAYER_ACTION_IDS = ValueLists.createIdToValueFunction(ServerPlayerAction::ordinal, ServerPlayerAction.values(), ValueLists.OutOfBoundsHandling.ZERO);

	/**
	 * A packet codec that can serialize and deserialize {@link ServerPlayerAction} enum values
	 */
	public static final PacketCodec<ByteBuf, ServerPlayerAction> SERVER_PLAYER_ACTION = PacketCodecs.indexed(SERVER_PLAYER_ACTION_IDS, ServerPlayerAction::ordinal);
}
