package dev.pswg.networking;

import dev.pswg.interaction.ClientPlayerAction;
import dev.pswg.interaction.ServerPlayerAction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.PacketDecoder;
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

	/**
	 * Creates a packet decoder for the given enum type
	 *
	 * @param <T>       The type of the enum
	 * @param enumClass The class of the enum
	 *
	 * @return A PacketDecoder that reads the enum constant from a RegistryByteBuf
	 */
	public static <T extends Enum<T>> PacketDecoder<RegistryByteBuf, T> readEnumConstant(Class<T> enumClass)
	{
		return (buf) -> (T)enumClass.getEnumConstants()[buf.readVarInt()];
	}

	/**
	 * Writes an enum constant to the given buffer
	 *
	 * @param instance The enum constant to write
	 * @param writer   The buffer to write to
	 */
	public static void writeEnumConstant(Enum<?> instance, RegistryByteBuf writer)
	{
		writer.writeVarInt(instance.ordinal());
	}
}
