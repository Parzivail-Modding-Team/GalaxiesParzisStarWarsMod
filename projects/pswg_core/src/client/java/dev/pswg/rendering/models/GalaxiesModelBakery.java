package dev.pswg.rendering.models;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.pswg.GalaxiesClient;
import dev.pswg.networking.GalaxiesPacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
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
	public record GQuadGeometry(Collection<GQuad> quads) implements UnbakedGeometry
	{
		private static final StreamCodec<ByteBuf, Collection<GQuad>> QUAD_COLLECTION_CODEC = ByteBufCodecs.collection(ArrayList::new, GQuad.PACKET_CODEC);

		public static final StreamCodec<ByteBuf, GQuadGeometry> PACKET_CODEC = GalaxiesPacketCodecs.gzip(new StreamCodec<>()
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
		});

		@Override
		public QuadCollection bake(TextureSlots textures, ModelBaker baker, ModelState settings, ModelDebugName model)
		{
			var geometryBuilder = new QuadCollection.Builder();

			var format = DefaultVertexFormat.BLOCK;
			try (ByteBufferBuilder bufferAllocator = ByteBufferBuilder.exactlySized(format.getVertexSize() * 4))
			{
				for (var quad : quads())
				{
					var material = baker.materials().resolveSlot(textures, quad.textureRef(), model);
					var sprite = material.sprite();
					var minUv = new Vector2f(sprite.getU0(), sprite.getV0());
					var maxUv = new Vector2f(sprite.getU1(), sprite.getV1());
					var packedUvs = new long[4];

					var uvExtent = maxUv.sub(minUv, new Vector2f());

					var bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.Mode.QUADS, format);

					var vertices = List.of(quad.a(), quad.b(), quad.c(), quad.d());
					for (int index = 0; index < vertices.size(); index++)
					{
						var vertex = vertices.get(index);
						var translatedTexCoords = new Vector2f(vertex.texCoords());
						translatedTexCoords.mul(uvExtent);
						translatedTexCoords.add(minUv);
						packedUvs[index] = UVPair.pack(translatedTexCoords.x, translatedTexCoords.y);

						bufferBuilder.addVertex(
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

					var faceNormal = new Vec3(0, 0, 0);
					for (var vertex : vertices)
					{
						faceNormal = faceNormal.add(
								vertex.normal().x / 4,
								vertex.normal().y / 4,
								vertex.normal().z / 4
						);
					}

					try (MeshData builtBuffer = bufferBuilder.buildOrThrow())
					{
						var materialInfo = baker.interner().materialInfo(BakedQuad.MaterialInfo.of(material, Transparency.TRANSLUCENT, -1, true, 0));
						geometryBuilder.addUnculledFace(new BakedQuad(
								baker.interner().vector(vertices.get(0).position()),
								baker.interner().vector(vertices.get(1).position()),
								baker.interner().vector(vertices.get(2).position()),
								baker.interner().vector(vertices.get(3).position()),
								packedUvs[0],
								packedUvs[1],
								packedUvs[2],
								packedUvs[3],
								Direction.getApproximateNearest(faceNormal),
								materialInfo
						));
					}
				}
			}

			return geometryBuilder.build();
		}
	}

	/**
	 * Attempts to load GQB geometry for the given model id.
	 *
	 * @param modelId The model identifier that might have GQB geometry.
	 *
	 * @return The GQB geometry if it exists, or an empty optional otherwise.
	 */
	public static Optional<UnbakedGeometry> getGeometry(Identifier modelId)
	{
		return Optional.ofNullable(GalaxiesClient.GQB_LOADER.getDefinitions().get(modelId));
	}

	/**
	 * Attempts to load GQB geometry for the given model
	 *
	 * @param model The model that might have GQB geometry
	 *
	 * @return The GQB geometry if it exists, or an empty optional otherwise
	 */
	public static Optional<UnbakedGeometry> getGeometry(ResolvedModel model)
	{
		return getGeometry(Identifier.parse(model.debugName()));
	}
}
