package com.parzivail.swg.render.pipeline;

import com.google.common.collect.Maps;
import com.google.gson.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class BlockPart
{
	public final Vector3f positionFrom;
	public final Vector3f positionTo;
	public final Map<EnumFacing, BlockPartFace> mapFaces;
	public final BlockPartRotation partRotation;
	public final boolean shade;

	public BlockPart(Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn, @Nullable BlockPartRotation partRotationIn, boolean shadeIn)
	{
		positionFrom = positionFromIn;
		positionTo = positionToIn;
		mapFaces = mapFacesIn;
		partRotation = partRotationIn;
		shade = shadeIn;
		setDefaultUvs();
	}

	private void setDefaultUvs()
	{
		for (Map.Entry<EnumFacing, BlockPartFace> entry : mapFaces.entrySet())
		{
			float[] afloat = getFaceUvs(entry.getKey());
			(entry.getValue()).blockFaceUV.setUvs(afloat);
		}
	}

	private float[] getFaceUvs(EnumFacing facing)
	{
		switch (facing)
		{
			case DOWN:
				return new float[] {
						positionFrom.x, 16.0F - positionTo.z, positionTo.x, 16.0F - positionFrom.z
				};
			case UP:
				return new float[] { positionFrom.x, positionFrom.z, positionTo.x, positionTo.z };
			case NORTH:
			default:
				return new float[] {
						16.0F - positionTo.x, 16.0F - positionTo.y, 16.0F - positionFrom.x, 16.0F - positionFrom.y
				};
			case SOUTH:
				return new float[] {
						positionFrom.x, 16.0F - positionTo.y, positionTo.x, 16.0F - positionFrom.y
				};
			case WEST:
				return new float[] {
						positionFrom.z, 16.0F - positionTo.y, positionTo.z, 16.0F - positionFrom.y
				};
			case EAST:
				return new float[] {
						16.0F - positionTo.z, 16.0F - positionTo.y, 16.0F - positionFrom.z, 16.0F - positionFrom.y
				};
		}
	}

	@SideOnly(Side.CLIENT)
	static class Deserializer implements JsonDeserializer<BlockPart>
	{
		public BlockPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
		{
			JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
			Vector3f vector3f = parsePositionFrom(jsonobject);
			Vector3f vector3f1 = parsePositionTo(jsonobject);
			BlockPartRotation blockpartrotation = parseRotation(jsonobject);
			Map<EnumFacing, BlockPartFace> map = parseFacesCheck(p_deserialize_3_, jsonobject);

			if (jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade"))
			{
				throw new JsonParseException("Expected shade to be a Boolean");
			}
			else
			{
				boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
				return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
			}
		}

		@Nullable
		private BlockPartRotation parseRotation(JsonObject object)
		{
			BlockPartRotation blockpartrotation = null;

			if (object.has("rotation"))
			{
				JsonObject jsonobject = JsonUtils.getJsonObject(object, "rotation");
				Vector3f vector3f = parsePosition(jsonobject, "origin");
				vector3f.scale(0.0625F);
				EnumFacingAxis enumfacing$axis = parseAxis(jsonobject);
				float f = parseAngle(jsonobject);
				boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
				blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
			}

			return blockpartrotation;
		}

		private float parseAngle(JsonObject object)
		{
			return JsonUtils.getFloat(object, "angle");
		}

		private EnumFacingAxis parseAxis(JsonObject object)
		{
			String s = JsonUtils.getString(object, "axis");
			EnumFacingAxis enumfacing$axis = EnumFacingAxis.byName(s.toLowerCase(Locale.ROOT));

			if (enumfacing$axis == null)
			{
				throw new JsonParseException("Invalid rotation axis: " + s);
			}
			else
			{
				return enumfacing$axis;
			}
		}

		private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext deserializationContext, JsonObject object)
		{
			Map<EnumFacing, BlockPartFace> map = parseFaces(deserializationContext, object);

			if (map.isEmpty())
			{
				throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
			}
			else
			{
				return map;
			}
		}

		private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext deserializationContext, JsonObject object)
		{
			Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
			JsonObject jsonobject = JsonUtils.getJsonObject(object, "faces");

			for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet())
			{
				EnumFacing enumfacing = parseEnumFacing(entry.getKey());
				map.put(enumfacing, deserializationContext.deserialize(entry.getValue(), BlockPartFace.class));
			}

			return map;
		}

		private EnumFacing parseEnumFacing(String name)
		{
			return EnumFacing.valueOf(name.toUpperCase());
		}

		private Vector3f parsePositionTo(JsonObject object)
		{
			Vector3f vector3f = parsePosition(object, "to");

			if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F)
			{
				return vector3f;
			}
			else
			{
				throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f parsePositionFrom(JsonObject object)
		{
			Vector3f vector3f = parsePosition(object, "from");

			if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F)
			{
				return vector3f;
			}
			else
			{
				throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f parsePosition(JsonObject object, String memberName)
		{
			JsonArray jsonarray = JsonUtils.getJsonArray(object, memberName);

			if (jsonarray.size() != 3)
			{
				throw new JsonParseException("Expected 3 " + memberName + " values, found: " + jsonarray.size());
			}
			else
			{
				float[] afloat = new float[3];

				for (int i = 0; i < afloat.length; ++i)
				{
					afloat[i] = JsonUtils.getFloat(jsonarray.get(i), memberName + "[" + i + "]");
				}

				return new Vector3f(afloat[0], afloat[1], afloat[2]);
			}
		}
	}
}
