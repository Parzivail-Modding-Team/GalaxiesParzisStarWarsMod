package dev.pswg.rendering.models;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GQuad(GVertex a, GVertex b, GVertex c, GVertex d, String textureRef)
{
	public static final StreamCodec<ByteBuf, GQuad> PACKET_CODEC = new StreamCodec<>()
	{
		@Override
		public GQuad decode(ByteBuf buf)
		{
			var a = GVertex.PACKET_CODEC.decode(buf);
			var b = GVertex.PACKET_CODEC.decode(buf);
			var c = GVertex.PACKET_CODEC.decode(buf);
			var d = GVertex.PACKET_CODEC.decode(buf);
			var textureRef = ByteBufCodecs.STRING_UTF8.decode(buf);
			return new GQuad(a, b, c, d, textureRef);
		}

		@Override
		public void encode(ByteBuf buf, GQuad value)
		{
			GVertex.PACKET_CODEC.encode(buf, value.a);
			GVertex.PACKET_CODEC.encode(buf, value.b);
			GVertex.PACKET_CODEC.encode(buf, value.c);
			GVertex.PACKET_CODEC.encode(buf, value.d);
			ByteBufCodecs.STRING_UTF8.encode(buf, value.textureRef);
		}
	};
}
