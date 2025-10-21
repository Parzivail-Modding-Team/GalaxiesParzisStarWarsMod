package dev.pswg.rendering.models;

import dev.pswg.networking.GalaxiesPacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record GVertex(Vector3f position, Vector3f normal, Vector2f texCoords, int color, int overlay, int light)
{
	public static final PacketCodec<ByteBuf, GVertex> PACKET_CODEC = new PacketCodec<>()
	{
		@Override
		public GVertex decode(ByteBuf buf)
		{
			var position = PacketCodecs.VECTOR_3F.decode(buf);
			var normal = PacketCodecs.VECTOR_3F.decode(buf);
			var texCoords = GalaxiesPacketCodecs.VECTOR_2F.decode(buf);
			var color = PacketCodecs.INTEGER.decode(buf);
			var overlay = PacketCodecs.INTEGER.decode(buf);
			var light = PacketCodecs.INTEGER.decode(buf);
			return new GVertex(position, normal, texCoords, color, overlay, light);
		}

		@Override
		public void encode(ByteBuf buf, GVertex value)
		{
			PacketCodecs.VECTOR_3F.encode(buf, value.position);
			PacketCodecs.VECTOR_3F.encode(buf, value.normal);
			GalaxiesPacketCodecs.VECTOR_2F.encode(buf, value.texCoords);
			PacketCodecs.INTEGER.encode(buf, value.color);
			PacketCodecs.INTEGER.encode(buf, value.overlay);
			PacketCodecs.INTEGER.encode(buf, value.light);
		}
	};
}
