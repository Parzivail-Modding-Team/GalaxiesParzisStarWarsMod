package dev.pswg.rendering.models;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * TODO: docs
 */
public final class GalaxiesModelBakery
{
	private record GQuadGeometry(Collection<GQuad> quads) implements Geometry
	{
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

	public static Optional<Geometry> getGeometry(BakedSimpleModel model)
	{
		// TODO: model loading... convert from interchange format to codec-ified format in data generator?
//		if (model.name().equals("pswg_blasters:item/blaster"))
//		{
//			var color = -1;
//			var overlay = OverlayTexture.DEFAULT_UV;
//			var light = LightmapTextureManager.MAX_LIGHT_COORDINATE;
//
//			var quads = PacketCodecs.collection(ArrayList::new, GQuad.PACKET_CODEC).decode(inputStream);
//
//			return Optional.of(new GQuadGeometry(quads));
//		}

		return Optional.empty();
	}
}
