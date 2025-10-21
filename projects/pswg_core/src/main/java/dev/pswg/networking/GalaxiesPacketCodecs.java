package dev.pswg.networking;

import dev.pswg.interaction.ClientPlayerAction;
import dev.pswg.interaction.ServerPlayerAction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines packet codecs and related utilities for common data types
 */
public final class GalaxiesPacketCodecs
{
	/**
	 * A packet codec that can serialize and deserialize {@link Hand} enum values
	 */
	public static final PacketCodec<RegistryByteBuf, Hand> HAND = forEnum(Hand.class);

	/**
	 * A packet codec that can serialize and deserialize {@link Identifier} lists
	 */
	public static final PacketCodec<ByteBuf, List<Identifier>> IDENTIFIER_LIST  = Identifier.PACKET_CODEC.collect(PacketCodecs.toList());

	/**
	 * A packet codec that can serialize and deserialize maps between {@link Identifier}s
	 */
	public static final PacketCodec<ByteBuf, Map<Identifier, Identifier>> IDENTIFIER_MAP = PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, Identifier.PACKET_CODEC);

	/**
	 * A packet codec that can serialize and deserialize {@link ClientPlayerAction} enum values
	 */
	public static final PacketCodec<RegistryByteBuf, ClientPlayerAction> CLIENT_PLAYER_ACTION = forEnum(ClientPlayerAction.class);


	/**
	 * A packet codec that can serialize and deserialize {@link ServerPlayerAction} enum values
	 */
	public static final PacketCodec<RegistryByteBuf, ServerPlayerAction> SERVER_PLAYER_ACTION = forEnum(ServerPlayerAction.class);

	/**
	 * A packet codec that can serialize and deserialize {@link Vector2f}s
	 */
	public static final PacketCodec<ByteBuf, Vector2f> VECTOR_2F = new PacketCodec<>()
	{
		@Override
		public Vector2f decode(ByteBuf buf)
		{
			return new Vector2f(buf.readFloat(), buf.readFloat());
		}

		@Override
		public void encode(ByteBuf buf, Vector2f vector)
		{
			buf.writeFloat(vector.x());
			buf.writeFloat(vector.y());
		}
	};

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

	/**
	 * Creates a packet codec for the given enum type.
	 *
	 * @param <T>   The type of the enum
	 * @param clazz The class of the enum
	 *
	 * @return A {@link PacketCodec} that can serialize and deserialize the enum type
	 */
	public static <T extends Enum<T>> PacketCodec<RegistryByteBuf, T> forEnum(Class<T> clazz)
	{
		return PacketCodec.of(GalaxiesPacketCodecs::writeEnumConstant, GalaxiesPacketCodecs.readEnumConstant(clazz));
	}
}
