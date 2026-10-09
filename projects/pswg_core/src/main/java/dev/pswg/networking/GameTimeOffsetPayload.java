package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.generated.codecs.IGameTimeOffsetPayloadCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Dimension-qualified offset from its local game time to saved Overworld time. */
@GenerateCodec
public record GameTimeOffsetPayload(Identifier dimension, long offset) implements CustomPacketPayload, IGameTimeOffsetPayloadCodec
{
	/** Native play-channel type. */
	public static final Type<GameTimeOffsetPayload> TYPE = new Type<>(Galaxies.id("game_time_offset"));

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
