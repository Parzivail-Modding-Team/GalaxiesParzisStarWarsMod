package dev.pswg.rendering.models;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public final class GalaxiesModelBakery
{
	private record GQuadGeometry(GQuad[] quads) implements Geometry
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
					var bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, format);

					var vertices = List.of(quad.a(), quad.b(), quad.c(), quad.d());
					for (var vertex : vertices)
						bufferBuilder.vertex(
								vertex.position().x,
								vertex.position().y,
								vertex.position().z,
								vertex.color(),
								vertex.texCoords().x,
								vertex.texCoords().y,
								vertex.overlay(),
								vertex.light(),
								vertex.normal().x,
								vertex.normal().y,
								vertex.normal().z
						);

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
						var ints = builtBuffer.getBuffer().asIntBuffer().array();
						geometryBuilder.add(new BakedQuad(
								ints,
								0,
								Direction.getFacing(faceNormal),
								baker.getSpriteGetter().get(textures, quad.textureRef(), model),
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
		// TODO: p3d loading
//		if (model.name().equals("pswg_blasters:item/blaster"))
//		{
//			var quads = ...
//			return Optional.of(new GQuadGeometry(quads));
//		}

		return Optional.empty();
	}
}
