package dev.pswg.rendering.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.codec.GalaxiesCodecs;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

/**
	 * The intermediary model data exported from, e.g., BlockBench
	 *
	 * @param data     The model data
	 * @param files    The face groups to be included in each file
	 * @param textures The texture definitions
	 * @param display  The display definitions
	 */
	public record GqbIntermediary(
			ModelData data,
			Optional<Map<String, List<String>>> files,
			JsonElement textures,
			JsonElement display
	)
	{
		public static final Codec<GqbIntermediary> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ModelData.CODEC.fieldOf("data").forGetter(GqbIntermediary::data),
				Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()).optionalFieldOf("files").forGetter(GqbIntermediary::files),
				ExtraCodecs.JSON.fieldOf("textures").forGetter(GqbIntermediary::textures),
				ExtraCodecs.JSON.fieldOf("display").forGetter(GqbIntermediary::display)
		).apply(instance, GqbIntermediary::new));

		/**
		 * The actual model data
		 *
		 * @param vertices  The vertex positions
		 * @param normals   The vertex normals
		 * @param texCoords The vertex texture coordinates
		 * @param faces     The face definitions
		 */
		private record ModelData(
				List<Vector3f> vertices,
				List<Vector3f> normals,
				List<Vector2f> texCoords,
				Map<String, List<ModelFace>> faces
		)
		{
			public static final Codec<ModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					GalaxiesCodecs.NAMED_VECTOR_3F.listOf().fieldOf("vertices").forGetter(ModelData::vertices),
					GalaxiesCodecs.NAMED_VECTOR_3F.listOf().fieldOf("normals").forGetter(ModelData::normals),
					GalaxiesCodecs.NAMED_VECTOR_2F.listOf().fieldOf("texCoords").forGetter(ModelData::texCoords),
					Codec.unboundedMap(Codec.STRING, ModelFace.CODEC.listOf()).fieldOf("faces").forGetter(ModelData::faces)
			).apply(instance, ModelData::new));

			/**
			 * A model face definition
			 *
			 * @param material The name of the texture on this face
			 * @param triplets The vertex pointer triplets
			 */
			private record ModelFace(
					String material,
					List<ModelFaceTriplet> triplets
			)
			{
				public static final Codec<ModelFace> CODEC = RecordCodecBuilder.create(instance -> instance.group(
						Codec.STRING.fieldOf("material").forGetter(ModelFace::material),
						ModelFaceTriplet.CODEC.listOf().fieldOf("triplets").forGetter(ModelFace::triplets)
				).apply(instance, ModelFace::new));

				/**
				 * A pointer to a position, texture coordinate, and normal
				 *
				 * @param p The 1-based position index
				 * @param t The 1-based texture coordinate index
				 * @param n The 1-based normal index
				 */
				private record ModelFaceTriplet(int p, int t, int n)
				{
					public static final Codec<ModelFaceTriplet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
							Codec.INT.fieldOf("p").forGetter(ModelFaceTriplet::p),
							Codec.INT.fieldOf("t").forGetter(ModelFaceTriplet::t),
							Codec.INT.fieldOf("n").forGetter(ModelFaceTriplet::n)
					).apply(instance, ModelFaceTriplet::new));
				}
			}
		}

		/**
		 * Creates a geometry from this intermediary model
		 *
		 * @param groups The face groups to be included in the geometry, or empty to include all groups
		 *
		 * @return The created geometry
		 */
		public GalaxiesModelBakery.GQuadGeometry createGeometry(Optional<HashSet<String>> groups)
		{
			var color = -1;
			var overlay = OverlayTexture.NO_OVERLAY;
			var light = LightCoordsUtil.FULL_BRIGHT;

			var quads = new ArrayList<GQuad>();

			for (var entry : data().faces().entrySet())
			{
				if (groups.map(set -> !set.contains(entry.getKey())).orElse(false))
					continue;

				var obj = entry.getValue();
				for (var face : obj)
				{
					quads.add(new GQuad(
							getVertex(face, 0, color, overlay, light),
							getVertex(face, 1, color, overlay, light),
							getVertex(face, 2, color, overlay, light),
							getVertex(face, 3, color, overlay, light),
							face.material
					));
				}
			}

			return new GalaxiesModelBakery.GQuadGeometry(quads);
		}

		/**
		 * Gets the face vertex at the given index, applying the specified color, overlay, and light
		 *
		 * @param face    The face to retrieve an index from
		 * @param i       The vertex index within the face, which will be clamped to the number of vertices present in the face
		 * @param color   The color to apply
		 * @param overlay The overlay to apply
		 * @param light   The light to apply
		 *
		 * @return The face vertex at the given index
		 */
		private GVertex getVertex(GqbIntermediary.ModelData.ModelFace face, int i, int color, int overlay, int light)
		{
			// Repeat the last vertex to create a quad from triangles
			var triplet = face.triplets.get(Math.min(i, face.triplets.size() - 1));

			var pos = data().vertices().get(triplet.p - 1);
			var texCoord = data().texCoords().get(triplet.t - 1);

			return new GVertex(
					new Vector3f(pos.x + 0.5f, pos.y, pos.z + 0.5f),
					data().normals().get(triplet.n - 1),
					new Vector2f(texCoord.x, 1 - texCoord.y),
					color, overlay, light
			);
		}

		/**
		 * Creates a vanilla JSON model definition from this intermediary model,
		 * containing only textures and display properties.
		 *
		 * @return A JsonElement containing the vanilla model data
		 */
		public JsonElement createModelDef()
		{
			var obj = new JsonObject();

			var tex = textures().getAsJsonObject();

			if (!tex.has("particle"))
				tex.addProperty("particle", "pswg:block/empty");

			obj.add("textures", tex);
			obj.add("display", display());

			return obj;
		}
	}