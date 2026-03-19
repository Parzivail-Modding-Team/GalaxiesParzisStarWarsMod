package dev.pswg.rendering.models;

import dev.pswg.networking.GalaxiesPacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record GVertex(Vector3f position, Vector3f normal, Vector2f texCoords, int color, int overlay, int light)
{
	public static final StreamCodec<ByteBuf, GVertex> PACKET_CODEC = new StreamCodec<>()
	{
		@Override
		public GVertex decode(ByteBuf buf)
		{
			var position = ByteBufCodecs.VECTOR3F.decode(buf);
			var normal = ByteBufCodecs.VECTOR3F.decode(buf);
			var texCoords = GalaxiesPacketCodecs.VECTOR_2F.decode(buf);
			var color = ByteBufCodecs.INT.decode(buf);
			var overlay = ByteBufCodecs.INT.decode(buf);
			var light = ByteBufCodecs.INT.decode(buf);
			return new GVertex(new Vector3f(position), new Vector3f(normal), new Vector2f(texCoords), color, overlay, light);
		}

		@Override
		public void encode(ByteBuf buf, GVertex value)
		{
			ByteBufCodecs.VECTOR3F.encode(buf, value.position);
			ByteBufCodecs.VECTOR3F.encode(buf, value.normal);
			GalaxiesPacketCodecs.VECTOR_2F.encode(buf, value.texCoords);
			ByteBufCodecs.INT.encode(buf, value.color);
			ByteBufCodecs.INT.encode(buf, value.overlay);
			ByteBufCodecs.INT.encode(buf, value.light);
		}
	};
}
