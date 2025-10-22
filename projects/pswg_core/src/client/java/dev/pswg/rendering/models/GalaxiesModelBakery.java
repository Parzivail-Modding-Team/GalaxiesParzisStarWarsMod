package dev.pswg.rendering.models;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.pswg.GalaxiesClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;

import java.util.*;

/**
 * Utilities for loading GQB models into the model bakery
 */
public final class GalaxiesModelBakery
{
	/**
	 * A geometry that bakes a collection of GQuads
	 *
	 * @param quads The quads to bake
	 */
	public record GQuadGeometry(Collection<GQuad> quads) implements Geometry
	{
		private static final PacketCodec<ByteBuf, Collection<GQuad>> QUAD_COLLECTION_CODEC = PacketCodecs.collection(ArrayList::new, GQuad.PACKET_CODEC);

		public static final PacketCodec<ByteBuf, GQuadGeometry> PACKET_CODEC = new PacketCodec<ByteBuf, GQuadGeometry>()
		{
			@Override
			public GQuadGeometry decode(ByteBuf buf)
			{
				return new GQuadGeometry(QUAD_COLLECTION_CODEC.decode(buf));
			}

			@Override
			public void encode(ByteBuf buf, GQuadGeometry value)
			{
				QUAD_COLLECTION_CODEC.encode(buf, value.quads());
			}
		};

		@Override
		public BakedGeometry bake(ModelTextures textures, Baker baker, ModelBakeSettings settings, SimpleModel model)
		{
			var geometryBuilder = new BakedGeometry.Builder();

			var format = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
			try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(format.getVertexSize() * 4))
			{
				for (var quad : quads())
				{
					var sprite = baker.getSpriteGetter().get(textures, quad.textureRef(), model);
					var minUv = new Vector2f(sprite.getMinU(), sprite.getMinV());
					var maxUv = new Vector2f(sprite.getMaxU(), sprite.getMaxV());

					var uvExtent = maxUv.sub(minUv, new Vector2f());

					var bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, format);

					var vertices = List.of(quad.a(), quad.b(), quad.c(), quad.d());
					for (var vertex : vertices)
					{
						var translatedTexCoords = new Vector2f(vertex.texCoords());
						translatedTexCoords.mul(uvExtent);
						translatedTexCoords.add(minUv);

						bufferBuilder.vertex(
								vertex.position().x,
								vertex.position().y,
								vertex.position().z,
								vertex.color(),
								translatedTexCoords.x,
								translatedTexCoords.y,
								vertex.overlay(),
								vertex.light(),
								vertex.normal().x,
								vertex.normal().y,
								vertex.normal().z
						);
					}

					var faceNormal = new Vec3d(0, 0, 0);
					for (var vertex : vertices)
					{
						faceNormal = faceNormal.add(
								vertex.normal().x / 4,
								vertex.normal().y / 4,
								vertex.normal().z / 4
						);
					}

					try (BuiltBuffer builtBuffer = bufferBuilder.end())
					{
						var intBuffer = builtBuffer.getBuffer().asIntBuffer();
						var ints = new int[intBuffer.capacity()];
						intBuffer.get(ints);

						geometryBuilder.add(new BakedQuad(
								ints,
								0,
								Direction.getFacing(faceNormal),
								sprite,
								true,
								0
						));
					}
				}
			}

			return geometryBuilder.build();
		}
	}

	/**
	 * Attempts to load GQB geometry for the given model
	 *
	 * @param model The model that might have GQB geometry
	 *
	 * @return The GQB geometry if it exists, or an empty optional otherwise
	 */
	public static Optional<Geometry> getGeometry(BakedSimpleModel model)
	{
		// Test to see if a GQB model exists for the MC model
		var key = Identifier.of(model.name() + ".gqb");
		var result = GalaxiesClient.GQB_LOADER.getDefinitions().getOrDefault(key, null);

		return Optional.ofNullable(result);
	}
}
